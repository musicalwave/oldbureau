package tap.execounting.entities;

import org.apache.tapestry5.beaneditor.NonVisual;
import org.apache.tapestry5.beaneditor.Validate;
import tap.execounting.data.EventState;
import tap.execounting.entities.interfaces.Dated;
import tap.execounting.util.DateUtil;
import tap.execounting.util.Pair;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

import static tap.execounting.data.Const.freeFromSchool;
import static tap.execounting.data.Const.freeFromTeacher;
import static tap.execounting.data.EventState.planned;

/**
 * This class does not support interface tap.execounting.util.entities.interfaces.Deletable, since
 * some events certainly should be removed, and it is not an accounting item,
 * but accounting unit.
 * 
 * @author truth0
 * 
 */
@Entity
@NamedQueries({
		@NamedQuery(name = Event.ALL, query = "Select se from Event se " +
                                                "inner join fetch se.eventType "),
		@NamedQuery(name = Event.BY_FACILITY_ID, query = "Select se from Event se where se.facilityId = :facilityId"),
		@NamedQuery(name = Event.BY_TEACHER_ID, query = "Select se from Event se where se.hostId = :teacherId"),
		@NamedQuery(name = Event.BY_TEACHER_ID_AND_DATE, query = "Select se from Event se "
				+ "where se.hostId = :teacherId "
				+ "and se.date between :earlierDate and :laterDate"),
		@NamedQuery(name = Event.BY_ROOM_ID_AND_DATE, query = "Select e from Event e where e.roomId = :roomId "
				+ "and date(e.date) = date(:date)"),
		@NamedQuery(name = Event.BY_TYPE_ID, query = "Select e from Event e where e.typeId = :typeId"),
		@NamedQuery(name = Event.BY_ROOM_ID, query = "from Event as e where e.roomId = :roomId"),
		@NamedQuery(name = Event.BETWEEN_DATE1_AND_DATE2, query = "from Event where date >= :date1 and date <= :date2"),
		@NamedQuery(name = Event.BY_STATE, query = "from Event as e where e.state=:stateCode"),
		@NamedQuery(name = Event.AFTER_DATE, query = "from Event as e where e.date >= :date"),
		@NamedQuery(name = Event.BEFORE_DATE, query = "from Event as e where e.date <= :date"),
		@NamedQuery(name = Event.BY_DATE_TEACHERID_TITLE, query = "from Event where date = :date and hostId = :teacherId"),
        @NamedQuery(name = Event.BY_CLIENT_ID,
                query = "SELECT e" +
                        " FROM Event as e" +
                        " INNER JOIN e.contracts as con" +
                        " INNER JOIN con.client as ivan" +
                        " WHERE ivan.id = :clientId"),
        @NamedQuery(name = Event.BY_ID_AND_CLIENT_ID,
                query = "SELECT e" +
                        " FROM Event as e" +
                        " INNER JOIN e.contracts as con" +
                        " INNER JOIN con.client as ivan" +
                        " WHERE e.id = :id" +
                        " AND ivan.id = :clientId"),
        @NamedQuery(name = Event.BY_CLIENT_ID_AND_DATES, query = "SELECT e" +
                " FROM Event as e" +
                " INNER JOIN e.contracts as con" +
                " INNER JOIN con.client as ivan" +
                " WHERE ivan.id = :clientId" +
                " AND e.date >= :date1" +
                " AND e.date <= :date2")})
@Table(name = "events")
public class Event implements Comparable<Event>, Dated {

	public static final String ALL = "Event.all";
	public static final String BY_FACILITY_ID = "Event.byFacilityId";
	public static final String BY_ROOM_ID_AND_DATE = "Event.byRoomIdAndDate";
	public static final String BY_TEACHER_ID = "Event.byTeacherId";
	public static final String BY_TEACHER_ID_AND_DATE = "Event.byTeacherIdAndDate";
	public static final String BY_TYPE_ID = "Event.byTypeId";
	public static final String BY_STATE = "Event.byStateCode";
	public static final String BY_ROOM_ID = "Event.byRoomId";
	public static final String BETWEEN_DATE1_AND_DATE2 = "Event.betweenDate1andDate2";
	public static final String AFTER_DATE = "Event.AfterDate";
	public static final String BEFORE_DATE = "Event.BeforeDate";
	public static final String BY_DATE_TEACHERID_TITLE = "Event.byDateAndTeacherIdAndTitle";
    public static final String BY_CLIENT_ID = "Event.byClientId";
    public static final String BY_CLIENT_ID_AND_DATES = "Event.byClientIdAndDates";
    public static final String BY_ID_AND_CLIENT_ID = "Event.byIdAndClientId";

