package console;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Date;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;
import java.text.ParseException;

import storage.Storage;
import storage.StorageException;
import storage.Visit;

public class Client {

	private static final int MAX_VISITS_TO_DISPLAY = 20;
	
	private static final String MENU_LIST   = "[L]ist visits";
	private static final String MENU_BOOK   = "[B]ook visit";
	private static final String MENU_CANCEL = "[C]ancel visit";
	private static final String MENU_FIND   = "[F]ind visit";
	private static final String MENU_SAVE   = "[S]ave";
	private static final String MENU_EXIT   = "[E]xit";
	private static final String MENU_CHOOSE = "Choose: ";
	
	private static final String MENU_SHOW_LAST        = "Show      [L]ast visits";
	private static final String MENU_SEARCH_BY_NAME   = "Search by [N]ame";
	private static final String MENU_SEARCH_BY_DATE   = "Search by [D]ate";
	
	private static final String LABEL_NAME            = "Type name: ";
	private static final String LABEL_DATE            = "Date dd/mm/yyyy: ";
	private static final String LABEL_VISITORS_NUMBER = "Type visitors number: ";
	private static final String LABEL_RETRY           = "Retry";
	private static final String LABEL_GUIDE           = "Do you need a guide? Y/N: ";
	private static final String LABEL_REDUCTION_INFO  = "With visitors number more than %s you have right for reduction";
	
	private static final String LABEL_REDUCTION_QUESTION = "Would you ask for reduction? Y/N:";
	
	private static final String LABEL_VISITORS        = "Type all visitor names: ";
	private static final String LABEL_VISITORS_EXIT   = "Type Exit to interrupt input before typing all names";
	private static final String LABEL_NO_VISITS       = "No visits booked";
	private static final String LABEL_NO_VISITS_FOUND = "No visits found";
	private static final String LABEL_YOUR_VISIT      = "Your visit: ";
	private static final String LABEL_SAVE            = "Do you want to save changes? Y/N: ";
	private static final String LABEL_CHANGES_SAVED   = "Changes saved";
	private static final String LABEL_CANCEL_SUCCESS  = "You've just canceled the visit (Not saved yet): ";
	private static final String LABEL_LAST_VISITS     = "Last visits: ";
	private static final String LABEL_VISITS_NUMBER   = "Number of visits found: ";
	private static final String LABEL_CANCEL          = "Choose visit to cancel: ";
	private static final String LABEL_CANCEL_THIS     = "Cancel this visit?";
	
	private static final String VISIT_DATE            = "Date: ";
	private static final String VISIT_NAME            = "Name: ";
	private static final String VISIT_VISITORS_NUMBER = "Visitors number: ";
	private static final String VISIT_GUIDE           = "Guide: ";
	private static final String VISIT_PRICE           = "Price: ";
	private static final String VISIT_VISITORS        = "Visitors names: ";
	
	private static final String ERROR_EMPTY_NAME      = "Name can not be empty";
	private static final String ERROR_INVALID_NAME    = "Invalid name";
	private static final String ERROR_EMPTY_DATE      = "Date can not be empty";
	private static final String ERROR_DATE_IN_PAST    = "Choose a date in the future";
	private static final String ERROR_INVALID_DATE    = "Invalid date";
	private static final String ERROR_EMPTY_NUMBER    = "Visitors number can not be empty";
	private static final String ERROR_INVALID_NUMBER  = "Invalid number";
	private static final String ERROR_GUIDE           = "You did not type visitors names, guide can not be assigned to your visit";
	private static final String ERROR_REDUCTION       = "With visitors number less than %s you have no right for reduction";
	private static final String ERROR_SYSTEM          = "System error";
	private static final String ERROR_CANCEL          = "Wrong visit number. Choose visit to cancel";
	
	private Visit visit;
	private Vector<Visit> visits;
	private Scanner input;
	private Storage storage;
	private boolean changed = false; 
	
	public static void main(String[] args) {
		Client client  = new Client();
		client.makeChoice();	
	}

	public Client()
	{	
		input   = new Scanner(System.in);
		storage = new Storage();
		visit   = new Visit();
		visits  = new Vector<Visit>();
	}
	
