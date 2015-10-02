package tap.execounting.dal.mediators;

import java.util.*;
import java.util.Map.Entry;

import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.ChainMap;
import tap.execounting.dal.mediators.interfaces.*;
import tap.execounting.data.ClientState;
import tap.execounting.data.ContractState;
import tap.execounting.entities.*;
import tap.execounting.security.AuthorizationDispatcher;
import tap.execounting.services.Authenticator;
import tap.execounting.util.DateUtil;

import static tap.execounting.util.DateUtil.fromNowPlusDays;
import static tap.execounting.util.DateUtil.retainByDatesEntry;
import static tap.execounting.util.Trans.*;
import static tap.execounting.data.ContractState.active;
import static tap.execounting.data.ContractState.frozen;

public class ClientMediator extends ProtoMediator<Client> implements ClientMed {
    @Inject
    private ContractMed contractMed;
    @Inject
    private PaymentMed paymentMed;
    @Inject
    private EventMed eventMed;
    @Inject
    private Authenticator authenticator;
    @Inject
    private AuthorizationDispatcher dispatcher;

    public ClientMediator() {
        clazz = Client.class;
    }

    public ClientMed setUnit(Client unit) {
        this.unit = unit;
        return this;
    }

    public ClientMed setUnitById(int id) {
        this.unit = dao.find(Client.class, id);
        return this;
    }

    public void delete(Client c) {
        // AUTHORIZATION MOUNT POINT DELETE
        if (dispatcher.canDeleteClients())
            if (c.getContracts().size() > 0)
                // TODO JAVASCRIPT ALERT MOUNT POINT
                throw new IllegalArgumentException(
                        "У данного клиента заключены с вами договора, пожалуйста сначала удалите их.");
            else {
                c.setName(c.getName() + " [deleted]");
                dao.update(c);
                dao.delete(Client.class, c.getId());
            }
    }

    public void comment(String text, long time) {
        // if text=="null" (a string) comment will be deleted
        // TODO upgrade later to support multiple comments for one client
        Comment c = getComment();
        if (c == null) {
            if (!text.equals("null")) {
                c = new Comment(Comment.ClientCode, authenticator
                        .getLoggedUser().getId(), unit.getId());
                c.setText(text);
                c.setDate(new Date(time));
                dao.create(c);
            }
        } else {
            if (text.equals("null"))
                dao.delete(Comment.class, c.getId());
            else {
                c.setText(text);
                c.setDate(new Date(time));
                dao.update(c);
            }
        }

    }

    public Comment getComment() {
        return dao.findUniqueWithNamedQuery(Comment.BY_CLIENT_ID,
                ChainMap.with("id", unit.getId()));
    }

    public void setClientComment(String comment) {
        unit.setComment(comment);
        unit.setCommentDate(new Date());
        dao.update(unit);
    }

    public ClientMed retainByManagerId(int managerId) {
        if (cacheIsNull())
            loadByManagerId(managerId);
        else for (int i = cache.size(); --i >= 0;)
            if (cache.get(i).getManagerId() != managerId)
                cache.remove(i);
        return this;
    }

    private void loadByManagerId(int managerId) {
        cache = dao.findWithNamedQuery(Client.BY_MANAGER_ID, ChainMap.with("managerId", managerId));
    }

    public boolean hasContracts() {
        try {
            return getContracts().size() > 0;
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            return false;
        }
    }

    /**
     * Will return contracts of the current unit (Client)
     * @return
     */
    public List<Contract> getContracts() {
        try {
            return unit.getContracts();
        } catch (Exception e) {
            e.printStackTrace();
            setUnitById(unit.getId());
            return unit.getContracts();
        }
    }

    public boolean hasActiveContracts() {
        boolean response = contractMed.setGroup(getContracts()).count(active) > 0;
        contractMed.reset();
        return response;
    }

    /**
     * @return list of active contracts of the current unit
     */
    public List<Contract> getActiveContracts() {
        try {
            return contractMed.setGroup(new ArrayList(getContracts()))
                    .retainByState(ContractState.active).getGroup(true);
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            return null;
        }
    }

