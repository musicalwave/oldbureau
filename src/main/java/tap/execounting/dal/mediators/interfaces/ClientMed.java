package tap.execounting.dal.mediators.interfaces;

import tap.execounting.data.ClientState;
import tap.execounting.entities.Client;
import tap.execounting.entities.Comment;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Teacher;

import java.util.Date;
import java.util.List;

public interface ClientMed {
//unit
	public Client getUnit();
	public ClientMed setUnit(Client c);

	public ClientMed setUnitById(int id);
	
	// Operations
	
	public void delete(Client c);
	// GETTERS
	//comment
	public Comment getComment();
	public void comment(String comment, long time);
	//contracts
	public boolean hasContracts();
	public List<Contract> getContracts();

		//active

    /**
     * This should return a list of active contracts of the current client.
     * @return
     */
	public boolean hasActiveContracts();
	public List<Contract> getActiveContracts();
	
		//frozen
	public boolean hasFrozenContracts();
	public List<Contract> getFrozenContracts();

		//canceled
	public boolean hasCanceledContracts();
	public List<Contract> getCanceledContracts();

		//trial
	public boolean hasTrialContracts();
	public List<Contract> getTrialContracts();

		//finished
	public boolean hasFinishedContracts();
	public List<Contract> getFinishedContracts();

	//balance
	public int getBalance();

	//state
	public ClientState getState();
	public void cancelClient();

	//date of first contract
	public Date getDateOfFirstContract();

	//teachers
	public List<Teacher> getActiveTeachers();

    public String getActiveTeachersString();

	//return
	public int getReturn();

//group
	public List<Client> getGroup();
	public List<Client> getGroup(boolean reset);
	public ClientMed setGroup(List<Client> group);
	public List<Client> getAllClients();
	public ClientMed reset();
	public String getFilterState();
	
	//filters
	
    /**
     * These are the people who have acquired queried status in given period
     * Following filters, also tune their contracts, to remove those contracts
     * that are not relevant to their state.
     * So continuers will have only second and further contracts, which also will be filtered by date.
     * @param date1
     * @param date2
     * @return
     */
	public ClientMed becameContinuers(Date date1, Date date2);

    /**
     * Novices will have only first standard contract
     * @param date1
     * @param date2
     * @return
     */
	public ClientMed becameNovices(Date date1, Date date2);

    /**
     * Trials will have only trial contracts
     * @param sa1
     * @param sa2
     * @return
     */
	public ClientMed becameTrials(Date sa1, Date sa2);

    /**
     * Should retain only those clients who match the state.
     * @param state
     * @return
     */
	public ClientMed retainByState(ClientState state);
	
	//Teacher
	public ClientMed retainByActiveTeacher(Teacher teacher);

	//Date of first contract (duration of relations)
	public ClientMed retainByDateOfFirstContract(Date date1, Date date2);
	
	//Date of planned payments
	public ClientMed retainByScheduledPayments(Date date1, Date date2);

    /**
     * Retains only those clients who are planning to pay in N days, or before.
     * @param days
     * @return
     */
    public ClientMed retainBySoonPayments(int days);
	
	//Name retainByState
	public ClientMed retainByName(String name);
	
	//debtors
	public ClientMed retainDebtors();

	//counters:
	public Integer countGroupSize();

	//state

	public Integer count(ClientState state, Date date1, Date date2);

		//continuers
	public Integer countContinuers(Date date1, Date date2);

		//newbies
	public Integer countNewbies(Date date1, Date date2);

		//trials
	public Integer countTrial(Date date1, Date date2);

		//canceled
	public Integer countCanceled(Date date1, Date date2);

		//inactive
	public Integer countUndefined(Date date1, Date date2);

		//frozen
	public Integer countFrozen(Date date1, Date date2);

    public ClientMed sortByName();

    public ClientMed sortByLastEventDate();

    public ClientMed sortBySoonestPayment();

    /**
     * Sets client.comment.
     * @param comment
     */
    public void setClientComment(String comment);

    public ClientMed retainByManagerId(int managerId);
}















