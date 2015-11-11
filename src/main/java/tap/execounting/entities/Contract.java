package tap.execounting.entities;

import org.apache.tapestry5.beaneditor.NonVisual;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tap.execounting.data.ContractState;
import tap.execounting.entities.interfaces.Dated;
import tap.execounting.util.DateUtil;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.*;

import static java.util.Calendar.DAY_OF_WEEK;
import static javax.persistence.FetchType.EAGER;
import static tap.execounting.data.EventState.*;
import static tap.execounting.entities.ContractType.FreeFromSchool;
import static tap.execounting.entities.ContractType.FreeFromTeacher;
import static tap.execounting.entities.Event.FREE_FROM_SCHOOL;
import static tap.execounting.entities.Event.FREE_FROM_TEACHER;
import static tap.execounting.util.DateUtil.dayOfWeekRus;
import static tap.execounting.util.DateUtil.floor;

/**
 * This class does not support interface tap.execounting.util.entities.interfaces.Deletable, since
 * some contracts certainly should be removed, and it is not an accounting item,
 * but accounting unit.
 * <p/>
 * Contract represents physical contract between a client and a school. Its role
 * - to group events, and set rules, how to calculate money. Contract
 * could have one of six types, each type set the rules how to calculate money.
 * For more info about ContractType, look ContractType.java Also if contract is
 * free for client -- then all planned events will be free. Free events will be
 * written off, from the free contracts, but for other types of contracts they
 * won't.
 *
 * @author truth0
 */
@Entity
@Table(name = "contracts")
@NamedQueries({
        @NamedQuery(name = Contract.ALL, query = "from Contract"),
        @NamedQuery(name = Contract.BY_DATES, query = "from Contract where date between "
                + ":earlierDate and :laterDate"),
        @NamedQuery(name = Contract.WITH_TEACHER, query = "from Contract where teacherId = :teacherId"),
        @NamedQuery(name = Contract.WITH_CLIENT, query = "from Contract where clientId = :clientId"),
        @NamedQuery(name = Contract.FROZEN, query = "from Contract where dateFreeze < :now and dateUnfreeze > :now"),
        @NamedQuery(name = Contract.CANCELED, query = "from Contract where canceled = true")})
public class Contract implements Comparable<Contract>, Dated {

    public static final String ALL = "Contract.all";
    public static final String BY_DATES = "Contract.byDates";
    public static final String WITH_TEACHER = "Contract.withTeacher";
    public static final String WITH_CLIENT = "Contract.withClient";
    public static final String FROZEN = "Contract.frozen";
    public static final String CANCELED = "Contract.canceled";
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @NonVisual
    @Column(name = "contract_id")
    private int id;
    private boolean canceled;
    private int clientId;
    @ManyToOne
    @JoinColumn(name = "clientId", updatable = false, insertable = false)
    private Client client;
    private String comment;
    private int contractTypeId;
    @OneToOne
    @JoinColumn(name = "contractTypeId", insertable = false, updatable = false)
    private ContractType contractType;
    private Date date;
    private int discount;
    private Date dateFreeze;
    private Date dateUnfreeze;
    private int eventsNumber;
    @ManyToMany(mappedBy = "contracts")
    // @JoinTable(name = "events_contracts", joinColumns = { @JoinColumn(name =
    // "contract_id") }, inverseJoinColumns = { @JoinColumn(name = "event_id")
    // })
    private List<Event> events = new ArrayList<>();
    @OneToOne
    @JoinColumn(name = "typeId", nullable = false, updatable = false, insertable = false)
    private EventType eventType;
    /**
     * If contract is certificate
     */
    private boolean gift;
    private int giftMoney;
    @OneToMany(fetch = EAGER)
    @JoinColumn(name = "contract_id")
    private List<Payment> payments = new ArrayList<>();
    @OneToOne(cascade = CascadeType.ALL)
    private WeekSchedule schedule;
    private int typeId;
    @Min(value = 1)
    private int teacherId;
    @OneToOne
    @JoinColumn(name = "teacherId", nullable = false, updatable = false, insertable = false)
    private Teacher teacher;

