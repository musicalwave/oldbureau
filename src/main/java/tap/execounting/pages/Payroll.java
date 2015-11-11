package tap.execounting.pages;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import tap.execounting.dal.mediators.interfaces.ContractMed;
import tap.execounting.dal.mediators.interfaces.EventMed;
import tap.execounting.dal.mediators.interfaces.TeacherMed;
import tap.execounting.data.Const;
import tap.execounting.data.EventState;
import tap.execounting.entities.*;
import tap.execounting.services.Authenticator;
import tap.execounting.util.DateUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static tap.execounting.util.DateUtil.ceil;
import static tap.execounting.util.DateUtil.floor;

/**
 * @author sharp.maestro@gmail.com
 * This class calculates month salary for employees
 * Source of events and contracts is produced by getContracts.
 * Sequence is following:
 * 1) getContracts() is called
 *     1. Events list is produced by calling raw()
 *     2. if retainByState option is on -- retainByState() is called, which removes unneeded events
 *     3. Then events are translated into contracts
 * 2) other calculations are performed Also if retainByState is
 *    off -- we count events which are free from school
 *    08.02.2013 Artem asked me to prevent administrators from viewing payrolls of teachers
 *    ok, I will throw an exception for them
 */
@Import(stylesheet = "context:/css/payroll.css")
public class Payroll {

    @Inject
    @Property
    private TeacherMed tM;
    @Inject
    private EventMed eM;
    @Inject
    private ContractMed cM;
    @Inject
    private Authenticator auth;
    private TeacherAddition addition;
    @Property
    private Date dateOne;
    @Property
    private Date dateTwo;
    private boolean filtration;
    @Property
    private Contract contract;
    private int iteration;
    private int specialIteration;
    private int trialIteration;
    private int freeFromSchoolIteration;
    private int totalMoney;
    private int trialMoney;
    private int specialMoney;
    private int freeFromSchoolMoney;
    /**
     * From 28.11.12 -- this method actually also caches the contracts if
     * cacheMode==true.
     * StandartContracts makes cachemode true, FreeFromSchoolContracts
      * makes cachemode==false.
      */
    private boolean cacheMode;
    private List<Contract> cache = null;

    void onActivate(int teacherId) {
        onActivate(teacherId, null, null, filtration);
    }

    boolean onActivate(int teacherId, String one, String two, boolean filtration) {
        tM.setId(teacherId);
        setDates(one, two);
        this.filtration = filtration;
        return true;
    }

    Object[] onPassivate() {
        return new Object[]{tM.getId(), dateOne, dateTwo, filtration};
    }

    void authCheck() {
        if (filtration && !auth.getLoggedUser().isTop())
            throw new IllegalAccessError("Please go back");
    }

    boolean userIsAllowedToSeeEventsPrices() {
        return auth.getLoggedUser().isTop();
    }

    void setupRender() {
        authCheck();

        iteration = 0;
        addition = tM.getAddition();
    }

    private Date toDate(String s) throws ParseException {
        return DateUtil.parse("dd.MM.yyyy", s);
    }

    private void setDates(String oneS, String twoS) {
        try {
            Date one = toDate(oneS);
            Date two = toDate(twoS);
            dateOne = one == null ? floor() : floor(one);
            dateTwo = two == null ? ceil() : ceil(two);
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new IllegalArgumentException("incorrect date input");
        }
    }

    public boolean getFiltration() {
        return filtration;
    }

    public int getIteration() {
        cM.setUnitId(contract.getId());
        totalMoney += getLessonPrice() * getLessonsNumber();
        return ++iteration;
    }

    public int getTrialIteration() {
        cM.setUnitId(contract.getId());
        trialMoney += getLessonPrice() * getLessonsNumber();
        return ++trialIteration;
    }

    public int getSpecialIteration() {
        cM.setUnitId(contract.getId());
        specialMoney += getLessonPrice() * getLessonsNumber();
        return ++specialIteration;
    }

    public int getFreeFromSchoolIteration() {
        cM.setUnitId(contract.getId());
        freeFromSchoolMoney += getLessonPrice() * getLessonsNumber();
        return ++freeFromSchoolIteration;
    }

    public Date getToday() {
        return new Date();
    }

    public String getField1() {
        try {
            return addition.getField_1();
        } catch (NullPointerException e) {
            return Const.NO_DATA;
        }
    }

    public String getField2() {
        try {
            return addition.getField_2();
        } catch (NullPointerException e) {
            return Const.NO_DATA;
        }
    }

    public String getField3() {
        try {
            return addition.getField_3();
        } catch (NullPointerException e) {
            return Const.NO_DATA;
        }
    }