	public static final byte FREE_FROM_SCHOOL = 1;
	public static final byte FREE_FROM_TEACHER = 2;
	public static final byte NOT_FREE = 0;

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "event_id")
	private int id;

	@Validate("required")
	@NotNull
	@Column(nullable = false, name = "host_id")
	private int hostId;

	@NotNull
	@Column(nullable = false, name = "facility_id")
	private int facilityId;

	@Column(name = "room_id")
	private int roomId;

	@Column(name = "date")
	private Date date;

	private int state;

	/**
	 * It is used to indicate free lessons null and 0 - for usual lessons. 1 -
	 * for free lessons from school, 2 - for free lessons from teacher
	 */
	private byte free;

	@NonVisual
	private boolean deleted;

	@Column(name = "type_id", unique = false)
	private int typeId;

	@ManyToOne
	@JoinColumn(name = "type_id", nullable = false, updatable = false, insertable = false)
	private EventType eventType;

	private String comment;

	@NonVisual
	private int shift_count;

    @NonVisual
    private boolean has_legal_shift;

	@ManyToMany
	@JoinTable(name = "events_contracts", joinColumns = { @JoinColumn(name = "event_id") }, inverseJoinColumns = { @JoinColumn(name = "contract_id") })
	private List<Contract> contracts = new ArrayList<>();

    @Transient
    private String teacherName;

    @Transient
    private String facilityName;

    @Transient
    private String roomName;

    @Transient
    private String typeName;

    @Transient
    private String clientNames;

    @Transient
    private String stateName;

    private int price;

	public Event() {
		date = new Date();
		state = EventState.complete.getCode();
	}

    private boolean commentContains(String substring){
        return comment != null && comment.contains(substring);
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getHostId() {
		return hostId;
	}

	public void setHostId(int hostId) {
		this.hostId = hostId;
	}

	public int getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(int facilityId) {
		this.facilityId = facilityId;
	}

	public List<Client> getClients() {
		List<Client> res = new ArrayList<Client>();
		for (Contract c : getContracts())
			res.add(c.getClient());
		return res;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public Date getDate() {
		if (date == null) {
			date = new Date();
			if (getComment() == null)
				setComment("");
			setComment(getComment().concat(" !! дату надо проверить"));
		}
		return date;
	}

    public boolean isBetweenDates(Date one, Date two) {
        return !getDate().before(one) && getDate().before(two);
    }

    public void setDate(Date date) {
		this.date = date;
	}

	public EventState getState() {
		return EventState.fromCode(state);
	}

	public void setState(EventState state) {
		this.state = state.getCode();
	}

	public String getComment() {
		return comment;
	}

    public String[] getComments() {
        return comment.split("\\n");
    }

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public List<Contract> getContracts() {
		return contracts;
	}

	public void setContracts(List<Contract> contracts) {
		this.contracts = contracts;
	}

	public boolean addContract(Contract con) {
		int conId = con.getId();
		boolean success = true;

		for (Contract c : getContracts()) {
			if (c.getId() == conId) {
				success = false;
				break;
			}
		}
		if (success)
			getContracts().add(con);

		return success;
	}

	public int compareTo(Event o) {
		return this.getDate().compareTo(o.getDate());
	}

	public EventType getEventType() {
		return eventType;
	}

	// returns school share from that event
	// public int getSchoolShare() {
	// int total = 0;
	// for (Contract c : getContracts()) {
	// // TODO: Подарочный сертификат?
	// if (c.getContractTypeId() == 2) {
	// total += 300;
	// continue;
	// }
	// int basicCost = c.getSingleLessonCost();
	// int percent = c.getEventType().getShare();
	// total += basicCost * percent;
	// }
	//
	// return total;
	// }

	/**
	 * @return this should say how much money all clients pay
	 */
	public int getMoney() {
		// First blood
		// Code supports only one-contract lessons
		if (free > 0)
			return 0;
		int total = 0;
		for (Contract c : getContracts())
			total += c.getSingleEventCost();
		return total;
	}

	@NonVisual
	public int getTeacherMoney() {
		if (free == FREE_FROM_TEACHER)
			return 0;
		return this.getEventType().getShareTeacher();
	}

	/**
	 * @return how much money did school earned from this lesson
	 */
	@NonVisual
	public int getSchoolMoney() {
		// if it is free from school -- school should pay money to teacher
		// anyway
		// so it would be with minus
		switch (free) {
		case FREE_FROM_TEACHER:
		case FREE_FROM_SCHOOL:
			return 0;
		default:
			// FIXME not for groups
			return getEventType().getSchoolMoney();
		}
	}

	public boolean haveContract(Contract con) {
		for (Contract c : getContracts())
			if (c.getId() == con.getId())
				return true;
		return false;
	}

	private Pair<Date,Date> getLegalShiftInterval(Date date, List<Date> dates) {
        if (date != null &&
            dates != null &&
            dates.size() > 1) {
                for (int i = 0; i < dates.size() - 1; i++)
                     if (date == dates.get(i)) {
                         if (i == 0)
                             return new Pair<>(date, dates.get(i + 1));
                         else if (i == dates.size() - 1)
                             return new Pair<>(dates.get(i - 1), date);
                         else
                             return new Pair<>(dates.get(i - 1), dates.get(i + 1));
                }
        }

		return null;
	}

	public void move(EventState newState, Date newDate) {
        addComment(getShiftComment(newState, newDate));
        updateShiftCount(newState, newDate);
		updateFacility(newDate);
		setState(planned);
		setDate(newDate);
	}

    private void updateFacility(Date newDate) {
        Contract contract = getFirstContract();
        if(contract != null) {
            Teacher teacher = contract.getTeacher();
            if(teacher != null) {
                Integer newFacilityId = teacher.getFacilityIdByDate(newDate);
                if(newFacilityId != null)
                    facilityId = newFacilityId;
            }
        }

    }

    private void updateShiftCount(EventState newState, Date newDate) {
       	if(newState == EventState.movedByClient) {
                if(legalShift(newDate))
                    has_legal_shift = true;
                else
                    shift_count++;
        }
    }

    private String getShiftComment(EventState newState, Date newDate) {
        String shiftCommentBlank = "$%s: %s -> %s";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return String.format(shiftCommentBlank,
                             newState.toString(),
                             dateFormat.format(date),
                             dateFormat.format(newDate));
        }

    private void addComment(String newComment) {
        if (comment == null || comment.isEmpty())
            comment = newComment;
        else
            comment = comment.concat("\n" + newComment);
    }

    private boolean legalShift(Date newDate) {
	    // only one legal shift per event is allowed
        if(has_legal_shift)
            return false;

        Contract contract = getFirstContract();
        if(contract != null && contract.hasSchedule()) {
            List<Date> eventDates = contract.getExtendedEventDates();
            Pair<Date, Date> legalInterval = getLegalShiftInterval(date, eventDates);
            if (legalInterval != null && !DateUtil.between(newDate, legalInterval.left, legalInterval.right))
                return false;
		}

		return true;
	}

    public Contract getFirstContract() {
        if(contracts != null && !contracts.isEmpty())
            return contracts.get(0);

        return null;
    }

	public boolean wasMoved() {
		if (comment.contains(EventState.movedByClient.toString())
				|| comment.contains(EventState.movedByTeacher.toString()))
			return true;
		return false;
	}

	/**
	 * Not full clone only ID and eventstate are copied. only for delete.
	 */
	public Event clone() {
		Event e = new Event();
		e.setId(this.id);
		e.setState(EventState.fromCode(this.state));
		return e;
	}

	@NonVisual
	public boolean isWriteOff() {
		return getEventType().isWriteOff();
	}

	/**
	 * @return checks if event is free for client. That means field free is null
	 *         or 0
	 */
	@NonVisual
	public boolean isFree() {
		return free != NOT_FREE;
	}

	@NonVisual
	public boolean isFreeFromSchool() {
		return free == FREE_FROM_SCHOOL;
	}

	@NonVisual
	public boolean isFreeFromTeacher() {
		return free == FREE_FROM_TEACHER;
	}

	public boolean containsClient(int clientId) {
		for (Contract c : getContracts())
			if (c.getClientId() == clientId)
				return true;
		return false;
	}

	public void setFree(byte freeVal) {
		this.free = freeVal;
		// If event is free either from school, or from teacher -- put that in
		// comments
		// initialize
		if (comment == null)
			comment = "";

		// clean up
		if (comment.contains(freeFromSchool))
			comment = comment.replace(freeFromSchool, "");
		if (comment.contains(freeFromTeacher))
			comment = comment.replace(freeFromTeacher, "");

		if (freeVal != NOT_FREE)
			comment = comment
					+ (freeVal == 1 ? freeFromSchool : freeFromTeacher);
	}

	public boolean hasComment() {
		return comment != null && !comment.isEmpty();
	}

    public boolean isPlanned() {
        return getState() == planned;
    }

    public boolean isShift() {
        return isShiftByClient() || isShiftByTeacher();
    }

    public boolean isShiftByClient() {
        return commentContains(EventState.movedByClient.toString());
    }

    /**
     * Not tested
     */
    public boolean isShiftByTeacher() {
        return commentContains(EventState.movedByTeacher.toString());
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getClientNames() {
        return clientNames;
    }

    public void setClientNames(String clientNames) {
        this.clientNames = clientNames;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

	public int getShiftCount() {
		return shift_count;
	}
}
