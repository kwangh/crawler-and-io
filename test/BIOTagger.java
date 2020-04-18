package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class BIOTagger {
	/**
	 * raw 문장에서 BIO 태그만 포함해주는 코드
	 * 임시방편 소스코드
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(
				"FullMenuRawPOS.txt"));
		BufferedReader br2 = new BufferedReader(new FileReader(
				"C:/Users/K/Desktop/FullMenu.txt"));
		BufferedWriter bw = new BufferedWriter(new FileWriter(
				"FullMenuBIO.txt"));

		StringBuilder sb = new StringBuilder();
		int count = 0, count2 = 0;
		String sentence, sentence2;
		String[] tokens, tokens2;
		int tokenLength;

		while ((sentence = br.readLine()) != null) {
			count++;
			sentence2 = br2.readLine();
			count2++;
			tokens = sentence.split("\\s+");
			tokens2 = sentence2.split("\\s+");

			if (tokens.length > 1 && tokens2.length > 1) {
				if (tokens[0].equals(tokens2[0])) {
					System.out.println("BIO : " + sentence2 + " " + count2);
					System.out.println("POS : " + sentence + " " + count);
					sb.append(sentence + " " + tokens2[2] + "\n");
				} else if (tokens2[0].length() > tokens[0].length()) {
					// BIO tag된 부분이 긴경우
					tokenLength = tokens2[0].length() - tokens[0].length();
					sb.append(sentence + " " + tokens2[2] + "\n");
					System.out.println("BIO : " + sentence2 + " " + count2);
					System.out.println("POS : " + sentence + " " + count);
					while (tokenLength > 0) {
						sentence = br.readLine();
						count++;
						tokens = sentence.split("\\s+");
						if (tokens.length == 1 && tokens2.length > 1) {
							sentence = br.readLine();
							count++;
							tokens = sentence.split("\\s+");
						}
						tokenLength = tokenLength - tokens[0].length();
						sb.append(sentence + " " + tokens2[2] + "\n");
						System.out.println("POS : " + sentence + " " + count);
					}
				} else {
					// POS tag된 부분이 긴경우
					tokenLength = tokens[0].length() - tokens2[0].length();
					System.out.println("POS : " + sentence + " " + count);
					System.out.println("BIO : " + sentence2 + " " + count2);
					while (tokenLength > 0) {
						sentence2 = br2.readLine();
						count2++;
						tokens2 = sentence2.split("\\s+");
						if (tokens.length > 1 && tokens2.length == 1) {
							sentence2 = br2.readLine();
							count2++;
							tokens2 = sentence2.split("\\s+");
						}
						tokenLength = tokenLength - tokens2[0].length();
						System.out.println("BIO : " + sentence2 + " " + count2);
					}
					sb.append(sentence + tokens2[2] + "\n");
				}
			} else if (tokens.length == 1 && tokens2.length > 1) {
				sentence = br.readLine();
				count++;
				tokens = sentence.split("\\s+");
			} else if (tokens.length > 1 && tokens2.length == 1) {
				sentence2 = br2.readLine();
				count2++;
				tokens2 = sentence2.split("\\s+");
			}else{
				sb.append("\n");
			}

			if (count % 1000 == 0) {
				bw.write(sb.toString());
				sb.setLength(0);
			}
		}
		bw.write(sb.toString());
		br.close();
		br2.close();
		bw.close();
		
		System.out.println("BIO all tagged");
	}
}