    public String getField4() {
        try {
            return addition.getField_4();
        } catch (NullPointerException e) {
            return Const.NO_DATA;
        }
    }

    public String getField5() {
        try {
            return addition.getField_5();
        } catch (NullPointerException e) {
            return Const.NO_DATA;
        }
    }

    public String getFromField() {
        try {
            String pre = addition.getField_4();
            pre = pre.substring(pre.indexOf(" от "));
            return pre;
        } catch (NullPointerException e) {
            return Const.NO_DATA;
        }
    }

    public List<Contract> getContracts() {
        if (cacheMode && cache != null)
            return cache;
        // Init
        List<Event> source = raw();

        // Filter those events that are free from teacher
        filterEventsFreeFromTeacher(source);
        if (getFiltration())
            filter(source);

        // Translate to contracts, which are viewable
        List<Contract> contracts = toContracts(source);

        if (cacheMode)
            cache = contracts;

        return contracts;
    }

    public List<Contract> getStandartContracts() {
        cacheMode = true;
        List<Contract> filtered = Contract.cleanList();
        List<Contract> localCache = getContracts();

        for (Contract c : localCache)
            if (c.getContractTypeId() == ContractType.Standard)
                filtered.add(c);
        return filtered;
    }

    public List<Contract> getTrialContracts() {
        List<Contract> filtered = Contract.cleanList();
        List<Contract> localCache = getContracts();
        for (Contract c : localCache)
            if (c.getContractTypeId() == ContractType.Trial)
                filtered.add(c);
        return filtered;
    }

    public List<Contract> getSpecialContracts() {
        List<Contract> filtered = Contract.cleanList();
        List<Contract> localCache = getContracts();
        for (Contract c : localCache)
            if (c.getContractTypeId() == ContractType.Special)
                filtered.add(c);

        return filtered;
    }

    public List<Contract> getFreeFromSchoolContracts() {
        List<Contract> filtered = Contract.cleanList();
        List<Contract> localCache = getContracts();
        for (Contract c : localCache)
            if (c.getContractTypeId() == ContractType.FreeFromSchool)
                filtered.add(c);

        cacheMode = false;
        cache = null;

        return filtered;
    }

    private List<Event> raw() {
        // step1
        eM.reset();
        List<Event> list1 = eM.retainByDatesEntry(dateOne, dateTwo).retainByTeacher(tM.getUnit())
                .retainByState(EventState.complete).getGroup();
        eM.reset();

        // Include events failed by client
        eM.retainByDatesEntry(dateOne, dateTwo).retainByTeacher(tM.getUnit())
                .retainByState(EventState.failedByClient).getGroup().addAll(list1);
        return eM.getGroup();
    }

    private void filter(List<Event> events) {
		/*
		 * In this part of the retainByState we will remove all events of trial
		 * contracts and where type contains "раз"
		 */
        Contract c;
        for (int i = events.size(); --i >= 0;) {
            List<Contract> cts = events.get(i).getContracts();
            for (int j = cts.size(); --j >= 0;) {
                c = cts.get(j);
                if (c.getContractTypeId() == ContractType.Trial
                        || c.getContractTypeId() == ContractType.Special
                        || c.getEventType().getTitle().contains("раз"))
                    cts.remove(j);
            }
            if (cts.size() == 0)
                events.remove(i);
        }
    }

    private void filterEventsFreeFromTeacher(List<Event> events){
        /*
		 * First do the event level filtration. If event is free, either from
		 * school or from teacher -- it should be filtered
		 */
        for (int i = events.size() - 1; i >= 0; i--)
            if (events.get(i).isFreeFromTeacher())
                events.remove(i);
    }

