package storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;
/** 
 * The {@code Storage} class provides put, get, delete methods
 * to access stored data. It uses {@link TreeMap} java library 
 * class that implements Red-Black balanced Tree
 * to effectively scale storage minimizing lookup cost
 * 
 * Two {@link TreeMap} are instantiated as class variables:
 * {@link #map} to keep visits sorted by date, and
 * {@link #index} to reorder visits by name
 * 
 * @author Valeriya Slovikovskaya
*/
public class Storage {
	
	/**
	 * Names of storage files
	 */
	public final static String RESOURCE_VISITS = "visits";
	public final static String RESOURCE_INDEX  = "index";
	
	/**
	 * Date-key to {@link Visit} map
	 */
	private TreeMap<String, Visit> map;
	
	/**
	 * Name-key to date-key map
	 */
	private TreeMap<String, String> index;
	
	/**
	 * Class constructor
	 * Loads storage from files
	 * 
	 * @see #read()
	 * 
	 */
	public Storage() {	
		read(RESOURCE_VISITS);
		read(RESOURCE_INDEX);
	}
	
	/**
	 * Loads storage from file
	 * 
	 * @param resourceName File name
	 */
	@SuppressWarnings("unchecked")
	private void read(String resourceName)
	{
		ObjectInputStream stream = null;
		try {
			stream = new ObjectInputStream(	new BufferedInputStream(new FileInputStream(resourceName)));
			if(stream != null) {
				switch(resourceName){
				case RESOURCE_VISITS:
					map = (TreeMap<String, Visit>) stream.readObject();
					break;
				case RESOURCE_INDEX:
					index = (TreeMap<String, String>) stream.readObject();
					break;
				}			
			}		
		} catch (FileNotFoundException e) {
			System.out.println("ATTENSION: file " + resourceName + " does not exsists.");
			System.out.println("It will be created.");
		} catch (ClassNotFoundException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		} finally {
			init(resourceName);
			try {
				if (stream != null)
					stream.close();
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}
	
	/**
	 * Creates empty storage instances
	 * in case storage files are unreadable
	 * 
	 * If only index file lost, 
	 * restore it, re-indexing map storage
	 * 
	 * @param resourceName
	 */
	private void init(String resourceName) 
	{
		switch(resourceName){
		case RESOURCE_VISITS:
			if (map == null) {
				map = new TreeMap<String, Visit>();
			} 
			break;
		case RESOURCE_INDEX:
			if(index == null) {
				index = new TreeMap<String, String>();
				if(map.size() != 0){
					System.out.println("reindexing...");
					reindex();
					System.out.println("done");
				} 			
			}
			break;
		}
	}
	
	/**
	 * Gets storage size
	 * 
	 * @return 
	 */
	public int size()
	{
		return map.size();
	}
	
	/**
	 * Re-indexes {@link #map} to restore {@link #index}
	 */
	public void reindex()
	{
		for(Visit visit : map.values()) {
			index.put(visit.getNameKey(), visit.getDateKey());
		}
	}
	
	/**
	 * Puts visit into storage
	 * 
	 * @param visit
	 * @throws StorageException
	 */
	public void put(Visit visit) throws StorageException
	{
		if(!visit.validate())
			throw new StorageException("Invalid visit");
		map.put(visit.getDateKey(), visit);
		index.put(visit.getNameKey(), visit.getDateKey());
	}
	
	/**
	 * Removes visit
	 * 
	 * @param visit
	 * @throws StorageException
	 */
	public void delete(Visit visit) throws StorageException
	{
		if(!visit.validate())
			throw new StorageException("Invalid visit");
		map.remove(visit.getDateKey());
		index.remove(visit.getNameKey());
	}
	
	/**
	 * Retrieves all visits from storage
	 * 
	 * @return
	 */
	public Vector<Visit> get()
	{
		Vector<Visit> result = new Vector<Visit>();
		if(map.size() == 0)
			return result;
		Collection<Visit> values = map.values();
		result.addAll(values);
		return result;
	}
	
	/**
	 * Gets last {@code count} visits from storage 
	 * 
	 * @param count
	 * @return
	 */
	public Vector<Visit> last(int count)
	{
		Vector<Visit> result = new Vector<Visit>();
		if(map.size() == 0)
			return result;
	    Iterator<String> id = map.descendingKeySet().iterator(); 
	    int i = 0;
	    while (id.hasNext()) {
	        result.add(map.get(id.next()));
			if(++i == count){
				break;
			}
	    } 
		return result;
	}
	
	/**
	 * Searches visits by name
	 * 
	 * @param name
	 * @return
	 */
	public Vector<Visit> get(String name)
	{
		Vector<Visit> result = new Vector<Visit>();
		if(index.size() == 0)
			return result;
		String key = index.ceilingKey(name);
		if(key == null)
			return result;
		for(String k : index.tailMap(key, true).values()){
			Visit v = map.get(k);
			if(!v.getName().equals(name))
				break;
			result.add(v);
		}
		return result;
	}
	
	/**
	 * Searches visits by date
	 * 
	 * @param date
	 * @return
	 */
	public Vector<Visit> get(Date date)
	{
		Vector<Visit> result = new Vector<Visit>();
		if(map.size() == 0)
			return result;
		String dateStr = Visit.sortDateFormat.format(date);
		String key = map.ceilingKey(dateStr);
		if(key == null)
			return result;
		for(Visit v : map.tailMap(key, true).values()){
			if(!v.getDate().equals(date))
				break;
			result.add(v);
		}
		return result;
	}
	
	/** 
	 * Rewrites storage files
	 * 
	 * @return
	 */
	public boolean save() {
		return save(RESOURCE_VISITS) && save(RESOURCE_INDEX);
	}
	
	/**
	 * Rewrites one of storage files
	 * 
	 * @param resourceName
	 * @return
	 */
	public boolean save(String resourceName)
	{
		ObjectOutputStream stream = null;
		try {
			stream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(resourceName)));		
			if(stream != null) {
				switch(resourceName){
				case RESOURCE_VISITS:
					stream.writeObject(map);
					break;
				case RESOURCE_INDEX:
					stream.writeObject(index);
					break;
				}			
			}	
			return true;		
		} catch (IOException e) {
			System.out.println("I/O ERROR");
			System.out.println(e);			
			return false;		
		} finally {
			try {
				if (stream != null)
					stream.close();
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}
}
