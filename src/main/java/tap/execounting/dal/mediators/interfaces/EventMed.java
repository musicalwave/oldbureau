package tap.execounting.dal.mediators.interfaces;

import tap.execounting.data.EventState;
import tap.execounting.data.EventTransferType;
import tap.execounting.entities.*;

import java.util.Date;
import java.util.List;

public interface EventMed {
	// unit methods:
	// unit
	public Event getUnit();

	public EventMed setUnit(Event unit);
	
	public Event getUnitById(int eventId);

	// getters:
	// Date
	public Date getDate();

	// Teacher
	public Teacher getTeacher();

	// Clients
	public List<Client> getClients();

	// Discipline (EventType)
	public EventType getEventType();

	// Contracts
	public List<Contract> getContracts();

	// Price
	public int getPrice();

	// State
	public EventState getState();

	// Comment
	public String getComment();

	// Facility
	public Facility getFacility();

	// Room
	public Room getRoom();

	// EventTypeLoader
	public EventType loadEventType(int id);

	// EventTypeAddition Loading
	public EventTypeAddition loadProbation(int id);
	
	// Creates or updates event instance
	public void save(Event event);
	
	// Moves event on dates
	public void move(EventState newState, Date newDate,
			EventTransferType transferType) throws IllegalAccessException;

	// group methods:
	// group
	public List<Event> getGroup();

	public List<Event> getGroup(boolean resetAfter);

	public EventMed setGroup(List<Event> items);

	public List<Event> getAllEvents();

	public void reset();

	public String getFilterState();

	// filters
	// Client
	public EventMed retainByClientId(int clientId);
	
	// Teacher
	public EventMed retainByTeacher(Teacher unit);

	// Contract
	public EventMed retainByContract(Contract unit);

	// State
	public EventMed retainByState(EventState state);

	// Facility
	public EventMed retainByFacility(Facility unit);

	// Room
	public EventMed retainByRoom(Room unit);

	// Discipline
	public EventMed retainByRoom(EventType type);

	// Date
	public EventMed retainByDatesEntry(Date date1, Date date2);

	// Filter paid events, which is complete and failed by client
	public EventMed retainPaidEvents();

	// counters
	// group length
	public Integer countGroupSize();

	// state
	public Integer count(EventState state);

	public Integer countEventsComplete();

	public Integer countEventsFired();

	public Integer countEventsMovedByClient();

	public Integer countEventsMovedByTeacher();

	// money
	public Integer countMoney();

	public Integer countTeacherMoney();

	public Integer countSchoolMoney();

	public Integer countMoneyOfCompleteEvents();

	public Integer countMoneyOfFailedEvents();

	public Integer countMoneyOfEventsFailedByClient();

	public Integer countMoneyOfEventsFailedByTeacher();

	// percent
	public Integer countGivenPercentOfMoney(int percent);

	// days
	public int countDaysInEventsGroup();

	public EventMed sortByDate(boolean ascending);

	// Return a list of events identified by date, client id, teacher id,
	// and event type title.
	public List<Event> getByDateClientIdTeacherIdAndEventTypeTitle(Date d,
			int clientId, int teacherId, String typeTitle);

    /**
     * Will return the most recent event from the group
     * @return
     */
    public Event lastByDate();

    public EventMed retainByEventTitleContaining(String typeId);

    public EventMed retainByTeacherId(Integer teacherId);

    public EventMed retainByRoomId(Integer roomId);

    public EventMed retainByStateCode(Integer state);

    public EventMed retainByFacilityId(Integer facilityId);

    public EventMed retainByClientIdAndDates(int id, Date date1, Date date2);
}
