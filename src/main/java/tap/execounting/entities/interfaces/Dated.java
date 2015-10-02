package tap.execounting.entities.interfaces;

import java.util.Date;

public interface Dated {
	public Date getDate();
    public boolean isBetweenDates(Date one, Date two);
}
