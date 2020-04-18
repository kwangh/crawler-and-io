package ku.io;

import java.io.File;
import java.util.ArrayList;

public class FileIO {

	public static ArrayList<String> readFileList(String path) {
		ArrayList<String> al = new ArrayList<String>();

		for (File file : new File(path).listFiles()) {
			if (file.isDirectory()) {
				al.addAll(readFileList(file.toString()));
			} else {
				al.add(file.toString());
			}
		}
		return al;
	}
	
	public static ArrayList<String> readFileList(File file) {
		ArrayList<String> al = new ArrayList<String>();

		for (File files : file.listFiles()) {
			if (files.isDirectory()) {
				al.addAll(readFileList(files));
			} else {
				al.add(files.toString());
			}
		}
		return al;
	}
}
