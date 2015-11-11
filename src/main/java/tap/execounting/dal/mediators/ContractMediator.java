package tap.execounting.dal.mediators;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.LazyInitializationException;
import tap.execounting.dal.ChainMap;
import tap.execounting.dal.mediators.interfaces.ClientMed;
import tap.execounting.dal.mediators.interfaces.ContractMed;
import tap.execounting.dal.mediators.interfaces.EventMed;
import tap.execounting.dal.mediators.interfaces.PaymentMed;
import tap.execounting.data.Const;
import tap.execounting.data.ContractState;
import tap.execounting.data.EventState;
import tap.execounting.entities.*;
import tap.execounting.services.ContractByClientNameComparator;
import tap.execounting.util.DateUtil;

import java.util.*;
import java.util.Map.Entry;

import static tap.execounting.data.ContractState.*;
import static tap.execounting.util.DateUtil.*;
import static tap.execounting.util.Trans.contractsToClients;

public class ContractMediator extends ProtoMediator<Contract> implements ContractMed {

    @Inject
    private EventMed eventMed;

    @Inject
    private PaymentMed paymentMed;

    @Inject
    private ClientMed clientMed;

    public ContractMediator(){clazz=Contract.class;}

    public Contract getUnit() {
        try {
            return unit;
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public ContractMed setUnit(Contract unit) {
        this.unit = unit;
        return this;
    }

    public ContractMed setUnitId(int id) {
        unit = dao.find(Contract.class, id);
        return this;
    }

    public String getTeacherName() {
        String name = unit.getTeacher().getName();
        return name == null ? Const.CONTRACT_TEACHER_NOT_DEFINED : name;
    }

    public String getClientName() {
        return unit.getClient().getName();
    }

    public EventType getEventType() {
        try {
            EventType et;
            // way1
            et = unit.getEventType();
            // way2
            // et = dao.find(EventType.class, unit.getTypeId());
            return et;
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ContractType getContractType() {
        try {
            ContractType ct;
            // way1
            ct = unit.getContractType();
            // way2
            // ct = dao.find(ContractType.class, unit.getContractTypeId());
            return ct;
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Date getDate() {
        try {
            return unit.getDate();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getLessonsNumber() {
        try {
            return unit.getEventsNumber();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getPrice() {
        try {
            return unit.getMoney();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return 0;
        }

    }

    // #LAZYINITException from 5 March 2013
    public ContractState getContractState() {
        try {
            return unit.getState();
        } catch (LazyInitializationException lazy) {
            lazy.printStackTrace();
            unit = dao.find(Contract.class, unit.getId());
            return unit.getState();
        }
    }

    /**
     * Returns string representation of contract state.
     * If contract has freeze dates -- this should be displayed.
     * All this is done to reduce the logic inside contract, and pages.
     * FURTHER candidate for internationalization
     *
     * @shrt if short is false -- then for the frozen string you will receive full info.
     * @return
     */
    public String getContractStateString(boolean $short) {
        ContractState cs = unit.getState();
        String freezeDateString = "";

        if(unit.hasFreezeDates()) {
            String freeze = format("dd MMM YY", unit.getDateFreeze()),
                    unfreeze = format("dd MMM YY", unit.getDateUnfreeze());
            String formattingPattern =
                    cs == ContractState.frozen ?
                            Const.CONTRACT_frozen_string_with_dates :
                            Const.CONTRACT_future_frozen_string_with_dates;

            freezeDateString = String.format(formattingPattern, freeze, unfreeze);
        }

        switch (cs) {
            case frozen:
                if ($short)
                    return frozen.toString();
                return freezeDateString;
            case active:
                return active.toString() + freezeDateString;
            case canceled:
                return canceled.toString() + freezeDateString;
            case complete:
                return complete.toString() + freezeDateString;
            default:
                throw new UnsupportedOperationException("such state is not supported " + cs.toString());
        }
    }

    public int getBalance() {
        try {
            return unit.getBalance();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return 0;
        }
    }


    public List<Event> getEvents() {
        try {
            List<Event> events;
            events = unit.getEvents();
            return events;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getRemainingLessons() {
        return unit.getEventsRemain();
    }

    public List<Payment> getPayments() {
        try {
            return unit.getPayments();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public EventType loadEventType(int id) {
        return dao.find(EventType.class, id);
    }

    /**
     * This method does all the planning activity for single contract. It plans
     * all available events, corresponding to the contract event schedule, which
     * corresponds to the teacher schedule.
     * Today (16 jan 2013) I have added contract type check.
     * If contract is free from teacher or it is free from school.
     *
     * TODO
     * new logic:
     * First generate a list of free dates for new events
     * then get a list of events to plan, if it is not enough,
     * create new undated events.
     * Then apply those free dates to that list of events,
     * and save them
     */
    public void doPlanEvents(Date dateOfFirstEvent) {
        refreshUnit();

        if(!unit.getSchedule().hasWorkingDay())
            return;

        if(dateOfFirstEvent == null)
            dateOfFirstEvent = floor();
        List<Date> free_dates = unit.generateFreeDates(dateOfFirstEvent);
        List<Event> events_to_plan = unit.getEventsToPlan();

        if(free_dates.size() != events_to_plan.size())
            throw new IllegalArgumentException("Free dates size not equal to the size of events to plan");

        Teacher host = dao.find(Teacher.class, unit.getTeacherId());

        for (int i = 0; i < events_to_plan.size(); i++) {
            Event e = events_to_plan.get(i);
            Date d = free_dates.get(i);
            int school_id = host.getScheduleDay( dayOfWeekRus(d) );
            e.setFacilityId(school_id);
            e.setHostId(host.getId());
            e.setRoomId( dao.find(Facility.class, school_id).getRooms().get(0).getRoomId() );
            e.setDate(d);
            eventMed.save(e);
        }
    }

    // When client is no longer doing any study, he is gone, and all his money
    // remain on the school account. So we should write them as profit, but we
    // don't share them with teacher.
    // Today (20.10.2012) the most simple and consistent way to this is:
    // 1) Delete all planned events.
    // 2) Add new event with 'Writeoff' header, whose price is equal to the
    // balance of the contract.
    // 3) Make a comment '$N events were written off, $WriteoffDate.
    public void doWriteOff() {

        // TODO WRITEOFF
        // Delete all planned events
        doRemovePlannedEvents();

        // 'Writeoff' events
        EventType writeOffEventType = loadWriteOffType();

        Event e = new Event();
        e.setTypeId(writeOffEventType.getId());
        e.setHostId(Const.WriteOffTeacherId);
        e.setState(EventState.complete);
        e.setComment("[" + writeOffEventType.getTitle() + "]");
        e.setFacilityId(Const.WriteOffFacilityId);
        e.setRoomId(Const.WriteOffRoomId);
        dao.create(e);
        e.getContracts().add(unit);
        dao.update(e);
        unit.setCanceled(true);
        unit.getEvents().add(e);

        // Comment
        String comment = "Списано " + writeOffEventType.getPrice() + " р.";

        if (unit.getComment() != null)
            comment = unit.getComment().concat(comment);
        unit.setComment(comment);
        dao.update(unit);
    }

    private EventType loadWriteOffType() {
        String title = Const.WriteOffPrefix + " : " + unit.getBalance();
        EventType writeOff = dao.findUniqueWithNamedQuery(EventType.WITH_TITLE,
                ChainMap.with("title", title));
        if (writeOff == null) {
            writeOff = new EventType();
            writeOff.setPrice(unit.getBalance());
            writeOff.setShareTeacher(0);
            writeOff.setTitle(title);
            writeOff.setDeleted(true);
            dao.create(writeOff);
        }

        return writeOff;
    }

    // Goal of this is to return all the remaining money - (complete cost of the contract) * 0.15
    // To the client
    // This is done through the special event which sends money to the
    // nonexisting teacher, and sends remaining to the school.
    public void doMoneyback() throws Exception {
        String exceptionMessage = "Баланс на договоре клиента, меньше 15% от стоимости договора. Операция возврата невозможна, попробуйте списание.";
        // Amount which school will receive is 15%, so check, that it exists.
        int completeCost = unit.getMoney();
        int schoolShare = (completeCost * 15) / 100;
        int balance = unit.getBalance();
        if (balance < schoolShare)
            throw new Exception(exceptionMessage);
        // Remove all planned events
        doRemovePlannedEvents();
        int clientShare = balance - schoolShare;
        EventType type = loadMoneybackType(schoolShare, clientShare);
        Event e = new Event();
        e.setTypeId(type.getId());
        e.setHostId(Const.WriteOffTeacherId);
        e.setState(EventState.complete);
        e.setComment("[" + type.getTitle() + "]");
        e.setFacilityId(Const.WriteOffFacilityId);
        e.setRoomId(Const.WriteOffRoomId);
        dao.create(e);
        e.getContracts().add(unit);
        dao.update(e);
        unit.setCanceled(true);
        unit.getEvents().add(e);

        // Comment
        String comment = "Школе: " + type.getSchoolMoney() + " р." + "\n" +
                "Клиенту: " + type.getShareTeacher() + " р.";

        if (unit.hasComment())
            comment = unit.getComment().concat(comment);
        unit.setComment(comment);
        dao.update(unit);
    }

    private EventType loadMoneybackType(int schoolShare, int clientShare) {
        String title = Const.MoneybackPrefix + " : " + clientShare + "|" + schoolShare;
        EventType moneyback = dao.findUniqueWithNamedQuery(EventType.WITH_TITLE,
                ChainMap.with("title", title));
        if (moneyback == null) {
            moneyback = new EventType();
            moneyback.setPrice(unit.getBalance());
            moneyback.setShareTeacher(clientShare);
            moneyback.setTitle(title);
            moneyback.setDeleted(true);
            dao.create(moneyback);
        }

        return moneyback;
    }

    public ContractMed doFreeze(Date freeze, Date unfreeze) {
        doRemovePlannedEvents();
        unit.setDateFreeze(freeze);
        unit.setDateUnfreeze(unfreeze);
        dao.update(unit);
        doPlanEvents(unfreeze);
        return this;
    }

    public ContractMed doUnfreeze() {
        unit.setDateFreeze(null);
        unit.setDateUnfreeze(null);
        update();
        doPlanEvents(new Date());
        update();
        return this;
    }

    private void update() {
        dao.update(unit);
    }

    /**
     * Removes planned events from contract
     * except shifted events.
     * TODO track useges. It was used in do plan events. Maybe it is not needed anymore.
     */
    private void doRemovePlannedEvents() {
        // Delete all planned events
        List<Event> events = unit.getEvents();
        Event event;
        for (int i = events.size();--i >= 0;) {
            event = events.get(i);
            if (event.isPlanned() && !event.isShift())
                removeEvent(event);
        }
    }

    /**
     * Removes element from contract with care.
     * First removes contract from event.
     * Updates event.
     * Then removes event from contract.
     * Then deletes events, if there is no other contracts.
     * Updates contract.
     *
     * @param e -- event to remove
     */
    private void removeEvent(Event e) {
        List<Contract> eventContracts = e.getContracts();
        for (int j = eventContracts.size(); --j >= 0;)
            if (eventContracts.get(j).getId() == unit.getId())
                eventContracts.remove(j);
        dao.update(e);
        for (int i = unit.getEvents().size(); --i >= 0;)
            if (e.getId() == unit.getEvents().get(i).getId()) {
                unit.getEvents().remove(i);
                break;
            }

        if (e.getContracts().size() == 0)
            dao.delete(Event.class, e.getId());

        dao.update(unit);
    }

    private void refreshUnit() {
        unit = dao.find(unit.getClass(), unit.getId());
    }

    // group methods
    private Map<String, Object> appliedFilters;

    private Map<String, Object> getAppliedFilters() {
        if (appliedFilters == null)
            appliedFilters = new HashMap<>(5);
        return appliedFilters;
    }

    public List<Contract> getGroup() {
        if (cache == null)
            load();
        return cache;
    }

    public List<Contract> getGroup(boolean reset) {
        List<Contract> innerCache = getGroup();
        if (reset)
            reset();
        return innerCache;
    }

    private void load() {
        cache = dao.findWithNamedQuery(Contract.ALL);
        appliedFilters = new HashMap<>();
    }

    public ContractMed setGroup(List<Contract> group) {
        cache = group;
        return this;
    }

    public ContractMed setGroupFromClients(List<Client> clients) {
        this.cache = new ArrayList<>();

        for (Client c : clients)
            this.cache.addAll(c.getContracts());

        return this;
    }

    public List<Client> getClients() {
        return contractsToClients(getGroup());
    }

    public List<Contract> getAllContracts() {
        return dao.findWithNamedQuery(Contract.ALL);
    }

    public ContractMed reset() {
        cache = null;
        appliedFilters = null;
        return this;
    }

    // intersection operation
    public ContractMed retain(List<Contract> contracts) {
        List<Contract> cache = getGroup();
        boolean found;
        // use this to store cached element id
        int ci;
        for (int i = cache.size() - 1; i >= 0; i--) {
            // Get id of i-th element and start search in contracts
            found = false;
            ci = cache.get(i).getId();
            for (Contract contract : contracts)
                if (ci == contract.getId()) {
                    // If found -- break
                    found = true;
                    break;
                }
            // Remove element with id ci from cache, if it is not found in contracts
            if (!found)
                cache.remove(i);
        }
        return this;
    }

    public String getFilterState() {
        StringBuilder sb = new StringBuilder();
        for (Entry<String, Object> entry : getAppliedFilters().entrySet())
            sb.append(entry.getKey() + ": " + entry.getValue().toString()
                    + "\n");
        return sb.toString();
    }

    public ContractMed retainByClient(Client c) {
        getAppliedFilters().put("Client", c);
        if (cache == null)
            cache = dao.findWithNamedQuery(Contract.WITH_CLIENT,
                    ChainMap.with("clientId", c.getId()));
        else {
            List<Contract> cache = getGroup();
            Contract con;

            for (int i = cache.size() - 1; i >= 0; i--) {
                con = cache.get(i);
                if (con.getClientId() == c.getId())
                    continue;
                cache.remove(i);
            }
        }
        return this;
    }

    public ContractMed retainByTeacher(Teacher t) {
        getAppliedFilters().put("Teacher", t);
        if (cacheIsNull())
            loadByTeacherId(t.getId());
        else {
            List<Contract> cache = getGroup();
            for (int i = cache.size() - 1; i >= 0; i--)
                if (cache.get(i).getTeacherId() != t.getId())
                    cache.remove(i);
        }
        return this;
    }

    private void loadByTeacherId(int id) {
        cache = dao.findWithNamedQuery(Contract.WITH_TEACHER,ChainMap.with("teacherId", id));
    }

    /**
     * Retains those contracts which match the text.
     *
     * @param state
     * @return
     */
    public ContractMed retainByState(ContractState state) {
        /*
        Logic:
        It is simple intersection operation.
        But! Frozen state, and canceled state are exist in the contract properties itself,
        that is why they have their own methods, to optimize speed etc.
         */
        getAppliedFilters().put("ContractState", state);

        if (state == frozen)
            return retainFrozen();
        if (state == canceled)
            return retainCanceled();

        List<Contract> cache = getGroup();
        // save current unit;
        Contract tempUnit = getUnit();

        for (int i = cache.size() - 1; i >= 0; i--)
            if (setUnit(cache.get(i)).
                    getContractState() != state)
                cache.remove(i);

        // restore unit
        setUnit(tempUnit);
        return this;
    }

    private ContractMed retainCanceled() {
        if(cacheIsNull())
            loadCanceled();
        else
            for(int i = cache.size() - 1; i >= 0; i--)
                if(!cache.get(i).isCanceled())
                    cache.remove(i);
        return this;
    }

    private void loadCanceled() {
        cache = dao.findWithNamedQuery(Contract.CANCELED);
    }

    private ContractMed retainFrozen() {
        if (cacheIsNull())
            loadFrozen();
        else
            for (int i = cache.size()-1; i >= 0; i--) {
                if(!cache.get(i).isFrozen())
                    cache.remove(i);
            }
        return this;
    }

    private void loadFrozen() {
        cache = dao.findWithNamedQuery(Contract.FROZEN, ChainMap.with("now", new Date()));
    }

    public ContractMed filterByState(ContractState state) {
        getAppliedFilters().put("ContractState", state);
        List<Contract> cache = getGroup();

        // save current unit;
        Contract tempUnit = getUnit();
        for (int i = cache.size() - 1; i >= 0; i--){
            setUnit(cache.get(i));
            if (getContractState() == state)
                cache.remove(i);
        }

        // restore unit
        setUnit(tempUnit);
        return this;
    }

    public ContractMed retainByDates(Date date1, Date date2) {
        getAppliedFilters().put("Date1", date1);
        getAppliedFilters().put("Date2", date2);

        retainByDatesEntry(cache, date1, date2);
        return this;
    }

    public ContractMed retainByPlannedPaymentsDate(Date date1, Date date2) {
        getAppliedFilters().put("PlannedPaymentsDate1", date1);
        getAppliedFilters().put("PlannedPaymentsDate2", date2);
        getGroup();
        cache = paymentMed.setGroupFromContracts(cache).retainByState(true).retainByDatesEntry(date1, date2).getContracts();

        return this;
    }

    public ContractMed retainByContractType(int type) {
        getAppliedFilters().put("ContractTypeId", type);
        List<Contract> cache = getGroup();
        Contract con;

        for (int i = cache.size(); --i >= 0;) {
            con = cache.get(i);
            if (con.getContractTypeId() == type)
                continue;
            cache.remove(i);
        }
        return this;
    }

    /**
     * Removes completed, canceled and frozen contracts
     * Retains only those contracts which have less or equal remaining lessons.
     *
     * @param remainingLessons upper bound to remain in the group
     * @return
     */
    public ContractMed retainExpiring(int remainingLessons) {
        filterByState(ContractState.complete);
        filterByState(ContractState.frozen);
        filterByState(ContractState.canceled);
        pushCriteria("RemainingLessons", remainingLessons);
        List<Contract> cache = getGroup();

        for (int i = cache.size(); --i >= 0;)
            if (cache.get(i).getEventsRemain() > remainingLessons)
                cache.remove(i);
        return this;
    }

    public ContractMed removeComplete() {
        getAppliedFilters().put("Complete", false);
        List<Contract> cache = getGroup();
        for (int i = cache.size(); --i >= 0;)
            if (cache.get(i).isComplete())
                cache.remove(i);
        return this;
    }

    public Integer countGroupSize() {
        try {
            return cache.size();
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public int countNotTrial() {
        int count = 0;
        for (Contract con : cache)
            if (con.notTrial())
                count++;
        return count;
    }

    public int countCertificateMoney() {
        int sum = 0;
        for (Contract c : getGroup())
            sum += c.getGiftMoney();
        return sum;
    }

    // Now it this does not retainByState anything
    public Integer count(ContractState state) {
        int count = 0;
        for (Contract c : getGroup())
            if (c.getState() == state)
                count++;
        return count;
    }

    // Sorting
    public ContractMed sortByDate(boolean ascending) {
        DateUtil.sort(getGroup(), !ascending);
        return this;
    }

    public ContractMed sortByClientName() {
        Collections.sort(getGroup(), new ContractByClientNameComparator());
        return this;
    }

    public List<ContractType> loadContractTypes() {
        return dao.findWithNamedQuery(ContractType.ALL);
    }

    public int countTrial() {
        getGroup();
        int sum = 0;
        for(Contract c : cache)
            if (c.isTrial())
                sum++;
        return sum;
    }

    public ContractMed filterByContractType(int contractTypeCode) {
        List<Contract> cache = this.cache;
        for(int i = cache.size();--i>=0;)
            if(cache.get(i).getContractTypeId() == contractTypeCode)
                cache.remove(i);
        return this;
    }

    public ContractMed retainFirstByDate() {
        sortByDate(true);

        for(int i = cache.size();--i>=1;)
            cache.remove(i);

        return this;
    }
}
