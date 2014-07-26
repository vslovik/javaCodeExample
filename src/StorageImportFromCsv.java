import java.io.IOException;

public class StorageImportFromCsv {
	final static String FILE = "/Users/vslovik/Dropbox/projects/java/Uffizzi/data.csv";

	public static void main(String... aArgs) throws IOException {
		Storage storage = new Storage("visitors");
		storage.importFromCsv(FILE);
	}
}