	public void makeChoice()
	{
		char choice;
		do {
			System.out.println();
			System.out.println(MENU_LIST);
			System.out.println(MENU_BOOK);
			System.out.println(MENU_CANCEL);
			System.out.println(MENU_FIND);
			System.out.println(MENU_SAVE);
			System.out.println(MENU_EXIT);
			System.out.println();
			System.out.print(MENU_CHOOSE);
			choice = input.next().charAt(0);
			input.nextLine();
			switch (choice) {
			case 'L':
				find();
				list();
				break;
			case 'B':
				book();
				break;	
			case 'C':
				cancel();
				break;
			case 'F':
				search();
				listMore();
				break;
			case 'S':
				save();
				break;
			case 'E':
				exit();
				break;
			}
		} while (choice != 'E');
	}
	
	private void exit() {
		if(changed) {
			char answer;
			do {
				System.out.println(LABEL_SAVE);
				answer = input.next().charAt(0);
				input.nextLine();
				if (answer == 'Y') {
					save();
				}
			} while (answer != 'Y' && answer != 'N');
		}
		input.close();
	}
	
	private void search() {
		visit = new Visit();
		char choice;
		do {
			System.out.println();
			System.out.println(MENU_SHOW_LAST);
			System.out.println(MENU_SEARCH_BY_NAME);
			System.out.println(MENU_SEARCH_BY_DATE);
			System.out.println();
			System.out.print(MENU_CHOOSE);
			choice = input.next().charAt(0);
			input.nextLine();
			if (choice == 'L') {
				find();
			} 
			else if (choice == 'N') {
				askForName();
				find(visit.getName());
			} 
			else if (choice == 'D') {
				askForDate();
				find(visit.getDate());
			} 

		} while (choice != 'L' && choice != 'N' && choice != 'D');
	}

	private void cancel() {
		visit = new Visit();
		search();
		if(visits.size() == 0) {
			System.out.println(LABEL_NO_VISITS_FOUND);
			return;
		}
		if (visits.size() == 1) {
			visit = visits.get(0);
			confirmToCancel();
		} else {
			pickToCancel(); 
		}
	}

	private void confirmToCancel() {
		char answer;
		do {
			System.out.println(LABEL_CANCEL_THIS);
			System.out.println(LABEL_CANCEL_THIS);
			answer = input.next().charAt(0);
			input.nextLine();
			if (answer == 'Y') {
				delete();
			}
		} while (answer != 'Y' && answer != 'N');
	}
	
	private void pickToCancel() 
	{
		boolean ok;
		do {
			ok = true;
			askWhatVisitToCancel(); 
			visit = visits.get(0);
			delete();
		} while (!ok);
	}
	
	private boolean askWhatVisitToCancel() 
	{
		enumerate();
		System.out.println(LABEL_CANCEL);
		try {
			String text = input.nextLine();
			if (text.length() == 0) {
				retry(ERROR_CANCEL);
				return false;
			}
			int index = Integer.parseInt(text) - 1;
			if (index < 0) {
				retry(ERROR_CANCEL);
				return false;
			}
			visit = visits.get(index);
		} catch (NumberFormatException e) {
			retry(ERROR_CANCEL);
			return false;
		} catch(ArrayIndexOutOfBoundsException e) {
			retry(ERROR_CANCEL);
			return false;
		}	
		return true;
	}
	
	// ============= storage operations =====================//
	
	private void find()
	{
		visits = storage.last(MAX_VISITS_TO_DISPLAY);
	}
	
	private void find(String name)
	{
		visits = storage.get(visit.getName());
	}
	
	private void find(Date date)
	{
		visits = storage.get(visit.getDate());
	}
	
	private void save()
	{
		if (storage.save()) {
			System.out.println(LABEL_CHANGES_SAVED);
		} else {
			System.out.println(ERROR_SYSTEM);
		}
	}
	
	private void delete(){
		try {
			storage.delete(visit);
			changed = true;
			System.out.println(LABEL_CANCEL_SUCCESS);
			System.out.println(visit);
		} catch (StorageException e) {
			System.out.println(ERROR_SYSTEM);
		}
	}

	// =========== display ==================//

	private void enumerate() {
		System.out.println(LABEL_VISITS_NUMBER + Integer.toString(visits.size()));
		System.out.println(String.format(LABEL_LAST_VISITS));
		int i = 0;
		for (Visit v : visits) {
			System.out.println(
					"[" + String.format("%2s", Integer.toString(visits.indexOf(v) + 1)) + "]"
					+ " " + v.toString()
					);
			if(++i == MAX_VISITS_TO_DISPLAY){
				break;
			}
		}
	}

