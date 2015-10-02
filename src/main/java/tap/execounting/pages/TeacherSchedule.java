package tap.execounting.pages;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.components.editors.AddEvent;
import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.models.selectmodels.TeacherSelectModel;
import tap.execounting.entities.Event;
import tap.execounting.entities.Facility;
import tap.execounting.entities.Teacher;
import tap.execounting.security.AuthorizationDispatcher;
import tap.execounting.util.DateUtil;

import static tap.execounting.util.DateUtil.*;

public class TeacherSchedule {
	@Persist
	private int teacherId;

	@Property
	private boolean adding;

	@Property
	private TeacherSelectModel teacherSelectModel;

	@Property
	private String eventTime;

	@Component
	private Zone editZone;

	@Component
	private AddEvent eventEditor;

	@Persist
	private Date firstDate;

	@Persist
	private Date secondDate;

	@Persist
	@Property
	private Date date;

	@Persist
	private Event[][] eventsArrayCached;

	@Property
	private String singledate;

	@Inject
	private CRUDServiceDAO dao;

	@Persist
	@Property
	private int row;

	@Inject
	private AuthorizationDispatcher dispatcher;

	void onActionFromWeekBackward() {
        addDays(firstDate, -7);
        addDays(secondDate, -7);
	}

	void onActionFromWeekForward() {
        addDays(firstDate,7);
        addDays(secondDate, 7);
	}

	void onSubmitFromDateController() {
		floor(firstDate);
		secondDate = datePlusDays(firstDate, 7);
        ceil(secondDate);
	}

	public List<String> getDateData() {
		List<String> list = new ArrayList<>();
		int p = datesRange();

        Date d = new Date(getFirstDate().getTime());
		for (int i = 0; i < p; i++) {
            list.add(format("MMMM | d", d));
            incrementDay(d);
		}
		return list;
	}

	Object onActionFromAddNew() {
		// AUTHORIZATION MOUNT POINT EVENT CREATE
		if (dispatcher.canCreateEvents()) {
			adding = true;
			eventEditor.setup(getTeacher());
		}
		return editZone;
	}

	public void setup(Teacher t) {
		setTeacher(t);

		firstDate = floor();
		secondDate = fromNowPlusDays(7, true);
	}

	public String getFacilityNameById(Long id) {
		return dao.find(Facility.class, id).getName();
	}

    /**
     * Produces an array of seven date objects
     * @return
     */
	public List<Date> getDates() {
        Date d = new Date(firstDate.getTime());

		List<Date> dates = new ArrayList<>(7);
		for (int i = 0; i < datesRange(); i++, incrementDay(d))
            dates.add(new Date(d.getTime()));

		return dates;
	}

	public Event getEvent() {
        int column = daysDiff(firstDate, date);
		Event result = eventsArrayCached[column][row - 1];

		return result;
	}

	public void setEvent(Event e) {
		dao.update(e);
	}

	public List<Integer> getRows() {
		if (eventsArrayCached == null)
			return null;

		int rows = 0;
		for (Event[] l : eventsArrayCached)
			if (l.length > rows)
				rows = l.length;

		List<Integer> result = new ArrayList<Integer>(rows);
		for (int i = 0; i < rows; i++)
			result.add(i + 1);
		return result;
	}

	public List<Event> datedEvents() {
		HashMap<String, Object> params = new HashMap<String, Object>(3);
		params.put("teacherId", getTeacher().getId());
		params.put("earlierDate", firstDate.getTime());
		params.put("laterDate", secondDate.getTime());
		return dao.findWithNamedQuery(Event.BY_TEACHER_ID_AND_DATE, params);
	}

	private int datesRange() {
		if (secondDate.before(firstDate))
			throw new IllegalArgumentException(
					"second date should be after first, not vice versa");
		return daysDiff(firstDate, secondDate);
	}

	public Date getFirstDate() {
		return firstDate;
	}

	public void setFirstDate(Date date) {
		firstDate = date;
	}

	public Date getSecondDate() {
		return secondDate;
	}

	private Event[][] getEventsArray() throws Exception {
		// preparations for processing
		int length = datesRange();
		List<Event> eventsToProcess = datedEvents();

		if (eventsToProcess == null || eventsToProcess.size() == 0)
			return null;

		List<Event>[] temp = new ArrayList[length];
        Date d = new Date(firstDate.getTime()), edate;
		int rows = 0;

		// processing
		for (int i = 0; i < length; i++) {
			temp[i] = new ArrayList<>();

			for (int j = eventsToProcess.size() - 1; j >= 0; j--) {

				Event processingEvent = eventsToProcess.get(j);
                edate = processingEvent.getDate();
				if (daysDiff(edate, d) == 0) {
					temp[i].add(processingEvent);
					eventsToProcess.remove(j);
				}
			}
			if (temp[i].size() > rows)
				rows = temp[i].size();
			sort(temp[i]);

            incrementDay(d);
		}
		if (eventsToProcess.size() != 0)
			throw new Exception("Bad algorithm, unprocessed events remains after processing");

		// postprocessing
		Event[][] result = new Event[length][rows];
		for (int i = 0; i < result.length; i++) {
			ListIterator<Event> iter = temp[i].listIterator();
			int j = 0;
			while (iter.hasNext()) {
				result[i][j] = iter.next();
				j++;
			}
		}
		return result;
	}

	private void sort(List<Event> list) {
		for (int i = 0; i < list.size(); i++)
			for (int j = list.size() - 1; j >= 0; j--) {
				Event ei = list.get(i);
				Event ej = list.get(j);
				if (ei.getDate().before(ej.getDate())) {
					list.set(i, ej);
					list.set(j, ei);
				}
			}
	}

	@SetupRender
	void onPrepareForRender() {
		List<Teacher> teachers = dao.findWithNamedQuery(Teacher.WORKING);
		teacherSelectModel = new TeacherSelectModel(teachers);

		StringBuilder sb = new StringBuilder();
		sb.append("datedEvents().size(): " + datedEvents().size());
		sb.append("\nschedule size: " + datesRange());
		System.out.println("\n\n" + sb.toString() + "\n\n");

		try {
			eventsArrayCached = getEventsArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(int teacherId) {
		this.teacherId = teacherId;
	}

	public Teacher getTeacher() {
		return dao.find(Teacher.class, teacherId);
	}

	public void setTeacher(Teacher teacher) {
		this.teacherId = teacher.getId();
	}

	public String getFormattedDate() {
		return format("dd MMMM YY", firstDate);
	}
}
