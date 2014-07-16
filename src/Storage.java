import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Vector;

public class Storage {
	
	private final String file;
	
	private Vector<Visit> visits = new Vector<Visit>();
	
	private boolean changed = false; 
	
	@SuppressWarnings("unchecked")
	public Storage(String file) {
		this.file = file;
		try {
			ObjectInputStream input = new ObjectInputStream(
					new BufferedInputStream(new FileInputStream(file))
					);
			try {
				visits = (Vector<Visit>) input.readObject();
			} catch (ClassCastException e){
				visits = new Vector<Visit>();
				save();
			}
			input.close();		
		} catch (FileNotFoundException e) {
			System.out.println("ATTENSION: file " + file + " does not exsists.");
			System.out.println("It will be created.");
			System.out.println();
		} catch (ClassNotFoundException e) {
			System.out.println("read ERROR");
			System.out.println(e);
		} catch (IOException e) {
			System.out.println("I/O ERROR");
			System.out.println(e);
		}
	}
	
	public void put(Visit visit)
	{
		if (!visits.contains(visit)) {
			visits.add(visit);
			changed = true; 
		}
	}
	
	// Garbage collect visits?
	public void delete(Visit visit)
	{
		Vector<Visit> result = new Vector<Visit>();
		for( Visit v : visits ) {
			if(!v.equals(visit))
				result.add(v);
		}
		visits = result;
		changed = true;
	}
	
	public Vector<Visit> get()
	{
		return visits;
	}
	
	public Vector<Visit> get(String name)
	{
		Vector<Visit> result = new Vector<Visit>();
		for( Visit v : visits ) {
			String vName = v.getName();
			if(vName.equals(name))
				result.add(v);
		}
		
		return result;
	}
	
	public Vector<Visit> get(Date date)
	{
		Vector<Visit> result = new Vector<Visit>();
		for( Visit v : visits ) {
			Date visitDate = v.getDate();
			if(visitDate.equals(date))
				result.add(v);
		}
		
		return result;
	}
	
	public boolean isChanged()
	{
		return changed;
	}
	
	public boolean save() {
		if (!changed)
			return true;
		try {
			ObjectOutputStream output = new ObjectOutputStream(
					new BufferedOutputStream(new FileOutputStream(file))
					);
			output.writeObject(visits);
			output.close();
			changed = false;
			return true;
		} catch (IOException e) {
			System.out.println("I/O ERROR");
			System.out.println(e);
			return false;
		}
	}
}