    List<Contract> toContracts(List<Event> source) {
        // First step is to fill the contracts with the events
        List<Contract> contracts = new ArrayList<Contract>();
        while (source.size() > 0) {
            // Initial event?
            Event init = source.get(source.size() - 1);
            // Temporary contract group
            List<Contract> cts = init.getContracts();

            // First contract to join this party?
            Contract c = new Contract();
            // Add this contract to the group
            contracts.add(c);

            // Create link for temporal storage
            Contract t = cts.get(cts.size() - 1);

            // Remove it from temporal group
            cts.remove(cts.size() - 1);
            // If temporary group is zero-size -- remove it from the source
            if (cts.size() == 0)
                source.remove(source.size() - 1);
            // Set up new contract which will be added to the result
            c.setId(t.getId());
            c.setContractTypeId(t.getContractTypeId());

            // Look ma, no hands. That is the bitchy place. Here I change the
            // order of everything. If school did event with type that differ
            // from what they have in contract -- the type here is taken from
            // the event
            // we had: c.setTypeId(t.getTypeId());
            // and now we got:
            c.setTypeId(init.getTypeId());
            c.getEvents().add(init);

            // Comment is another crutch in the school cruthed business logic.
            // If event has comment about substution ("замена") --> we should
            // show this in PayRoll
            if (eventHasSubstitutionComment(init)) {
                c.setComment(init.getComment());
                continue;
            }

            for (int i = source.size() - 1; i >= 0; i--) {
                if (eventHasSubstitutionComment(source.get(i)))
                    continue;
                // Take the next contracts group
                cts = source.get(i).getContracts();
                // For each contract in that group
                for (int j = cts.size() - 1; j > -1; j--)
                    // Another change, where I am not sure. That could make
                    // mistakes in the future, possible unprocessed contracts.
                    // Earlier we had only first part of the condition
                    if (cts.get(j).getId() == c.getId()
                            && cts.get(j).getTypeId() == init.getTypeId()) {
                        c.getEvents().add(source.get(i));
                        cts.remove(j);

                        if (cts.size() == 0)
                            source.remove(i);
                    }
            }
        }

        // Step 2. Loading EventTypes
        for (Contract c : contracts)
            c.setEventType(eM.loadEventType(c.getTypeId()));

        // Here could be grouping of events by contract client name
        // Step three is to separate the probation included events
        // Only for teachers-probationers
        Date prob = tM.getUnit().getProbationEndDate();
        if (prob != null && inProbation(dateOne)) {
            List<Contract> probContracts = new ArrayList<Contract>();
            for (int i = contracts.size() - 1; i >= 0; i--) {
                // setup
                Contract c = contracts.get(i);
                Contract pc = probationized(c);

                for (int j = c.getEvents().size() - 1; j >= 0; j--) {
                    if (inProbation(c.getEvents().get(j).getDate())) {
                        pc.getEvents().add(c.getEvents().get(j));
                        c.getEvents().remove(j);
                    }
                }

                // if contract with probation have events it should be added to
                // the stack
                if (pc.getEvents().size() > 0)
                    probContracts.add(pc);
            }
            contracts.addAll(probContracts);
        }
        // Step four. Removing empty contracts
        for (int i = contracts.size() - 1; i >= 0; i--)
            if (contracts.get(i).getEvents().size() == 0)
                contracts.remove(i);

        return contracts;
    }

    private boolean eventHasSubstitutionComment(Event event) {
        String substitutionComment = "замен";

        if (event.hasComment()) {
            String lowercase = event.getComment().toLowerCase();
            boolean contains = lowercase.contains(substitutionComment);
            return contains;
        }
        return false;
    }

    /**
     * Adds probation prices and title to the returned contract
     *
     * @param input
     * @return
     */
    private Contract probationized(Contract input) {

        Contract out = new Contract();
        out.setId(input.getId());
        EventType inType = input.getEventType();
        EventType pType = new EventType();
        pType.setPrice(inType.getPrice());
        pType.setShareTeacher(inType.getShareTeacher());
        pType.setId(inType.getId());
        out.setEventType(pType);
        out.setContractTypeId(input.getContractTypeId());

        // Title
        pType.setTitle("Стаж. " + inType.getTitle());
        // Lowering the teacher's money
        EventTypeAddition pa = eM.loadProbation(pType.getId());
        if (pa != null)
            pType.setShareTeacher(pType.getShareTeacher()
                    - pa.getAdditionValue());

        return out;
    }

    private boolean inProbation(Date d) {
        Date prob = tM.getUnit().getProbationEndDate();
        if (prob == null)
            return false;
        return !d.after(prob);
    }

    public String getName() {
        return cM.getClientName();
    }

    public String getType() {
        return contract.getEventType().getTypeTitle();
    }

    public String getComment() {
        return contract.getComment();
    }

    public int getLessonPrice() {
        if (userIsAllowedToSeeEventsPrices())
            return contract.getEventType().getShareTeacher();
        return 0;
    }

    public int getLessonsNumber() {
        return contract.getEvents().size();
    }

    public int getTotalMoney() {
        return totalMoney + trialMoney + specialMoney + freeFromSchoolMoney;
    }

    public int getTrialMoney() {
        return trialMoney;
    }

    public int getSpecialMoney() {
        return specialMoney;
    }

    public int getFreeFromSchoolMoney() {
        return freeFromSchoolMoney;
    }

    // Total money -- is the only thing that gets taxed.
    // Trial money, and special money -- are not.
    public int getTax() {
        return totalMoney * 13 / 100;
    }

    public int getTaxed() {
        return getTotalMoney() - getTax();
    }
}