	private void list() {
		System.out.println(LABEL_VISITS_NUMBER + Integer.toString(visits.size()));
		System.out.println(String.format(LABEL_LAST_VISITS));
		int i = 0;
		if (visits.size() == 0)
			System.out.println(LABEL_NO_VISITS);
		for (Visit v : visits) {
			System.out.println(v);
			if(++i == MAX_VISITS_TO_DISPLAY){
				break;
			}
		}
	}
	
	private void listMore() {
		System.out.println(LABEL_VISITS_NUMBER + Integer.toString(visits.size()));
		System.out.println(String.format(LABEL_LAST_VISITS));
		int i = 0;
		if (visits.size() == 0)
			System.out.println(LABEL_NO_VISITS);
		for (Visit v : visits) {
			System.out.println(v);
			if(v.hasGuide()) {
				System.out.println(String.format("%40s", VISIT_VISITORS));
				for(String name : v.getVisitorNames())
					System.out.println(String.format("%38s", name));
				System.out.println();
			}
			if(++i == MAX_VISITS_TO_DISPLAY){
				break;
			}
		}
	}
	
	private void show() {
		System.out.println(LABEL_YOUR_VISIT);
		System.out.println(String.format("%-20s", VISIT_DATE) + Visit.dateFormat.format(visit.getDate()));
		System.out.println(String.format("%-20s", VISIT_NAME) + visit.getName());
		System.out.println(String.format("%-20s", VISIT_VISITORS_NUMBER) + Integer.toString(visit.getVisitorNumber()));
		System.out.println(String.format("%-20s", VISIT_GUIDE) + (visit.hasGuide() ? "guide" : ""));
		System.out.println(String.format("%-20s", VISIT_PRICE) + visit.getPrice() + " euro" + (visit.hasReduction() ? " reduction " : ""));
		System.out.println(VISIT_VISITORS);
		for(String name : visit.getVisitorNames()) {
			System.out.println("  " + name);
		}
	}
	
	//=======================================//
	
	private void book() {
		visit = new Visit();
		askForData();
		try {
			storage.put(visit);	
			changed = true;
			show();
		} catch (StorageException e) {
			System.out.println(ERROR_SYSTEM);
		}
	}
	
	private void askForData() {
		boolean ok = false;
		do {
			ok = true;
			askForNameDateNumber();
			askForGuide();
			askForReduction();
		} while (!ok);
	}
	
	private void askForDate() {
		boolean ok = false;
		do {
			ok = setDate();
		} while (!ok);
	}
	
	private void askForName() {
		boolean ok = false;
		do {
			ok = setName();
		} while (!ok);
	}

	private void askForGuide() {
		char answer;
		do {
			System.out.println(LABEL_GUIDE);
			answer = input.next().charAt(0);
			input.nextLine();
			if (answer == 'Y') {
				askForVisitors();			
				if (visit.getVisitorNames().size() == 0) {
					System.out.println(ERROR_GUIDE);
				} else {
					visit.setGuide();
				}
			}
		} while (answer != 'Y' && answer != 'N');
	}
	
	private void askForReduction() {
		int visitorNumber = visit.getVisitorNumber();
		if (visitorNumber > Visit.applyReductionTreshold) {
			char answer;
			do {
				System.out.println(String.format(LABEL_REDUCTION_INFO, Visit.applyReductionTreshold));
				System.out.println(LABEL_REDUCTION_QUESTION);
				answer = input.next().charAt(0);
				input.nextLine();
				if (answer == 'Y') {
					askForVisitors();				
					if (visit.getVisitorNumber() < Visit.applyReductionTreshold) {
						System.out.println(String.format(ERROR_REDUCTION, Visit.applyReductionTreshold));
					} else {
						visit.setReduction();
					}
				}
			} while (answer != 'Y' && answer != 'N');
		}
	}
		
	private void askForVisitors() {
		Vector<String> visitorNames = new Vector<String>();
		System.out.println(LABEL_VISITORS);
		System.out.println(LABEL_VISITORS_EXIT);
		for (int i = 0; i < visit.getVisitorNumber(); i++) {
			System.out.println(Integer.toString(i+1) + ": ");
			String text = input.nextLine();
			if ("Exit" == text) {
				visit.setVisitorNumber(visitorNames.size());
				break;
			} else {
				visitorNames.add(text);
			}
		}
		visit.setVisitorNames(visitorNames);
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
				return false;
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
