package ku.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class 문장합치기 {
	public static void run(String readPath, String writePath)
			throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(readPath));
		BufferedWriter bw = new BufferedWriter(new FileWriter(writePath));
		String sentence;
		StringBuilder sb = new StringBuilder(); // 원래 문장
		String[] tokens;
		ArrayList<String> tokenList = new ArrayList<String>();

		while ((sentence = br.readLine()) != null) {
			tokens = sentence.split("\\s+");
			if (tokens.length > 1) {
				tokenList.add(tokens[0]);
			} else {
				for (int i = 0; i < tokenList.size(); i++) {
					if (i == tokenList.size() - 2) {
						sb.append(tokenList.get(i));
					} else if (i == tokenList.size() - 1) {
						sb.append(tokenList.get(i) + "\n");
					} else {
						sb.append(tokenList.get(i) + " ");
					}
				}

				bw.write(sb.toString());
				sb.setLength(0);
				tokenList.clear();
			}
		}
		for (int i = 0; i < tokenList.size(); i++) {
			if (i == tokenList.size() - 2) {
				sb.append(tokenList.get(i));
			} else if (i == tokenList.size() - 1) {
				sb.append(tokenList.get(i) + "\n");
			} else {
				sb.append(tokenList.get(i) + " ");
			}
		}

		bw.write(sb.toString());
		br.close();
		bw.close();
	}

	public static void main(String[] args) throws IOException {
		run("C:/Users/K/Desktop/FullMenu.txt",
				"C:/Users/K/Desktop/FullMenuRaw.txt");
	}
}
