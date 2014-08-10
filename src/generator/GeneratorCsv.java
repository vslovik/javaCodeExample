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

public class GeneratorCsv {

	final static String FILE_INPUT  = "nomi.txt";
	final static String FILE_OUTPUT = "data.csv";	
	final static String DELIMITER   = ";";	
	
	final static Charset ENC = StandardCharsets.UTF_8;
	
	final static int DATE_SPAN_YEARS = 2;
	final static int MAX_VISITORS    = 25;
	
	private long span;
	private Random random;
	private FileWriter writer;
	private List<String> lines;

	public GeneratorCsv() {
		String path = System.getProperty("user.dir");
		
		span = ((long)DATE_SPAN_YEARS*365*24*60*60)*1000;
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
	
	private Date getRandomDate(){
		return new Date(System.currentTimeMillis() + (long) 86400000*random.nextInt(DATE_SPAN_YEARS*365));
	}
	
	public static void main(String[] args) {
		System.out.println("start");
		new GeneratorCsv();
		System.out.println("done");
	}
}
