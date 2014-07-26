
import java.util.Scanner;
import java.util.Date;
import java.text.*;
import java.util.Vector;

public class Client {

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		Storage storage = new Storage("visitors");
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
				list(storage);
				break;
			case 'B':
				book(input, storage);
				break;
			case 'C':
				cancel(input, storage);
				break;
			case 'F':
				Vector<Visit> visits = search(input, storage);
				System.out.println("List of visits found: ");
				show(visits);
				break;
			case 'S':
				if (storage.save())
					System.out.println("Changes saved.");
				break;
			case 'E':
				exit(input, storage);
				break;
			}
		} while (choice != 'E');
	}

	private static void list(Storage storage) {
		Vector<Visit> visits = storage.get();
		if (visits.size() == 0)
			System.out.println("No visits booked.");
		else
			show(visits);
	}

	private static void book(Scanner input, Storage storage) {
		boolean ok;
		do {
			ok = true;
			try {
				Visit visit = bookVisit(input);
				askForGuide(input, visit);
				askForReduction(input, visit);
				storage.put(visit);
				System.out.println("Your booking data: ");
				show(visit);
			} catch (ClientException e) {
				input.nextLine();
				System.out.println(e.getMessage());
				System.out.println("Retry.");
				ok = false;
			}
		} while (!ok);
	}
	
	private static Visit bookVisit(Scanner input) throws ClientException
	{
		Visit visit = new Visit();
		visit.setUp("setName", input);
		visit.setUp("setDate", input);
		visit.setUp("setVisitorNumber", input);
		
		return visit;	
	}

	private static void askForGuide(Scanner input, Visit visit) {
		char answer;
		do {
			System.out.println("Do you need a guide? Y/N:");
			answer = input.next().charAt(0);
			input.nextLine();
			if (answer == 'Y') {
				listVisitorNames(input, visit);
				visit.setGuide();
				if (!visit.hasGuide() && (visit.getVisitorNames().size() == 0)) {
					System.out.println("You did not typed visitors names, "
							+ "guide can not be assigned to your visit");
				}
			}
		} while (answer != 'Y' && answer != 'N');
	}

	private static void askForReduction(Scanner input, Visit visit) {
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
					listVisitorNames(input, visit);
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

	private static void listVisitorNames(Scanner input, Visit visit) {
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

	private static void cancel(Scanner input, Storage storage) {
		Vector<Visit> visits = (storage.get().size() <= 10) ? storage.get()
				: search(input, storage);
		if (visits.size() == 1) {
			storage.delete(visits.get(0));
			System.out.println("You've just canceled the visit "
					+ visits.get(0) + " (Not saved yet.)");
			return;
		}
		enumerate(visits);
		boolean ok = false;
		do {
			System.out.println("Choose visit to cancel: 1..."
					+ (visits.size() - 1));
			int index = input.nextInt() - 1;
			if (index < visits.size()) {
				ok = true;
				storage.delete(visits.get(index));
				System.out.println("You've just canceled the visit "
						+ visits.get(index) + " (Not saved yet.)");
			} else {
				System.out.println("Wrong number! Choose visit to cancel: 0..."
						+ visits.size());
			}
		} while (!ok);
	}

	private static Vector<Visit> search(Scanner input, Storage storage) {
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
				System.out.println("Date dd/mm/yy:");
				Date date = new Date();
				String ind = input.nextLine();
				DateFormat df = new SimpleDateFormat("dd/mm/yy");
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

	private static void enumerate(Vector<Visit> visits) {
		int i = 0;
		for (Visit v : visits)
			System.out.println(i + " " + v.toString());
	}

	private static void show(Vector<Visit> visits) {
		if(visits.size() == 0)
			return;
		for (Visit v : visits)
			System.out.println(v);
	}

	private static void show(Visit visit) {
		System.out.println(visit);
	}

	private static void exit(Scanner input, Storage storage) {
		if (storage.isChanged()) {
			char answer;
			do {
				System.out.println("Do you want to save changes? Y/N:");
				answer = input.next().charAt(0);
				input.nextLine();
				if (answer == 'Y') {
					storage.save();
				}
			} while (answer != 'Y' && answer != 'N');
		}
		input.close();
	}
}
