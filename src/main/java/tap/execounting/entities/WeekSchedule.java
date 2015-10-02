package tap.execounting.entities;

import org.apache.tapestry5.beaneditor.NonVisual;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Database table for that table should cleaned sometimes.
 * @author truth0
 *
 */
@Entity
@Table(name = "weekSchedules")
public class WeekSchedule {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private boolean monday;
	private boolean tuesday;
	private boolean wednesday;
	private boolean thursday;
	private boolean friday;
	private boolean saturday;
	private boolean sunday;

	/**
	 * @param day -- integer in range 1..7
	 * @return true if this day is checked
	 */
	public boolean get(int day) {
		switch (day) {
		case 1:
			return monday;
		case 2:
			return tuesday;
		case 3:
			return wednesday;
		case 4:
			return thursday;
		case 5:
			return friday;
		case 6:
			return saturday;
		case 7:
			return sunday;
		default:
			return false;
		}
	}

	public boolean get(String day){
		day = day.toLowerCase();
		
		// monday
		if(day.equals("monday") || day.equals("пн") || day.equals("понедельник"))
			return monday;
		
		// tuesday
		if(day.equals("tuesday") || day.equals("вт") || day.equals("вторник"))
			return tuesday;
		
		// wednesday
		if(day.equals("wednesday") || day.equals("ср") || day.equals("среда"))
			return wednesday;
		
		// thursday
		if(day.equals("thursday") || day.equals("чт") || day.equals("четверг"))
			return thursday;
		
		// friday
		if(day.equals("friday") || day.equals("пт") || day.equals("пятница"))
			return friday;
		
		// saturday
		if(day.equals("saturday") || day.equals("сб") || day.equals("суббота"))
			return saturday;
		
		// sunday
		if(day.equals("sunday") || day.equals("вс") || day.equals("воскресенье"))
			return sunday;
		
		return false;
	}
	
	public boolean getMonday() {
		return monday;
	}

	public void setMonday(boolean monday) {
		this.monday = monday;
	}

	public boolean getTuesday() {
		return tuesday;
	}

	public void setTuesday(boolean tuesday) {
		this.tuesday = tuesday;
	}

	public boolean getWednesday() {
		return wednesday;
	}

	public void setWednesday(boolean wednesday) {
		this.wednesday = wednesday;
	}

	public boolean getThursday() {
		return thursday;
	}

	public void setThursday(boolean thursday) {
		this.thursday = thursday;
	}

	public boolean getFriday() {
		return friday;
	}

	public void setFriday(boolean friday) {
		this.friday = friday;
	}

	public boolean getSaturday() {
		return saturday;
	}

	public void setSaturday(boolean saturday) {
		this.saturday = saturday;
	}

	public boolean getSunday() {
		return sunday;
	}

	public void setSunday(boolean sunday) {
		this.sunday = sunday;
	}

    private boolean[] array() {
        return new boolean[]
                {monday,tuesday,wednesday,thursday,friday,saturday,sunday};
    }

    @NonVisual
    public int countWorkingDays() {
        byte count = 0;
        for(boolean day : array())
            if(day) count++;
        return count;
    }

    @NonVisual
    public boolean hasWorkingDay() {
        return countWorkingDays() > 0;
    }
}
