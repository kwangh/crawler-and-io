package ku.crawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class WebCrawlerTest {
	public static void textCrawler(String path, String result) throws IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(result), "UTF-8"));
		BufferedReader br = new BufferedReader(new FileReader(new File(path)));
		StringBuilder sb = new StringBuilder();

		String urlToFetch, s;
		int i = 0;
		Document doc;

		while ((s = br.readLine()) != null) {
			i++;
			urlToFetch = s;
			try {
				doc = Jsoup.connect(urlToFetch).get();
				if (doc.select("div[id=detailsPlayerID]").text() != null && doc.select("table[id=realLifeTable] td").first().text() != null) {
					sb.append("Player" + doc.select("div[id=detailsPlayerID]").text() + ": " + doc.select("table[id=realLifeTable] td").first().text() + "\n");

					if (i % 1000 == 0) {
						bw.write(sb.toString());
						sb.setLength(0);
					}
				}
			} catch (Exception e) {
				System.out.println(urlToFetch);
			}
		}
		br.close();
		bw.close();
	}

	public static void webCrawler(String result) throws IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(result), "UTF-8"));
		StringBuilder sb = new StringBuilder();

		String urlToFetch;
		Document doc;

		for (int i = 1; i < 76953; i++) {

			urlToFetch = "http://en.soccerwiki.org/player.php?pid=" + i;
			try {
				doc = Jsoup.connect(urlToFetch).get();

				Elements playerTable = doc.select("table[id=realLifeTable] td");
				sb.append("PID[" + doc.select("div[id=detailsPlayerID]").text() + "]\n");

				// name
				sb.append("Name[" + playerTable.get(0).text() + "]\n");
				// club
				sb.append("Club[" + playerTable.get(1).text() + "]\n");
				// age
				sb.append("Age[" + playerTable.get(2).text() + "]\n");
				// birth
				sb.append("Birth[" + playerTable.get(3).text() + "]\n");
				// nation
				sb.append("Nation[" + playerTable.get(4).text().replace("\u00a0", "").trim() + "]\n");
				// height
				if (playerTable.get(5).text().equals("N/A")) {
					sb.append("Height[null]\n");
				} else {
					sb.append("Height[" + playerTable.get(5).text() + "]\n");
				}
				// weight
				if (playerTable.get(6).text().equals("N/A")) {
					sb.append("Weight[null]\n");
				} else {
					sb.append("Weight[" + playerTable.get(6).text() + "]\n");
				}
				// position
				sb.append("Position[" + playerTable.get(7).text() + "]\n");
				// preferred_foot
				sb.append("Foot[" + playerTable.get(8).text() + "]\n");
				if (i % 1000 == 0) {
					bw.write(sb.toString());
					sb.setLength(0);
				}
			} catch (Exception e) {
				System.out.println(urlToFetch);
			}
		}

		bw.write(sb.toString());
		sb.setLength(0);
		bw.close();
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		// textCrawler("catch문에서나온거.txt","playerdataExtra.txt");

		webCrawler("test.txt");

		// String urlToFetch =
		// "http://en.wikipedia.org/wiki/List_of_foreign_Serie_A_players";
		//
		// Document doc = Jsoup.connect(urlToFetch).get();
		//
		// File input = new File("C:\\Users\\K\\Desktop\\playerdata.xml");
		//
		// Elements playerList = doc.select("div.mw-content-ltr >ul > li");
		//
		// for (Element pl : playerList) {
		// // System.out.println("id: "+pl.attr("id"));
		// // bw.write("id: "+pl.attr("id")+ "\n");
		// // System.out.println("Player: "+pl.attr("f")+" "+pl.attr("s"));
		// bw.write("Player" + pl.attr("id") + ": " + pl.attr("f") + " " +
		// pl.attr("s") + "\n");
		// }
		// String[] s;
		// for (Element pl : playerList) {
		// s = pl.text().split("–");
		//
		// if (s.length > 2) {
		// if (s[0].length() > 1) {
		// if (s[0].startsWith(" ")) {
		// s[0] = s[0].substring(1);
		// }
		// if (s[0].endsWith(" ")) {
		// s[0] = s[0].substring(0, s[0].length() - 1);
		// }
		// // System.out.println("Player: " + s[0]);
		// bw.write("Player: " + s[0] + "\n");
		//
		// if (s[1].contains("-")) {
		// s[1] = s[1].split("-")[0];
		// }
		// if (s[1].startsWith(" ")) {
		// s[1] = s[1].substring(1);
		// }
		// if (s[1].endsWith(" ")) {
		// s[1] = s[1].substring(0, s[1].length() - 1);
		// }
		// // System.out.println("Team: " + s[1]);
		// bw.write("Team: " + s[1] + "\n");
		// } else {
		// if (s[1].startsWith(" ")) {
		// s[1] = s[1].substring(1);
		// }
		// if (s[1].endsWith(" ")) {
		// s[1] = s[1].substring(0, s[1].length() - 1);
		// }
		// // System.out.println("Player: " + s[1]);
		// bw.write("Player: " + s[1] + "\n");
		//
		// if (s[2].contains("-")) {
		// s[2] = s[2].split("-")[0];
		// }
		// if (s[2].startsWith(" ")) {
		// s[2] = s[2].substring(1);
		// }
		// if (s[2].endsWith(" ")) {
		// s[2] = s[2].substring(0, s[2].length() - 1);
		// }
		// // System.out.println("Team: " + s[2]);
		// bw.write("Team: " + s[2] + "\n");
		// }
		//
		// }
		// bw.write("Player: " + pl.child(0).text() + "\n");
		// bw.write("Team: " + pl.child(1).text() + "\n");
		// System.out.println("Player: " + s[0]);
		// System.out.println("Team: " + s[1]);
		// System.out.println("Player: " + pl.child(0).text());
		// System.out.println("Team: " + pl.child(1).text());
		// }
		// bw.close();
	}
}
