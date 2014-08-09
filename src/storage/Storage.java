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
import java.util.TreeMap;
import java.util.Vector;

public class Storage {
	
	public final static String FILE_VISITS = "visits";
	public final static String FILE_INDEX  = "index";
	
	private Vector<Visit> visits = new Vector<Visit>();
	private TreeMap<String, Visit> map;
	private TreeMap<String, String> index;
	
	@SuppressWarnings("unchecked")
	public Storage() {
		try {
			ObjectInputStream input = new ObjectInputStream(
					new BufferedInputStream(new FileInputStream(FILE_VISITS))
					);
			map = (TreeMap<String, Visit>) input.readObject();
			
			input.close();		
			
			input = new ObjectInputStream(
					new BufferedInputStream(new FileInputStream(FILE_INDEX))
					);
			index = (TreeMap<String, String>) input.readObject();
			
			input.close();	
		
		} catch (FileNotFoundException e) {
			System.out.println("ATTENSION: file " + FILE_VISITS + " does not exsists.");
			System.out.println("It will be created.");
			map = new TreeMap<String, Visit>();
			index = new TreeMap<String, String>();
		} catch (ClassNotFoundException e) {
			map = new TreeMap<String, Visit>();
			index = new TreeMap<String, String>();
		} catch (IOException e) {
			System.out.println("I/O ERROR");
			System.out.println(e);
			map = new TreeMap<String, Visit>();
			index = new TreeMap<String, String>();
		}
	}
	
	public void put(Visit visit) throws StorageException
	{
		if(!visit.validate())
			throw new StorageException("Invalid visit");
		map.put(visit.getDateKey(), visit);
		index.put(visit.getNameKey(), visit.getDateKey());
	}
	
	public void delete(Visit visit) throws StorageException
	{
		if(!visit.validate())
			throw new StorageException("Invalid visit");
		map.remove(visit.getDateKey());
		index.remove(visit.getNameKey());
	}
	
	public Vector<Visit> get()
	{
		Vector<Visit> result = new Vector<Visit>();
		if(map.size() == 0)
			return result;
		Collection<Visit> values = map.values();
		result.addAll(values);
		return result;
	}
	
	public Vector<Visit> get(String name)
	{
		Vector<Visit> result = new Vector<Visit>();
		if(index.size() == 0)
			return result;
		String key = index.ceilingKey(name);
		System.out.println(key);
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
	
	public Vector<Visit> get(Date date)
	{
		Vector<Visit> result = new Vector<Visit>();
		if(map.size() == 0)
			return result;
		String dateStr = Visit.dateFormat.format(date);
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
	
	public boolean save() {
		try {
			ObjectOutputStream output = new ObjectOutputStream(
					new BufferedOutputStream(new FileOutputStream(FILE_VISITS))
					);
			output.writeObject(map);
			output.close();
			output = new ObjectOutputStream(
					new BufferedOutputStream(new FileOutputStream(FILE_INDEX))
					);
			output.writeObject(index);
			output.close();
			return true;
		} catch (IOException e) {
			System.out.println("I/O ERROR");
			System.out.println(e);
			return false;
		}
	}
}
