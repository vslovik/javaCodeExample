import java.sql.Timestamp;
import java.util.Date;

public class RandomDate {
	private long beginTime;
	private long endTime;

	public RandomDate (String start, String end) {
	    beginTime = Timestamp.valueOf(start).getTime();
	    endTime = Timestamp.valueOf(end).getTime();
	}

	/**
	 * Method should generate random number that represents 
	 * a time between two dates.
	 * 
	 * @return
	 */
	private long getRandomTimeBetweenTwoDates () {
	    long diff = endTime - beginTime + 1;
	    return beginTime + (long) (Math.random() * diff);
	}
	
	public Date getDate() {
		return new Date(getRandomTimeBetweenTwoDates());
	}
}