    /**
     * @return true if client has frozen contracts
     */
    public boolean hasFrozenContracts() {
        boolean response = contractMed.setGroup(getContracts()).count(frozen) > 0;
        contractMed.reset();
        return response;
    }

    public ClientMed becameContinuers(Date date1, Date date2) {
        List<Client> list = getGroup();
        for (int i = list.size(); --i >= 0;) {
            setUnit(list.get(i));
            if (!becameContinuer(date1, date2))
                list.remove(i);
        }
        return this;
    }

    public ClientMed becameNovices(Date date1, Date date2) {
        List<Client> list = getGroup();
        for (int i = list.size(); --i >= 0;) {
            setUnit(list.get(i));
            if (!becameNovice(date1, date2))
                list.remove(i);
        }
        return this;
    }

    public ClientMed becameTrials(Date date1, Date date2) {
        List<Client> list = getGroup();
        for (int i = list.size(); --i >= 0;) {
            setUnit(list.get(i));
            if (!becameTrial(date1, date2))
                list.remove(i);
        }
        return this;
    }

    /**
     * Glen did not became trial if he has trial contracts before lower bound.
     * @param lowerBound earliest date
     * @param upperBound lastest date
     * @return whether is client purchased trial contract in this period
     */
    public boolean becameTrial(Date lowerBound, Date upperBound) {
        if(lowerBound.after(upperBound)) throw new IllegalArgumentException("Lower bound date should be before upper bound date");
        if(!hasContracts()) return false;

        List<Contract> cache = new ArrayList<>(getContracts());
        contractMed.setGroup(new ArrayList<>(cache)).retainByDates(null, upperBound);
        int countBeforeDate2 = contractMed.countTrial();
        // That means that he did not have contracts in that period at all
        if (countBeforeDate2 == 0)
            return false;

        // TODO ask Ivan, should we count trial in previous periods
        contractMed.setGroup(new ArrayList<>(cache)).retainByDates(null, lowerBound);
        int countBeforeDate1 = contractMed.countTrial();
        // If he already has contracts before date1 -- he already tried
        // something
        if (countBeforeDate1 == countBeforeDate2)
            return false;
        int notTrial = contractMed.countNotTrial();
        // If he has more contracts in that period, than he have usual, nontrial
        // contracts
        // That means he have trial contracts here
        if (countBeforeDate2 > notTrial){

            // Filter contracts, to retain only relevant
            contractMed.setGroup(getContracts()).retainByDates(lowerBound, upperBound).retainByContractType(ContractType.Trial);

            return true;
        }
        return false;
    }

    /**
     * @param lowerBound
     * @param upperBound
     * @return true if client (unit) became novice between this two dates
     */
    public boolean becameNovice(Date lowerBound, Date upperBound) {
        if(!hasContracts()) return false;

        List<Contract> cache = getContracts();

        int countBeforeDate2 = contractMed.setGroup(new ArrayList<>(cache)).retainByDates(null, upperBound).countNotTrial();
        int countBeforeDate1 = contractMed.setGroup(new ArrayList<>(cache)).retainByDates(null, lowerBound).countNotTrial();

        if (countBeforeDate1 > 0) // he is not novice at all
            return false;
        // else he became novice after lowerBound. But was it before upperBound?
        if(countBeforeDate2 == 0)
            return false;

        // If we are here, then client became novice between lowerBound and upperBound
        contractMed.setGroup(cache).retainByDates(lowerBound, upperBound).filterByContractType(ContractType.Trial).retainFirstByDate();

        return true;
    }

