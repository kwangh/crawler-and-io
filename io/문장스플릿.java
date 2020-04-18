package ku.io;

public class 문장스플릿 {
	/**
	 * @return 단어들
	 * @param sentence
	 *            문장 형식 - 단어 단어\n단어 단어\n
	 */
	public static String[][] 문장단위스플릿(String sentence) {
		String[] lines = sentence.split("\n");
		String[][] tokens = new String[lines.length][];
		for (int i = 0; i < lines.length; i++)
			tokens[i] = lines[i].split("\\s+");

		// System.out.println(tokens.length); //줄 길이

		return tokens;
	}
	
	public static void main(String[] args) {
		String s = "serine|methyl group|cofactor|cysteine|dmso|asparagine|brdu|2-mercaptoethanol|glycine|alanine|tris|sinefungin|phenylalanine|lysine|hmts|methionine|hygromycin|maintain a|ezh2 inhibitor|histidine";
		System.out.println(s.replaceAll("\\|", " "));
	}
}
