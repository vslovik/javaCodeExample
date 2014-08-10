package storage;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Vector;
import java.util.Scanner;

import console.ClientException;

public class Visit implements Serializable {

	private static final long serialVersionUID = 8356740381864305284L;
	public static final int visitPrice = 5;
	public static final int guidePrice = 50;
	public static final int visitPriceReduced = 3;
	public static final int guidePriceReduced = 40;
	public static final int applyReductionTreshold = 10;
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	public static final SimpleDateFormat sortDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public long createdAt = 5;
	private String name;
	private Date date;
	private Integer visitorNumber;
	private Boolean guide = false;
	private Boolean reduction = false;
	private Vector<String> visitorNames = new Vector<String>();
	
	public Visit(){
		this.createdAt =  new Date().getTime();
	}
	
	public Visit(String name, Date date, int visitorNumber)
	{
		this.createdAt =  new Date().getTime();
		this.name = name;
		this.date = date;
		this.visitorNumber = visitorNumber;
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
	
	public void addVisitorName(String name)
	{
		if(name.length() == 0)
			return;
		this.visitorNames.add(name);
		this.visitorNumber = visitorNames.size();
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
	
	public String getDateKey(){
		return sortDateFormat.format(date) + "-" + name + "-" + Long.toString(createdAt);
	}
	
	public String getNameKey(){
		return name + "-" + sortDateFormat.format(date) + "-" + Long.toString(createdAt);
	}
	
	public boolean validate(){
		if(name.length() == 0)
			return false;
		if(visitorNumber == 0)
			return false;
		if (date == null)
			return false;
		return true;
	}
	
	public String toString()
	{
		String description = "Name: " + name 
				+ " Date: " + dateFormat.format(date) 
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
