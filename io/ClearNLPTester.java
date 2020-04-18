package ku.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import com.clearnlp.component.AbstractComponent;
import com.clearnlp.dependency.DEPNode;
import com.clearnlp.dependency.DEPTree;
import com.clearnlp.nlp.NLPGetter;
import com.clearnlp.nlp.NLPMode;
import com.clearnlp.reader.AbstractReader;
import com.clearnlp.segmentation.AbstractSegmenter;
import com.clearnlp.tokenization.AbstractTokenizer;
import com.clearnlp.util.UTInput;
import com.clearnlp.util.UTOutput;

public class ClearNLPTester {
	final String language = AbstractReader.LANG_EN;
	AbstractTokenizer tokenizer;
	AbstractComponent tagger;
	AbstractComponent parser;
	AbstractComponent identifier;
	AbstractComponent classifier;
	AbstractComponent labeler;

	AbstractComponent[] components;

	public void ClearNLPInitializer(String modelType) throws IOException {
		tokenizer = NLPGetter.getTokenizer(language);
		tagger = NLPGetter.getComponent(modelType, language, NLPMode.MODE_POS);
		parser = NLPGetter.getComponent(modelType, language, NLPMode.MODE_DEP);
		identifier = NLPGetter.getComponent(modelType, language, NLPMode.MODE_PRED);
		classifier = NLPGetter.getComponent(modelType, language, NLPMode.MODE_ROLE);
		labeler = NLPGetter.getComponent(modelType, language, NLPMode.MODE_SRL);
		components = new AbstractComponent[] { tagger, parser, identifier, classifier, labeler };
	}

	public ClearNLPTester(String modelType, String sentence) throws Exception {
		ClearNLPInitializer(modelType);
		process(tokenizer, components, sentence);
	}

	public ClearNLPTester(String modelType, String inputFile, String outputFile) throws Exception {
		ClearNLPInitializer(modelType);
		process(tokenizer, components, UTInput.createBufferedFileReader(inputFile), UTOutput.createPrintBufferedFileStream(outputFile));
	}

	public ClearNLPTester(String inputFile) throws Exception {
		String modelType = "general-en";
		ClearNLPInitializer(modelType);
		processString(tokenizer, components, new BufferedReader(new FileReader(inputFile)));
	}

	public void process(AbstractTokenizer tokenizer, AbstractComponent[] components, String sentence) {
		DEPTree tree = NLPGetter.toDEPTree(tokenizer.getTokens(sentence));

		for (AbstractComponent component : components) {
			component.process(tree);
		}

		// System.out.println(sentence);
		// semantic role labeler 포함
		// System.out.println(tree.toStringSRL() + "\n");
		// dependency parser 까지 포함
		// System.out.println(tree.toStringDEP() + "\n");

		int size = tree.size();

		for (int i = 1; i < size; i++) {
			DEPNode dnode = tree.get(i);

			// int id = dnode.id;
			String form = dnode.form;
			// String lemma = dnode.lemma;
			String pos = dnode.pos;
			// int parent = dnode.getHead().id;
			// String deplab = dnode.getHeadArc().getLabel();

			// morphAnalyzer 제외
			// http://clearnlp.wikispaces.com/morphAnalyzer
			// System.out.println(id + " " + form + " " + lemma + " " + pos +
			// " " + parent + " " + deplab);
			System.out.println(form + " " + pos);
			// System.out.println(form + ' ' + pos);
		}
	}

	public void process(AbstractTokenizer tokenizer, AbstractComponent[] components, BufferedReader reader, PrintStream fout) {
		AbstractSegmenter segmenter = NLPGetter.getSegmenter(language, tokenizer);
		DEPTree tree;
		StringBuilder sb = new StringBuilder();

		for (List<String> tokens : segmenter.getSentences(reader)) {
			tree = NLPGetter.toDEPTree(tokens);

			for (AbstractComponent component : components)
				component.process(tree);

			// fout.println(tree.toStringSRL() + "\n");
			int size = tree.size();

			for (int i = 1; i < size; i++) {
				DEPNode dnode = tree.get(i);
				DEPNode tmp;

				// int id = dnode.id;
				String form = dnode.form;
				// String lemma = dnode.lemma;
				String pos = dnode.pos;
				int parent = dnode.getHead().id;
				String deplab = dnode.getHeadArc().getLabel();

				// morphAnalyzer 제외
				// http://clearnlp.wikispaces.com/morphAnalyzer
				tmp = tree.get(parent);
				// sb.append(form + " " + pos + " " + tmp.form + " " + tmp.pos +
				// "\n");
				// sb.append(form + " " + pos + " " + tmp.form + "\n");
				sb.append(form + " " + pos + " " + tmp.form + " " + tmp.pos + " " + deplab + "\n");
				// sb.append(form + " " + pos + " " + tmp.form + " " + deplab +
				// "\n");
				// sb.append(form + " " + pos + "\n");
			}
			sb.append('\n');
		}

		fout.append(sb.toString());
		fout.close();
	}

	public void processString(AbstractTokenizer tokenizer, AbstractComponent[] components, BufferedReader reader) {
		AbstractSegmenter segmenter = NLPGetter.getSegmenter(language, tokenizer);
		DEPTree tree;
		// Set<String> s = new HashSet<String>();
		for (List<String> tokens : segmenter.getSentences(reader)) {
			tree = NLPGetter.toDEPTree(tokens);
			for (AbstractComponent component : components)
				component.process(tree);

			// if(tree.get(1).pos.equals("NN"))
			for (int i = 1; i < tree.size() - 3; i++)
				if (tree.get(i).pos.equals("XX"))
					System.out.println("단어: " + tree.get(i).form);

			// if (!s.contains(tree.get(1).pos))
			// s.add(tree.get(1).pos);
		}
		// System.out.println(s);
	}

	public static void main(String[] args) throws Exception {
		// String modelType = "general-en"; // "general-en" or "medical-en"
		// modelType = "medical-en"; // "general-en" or "medical-en"
		// String inputFile =
		// "C:/Users/K/Downloads/CRF연습-음식식별기/separated/The Cheesecake Factory Menu.txt";
		// String outputFile = "TheCheesecakeFactoryMenuPOS.txt";

//		String inputFile = "Z:/[BOSS]/WordNetChecked_bossNewDic_2015-04-14.txt";
//		new ClearNLPTester(inputFile);

		// try {
		// String sentence = "I'd like to meet Dr... Choi.";
		// new ClearNLPTester(modelType, sentence);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		// try {
		// new ClearNLPTester(modelType, inputFile, outputFile);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		System.out.println("ClearNLP ended");
	}
}
