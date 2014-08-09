package generator;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;
import java.util.Date;
import java.util.List;

/** Assumes UTF-8 encoding. JDK 7+. */
public class StorageTestDataGenerator {
 final static String FILE = "/Users/vslovik/Dropbox/projects/java/Uffizzi/nomi.u.txt";
 final static Charset ENC = StandardCharsets.UTF_8;
  private List<String> lines;	
  
  public static void main(String... aArgs) throws IOException {	
    StorageTestDataGenerator parser = new StorageTestDataGenerator(FILE);
    parser.processLineByLine();
  }
  
  /**
   Constructor.
   @param aFileName full name of an existing, readable file.
  */
  public StorageTestDataGenerator(String file) throws IOException{
    fFilePath = Paths.get(file);
	Path path = Paths.get(file);
    lines = Files.readAllLines(path, ENC);;
  }
    
  public final void processLineByLine() throws IOException {
	Random randomGenerator = new Random();
	RandomDate randomDateGenerator = new RandomDate("2014-08-15 00:00:00", "2015-08-15 00:00:00");
    try (Scanner scanner =  new Scanner(fFilePath, ENCODING.name())){
      while (scanner.hasNextLine()){
        processLine(scanner.nextLine(), randomGenerator, randomDateGenerator);
      }   
      scanner.close();
    }
  }
  
  protected void processLine(String aLine, Random randomGenerator, RandomDate randomDateGenerator){
    //use a second Scanner to parse the content of each line 
	  int numberVisitors = 1 + randomGenerator.nextInt(24);
	  boolean guide = randomGenerator.nextBoolean();
	  boolean reduction;
	  if(numberVisitors > 10){
		  reduction = randomGenerator.nextBoolean();
	  } else {
		  reduction = false;
	  }
	  Date date = randomDateGenerator.getDate();
	  SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	  List<String> visitors = new ArrayList<String>();
		if (guide || reduction) {
			for (int i = 0; i < numberVisitors; i++) {
				visitors.add(lines.get(randomGenerator.nextInt(lines.size())));
			}
		}
	  aLine += ";" + df.format(date);
	  aLine += ";" + numberVisitors;   
	  aLine += ";" + (guide ? "YES" : "NO");
	  aLine += ";" + (reduction ? "YES" : "NO");
	  aLine += ";" + visitors.toString();
   // aLine += ";" + date.toString();
	  System.out.println(aLine);	
	  
//    Scanner scanner = new Scanner(aLine);
//    scanner.useDelimiter("=");
//    if (scanner.hasNext()){
//      //assumes the line has a certain structure
//      String name = scanner.next();
//      String value = scanner.next();
//      log("Name is : " + quote(name.trim()) + ", and Value is : " + quote(value.trim()));
//    }
//    else {
//      log("Empty or invalid line. Unable to process.");
//    }
//    scanner.close();
  }
  
  // PRIVATE 
  private final Path fFilePath;
  private final static Charset ENCODING = StandardCharsets.UTF_8;  
  
} 