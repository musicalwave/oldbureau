package tap.execounting.dal.mediators;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tap.execounting.dal.ChainMap;
import tap.execounting.dal.mediators.interfaces.PaymentMed;
import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Payment;
import tap.execounting.util.DateUtil;
import tap.execounting.util.Trans;

import static tap.execounting.util.Trans.clientsToPayments;
import static tap.execounting.util.Trans.contractsToClients;
import static tap.execounting.util.Trans.contractsToPayments;

public class PaymentMediator extends ProtoMediator<Payment> implements PaymentMed {

    public PaymentMediator(){clazz=Payment.class;}

    public void create(Payment pmnt) { dao.create(pmnt); }

    public void delete(int paymentId) { dao.delete(Payment.class, paymentId); }

    public void update(Payment pay) {
        dao.update(pay);
    }

    public PaymentMed setUnit(Payment unit) {
		this.unit = unit;
		return this;
	}

	public boolean getPlanned() {
		return unit.isScheduled();
	}

	public String getComment() {
		try {
			return unit.getComment();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
	}

	public int getAmount() {
		return unit.getAmount();
	}

	public Date getDate() {
		return unit.getDate();
	}

    public PaymentMed reset(){
        criterias = null;
        cache = null;
        return this;
    }

	public List<Payment> getGroup() {
		if (cache == null)
			cache = getAllPayments();
		return cache;
	}

	public List<Payment> getGroup(boolean reset) {
		List<Payment> innerCache = getGroup();
		if (reset)
			reset();
		return innerCache;
	}

	public PaymentMed setGroup(List<Payment> payments) {
		cache = payments;
		return this;
	}

    public PaymentMed setGroupFromContracts(List<Contract> contracts) {
        cache = contractsToPayments(contracts);
        return this;
    }

    public PaymentMed setGroupFromClients(List<Client> clients){
        cache = clientsToPayments(clients);
        return this;
    }

	public List<Payment> getAllPayments() {
		return dao.findWithNamedQuery(Payment.ALL);
	}

	public PaymentMed retainByContract(Contract unit) {
		if (cacheIsNull()) {
			loadByContractId(unit.getId());
		} else {
			for (int i = cache.size() - 1; i >= 0; i--)
				if (cache.get(i).getContractId() != unit.getId())
					cache.remove(i);
		}
        pushCriteria("Contract", unit);
		return this;
	}

    private void loadByContractId(int id) {
        cache = dao.findWithNamedQuery(Payment.BY_CONTRACT_ID,
                ChainMap.with("contractId", id));
    }

    public PaymentMed retainByDatesEntry(Date date1, Date date2) {
		if (cacheIsNull())
            loadByDatesEntry(date1, date2);
        else {
            DateUtil.retainByDatesEntry(cache, date1, date2);
            pushCriteria("Date1", date1);
            pushCriteria("Date2", date2);
        }
		return this;
	}

    private void loadByDatesEntry(Date date1, Date date2) {
        // Query should not get null values
        date1 = date1 == null ? new Date(0) : date1;
        date2 = date2 == null ? new Date(Long.MAX_VALUE) : date2;
        cache = dao.findWithNamedQuery(Payment.BY_DATES,
                ChainMap.w("earlierDate", date1).and("laterDate", date2));
    }


    public PaymentMed retainByState(boolean state) {
		if (cacheIsNull())
			if (state)
				cache = dao.findWithNamedQuery(Payment.SCHEDULED);
			else
				cache = dao.findWithNamedQuery(Payment.NOT_SCHEDULED);
		else {
			for (int i = cache.size() - 1; i >= 0; i--)
				if (cache.get(i).isScheduled() != state)
					cache.remove(i);
		}
		pushCriteria("StatePlanned", state);
		return this;
	}

	public Integer countGroupSize() {
		try {
			return cache.size();
		} catch (NullPointerException npe) {
			return null;
		}
	}

	public Integer countAmount() {
		int sum = 0;
		List<Payment> cache = getGroup();
		for (Payment p : cache)
			sum += p.getAmount();
		return sum;
	}

	public int countRealPaymentsAmount() {
		int sum = 0;
		for (Payment p : getGroup())
			if (!p.isScheduled())
				sum += p.getAmount();
		return sum;
	}

	public int countScheduledPaymentsAmount() {
		int sum = 0;
		for (Payment p : getGroup())
			if (p.isScheduled())
				sum += p.getAmount();
		return sum;
	}

	public List<Contract> getContracts() {
		Set<Contract> contractSet = new HashSet<Contract>();
		for (Payment p : getGroup())
			contractSet.add(p.getContract());
		return new ArrayList<>(contractSet);
	}

    public List<Client> toClients() {
        return Trans.paymentsToClients(cache);
    }

    public PaymentMed sortByDate(boolean descending) {
        DateUtil.sort(getGroup(), descending);
        return this;
    }

    /**
     * Will count return for completed payments, that match the date
     * @param date1
     * @param date2
     * @return
     */
    public Integer countReturn(Date date1, Date date2) {
		int sum = 0;
        for(Payment p : cache)
            if(p.isBetweenDates(date1, date2) && p.isComplete())
                sum += p.getAmount();
        return sum;
    }

}
