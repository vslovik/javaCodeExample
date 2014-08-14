package storage;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;
import java.util.Vector;

/** 
 * The {@code ImportFromCsv} class is created
 * to import visits data from .csv file
 * 
 * @author  Valeriya Slovikovskaya
*/
public class ImportFromCsv {

	/**
	 * Input file name
	 */
	final static String FILE_INPUT = "data.csv";	
	
	/**
	 * Field delimiter for .csv file
	 */
	final static String FIELDS_DELIMITER   = ";";
	
	/**
	 * Visitors list delimiter
	 */
	final static String VISITORS_DELIMITER = ",";	
	
	/**
	 * Input file encoding
	 */
	final static Charset ENC = StandardCharsets.UTF_8;
	
	/**
	 * {@link List<String>} of input file lines
	 */
	private List<String> lines;
	
	/**
	 * {@link Storage} instance
	 */
	private Storage storage;
	
	/**
	 * Class constructor
	 * Initializes {@link Storage}
	 * Reads input file, process it line by line
	 * Saves data in the {@link Storage}
	 * 
	 * @see #createVisit(String)
	 * @see #writeVisit(Visit)
	 */
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
	
	/**
	 * Process input line
	 * 
	 * @param line Line (one visit's representation)
	 */
	private void process(String line){
		try {
			storage.put(createVisit(line));
		} catch (StorageException e) {
			System.out.println(e);
		}
	}
	
	/**
	 * Creates Visit instance in base of parsed input line
	 * 
	 * @param line Input line
	 * @return created visit
	 */
	private Visit createVisit(String line)
	{
		Visit visit = new Visit();
		String[] data = line.split(FIELDS_DELIMITER);
		visit.setName(data[0]);
		try {
			visit.setDate(Visit.dateFormat.parse(data[1]));
		} catch (ParseException e) {
			System.out.println(e);
		}
		visit.setVisitorNumber(Integer.parseInt(data[2]));
		
		Vector<String> visitors = new Vector<String>();
		if (data[5].length() != 0) {
			String[] arr = data[5].substring(1, data[5].length() - 1).split(
					VISITORS_DELIMITER);
			if (arr.length > 0) {
				for (String name : arr) {
					visitors.add(name);
				}
			}
		}
		visit.setVisitorNames(visitors);
		
		if(data[3].equals("yes")){
			visit.setGuide();
		}
		if(data[4].equals("yes")){
			visit.setReduction();
		}
		return visit;
	}
	
	/**
	 * Main method: creates class instance
	 * @param args
	 */
	public static void main(String[] args) throws IOException {	
		new ImportFromCsv();
	}
}

