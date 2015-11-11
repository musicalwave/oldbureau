package tap.execounting.dal.mediators.interfaces;

import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Payment;

import java.util.Date;
import java.util.List;

public interface PaymentMed {
//unit methods
	//unit
    public void create(Payment pmnt);
    public void delete(int paymentId);
    public void update(Payment pay);

    public Payment getUnit();
	public PaymentMed setUnit(Payment unit);
    public Payment getUnitById(int paymentId);

	//getters
	//planned
	public boolean getPlanned();

	//Comment
	public String getComment();
	
	//Amount
	public int getAmount();
	
	//Date
	public Date getDate();
	
//group methods
	//group
	public List<Payment> getGroup();
	public PaymentMed setGroup(List<Payment> payments);
	public PaymentMed setGroupFromContracts(List<Contract> contracts);
    public PaymentMed setGroupFromClients(List<Client> clients);

    public List<Payment> getAllPayments();
	public PaymentMed reset();

	//filters
	//contract
	public PaymentMed retainByContract(Contract unit);
	
	//Date
	public PaymentMed retainByDatesEntry(Date date1, Date date2);

	//Planned (state)
	public PaymentMed retainByState(boolean state);

	//counters
	public Integer countGroupSize();
	
	//amount
	public Integer countAmount();
	public int countRealPaymentsAmount();
	public int countScheduledPaymentsAmount();
	// Maps payments into contracts
	public List<Contract> getContracts();
    public List<Client> toClients();

    public PaymentMed sortByDate(boolean descending);
}
