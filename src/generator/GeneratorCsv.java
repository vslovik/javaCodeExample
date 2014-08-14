package generator;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import storage.Visit;

/** 
 * The {@code GeneratorCsv} generates test visits data
 * based on the list of names "nomi.txt"
 * To create next visit data:
 * randomly picks:
 * name from the list,
 * date from 2 two future years time interval,
 * visitors names from the same list of names.
 * Guide and reduction (for visits with number
 * of visitors above the threshold) are also
 * random (boolean) values.
 * 
 * Writes data into .csv file
 * 
 * @author  Valeriya Slovikovskaya
*/
public class GeneratorCsv {

	/**
	 * Input file
	 */
	final static String FILE_INPUT  = "nomi.txt";
	
	/**
	 * Output file
	 */
	final static String FILE_OUTPUT = "data.csv";	
	
	/**
	 * Field delimiter for .csv file
	 */
	final static String DELIMITER   = ";";	
	
	/**
	 * Input file encoding
	 */
	final static Charset ENC = StandardCharsets.UTF_8;
	
	/**
	 * Time interval in years
	 */
	final static int DATE_SPAN_YEARS = 2;
	
	/**
	 * Maximum number of visitors per visit
	 */
	final static int MAX_VISITORS    = 25;
	
	/**
	 * Instance of random values generator
	 */
	private Random random;
	
	/**
	 * File writer instance
	 */
	private FileWriter writer;
	
	/**
	 * List of input file lines
	 */
	private List<String> lines;

	/**
	 * Class constructor
	 * Initializes random generator
	 * Opens output stream writer
	 * Reads input
	 * Calls private methods: 
	 * {@link #createVisit(String)} and 
	 * {@link #writeVisit(Visit)}
	 * 
	 * Clean up resources on program termination
	 * 
	 * @see #createVisit(String)
	 * @see #writeVisit(Visit)
	 */
	public GeneratorCsv() {
		String path = System.getProperty("user.dir");
		
		random = new Random();
	
		try {
			writer = new FileWriter(path + "/" + FILE_OUTPUT);
			lines = Files.readAllLines(Paths.get(path + "/" + FILE_INPUT), ENC);
			for(String name : lines) {
				writeVisit(createVisit(name));
			}	
			writer.flush();		
		} catch (IOException e) {
			System.out.println(e);
			return;
		} finally {
			try {
				if(writer != null)
					writer.close();
			} catch (IOException e) {
				System.out.println(e);
			}
		};
	}
	
	/**
	 * Write the single visit line to the output 
	 * 
	 * @param visit Visit
	 * 
	 * @throws IOException
	 */
	private void writeVisit(Visit visit) throws IOException {
		writer.append(visit.getName());
		writer.append(DELIMITER);
		writer.append(Visit.dateFormat.format(visit.getDate()));
		writer.append(DELIMITER);
		writer.append(Integer.toString(visit.getVisitorNumber()));
		writer.append(DELIMITER);
		writer.append(visit.hasGuide() ? "yes" : "no");
		writer.append(DELIMITER);
		writer.append(visit.hasReduction() ? "yes" : "no");
		writer.append(DELIMITER);
		writer.append(visit.getVisitorNames().toString());
		writer.append("\n");
	}
	
	/**
	 * Creates Visit instance with randomly chosen properties
	 * 
	 * @param name Name, taken from input file
	 * @return created visit
	 */
	private Visit createVisit(String name){
		Visit visit = new Visit();
		visit.setName(name);
		visit.setDate(getRandomDate());
		visit.setVisitorNumber(1 + random.nextInt(MAX_VISITORS));
		Vector<String> visitors = new Vector<String>();
		for (int i = 0; i < visit.getVisitorNumber(); i++) {
			visitors.add(lines.get(random.nextInt(lines.size())));
		}
		visit.setVisitorNames(visitors);
		if(random.nextBoolean()){
			visit.setGuide();
		}
		if(random.nextBoolean()){
			visit.setReduction();
		}
		return visit;
	}
	
	/**
	 * Picks random date from {@value #DATE_SPAN_YEARS} interval
	 * 
	 * @return randomly generated date
	 */
	private Date getRandomDate(){
		return new Date(System.currentTimeMillis() + (long) 86400000*random.nextInt(DATE_SPAN_YEARS*365));
	}
	
	/**
	 * Main method: creates class instance
	 * Shows start and termination messages
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("start");
		new GeneratorCsv();
		System.out.println("done");
	}
}
