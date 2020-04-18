package ku.mallet.ner.crf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;
import cc.mallet.fst.CRF;
import cc.mallet.fst.CRFTrainerByLabelLikelihood;
import cc.mallet.fst.MaxLatticeDefault;
import cc.mallet.fst.Transducer;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.SimpleTaggerSentence2TokenSequence;
import cc.mallet.pipe.TokenSequence2FeatureVectorSequence;
import cc.mallet.pipe.iterator.LineGroupIterator;
import cc.mallet.pipe.tsf.OffsetConjunctions;
import cc.mallet.pipe.tsf.RegexMatches;
import cc.mallet.pipe.tsf.TokenFirstPosition;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Sequence;

public class CRFTrainer {

	public CRFTrainer() {
	}

	public Pipe pipePreprocessor() {
		Pipe pipe;
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
		int[][] conjunctions = new int[2][];
		conjunctions[0] = new int[] { -1 };
		conjunctions[1] = new int[] { 1 };

		pipeList.add(new SimpleTaggerSentence2TokenSequence());
		pipeList.add(new OffsetConjunctions(conjunctions));
		pipeList.add(new RegexMatches("CAPITALIZED", Pattern.compile("^\\p{Lu}.*")));
		pipeList.add(new TokenFirstPosition("FIRSTTOKEN"));
		pipeList.add(new TokenSequence2FeatureVectorSequence());

		pipe = new SerialPipes(pipeList);
		return pipe;
	}

	public void Train(String trainingFilename, String outputCRFPath) throws IOException {
		Pipe pipe = pipePreprocessor();
		pipe.getTargetAlphabet().lookupIndex("O");
		InstanceList instances = null;

		instances = new InstanceList(pipe);

		instances.addThruPipe(new LineGroupIterator(new FileReader(trainingFilename), Pattern.compile("^\\s*$"), true));

		CRF crf = new CRF(pipe, null);
		// crf.addStatesForBiLabelsConnectedAsIn(instances);
		crf.addStatesForLabelsConnectedAsIn(instances);
		// crf.addStatesForThreeQuarterLabelsConnectedAsIn(instances);
		// crf.addStartState();

		CRFTrainerByLabelLikelihood trainer = new CRFTrainerByLabelLikelihood(crf);
		trainer.setGaussianPriorVariance(10.0);
		// CRFTrainerByStochasticGradient trainer =
		// new CRFTrainerByStochasticGradient(crf, 1.0);

		// CRFTrainerByL1LabelLikelihood trainer =
		// new CRFTrainerByL1LabelLikelihood(crf, 0.75);

		// trainer.addEvaluator(new PerClassAccuracyEvaluator(Instances,
		// "training"));
		trainer.train(instances);

		File outputCRF = new File(outputCRFPath);
		ObjectOutputStream oos;
		oos = new ObjectOutputStream(new FileOutputStream(outputCRF));
		oos.writeObject(crf);
		oos.close();
	}

	// /**
	// *
	// * @param testPath
	// * NLP적용 되지 않은 문장 처리
	// * @return Reader 형식
	// */
	// public Reader NLPPreprocessor(String testPath) {
	// ClearNLPTester c = null;
	// try {
	// c = new ClearNLPTester(testPath);
	// } catch (Exception e) {
	// System.out.println("clear NLP error");
	// e.printStackTrace();
	// }
	// return new StringReader(c.getResult());
	// }

	@SuppressWarnings("rawtypes")
	public void test(String trainedCRF, String testPath, int fileIndex) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter("Result" + fileIndex + ".txt"));

		InstanceList testData = null;
		testData = new InstanceList(pipePreprocessor());

		testData.addThruPipe(new LineGroupIterator(new FileReader(testPath), Pattern.compile("^\\s*$"), true));

		ObjectInputStream s = new ObjectInputStream(new FileInputStream(trainedCRF));
		CRF crf = null;
		try {
			crf = (CRF) s.readObject();
			s.close();
		} catch (ClassNotFoundException e) {
			System.out.println("mallet file error");
			e.printStackTrace();
		}
		if (crf != null) {
			for (int i = 0; i < testData.size(); i++) {
				Sequence input = (Sequence) testData.get(i).getData();
				Sequence[] outputs = apply(crf, input, 1);
				int k = outputs.length;
				boolean error = false;
				for (int a = 0; a < k; a++) {
					if (outputs[a].size() != input.size()) {
						System.out.println("Failed to decode input sequence " + i + ", answer " + a);
						error = true;
					}
				}
				if (!error) {
					for (int j = 0; j < input.size(); j++) {
						StringBuffer buf = new StringBuffer();
						for (int a = 0; a < k; a++) {
							buf.append(outputs[a].get(j).toString()).append(" ");
						}

						FeatureVector fv = (FeatureVector) input.get(j);
						buf.append(fv.toString(true));
						// System.out.println(buf.toString());
						bw.write(buf.toString() + "\n");
					}
					// System.out.println();
					bw.write("\n");
				}
			}
		}
		bw.close();
	}

	/**
	 * Apply a transducer to an input sequence to produce the k highest-scoring
	 * output sequences.
	 * 
	 * @param model
	 *            the <code>Transducer</code>
	 * @param input
	 *            the input sequence
	 * @param k
	 *            the number of answers to return
	 * @return array of the k highest-scoring output sequences
	 */
	@SuppressWarnings("rawtypes")
	public static Sequence[] apply(Transducer model, Sequence input, int k) {
		Sequence[] answers;
		if (k == 1) {
			answers = new Sequence[1];
			answers[0] = model.transduce(input);
		}
		else {
			MaxLatticeDefault lattice = new MaxLatticeDefault(model, input, null, 100000);

			answers = lattice.bestOutputSequences(k).toArray(new Sequence[0]);
		}
		return answers;
	}

	public static void main(String[] args) {
		CRFTrainer c = new CRFTrainer();
		Date startTime = new Date();
//		for (int i = 0; i < 10; i++) {
//			try {
//				c.Train("traindata" + i + ".txt", "menucrf" + i + ".mallet");
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		Date trainEndTime = new Date();
//		long trainingTime = trainEndTime.getTime() - startTime.getTime();
//		System.out.println("Training TIME : " + trainingTime + "(ms)");

		 try {
		 for (int i = 0; i < 10; i++) {
		 c.test("menucrf" + i + ".mallet", "testdata" + i + ".txt", i);
		 }
		 } catch (IOException e) {
		 e.printStackTrace();
		 }
		 Date testEndTime = new Date();
		 long testingTime = testEndTime.getTime() - startTime.getTime();
		 System.out.println("Testing TIME : " + testingTime + "(ms)");
	}
}