    @Transient
    private String clientName;
    @Transient
    private String info;
    @Transient
    private Date lastScheduledEventDate;
    @Transient
    private String lastScheduledEventFacility;
    @Transient
    private Date clientCommentDate;
    @Transient
    private String clientCommentText;

    public Contract() {
        setDate(floor(new Date()));
        setContractTypeId(1);
    }

    public static List<Contract> cleanList() {
        return new ArrayList<>();
    }

    // Unit properties and methods

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public WeekSchedule getSchedule() {
        if (schedule == null)
            schedule = new WeekSchedule();
        return schedule;
    }

    public void setSchedule(WeekSchedule schedule) {
        this.schedule = schedule;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isBetweenDates(Date one, Date two) {
        return !getDate().before(one) && getDate().before(two);
    }

    /**
     * @return Id of event type of that contract
     */
    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    /**
     * @return returns written number of events in contract
     */
    public int getEventsNumber() {
        return eventsNumber;
    }

    public void setEventsNumber(int eventsNumber) {
        this.eventsNumber = eventsNumber;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isGift() {
        return gift;
    }

    public void setGift(boolean gift) {
        this.gift = gift;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int dicount) {
        this.discount = dicount;
    }

    public boolean isFrozen() {
        Date now = new Date();
        return dateUnfreeze != null && dateFreeze != null
                && now.before(dateUnfreeze) && now.after(dateFreeze);
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public int getContractTypeId() {
        return contractTypeId;
    }

    public void setContractTypeId(int contractTypeId) {
        this.contractTypeId = contractTypeId;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public Teacher getTeacher() {
        Teacher t;
        try {
            t = teacher;
        } catch (NullPointerException npe) {
            System.out.print("\n\nNPE at getTeacher, method will return null");
            t = null;
        } catch (Exception e) {
            System.out.println("\n\nException at getTeacher " + e.getMessage()
                    + ". Method will return null value");
            t = null;
        }
        return t;
    }

    public void setTeacher(Teacher t){
        this.teacher = t;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType et) {
        this.eventType = et;
    }

    public boolean isActive() {
        boolean active = !isComplete() && !isFrozen() && !isCanceled();
        return active;
    }

    public ContractState getState() {
        if (isComplete())
            return ContractState.complete;
        else if (isCanceled())
            return ContractState.canceled;
        else if (isFrozen())
            return ContractState.frozen;
        else
            return ContractState.active;
    }

    /**
     * @return full contract price
     */
    public int getMoney() {
        int lessonCost = getEventType().getPrice();
        int lessons = getEventsNumber();
        int total = lessonCost * lessons;
        total -= discount;
        total += getGiftMoney();
        return total;
    }

    // Lessons Event etc. methods
    public List<Event> getEventsCopied() {
        List<Event> list = new ArrayList<>();
        for (Event e : events)
            list.add(e.clone());
        return list;
    }

    public List<Event> getEvents(boolean asc) {
        List<Event> list = getEvents();
        Collections.sort(list);
        if (!asc)
            Collections.reverse(list);
        return list;
    }

    public List<Event> getFinishedEvents() {
        List<Event> events = new ArrayList<>();
        for (Event e : getEvents())
            if (e.getState() == complete)
                events.add(e);
        return events;
    }

    @Deprecated
    public int getSingleEventCost() {
        int totalLessonsCost = eventsNumber * eventType.getPrice() - discount;
        int singleLessonCost = totalLessonsCost / eventsNumber;
        return singleLessonCost;
    }

    /**
     * For each event take its cost from the event type and add it up
     * @return the sum of each event price
     */
    public int getCompleteEventsCost() {
        if (!notFree())
            return 0;
        int sum = 0;
        for (Event event : getEvents()) {
            if (event.isFree()) continue;
            if (event.isWriteOff()) continue;

            // Count event only if it is either complete, or failed by client,
            // and it is not marked as free.
            if (event.getState() == complete ||
                event.getState() == failedByClient)
                sum += event.getEventType().getPrice();
        }
        return sum;
    }

    /**
     * Does not counts the writeoffs; Also if contract is free for client --
     * then all planned events will be free. Free events will be written off,
     * from the free contracts, but for other types of contracts they won't.
     *
     * @param countFailedByClientAsComplete
     * @return
     */
    public int getEventsCompleteNumber(boolean countFailedByClientAsComplete) {
        int count = 0;

        if (countFailedByClientAsComplete) {
            for (Event e : getEvents())
                if (e.getState() == complete
                        || e.getState() == failedByClient)
                    count++;
        } else
            for (Event e : getEvents())
                if (e.getState() == complete)
                    count++;

        for (Event e : getEvents())
            if (e.isWriteOff() || (notFree() && e.isFree()))
                count--;
        return count;
    }

    /**
     * Creates a list of events which could be planned.
     * The resulting list consists of existing planned
     * events, and new ones, created to fill the free
     * slots.
     * Logic:
     *   Get remaining events number.
     *   Get already planned events.
     *   If the contract does not have enough events,
     *   create more.
     * The date, the school (facility), and the room id
     * should be set.
     * @return List<Event> which you can reschedule
     */
    @NonVisual
    public List<Event> getEventsToPlan() {
        int lessons_remaining = getEventsRemain();
        List<Event> already_planned = getEventsScheduled();

        int events_to_create = lessons_remaining - already_planned.size();
        if (events_to_create<0)
            throw new NotImplementedException();
        if (events_to_create == 0)
            return already_planned;

        List<Event> events_to_schedule = already_planned;
        while(events_to_create > 0) {
            Event e = new Event();
            e.getContracts().add(this);
            e.setHostId(teacherId);
            e.setState(planned);
            e.setTypeId(typeId);

            if (contractTypeId == FreeFromSchool)
                e.setFree(FREE_FROM_SCHOOL);
            else if (contractTypeId == FreeFromTeacher)
                e.setFree(FREE_FROM_TEACHER);

            events_to_schedule.add(e);
            events_to_create--;
        }
        return events_to_schedule;
    }



    /**
     * Generates list of dates suitable to plan events.
     * List size is defined by remaining lessons number.
     * @param dateOfFirstEvent non-null, inclusive
     * @return List<Date> dates to plan events
     */
    @NonVisual
    public List<Date> generateFreeDates(Date dateOfFirstEvent) {
        int lessons_remain = getEventsRemain();
        List<Date> new_event_dates = new ArrayList<>(lessons_remain);
        Calendar date = DateUtil.getMoscowCalendar(dateOfFirstEvent);

        while (lessons_remain > 0) {
            boolean the_date_is_ok = schedule.get(dayOfWeekRus(date.getTime()));
            if(the_date_is_ok){
                new_event_dates.add(date.getTime());
                lessons_remain--;
            }
            date.add(DAY_OF_WEEK, 1);
        }

        return new_event_dates;
    }

    private boolean notFree() {
        return contractTypeId != FreeFromSchool
                && contractTypeId != FreeFromTeacher;
    }

    public int getEventsRemain() {
        return eventsNumber - getEventsCompleteNumber(true); // Complete lessons, including those, failed by client
    }

    public List<Event> getEventsScheduled() {
        List<Event> events = new ArrayList<>();
        for (Event e : getEvents())
            if (e.getState() == planned)
                events.add(e);
        return events;
    }

    // Money and Payments

    /**
     * @return сумма денег уплаченная клиентом по этому договору.
     */
    public int getMoneyPaid() {
        int total = 0;
        for (Payment p : getPayments()) {
            if (!p.isScheduled()) {
                total += p.getAmount();
            }
        }
        total += getGiftMoney();
        return total;
    }

    public Client getClient() {
        return client;
    }

    public List<Payment> getPaymentsScheduled() {
        List<Payment> payments = new ArrayList<>();
        for (Payment p : getPayments())
            if (p.isScheduled())
                payments.add(p);
        return payments;
    }

    public boolean isComplete() {
        return getEventsRemain() <= 0;
    }

    public boolean isPaid() {
        return getMoneyPaid() >= getMoney();
    }

    /**
     * The balance is computed by the following rules:
     * 1) Take money that was paid by client
     * 2) Subtract the cost of complete events
     * 3) Subtract the written off money (списания)
     *
     * @return
     */
    public int getBalance() {
        return getMoneyPaid() - getCompleteEventsCost() - getMoneyWrittenOff();
    }

    private int getMoneyWrittenOff() {
        for (Event e : getEvents())
            if (e.isWriteOff())
                return e.getEventType().getPrice();
        return 0;
    }

    public ContractType getContractType() {
        return contractType;
    }

    // Util
    public int compareTo(Contract contract) {
        return getDate().compareTo(contract.getDate());
    }

    @NonVisual
    public boolean hasSchedule() {
        if (this.schedule == null)
            return false;
        for (int i = 1; i < 8; i++)
            if (schedule.get(i))
                return true;
        return false;
    }

    @NonVisual
    public int getEventsShiftedByClient() {
        int count = 0;
        for (Event e : getEvents())
            if (e.isShiftByClient())
                count++;
        return count;
    }

    @NonVisual
    public List<Event> getEventsShifted() {
        List<Event> list = new ArrayList<>();
        for (Event e : getEvents())
            if (e.isShift())
                list.add(e);
        return list;
    }

    @NonVisual
    public boolean hasEventShiftsByClient() {
        return getEventsShiftedByClient() > 0;
    }

    /**
     * @return true if contractTypeId != Trial type of contract
     */
    public boolean notTrial() {
        return this.contractTypeId != ContractType.Trial;
    }

    public int getGiftMoney() {
        // TODO do something with database and eliminate default value
        if (isGift())
            return giftMoney > 0 ? giftMoney : 300;
        return giftMoney;
    }

    public void setGiftMoney(int giftMoney) {
        this.giftMoney = giftMoney;
    }

    public boolean hasComment() {
        return comment != null && !comment.isEmpty();
    }

    public Date getDateFreeze() {
        return dateFreeze;
    }

    public void setDateFreeze(Date freeze) {
        dateFreeze = freeze;
    }

    public Date getDateUnfreeze() {
        return dateUnfreeze;
    }

    public void setDateUnfreeze(Date unfreezed) {
        dateUnfreeze = unfreezed;
    }

    @NonVisual
    public boolean isTrial() {
        return contractTypeId == ContractType.Trial;
    }

    public boolean hasFreezeDates() {
        return dateFreeze != null || dateUnfreeze != null;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Date getLastScheduledEventDate() {
        return lastScheduledEventDate;
    }

    public void setLastScheduledEventDate(Date lastScheduledEventDate) {
        this.lastScheduledEventDate = lastScheduledEventDate;
    }

    public Date getClientCommentDate() {
        return clientCommentDate;
    }

    public void setClientCommentDate(Date clientCommentDate) {
        this.clientCommentDate = clientCommentDate;
    }

    public String getClientCommentText() {
        return clientCommentText;
    }

    public void setClientCommentText(String clientCommentText) {
        this.clientCommentText = clientCommentText;
    }

    public String getLastScheduledEventFacility() {
        return lastScheduledEventFacility;
    }

    public void setLastScheduledEventFacility(String lastScheduledEventFacility) {
        this.lastScheduledEventFacility = lastScheduledEventFacility;
    }
}