    /**
     * @param lowerBound
     * @param upperBound
     * @return true if client (unit) have acquired continuer status between this
     *         two dates
     */
    public boolean becameContinuer(Date lowerBound, Date upperBound) {
        if(!hasContracts()) return false;

        // We need to say if client have acquired / maintained continuer state in given period
        // This means -- we are not interested in those who acquired this state
        // no before nor after.
        // First try could be to get all his contracts before upperBound and see if
        // he has continuer state.
        // Then we could remove contract after lowerBound and see if state changed
        // If so -- return true
        // Else return false;

        List<Contract> cache = getContracts();

        // So -- take contracts before the second date
        int countBeforeDate2 = contractMed.setGroup(new ArrayList<>(cache)).retainByDates(null, upperBound).countNotTrial();
        // If count < 2 -- he is not continuer at all. If count >= 2 -- he is a
        // continuer
        if (countBeforeDate2 < 2)
            return false;
        // OK -- he is a continuer.
        // Lets look if he was a continuer already
        // Take only those contracts before the lowerBound
        int countBeforeDate1 = contractMed.setGroup(new ArrayList<>(cache)).retainByDates(null, lowerBound).countNotTrial();

        // That means -- he already was a continuer, before this date
        if (countBeforeDate1 == countBeforeDate2)
            return false;

        contractMed.setGroup(cache).retainByDates(lowerBound, upperBound).filterByContractType(ContractType.Trial);

        return true;
    }

    public List<Contract> getFrozenContracts() {
        try {
            return contractMed.setGroup(getContracts())
                    .retainByState(frozen).getGroup();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            return null;
        }
    }

    public boolean hasCanceledContracts() {
        boolean response = contractMed.setGroup(getContracts())
                .retainByState(ContractState.canceled).countGroupSize() > 0;
        contractMed.reset();
        return response;
    }

    public List<Contract> getCanceledContracts() {
        try {
            return contractMed.setGroup(getContracts())
                    .retainByState(ContractState.canceled).getGroup();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            return null;
        }
    }

    public boolean hasTrialContracts() {
        for (Contract c : getContracts())
            if (c.getContractTypeId() == ContractType.Trial)
                return true;
        return false;
    }

    public List<Contract> getTrialContracts() {
        return contractMed.setGroup(getContracts())
                .retainByContractType(ContractType.Trial).getGroup();
    }

    public boolean hasFinishedContracts() {
        boolean response = contractMed.setGroup(getContracts())
                .retainByState(ContractState.complete).countGroupSize() > 0;
        contractMed.reset();
        return response;
    }

    public List<Contract> getFinishedContracts() {
        try {
            return contractMed.setGroup(getContracts())
                    .retainByState(ContractState.complete).getGroup();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            return null;
        }
    }

    public int getBalance() {
        return unit.getBalance();
    }

    public ClientState getState() {
        // From the last session 26.12.12
        // Only trial contracts -- trial
        // Only one standard contract -- standard
        // More than one standard contract -- continuer.
        // No inactive state

        // if client is canceled - he have special mark
        if (unit.isCanceled())
            return ClientState.canceled;
        if (hasFrozenContracts())
            return ClientState.frozen;
        if (!hasContracts() || !hasActiveContracts())
            return ClientState.inactive;

        int notTrialCounter = 0;
        for (Contract c : unit.getContracts())
            if (c.notTrial())
                notTrialCounter++;
        switch (notTrialCounter) {
            case 0:
                return ClientState.trial;
            case 1:
                return ClientState.beginner;
            default:
                return ClientState.continuer;
        }
    }

    private boolean doesNotHaveActiveContracts() {
        return getActiveContracts().size() == 0;
    }

    public void cancelClient() {
        unit.setCanceled(true);
    }

    public Date getDateOfFirstContract() {
        try {
            return unit.getFirstContractDate();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            return null;
        }
    }

    public List<Teacher> getActiveTeachers() {
        if(!hasActiveContracts()) return null;

        List<Contract> contracts = getActiveContracts();

        Set<Teacher> res = new HashSet<>(5);
        for (Contract c : contracts)
            res.add(c.getTeacher());
        return new ArrayList<>(res);
    }

    public String getActiveTeachersString() {
        StringBuilder sb = new StringBuilder();
        List<Teacher> ts = getActiveTeachers();
        for (int i = 0; i < ts.size(); i++) {
            sb.append(ts.get(i).getName());
            if (i < ts.size() - 1)
                sb.append(", ");
        }
        return sb.toString();
    }

