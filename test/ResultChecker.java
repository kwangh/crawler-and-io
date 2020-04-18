package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ResultChecker {
	private static ArrayList<Integer> bTagged, iTagged;

	public static boolean getTagList(int fileIndex) {
		BufferedReader br = null;
		boolean result = false;
		int rowNum = 0;
		String s;
		bTagged = new ArrayList<Integer>();
		iTagged = new ArrayList<Integer>();
		try {
			br = new BufferedReader(new FileReader("testdataAnswer" + fileIndex
					+ ".txt"));
			result = true;

			while ((s = br.readLine()) != null) {
				if (s.contains("B-FOOD")) {
					bTagged.add(rowNum);
				} else if (s.contains("I-FOOD")) {
					iTagged.add(rowNum);
				}
				rowNum++;
			}
			System.out.println("--------(" + fileIndex
					+ ") Tag list returned--------");
			br.close();
		} catch (IOException e) {
			System.out.println("testdataAnswer" + fileIndex + ".txt missing.");
			e.printStackTrace();
		}
		return result;
	}

	public static void main(String[] args) {
		BufferedReader br;
		String s;
		String[] tokens;
		int rowNum = 0;
		double totalPrecision = 0, totalRecall = 0;

		for (int i = 0; i < 10; i++) {
			if (getTagList(i)) {
				double precisionCount = 0, recallCount = bTagged.size()
						+ iTagged.size(), correctCount = 0;
				try {
					br = new BufferedReader(new FileReader("Result" + i
							+ ".txt"));
					while ((s = br.readLine()) != null) {
						tokens = s.split(" ");
						if (tokens.length > 1) {
							// 2best
							if (tokens[0].equals("B-FODD")
									|| tokens[0].equals("I-FOOD")
									|| tokens[1].equals("B-FOOD")
									|| tokens[1].equals("I-FOOD")) {
								precisionCount++;
								if (bTagged.contains(rowNum)
										|| iTagged.contains(rowNum)) {
									correctCount++;
								}
							}
							// //1best
							// if (tokens[0].equals("B-FODD") ||
							// tokens[0].equals("I-FOOD") ) {
							// precisionCount++;
							// if (bTagged.contains(rowNum) ||
							// iTagged.contains(rowNum)) {
							// correctCount++;
							// }
							// }
						}
						rowNum++;
					}
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (precisionCount != 0) {
					System.out.println("precision : " + correctCount
							/ precisionCount);
					totalPrecision += correctCount / precisionCount;
				} else {
					System.out.println("precision : 0");
				}
				if (recallCount != 0) {
					System.out
							.println("Recall : " + correctCount / recallCount);
					totalRecall += correctCount / recallCount;
				} else {
					System.out.println("Recall : 0");
				}
			}
			rowNum = 0;
		}

		System.out.println("Total Precision : " + totalPrecision / 10
				+ "\nTotal Recall : " + totalRecall / 10);
	}
}
