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
			System.out.println("[L]ist booking");
			System.out.println("[B]ook visit");
			System.out.println("[C]ancel visit");
			System.out.println("[F]ind booking");
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

	private void book() {
		boolean ok;
		do {
			ok = true;
			try {
				visit = bookVisit();
				askForGuide();
				askForReduction();
				storage.put(visit);
				System.out.println("Your booking data: ");
				show();
			} catch (ClientException e) {
				input.nextLine();
				System.out.println(e);
				System.out.println("Retry.");
				ok = false;
			} catch (StorageException e) {
				System.out.println(e);
			}
		} while (!ok);
	}
	
	private Visit bookVisit() throws ClientException
	{
		setUp("setName");
		setUp("setDate");
		setUp("setVisitorNumber");		
		return visit;	
	}

	private void askForGuide() {
		char answer;
		do {
			System.out.println("Do you need a guide? Y/N:");
			answer = input.next().charAt(0);
			input.nextLine();
			if (answer == 'Y') {
				listVisitorNames();
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
					listVisitorNames();
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

	private void listVisitorNames() {
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

	private void show() {
		System.out.println(visit);
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
	
	private void setUp(String methodName)
	{
		boolean ok = false;
		do {
			try {
				Class<?> paramTypes[] = new Class[1];
				paramTypes[0] = Scanner.class;
				Method method = this.getClass().getMethod(methodName, paramTypes);
				method.invoke(this, input);
				ok = true;
			} catch (NoSuchMethodException e) {
				System.out.println(e);
			} catch (IllegalAccessException e) {
			} catch(InvocationTargetException e) {
				System.out.println(e);
				System.out.println("Retry.");
				ok = false;
			}
		} while (!ok);
	}
	
	@SuppressWarnings("unused")
	private void setDate() throws ClientException
	{
		System.out.println("Date dd/mm/yyyy:");
		try {
			Date date = Visit.dateFormat.parse(input.nextLine());
			if (date.compareTo(new Date()) < 0)
				visit.setDate(date);
			else
				throw new ClientException(
						"You should choose the date in the future.");
		} catch (ParseException e) {
			throw new ClientException("Wrong data format! Should be dd/mm/yy.",
					e);
		}
	}
	
	@SuppressWarnings("unused")
	private void setName(Scanner input) throws ClientException {
		System.out.println("Type name:");
		try {
			visit.setName(input.nextLine());
		} catch (InputMismatchException e) {
			throw new ClientException("Invalid name.", e);
		}
	}
	
	@SuppressWarnings("unused")
	private void setVisitorNumber(Scanner input)
			throws ClientException {
				System.out.println("Type number of visitors:");
			try {
				visit.setVisitorNumber(input.nextInt());
			} catch (InputMismatchException e) {
				throw new ClientException("Invalid number of visitors.", e);
			}
		}
}