    public int getReturn() {
        List<Contract> t = getContracts();
        if (t == null || t.size() == 0)
            return 0;
        int summ = 0;
        for (Contract c : t)
            for (Payment p : c.getPayments())
                if (!p.isScheduled())
                    summ += p.getAmount();
        return summ;
    }

    private Map<String, Object> appliedFilters;

    private Map<String, Object> getAppliedFilters() {
        if (appliedFilters == null)
            appliedFilters = new HashMap<String, Object>(5);
        return appliedFilters;
    }

    private void load() {
        cache = dao.findWithNamedQuery(Client.ALL);
        appliedFilters = new HashMap<String, Object>();
    }

    public ClientMed reset() {
        cache = null;
        appliedFilters = null;
        return this;
    }

    @Override
    public List<Client> getGroup() {
        if (cache == null)
            load();
        return cache;
    }

    public List<Client> getGroup(boolean reset) {
        List<Client> innerCache = getGroup();
        if (reset)
            reset();
        return innerCache;
    }

    public ClientMed setGroup(List<Client> group) {
        cache = group;
        return this;
    }

    public List<Client> getAllClients() {
        return dao.findWithNamedQuery(Client.ALL);
    }

    public String getFilterState() {
        StringBuilder sb = new StringBuilder();
        for (Entry<String, Object> entry : getAppliedFilters().entrySet())
            sb.append(entry.getKey() + ": " + entry.getValue().toString()
                    + "\n");
        return sb.toString();
    }

    public ClientMed retainByState(ClientState state) {
        getAppliedFilters().put("ClientState", state);
        // Canceled is persisted property -> optimized method is call
        if (state == ClientState.canceled)
            return retainCanceled();

        List<Client> cache = getGroup();
        // save current unit
        Client unit = getUnit();

        // retainByState
        if (state == ClientState.active)
            retainActive();
        else
            for (int i = cache.size() - 1; i >= 0; i--) {
                setUnit(cache.get(i));
                if (getState() == state)
                    continue;
                cache.remove(i);
            }

        // restore unit
        setUnit(unit);
        return this;
    }

    private ClientMed retainCanceled() {
        if (cacheIsNull())
            cache = dao.findWithNamedQuery(Client.CANCELED);
        else
            for (int i = cache.size() - 1; i >= 0; i--)
                if (!cache.get(i).isCanceled())
                    cache.remove(i);
        return this;
    }

    private ClientMed retainActive() {
        getGroup();
        for (int i = cache.size() - 1; i >= 0; i--) {
            setUnit(cache.get(i));
            ClientState cs = getState();
            if (cs == ClientState.beginner || cs == ClientState.continuer
                    || cs == ClientState.trial)
                continue;
            cache.remove(i);
        }
        return this;
    }

    public ClientMed retainByActiveTeacher(Teacher teacher) {
        getAppliedFilters().put("Active teacher", teacher);
        List<Contract> contractsCache;
        if (!cacheIsNull())
            contractMed.setGroup(clientsToContracts(cache));

        contractsCache = contractMed.retainByTeacher(teacher)
                .retainByState(ContractState.active).getGroup(true);
        cache = contractsToClients(contractsCache);

        return this;
    }

    public ClientMed retainByDateOfFirstContract(Date date1, Date date2) {
        pushCriteria("Date of first contract 1", date1);
        pushCriteria("Date of first contract 2", date2);
        retainByDatesEntry(cache, date1, date2);
        return this;
    }

    public ClientMed retainByScheduledPayments(Date date1, Date date2) {
        getAppliedFilters().put("Date of planned payments 1", date1);
        getAppliedFilters().put("Date of planned payments 2", date2);

        getGroup();

        setGroup(paymentMed.setGroupFromClients(cache).retainByDatesEntry(date1, date2).retainByState(true).toClients());
        return this;
    }

    public ClientMed retainBySoonPayments(int days) {
        return retainByScheduledPayments(null, fromNowPlusDays(14, true));
    }

