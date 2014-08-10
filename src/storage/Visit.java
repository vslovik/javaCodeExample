package storage;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Vector;
import java.util.Scanner;

public class Visit implements Serializable {

	private static final long serialVersionUID = 8356740381864305284L;
	public static final int visitPrice = 5;
	public static final int guidePrice = 50;
	public static final int visitPriceReduced = 3;
	public static final int guidePriceReduced = 40;
	public static final int applyReductionTreshold = 10;
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	public static final SimpleDateFormat sortDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public long createdAt;
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
	
	public Date getDate()
	{
		return date;
	}
	
	public void setDate(Date date)
	{
		this.date = date;
	}
	
	public int getVisitorNumber()
	{
		return visitorNumber;
	}
	
	public void setVisitorNumber(int visitorNumber)
	{
		this.visitorNumber = visitorNumber;
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
		if (visitorNames.size() >= applyReductionTreshold) {
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
	
	public int getPrice() 
	{
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
	
	public String getDateKey()
	{
		return sortDateFormat.format(date) + "-" + name + "-" + Long.toString(createdAt);
	}
	
	public String getNameKey(){
		return name + "-" + sortDateFormat.format(date) + "-" + Long.toString(createdAt);
	}
	
	public boolean validate()
	{
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
		String description = " " + String.format("%-12s", dateFormat.format(date)) 
				+ String.format("%-14s", name)
				+ "visitors: " + String.format("%-4s", Integer.toString(visitorNumber));
		description += String.format("%-6s", hasGuide() ? "guide" : "");
		description += " " + String.format("%4s", getPrice()) + " euro";	
		if(hasReduction())
			description += " reduction ";
		
		return description;
	}
	
}
