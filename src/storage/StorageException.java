package storage;
/**
 * Signals that an Client exception of some sort has occurred. 
 */
public
class StorageException extends Exception {
	
	private static final long serialVersionUID = 1773217326946381591L;
	
    public StorageException(String message) {
        super(message);
    }
}
