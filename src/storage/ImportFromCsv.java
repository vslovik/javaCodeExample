package storage;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;
import java.util.Vector;

public class ImportFromCsv {

	final static String FILE_INPUT = "data.csv";	
	final static String DELIMITER   = ";";		
	final static Charset ENC = StandardCharsets.UTF_8;
	
	private List<String> lines;
	private Storage storage;
	
	public ImportFromCsv() {
		storage = new Storage();
		String path = System.getProperty("user.dir");
		System.out.println("start");
		try {
			lines = Files.readAllLines(Paths.get(path + "/" + FILE_INPUT), ENC);
			for(String line : lines) {
				process(line);
			}	
			storage.save();		
		} catch (IOException e) {
			System.out.println(e);
			return;
		} 
		System.out.println("done");
	}
	
	private void process(String line){
		try {
			storage.put(createVisit(line));
		} catch (StorageException e) {
			System.out.println(e);
		}
	}
	
	private Visit createVisit(String line)
	{
		Visit visit = new Visit();
		String[] data = line.split(DELIMITER);
		visit.setName(data[0]);
		try {
			visit.setDate(Visit.dateFormat.parse(data[1]));
		} catch (ParseException e) {
			System.out.println(e);
		}
		visit.setVisitorNumber(Integer.parseInt(data[2]));
		
		Vector<String> visitors = new Vector<String>();
		String[] arr = data[3].substring(1, data[3].length()).split(DELIMITER);
		if(arr.length > 0){
			for(String name : arr){
				visitors.add(name);
			}
		}
		visit.setVisitorNames(visitors);
		
		if(data[4] == "yes"){
			visit.setGuide();
		}
		if(data[4] == "yes"){
			visit.setReduction();
		}
		return visit;
	}
	
	public static void main(String[] args) throws IOException {	
		new ImportFromCsv();
	}
}

