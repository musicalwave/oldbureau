package tap.execounting.dal.mediators.interfaces;

import tap.execounting.data.ContractState;
import tap.execounting.entities.*;

import java.util.Date;
import java.util.List;

public interface ContractMed {
	// unit methods
	// unit
	public Contract getUnit();

	public ContractMed setUnit(Contract unit);

	public ContractMed setUnitId(int id);

	// getters:

    /**
     * Teacher name
     * @return name of the teacher responsible for the contract
     */
    public String getTeacherName();

    /**
     * Client name
     * @return name of the client written in contract
     */
	public String getClientName();

    /**
     * Discipline, or subject type, or event type.
     * @return
     */
	public EventType getEventType();

    /**
     * State of the contract
     * @return
     */
	public ContractState getContractState();

    /**
     * String representation of contract state
     * @return
     */
    public String getContractStateString(boolean shrt);

	// date
	public Date getDate();

	// remaining events
	public int getRemainingLessons();

	// price
	public int getPrice();

	// Unit action methods

	// Write off all remaining events, to transfer all the money to the school
	// account.
	public void doWriteOff();
	
	public void doMoneyback() throws Exception;

	// Event planner method
	public void doPlanEvents(Date eventsStartDate);
	
	// Freezes the contract, also replanning the events after unfreeze date
	public ContractMed doFreeze(Date dateFreeze, Date dateUnfreeze);

    // Unfreezes the contract, by removing freeze dates
    public ContractMed doUnfreeze();

	// group methods
	// group
    public List<Contract> getGroup();

    // resets group after returning of the contracts
	public List<Contract> getGroup(boolean reset);

	public ContractMed setGroup(List<Contract> group);
	
	// Extracts all contracts from ever client and adds it to group
	public ContractMed setGroupFromClients(List<Client> clients);
	
	// Translates all contracts in group to clients
	public List<Client> getClients();

	public List<Contract> getAllContracts();

	public ContractMed reset();


	// //filters:

	// client
	public ContractMed retainByClient(Client c);

	// teacher
	public ContractMed retainByTeacher(Teacher t);

	// state
	public ContractMed retainByState(ContractState state);

	// date
	public ContractMed retainByDates(Date date1, Date date2);

	// contract type
	public ContractMed retainByContractType(int type);

    // remaining lessons
    public ContractMed retainExpiring(int remainingLessons);

	// this removes finished contracts from group. added as Tema asked, to
	// remove finished trials from TeacherPage
	public ContractMed removeComplete();

	// //counters:

	// actual group size
	public Integer countGroupSize();
	
	// Trial / Not trial dichotomy is in wide use.
	public int countNotTrial();

    // state
	public Integer count(ContractState state);
	
	// money paid for the certificate if contract has so
	public int countCertificateMoney();

	// Intersection operation
	public ContractMed retain(List<Contract> contracts);

	public ContractMed sortByDate(boolean asc);

    public ContractMed sortByClientName();

    public List<ContractType> loadContractTypes();

    public int countTrial();

    public ContractMed filterByContractType(int contractTypeCode);

    public ContractMed retainFirstByDate();
}
