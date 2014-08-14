package storage;
import java.io.Serializable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

/** 
 * The {@code Visit} class represents visit entity
 * 
 * @author  Valeriya Slovikovskaya
*/
public class Visit implements Serializable {

	private static final long serialVersionUID = 8356740381864305284L;
	
	/**
	 * Visit's price
	 */
	public static final int visitPrice = 5;
	
	/**
	 * Guide price
	 */
	public static final int guidePrice = 50;
	
	/**
	 * Reduced visit's price
	 */
	public static final int visitPriceReduced = 3;
	
	/**
	 * Reduced guide's price
	 */
	public static final int guidePriceReduced = 40;
	
	/**
	 * Number of visitors threshold to suggest reduction
	 */
	public static final int applyReductionTreshold = 11;
	
	/**
	 * Date format to display, parse and keep visit's data
	 */
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	/**
	 * Sql-like date format for date-base visits sort
	 */
	public static final SimpleDateFormat sortDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * Time stamp of visit's creation
	 */
	public long createdAt;
	
	/**
	 * Name
	 */
	private String name;
	
	/**
	 * Date
	 */
	private Date date;
	
	/**
	 * Number of visitors
	 */
	private Integer visitorNumber;
	
	/**
	 * Guide need flag
	 */
	private Boolean guide = false;
	
	/**
	 * Reduction requested flag
	 */
	private Boolean reduction = false;
	
	/**
	 * Collection {@link Vector<String>} of visitors names
	 */
	private Vector<String> visitorNames = new Vector<String>();
	
	/**
	 * Constructor. Instantiates empty visit 
	 */
	public Visit(){
		this.createdAt =  new Date().getTime();
	}
	
	/**
	 * Constructor. Instantiates visit with non empty name, 
	 * date and number of visitors 
	 * 
	 * @param name          Name
	 * @param date          Date
	 * @param visitorNumber Number of visitors
	 */
	public Visit(String name, Date date, int visitorNumber)
	{
		this.createdAt =  new Date().getTime();
		this.name = name;
		this.date = date;
		this.visitorNumber = visitorNumber;
	}
	
	/**
	 * Gets name
	 * 
	 * @return
	 */
	public String getName() 
	{
		return name;
	}
	
	/**
	 * Sets name
	 * 
	 * @param name
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Gets date
	 * 
	 * @return
	 */
	public Date getDate()
	{
		return date;
	}
	
	public void setDate(Date date)
	{
		this.date = date;
	}
	
	/**
	 * Gets number of visitors
	 * 
	 * @return
	 */
	public int getVisitorNumber()
	{
		return visitorNumber;
	}
	
	/**
	 * Sets visitors number
	 * 
	 * @param visitorNumber
	 */
	public void setVisitorNumber(int visitorNumber)
	{
		this.visitorNumber = visitorNumber;
	}

	/**
	 * Checks if visit has guide
	 * 
	 * @return
	 */
	public Boolean hasGuide()
	{
		return guide;
	}
	
	/**
	 * Checks if visit has reduction
	 * 
	 * @return
	 */
	public Boolean hasReduction()
	{
		return reduction;
	}
	
	/**
	 * Assigns guide to visit
	 */
	public void setGuide()
	{
		if(visitorNames.size() == 0)
			return;
		this.guide = true;
	}
	
	/**
	 * Assigns reduction to visit
	 */
	public void setReduction()
	{
		if (visitorNames.size() >= applyReductionTreshold) {
			this.reduction = true;
		}
	}
	
	/**
	 * Adds visitor to list of visitors
	 * Updates number of visitors
	 * 
	 * @param name Visitor name
	 */
	public void addVisitorName(String name)
	{
		if(name.length() == 0)
			return;
		this.visitorNames.add(name);
		this.visitorNumber = visitorNames.size();
	}
	
	/**
	 * Assigns list of visitors to visit
	 * 
	 * @param visitorNames List of visitors names
	 */
	public void setVisitorNames(Vector<String> visitorNames)
	{
		if(visitorNames.size() == 0)
			return;
		this.visitorNames = visitorNames;
		this.visitorNumber = visitorNames.size();
	}
	
	/**
	 * Gets visitors names collection 
	 * {@link Vector<String>}
	 * 
	 * @return
	 */
	public Vector<String> getVisitorNames()
	{
		return visitorNames;
	}
	
	/**
	 * Gets price
	 * 
	 * @return
	 */
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
	
	/**
	 * Gets price constants for visit price calculation
	 * 
	 * @return
	 */
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
	
	/**
	 * Gets key to place visit in the date-based-sorted visit's storage 
	 * 
	 * @return
	 */
	public String getDateKey()
	{
		return sortDateFormat.format(date) + "-" + name + "-" + Long.toString(createdAt);
	}
	
	/**
	 * Gets key to place visit in the name-based-sorted visit's storage 
	 * 
	 * @return
	 */
	public String getNameKey(){
		return name + "-" + sortDateFormat.format(date) + "-" + Long.toString(createdAt);
	}
	
	/**
	 * Validates visit: 
	 * valid visit has non empty name
	 * non zero number of visitors
	 * not-null date
	 * 
	 * @return
	 */
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
	
	/**
	 * Gets string representation of visit
	 */
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