    public ClientMed retainByName(String name) {
        pushCriteria("Client name", name);
        // Setup
        name = name.toLowerCase();

        if (cacheIsNull())
            loadByName(name);
        else {
            String s;
            List<Client> cache = getGroup();
            for (int i = cache.size(); --i >= 0; ) {
                s = cache.get(i).getName().toLowerCase();
                if (!s.contains(name) && !s.equals(name))
                    cache.remove(i);
            }
        }
        return this;
    }

    private void loadByName(String name) {
        cache = dao.findWithNamedQuery(Client.BY_NAME,
                ChainMap.with("name", '%' + name + '%'));
    }

    public ClientMed retainDebtors() {
        Client c;
        List<Client> cache = getGroup();
        for (int i = cache.size() - 1; i >= 0; i--) {
            c = cache.get(i);
            if (c.getBalance() >= 0 || c.isCanceled())
                cache.remove(i);
        }
        return this;
    }

    public Integer countGroupSize() {
        return getGroup().size();
    }

    // TODO REDO
    public Integer count(ClientState state, Date date1, Date date2) {
        return retainByDateOfFirstContract(date1, date2).retainByState(state)
                .countGroupSize();
    }

    public Integer countContinuers(Date date1, Date date2) {
        return count(ClientState.continuer, date1, date2);
    }

    public Integer countNewbies(Date date1, Date date2) {
        return count(ClientState.beginner, date1, date2);
    }

    public Integer countTrial(Date date1, Date date2) {
        return count(ClientState.trial, date1, date2);
    }

    public Integer countCanceled(Date date1, Date date2) {
        return count(ClientState.canceled, date1, date2);
    }

    public Integer countUndefined(Date date1, Date date2) {
        return count(ClientState.inactive, date1, date2);
    }

    public Integer countFrozen(Date date1, Date date2) {
        return count(ClientState.frozen, date1, date2);
    }

    public ClientMed sortByName() {
        getGroup();
        Collections.sort(cache, Client.NameComparator);
        return this;
    }

    /**
     * Logic is: make a map Client -> Date of the last event.
     * Sort by date of the last event.
     * @return
     */
    public ClientMed sortByLastEventDate() {
        List<Entry<Date, Client>> dateMap = new ArrayList();
        List<Event> ecache = eventMed.retainByDatesEntry(fromNowPlusDays(-31),
                fromNowPlusDays(1)).getGroup();
        Date d;
        for (Client c : cache) {
            try {
                d = eventMed.setGroup(new ArrayList(ecache)).retainByClientId(c.getId()).lastByDate().getDate();
                dateMap.add(new AbstractMap.SimpleEntry(d, c));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                dateMap.add(new AbstractMap.SimpleEntry(new Date(0), c));
            }
        }
        Collections.sort(dateMap, new Comparator<Entry<Date, Client>>() {
            public int compare(Entry<Date, Client> o1, Entry<Date, Client> o2) {
                return o2.getKey().compareTo(o1.getKey());
            }
        });
        cache.clear();
        for (Entry<Date, Client> entry : dateMap)
            cache.add(entry.getValue());
        return this;
    }

    /**
     * Make a map Client -> Date of the earliest scheduled payment
     * @return
     */
    public ClientMed sortBySoonestPayment() {
        List<Entry<Date, Client>> dateMap = new ArrayList();
        List<Event> ecache = eventMed.retainByDatesEntry(fromNowPlusDays(-31),
                fromNowPlusDays(1)).getGroup();
        Date d;
        for (Client c : cache) {
            try {
                d = eventMed.setGroup(new ArrayList(ecache)).retainByClientId(c.getId()).lastByDate().getDate();
                dateMap.add(new AbstractMap.SimpleEntry(d, c));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                dateMap.add(new AbstractMap.SimpleEntry(new Date(0), c));
            }
        }
        Collections.sort(dateMap, new Comparator<Entry<Date, Client>>() {
            public int compare(Entry<Date, Client> o1, Entry<Date, Client> o2) {
                return o2.getKey().compareTo(o1.getKey());
            }
        });
        cache.clear();
        for (Entry<Date, Client> entry : dateMap)
            cache.add(entry.getValue());
        return this;
    }
}
