import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Vector;
import java.util.Scanner;

public class Visit implements Serializable {
	
	private String name;
	private Date date;
	private Integer visitorNumber;
	
	private Boolean guide = false;
	private Boolean reduction = false;
	private Vector<String> visitorNames = new Vector<String>();
	
	static final long serialVersionUID = 1;

	public static final int visitPrice = 5;
	public static final int guidePrice = 50;
	public static final int visitPriceReduced = 3;
	public static final int guidePriceReduced = 40;
	public static final int applyReductionTreshold = 10;
	
	Visit(){}
	
	Visit(String name, Date date, int visitorNumber)
	{
		this.name = name;
		this.date = date;
		this.visitorNumber = visitorNumber;
	}
	
	Visit(String[] data) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");				  
		this.name = data[0];
		this.date = df.parse(data[1]);
		//visit.setVisitorNumber(visitData[2]);	// ToDo
		this.visitorNumber = 3;
		String visitorsString = data[5];
		String[] visitors = visitorsString.split(",");
		Vector<String> visitorNames = new Vector<String>();
		for(int i = 0; i < visitors.length; i++) {
			System.out.println(visitors[i]);
			visitorNames.add(visitors[i]);
		}
		System.out.println(data.toString());
		//System.out.println(visitorNames.toString());
		if (visitorNames.size() > 0) {
			setVisitorNumber(visitorNames.size());
			setVisitorNames(visitorNames);
			if (data[3] == "YES") {
				setGuide();
			}
			if (data[4] == "YES") {
				setReduction();
			}
		}		
	}
	
	public String getName() 
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setName(Scanner input) throws ClientException {
		System.out.println("Type name:");
		try {
			this.name = input.nextLine();
		} catch (InputMismatchException e) {
			throw new ClientException("Invalid name.", e);
		}
	}
	
	public Date getDate()
	{
		return date;
	}
	
	public void setDate(Date date)
	{
		this.date = date;
	}
	
	public void setDate(Scanner input) 
	throws ClientException
	{
		System.out.println("Date dd/mm/yy:");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
		try {
			Date date = dateFormat.parse(input.nextLine());
			if (date.compareTo(new Date()) < 0)
				this.date = date;
			else
				throw new ClientException(
						"You should choose the date in the future.");
		} catch (ParseException e) {
			throw new ClientException("Wrong data format! Should be dd/mm/yy.",
					e);
		}
	}
	
	public int getVisitorNumber()
	{
		return visitorNumber;
	}
	
	public void setVisitorNumber(int visitorNumber)
	{
		this.visitorNumber = visitorNumber;
	}
	
	public void setVisitorNumber(Scanner input)
		throws ClientException {
			System.out.println("Type number of visitors:");
		try {
			this.visitorNumber = input.nextInt();
		} catch (InputMismatchException e) {
			throw new ClientException("Invalid number of visitors.", e);
		}
	}

	public Boolean hasGuide()
	{
		return guide;
	}
	
	public Boolean hasReduction()
	{
		return reduction;
	}
	
	public void setGuide()
	{
		if(visitorNames.size() == 0)
			return;
		this.guide = true;
	}
	
	public void setReduction()
	{
		if (visitorNames.size() > applyReductionTreshold) {
			this.reduction = true;
		}
	}
	
	public void setVisitorNames(Vector<String> visitorNames)
	{
		if(visitorNames.size() == 0)
			return;
		this.visitorNames = visitorNames;
		this.visitorNumber = visitorNames.size();
	}
	
	public Vector<String> getVisitorNames()
	{
		return visitorNames;
	}
	
	public int getPrice() {
		int sum = 0;
		int[] prices = getPrices();
		sum += visitorNumber * prices[0];
		if (guide) {
			sum += prices[1];
		}

		return sum;
	}
	
	private int[] getPrices()
	{
		if(hasReduction()){
			int[] arr = {visitPriceReduced, guidePriceReduced};
			
			return arr;
		} else {
			int[] arr = {visitPrice, guidePrice};
			
			return arr;
		}
	}
	
	public String toString()
	{
		DateFormat df = new SimpleDateFormat("dd/mm/yy");
		String description = "Name: " + name 
				+ " Date: " + df.format(date) 
				+ " Number of visitors: " + visitorNumber
				+ " Guide: " + (hasGuide() ? " yes" : " no");
		description += " Price ";
		if(hasReduction())
			description += "(reduced)";
		description += ": " + getPrice() + " euro";	
		if(hasGuide() || hasReduction())
			description += visitorNames.toString();
		
		return description;
	}
	
	public void setUp(String methodName, Scanner input)
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
				System.out.println(e.getMessage());
			} catch (IllegalAccessException e) {
			} catch(InvocationTargetException e) {
				System.out.println(e.getMessage());
				System.out.println("Retry.");
				ok = false;
			}
		} while (!ok);
	}

}
