package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Database {
	public List<DatabaseLine> files;
	private BufferedReader in;
	int lastID;
	Database() throws IOException{
		File yourFile = new File("src/application/txt/files.txt");
		files = new ArrayList<DatabaseLine>();
		String line = null;
		in = new BufferedReader(new FileReader(yourFile));
		lastID=0;
		while((line = in.readLine()) != null) {
			files.add(new DatabaseLine(line));
			lastID++;
		}
	}
	
	public void addLine(String line) {
		files.add(new DatabaseLine(lastID + "\t" + line));
		lastID++;
	}
	
	public void toFile() throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("src/application/txt/files.txt", "UTF-8");
		for(DatabaseLine a : files)
			writer.println(a.toString());
		writer.close();
	}
}
