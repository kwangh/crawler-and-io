package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import ku.io.문장스플릿;

public class ShuffleTester {

	private ArrayList<String> al;
	//10 fold 하기 위해 자를 문장 개수
	private static int TEN_FOLD;

	public ShuffleTester(String path) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(path));
		String sentence;
		StringBuilder sb = new StringBuilder();
		al = new ArrayList<String>();
		while ((sentence = br.readLine()) != null) {
			if (sentence.length() != 0) {
				//형식 : 문장\n
				sb.append(sentence + "\n");
			} else {
				al.add(sb.toString());
				sb.setLength(0);
			}
		}
		al.add(sb.toString());
		TEN_FOLD=al.size()/10;
		//섞는다
		Collections.shuffle(al);
		br.close();

		printOut();
	}

	public void printOut() throws IOException {
		BufferedWriter trainBw, testABw, testBw;

		for (int i = 0; i < 10; i++) {
			testABw = new BufferedWriter(new FileWriter("testdataAnswer" + i
					+ ".txt"));
			trainBw = new BufferedWriter(new FileWriter("traindata" + i
					+ ".txt"));
			testBw = new BufferedWriter(new FileWriter("testdata" + i + ".txt"));
			for (int j = 0; j < al.size(); j++) {

				if (j >= TEN_FOLD * i && j < TEN_FOLD * (i + 1)) {
					testABw.write(al.get(j) + "\n");
					String sentence = testdataMaker(al.get(j));
					testBw.write(sentence+"\n");
				} else {
					trainBw.write(al.get(j) + "\n");
				}
			}
			testABw.close();
			trainBw.close();
			testBw.close();
		}
	}

	/**
	 * 
	 * @param sentences
	 * 			문장\n문장\n
	 * @return
	 * 			BIOtag를 제거한 문장
	 */
	public String testdataMaker(String sentences) {
		String[][] tokens = 문장스플릿.문장단위스플릿(sentences);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tokens.length; i++) {
			for(int j=0;j<tokens[i].length-1;j++){
				//BIO tag를 제외한 문장을 연결해 준다.
				if(j==tokens[i].length-2)
					sb.append(tokens[i][j]+"\n");
				else
					sb.append(tokens[i][j]+" ");
			}
		}
		
		return sb.toString();
	}

	public static void main(String[] agrs) {
		try {
			new ShuffleTester("FullMenuBIO.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
