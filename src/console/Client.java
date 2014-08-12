package console;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Date;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.*;
import java.util.Vector;
import java.text.ParseException;

import storage.Storage;
import storage.StorageException;
import storage.Visit;

public class Client {

	private static final String LABEL_NAME            = "Type name:";
	private static final String LABEL_DATE            = "Date dd/mm/yyyy:";
	private static final String LABEL_VISITORS_NUMBER = "Type visitors number:";
	private static final String LABEL_RETRY           = "Retry";
	
	private static final String ERROR_EMPTY_NAME      = "Name can not be empty";
	private static final String ERROR_INVALID_NAME    = "Invalid name";
	private static final String ERROR_EMPTY_DATE      = "Date can not be empty";
	private static final String ERROR_DATE_IN_PAST    = "Choose a date in the future";
	private static final String ERROR_INVALID_DATE    = "Invalid date";
	private static final String ERROR_EMPTY_NUMBER    = "Visitors number can not be empty";
	private static final String ERROR_INVALID_NUMBER  = "Invalid number";
	private static final String ERROR_GUIDE           = "Tell if guide is needed";
	private static final String ERROR_REDUCTION       = "Tell if you ask for reduction";
	private static final String ERROR_SYSTEM          = "System error";
	
	Visit visit;
	Scanner input;
	Storage storage;
	
	public static void main(String[] args) {
		Client client  = new Client();
		client.makeChoice();	
	}

	public Client()
	{	
		input   = new Scanner(System.in);
		storage = new Storage();
		visit   = new Visit();
	}
	
	public void makeChoice()
	{
		char choice;
		do {
			System.out.println();
			System.out.println("[L]ist visits");
			System.out.println("[B]ook visit");
			System.out.println("[C]ancel visit");
			System.out.println("[F]ind visit");
			System.out.println("[S]ave");
			System.out.println("[E]xit");
			System.out.println();
			System.out.print("Choose: ");
			choice = input.next().charAt(0);
			input.nextLine();
			switch (choice) {
			case 'L':
				list();
				break;
			case 'B':
				book();
				break;
			case 'C':
				cancel();
				break;
			case 'F':
				Vector<Visit> visits = search();
				System.out.println("List of visits found: ");
				show(visits);
				break;
			case 'S':
				if (storage.save())
					System.out.println("Changes saved.");
				break;
			case 'E':
				exit();
				break;
			}
		} while (choice != 'E');
	}
	
	private void list() {
		Vector<Visit> visits = storage.get();
		if (visits.size() == 0)
			System.out.println("No visits booked.");
		else
			show(visits);
	}
	
	private void cancel() {
		Vector<Visit> visits = (storage.get().size() <= 10) ? storage.get()
				: search();
		if (visits.size() == 1) {
			try {
				storage.delete(visits.get(0));
			} catch (StorageException e) {
				System.out.println(e);
			}
			System.out.println("You've just canceled the visit "
					+ visits.get(0) + " (Not saved yet.)");
			return;
		}
		enumerate(visits);
		boolean ok = false;
		do {
			System.out.println("Choose visit to cancel: 1..."
					+ (visits.size()));
			int index = input.nextInt();
			if (index < visits.size()) {
				ok = true;
				try {
					storage.delete(visits.get(index));
				} catch (StorageException e) {
					System.out.println(e);
				}
				System.out.println("You've just canceled the visit "
						+ visits.get(index) + " (Not saved yet.)");
			} else {
				System.out.println("Wrong number! Choose visit to cancel: 0..."
						+ visits.size());
			}
		} while (!ok);
	}

	private Vector<Visit> search() {
		char choice;
		boolean ok;
		Vector<Visit> visits;
		do {
			System.out.println();
			System.out.println("Search by [N]ame");
			System.out.println("Search by [D]ate");
			System.out.println();
			System.out.print("Choose: ");
			choice = input.next().charAt(0);
			input.nextLine();
			if (choice == 'N') {
				System.out.println("Type name:");
				String name = input.nextLine();
				visits = storage.get(name);
			} else {
				System.out.println("Date dd/mm/yyyy:");
				Date date = new Date();
				String ind = input.nextLine();
				DateFormat df = new SimpleDateFormat("dd/mm/yyyy");
				do {
					try {
						date = df.parse(ind);
						ok = true;
					} catch (ParseException e) {
						System.out.println("Unable to parse " + ind + "Retry!");
						ok = false;
					}
				} while (!ok);
				visits = storage.get(date);
			}
		} while (choice != 'N' && choice != 'D');
		return visits;
	}

	private void enumerate(Vector<Visit> visits) {
		int i = 0;
		for (Visit v : visits)
			System.out.println(++i + " " + v.toString());
	}

	private void show(Vector<Visit> visits) {
		if(visits.size() == 0)
			return;
		for (Visit v : visits)
			System.out.println(v);
	}



