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
	
	public final static String RESOURCE_VISITS = "visits";
	public final static String RESOURCE_INDEX  = "index";
	
	private TreeMap<String, Visit> map;
	private TreeMap<String, String> index;
	
	public Storage() {	
		read(RESOURCE_VISITS);
		read(RESOURCE_INDEX);
	}
	
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
	
	private void init(String resourceName) {
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
	
	public void reindex(){
		for(Visit visit : map.values()) {
			index.put(visit.getNameKey(), visit.getDateKey());
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
		System.out.println(name);
		Vector<Visit> result = new Vector<Visit>();
		if(index.size() == 0)
			return result;
		String key = index.ceilingKey(name);
		System.out.println(key);
		String kk = index.floorKey(name);
		System.out.println(kk);
		if(key == null)
			return result;
		for(String k : index.tailMap(key, true).values()){
			System.out.println(k);
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
	
	public boolean save() {
		return save(RESOURCE_VISITS) && save(RESOURCE_INDEX);
	}
	
	public boolean save(String resourceName) {
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
