package tap.execounting.util;

import org.apache.commons.lang.time.DateUtils;
import tap.execounting.entities.interfaces.Dated;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.SECOND;

public class DateUtil {

    private TimeZone timeZone;

    private HashMap<String, String> dayOfWeekNames;

    private static int HOUR = Calendar.HOUR;
    private static int MINUTE = Calendar.MINUTE;
    private static int YEAR = Calendar.YEAR;
    private static int MONTH = Calendar.MONTH;
    private static int DAY_OF_YEAR = Calendar.DAY_OF_YEAR;
    //private static int DAY_OF_MONTH = Calendar.DAY_OF_MONTH;
    private static int MILLISECOND = Calendar.MILLISECOND;

    public DateUtil() {
        timeZone = TimeZone.getTimeZone("Europe/Moscow");
        dayOfWeekNames = new HashMap<>(7);
        dayOfWeekNames.put("Monday", "Понедельник");
        dayOfWeekNames.put("Tuesday", "Вторник");
        dayOfWeekNames.put("Wednesday", "Среда");
        dayOfWeekNames.put("Thursday", "Четверг");
        dayOfWeekNames.put("Friday", "Пятница");
        dayOfWeekNames.put("Saturday", "Суббота");
        dayOfWeekNames.put("Sunday", "Воскресенье");
    }

    public DateUtil(TimeZone timeZone) {
        this.setTimeZone(timeZone);
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public static Date fromNowPlusDays(int days) {
        Calendar calendar = getMoscowCalendar();
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }

    /**
     * Sets the hour, minute, second and millisecond of given date to zero
     * @param date Date instance to change
     * @return will return the link to the same date instance
     */
    public static Date floor(Date date) {
        Calendar c = getMoscowCalendar(date);
        c.set(HOUR_OF_DAY, 0);
        c.set(MINUTE, 0);
        c.set(SECOND, 0);
        c.set(MILLISECOND, 0);
        date.setTime(c.getTimeInMillis());
        return date;
    }

    /**
     * Changes the given date.
     * Sets the hour=23, the minute=59, the second=59,
     * and millisecond=999
     * @param date Date instance to change
     * @return will return the link to the same date instance
     */
    public static Date ceil(Date date) {
        floor(date);
        Calendar c = getMoscowCalendar(date);
        c.add(DAY_OF_YEAR, 1);
        c.add(MILLISECOND, -1);
        date.setTime(c.getTimeInMillis());
        return date;
    }

    /**
     * Changes the given date.
     * Adds one day, to given instance.
     * @param date Date instance to change
     */
    public static void incrementDay(Date date){
        long newTime = datePlusDays(date, 1).getTime();
        date.setTime(newTime);
    }

    /**
     * Produces new date.
     * New date will differ from given, on given amount of days.
     * @param date
     * @param days
     * @return
     */
    public static Date datePlusDays(Date date, int days) {
        Calendar calendar = getMoscowCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }

    public static List<Date> generateDaySet(Date eventsDate, int days) {
        List<Date> list = new ArrayList<>(days);
        for (int i = 0; i < days; i++)
            list.add(datePlusDays(eventsDate, i));
        return list;
    }

    public static String monthName(Date eventsDate) {
        return format("MMMM", eventsDate);
    }

    public static Date datePlusMonths(Date d, int months) {
        Calendar c = getMoscowCalendar(d);
        c.add(MONTH, months);
        return c.getTime();
    }

    /**
     * This will return the module of the difference between dates
     * @param first
     * @param second
     * @return
     */
    public static int daysDiff(Date first, Date second){
        // In order to return
        boolean firstBeforeSecond = first.before(second);
        Date later = firstBeforeSecond ? second : first;
        Date former = firstBeforeSecond ? first : second;
        return (int)( (later.getTime() - former.getTime())
                / (1000 * 60 * 60 * 24) );
    }

    public static Date trimToMonth(Date date) {
        Calendar c = getMoscowCalendar(date);
        Calendar r = getMoscowCalendar();
        r.setTimeInMillis(-10800000);

        r.set(YEAR, c.get(YEAR));
        r.set(MONTH, c.get(MONTH));
        return r.getTime();
    }

    public static Date maxOutDayTime(Date date) {
        Calendar c = getMoscowCalendar(floor(date));
        c.add(DAY_OF_YEAR, 1);
        c.add(MILLISECOND, -1);
        return c.getTime();
    }

    public static TimeZone getMoscowTimeZone() {
        return java.util.TimeZone.getTimeZone("Europe/Moscow");
    }

    public static Calendar getMoscowCalendar() {
        return new GregorianCalendar(getMoscowTimeZone());
    }

    public static Calendar getMoscowCalendar(Date time) {
        Calendar c = getMoscowCalendar();
        c.setTime(time);
        return c;
    }

    /**
     * remove all items that do not fit in that date borders
     */
    public static void retainByDatesEntry(List<? extends Dated> items, Date date1, Date date2) {
        Date stamp;
        int i = items.size();
        if (date1 != null && date2 != null) {
            for (; --i >= 0; ) {
                stamp = items.get(i).getDate();
                if (stamp.after(date2) || stamp.before(date1))
                    items.remove(i);
            }
        } else if (date1 != null) {
            for (; --i >= 0; )
                if (items.get(i).getDate().before(date1))
                    items.remove(i);
        } else if (date2 != null) {
            for (; --i >= 0; )
                if (!items.get(i).getDate().before(date2))
                    items.remove(i);
        }
    }

    /**
     * @param d from which to calculate this day of week
     * @return integer for day of week from 1 to 7
     */
    public static int dayOfWeekRus(Date d) {
        // TODO check
        Calendar date = new GregorianCalendar();
        date.setTime(d);
        int dow = date.get(Calendar.DAY_OF_WEEK);
        dow = dow == 1 ? 7 : dow - 1;
        return dow;
    }

    public static String format(String format, Date date) {
        SimpleDateFormat f = new SimpleDateFormat(format, new Locale("ru", "RU"));
        return f.format(date);
    }

    public static void sort(List<? extends Dated> list, boolean descending) {
        Collections.sort(list, new DatedComparator());
        if (descending)
            Collections.reverse(list);
    }

    public static Comparator DateComparator = new DateComparator();

    public static Date fromNowPlusDays(int i, boolean ceilNotFloor) {
        Date t = fromNowPlusDays(i);
        return ceilNotFloor ? ceil(t) : floor(t);
    }

    /**
     * returns new date with floor applied
     *
     * @return
     */
    public static Date floor() {
        return floor(new Date());
    }

    public static Date floor(long time) {
        return floor(new Date(time));
    }

    public static Date ceil() {
        return ceil(new Date());
    }

    public static Date parse(String format, String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.parse(date);
    }

    /**
     * This will increase the given date, by given amount of days.
     * @param date
     * @param days
     */
    public static void addDays(Date date, int days) {
        long newTime = DateUtils.addDays(date,days).getTime();
        date.setTime(newTime);
    }

    public static Date now() {
        return new Date();
    }

    public  static boolean between(Date date, Date leftDate, Date rightDate) {
        return (date.after(leftDate) && date.before(rightDate));
    }

    public static boolean betweenInclusive(Date date, Date leftDate, Date rightDate){
        return (date == leftDate) || (date == rightDate) || between(date, leftDate, rightDate);
    }

}

class DatedComparator implements Comparator<Dated> {

    public int compare(Dated first, Dated second) {
        return first.getDate().compareTo(second.getDate());
    }
}

class DateComparator implements Comparator<Date> {
    public int compare(Date one, Date two) {
        return one.compareTo(two);
    }
}