	private void exit() {
		char answer;
		do {
			System.out.println("Do you want to save changes? Y/N:");
			answer = input.next().charAt(0);
			input.nextLine();
			if (answer == 'Y') {
				storage.save();
			}
		} while (answer != 'Y' && answer != 'N');
		input.close();
	}
	
	// ==============================================
	
	private void book() {
		boolean ok;
		do {
			ok = true;
			try {
				askForNameDateNumber();
				askForGuide();
				askForReduction();
				
				storage.put(visit);
				System.out.println("Your visit: ");
				
				show();
				
			} catch (StorageException e) {
				System.out.println(e);
			}
		} while (!ok);
	}
	
	private void askForGuide() {
		char answer;
		do {
			System.out.println("Do you need a guide? Y/N:");
			answer = input.next().charAt(0);
			input.nextLine();
			if (answer == 'Y') {
				askForVisitors();
				visit.setGuide();
				if (!visit.hasGuide() && (visit.getVisitorNames().size() == 0)) {
					System.out.println("You did not type visitors names, "
							+ "guide can not be assigned to your visit");
				}
			}
		} while (answer != 'Y' && answer != 'N');
	}
	
	private void askForReduction() {
		int visitorNumber = visit.getVisitorNumber();
		if (visitorNumber > Visit.applyReductionTreshold) {
			char answer;
			do {
				System.out.println("Number of visitors, "
						+ visit.getVisitorNumber() + " more than "
						+ Visit.applyReductionTreshold);
				System.out.println("You have right for reduction. "
						+ "Would you ask for it? Y/N:");
				answer = input.next().charAt(0);
				input.nextLine();
				if (answer == 'Y') {
					askForVisitors();
					visit.setReduction();
					if ((visit.getVisitorNumber() < Visit.applyReductionTreshold)
							&& !visit.hasReduction()) {
						System.out.println("Number of visitors, "
								+ visit.getVisitorNumber() + " not more than "
								+ Visit.applyReductionTreshold);
						System.out.println("You have no right for reduction.");
					}
				}
			} while (answer != 'Y' && answer != 'N');
		}
	}
	
	private void askForVisitors() {
		int visitorNumber = visit.getVisitorNumber();
		System.out.println("Type all " + visitorNumber + " visitor names:");
		System.out.println("Type Exit to interrupt input berfore reaching "
				+ visitorNumber + " names");
		Vector<String> visitorNames = new Vector<String>();
		;
		for (int i = 0; i < visitorNumber; i++) {
			if ("Exit" == input.nextLine()) {
				visit.setVisitorNumber(visitorNames.size());
				break;
			} else {
				visitorNames.add(input.nextLine());
			}
		}
		visit.setVisitorNames(visitorNames);
	}
	
	private void show() {
		System.out.println(visit);
	}
	
	private void askForNameDateNumber() 
	{
		setUp("setName");
		setUp("setDate");
		setUp("setVisitorNumber");		
	}
	
	private void setUp(String methodName) {
		boolean ok = false;
		do {
			try {
				Method method = this.getClass().getMethod(methodName);
				ok = (boolean) method.invoke(this);
			} catch (NoSuchMethodException e) {
				System.out.println(e);
				ok = true;
			} catch (IllegalAccessException e) {
				System.out.println(e);
				ok = true;
			} catch (InvocationTargetException e) {
				System.out.println(LABEL_RETRY);
				ok = true;
			}
		} while (!ok);
	}
	
	
	private void retry(String message)
	{
		System.out.println(message);
		System.out.println(LABEL_RETRY);
	}
	
	public boolean setName() 
	{
		System.out.println(LABEL_NAME);
		try {
			String text = input.nextLine();
			if (text.length() == 0) {
				retry(ERROR_EMPTY_NAME);
				return false;
			}
			visit.setName(text);
		} catch (InputMismatchException e) {
			retry(ERROR_INVALID_NAME);
			return false;
		}
		
		return true;
	}
	
	public boolean setDate()
	{
		System.out.println(LABEL_DATE);
		try {
			String text = input.nextLine();
			if (text.length() == 0) {
				retry(ERROR_EMPTY_DATE);
			}
			Date date = Visit.dateFormat.parse(text);
			if (date.compareTo(new Date()) <= 0) {
				retry(ERROR_DATE_IN_PAST);
				return false;
			}
			visit.setDate(date);
		} catch (ParseException e) {
			retry(ERROR_INVALID_DATE);
			return false;
		}
		return true;
	}
	
	public boolean setVisitorNumber() 
	{
		System.out.println(LABEL_VISITORS_NUMBER);
		try {
			String text = input.nextLine();
			if (text.length() == 0) {
				retry(ERROR_EMPTY_NUMBER);
				return false;
			}
			int number = Integer.parseInt(text);
			if (number <= 0) {
				retry(ERROR_INVALID_NUMBER);
				return false;
			}
			visit.setVisitorNumber(number);
		} catch (NumberFormatException e) {
			retry(ERROR_INVALID_NUMBER);
			return false;
		}	
		return true;
	}
}
