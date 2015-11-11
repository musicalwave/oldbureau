package tap.execounting.dal.mediators;

import org.apache.tapestry5.ioc.annotations.Inject;
import tap.execounting.dal.ChainMap;
import tap.execounting.dal.mediators.interfaces.ClientMed;
import tap.execounting.dal.mediators.interfaces.ContractMed;
import tap.execounting.dal.mediators.interfaces.EventMed;
import tap.execounting.dal.mediators.interfaces.TeacherMed;
import tap.execounting.data.ContractState;
import tap.execounting.data.EventState;
import tap.execounting.entities.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TeacherMediator extends ProtoMediator<Teacher> implements TeacherMed {

	@Inject
	private EventMed eventMed;
	@Inject
	private ClientMed clientMed;
	@Inject
	private ContractMed contractMed;

    public TeacherMediator(){clazz=Teacher.class;}

	public List<Teacher> getAllTeachers() {
		return dao.findWithNamedQuery(Teacher.ALL);
	}

	public List<Teacher> getWorkingTeachers() {
		return dao.findWithNamedQuery(Teacher.WORKING);
	}

	// Unit methods:
	public Teacher getUnit() {
		return unit;
	}

	public TeacherMed setUnit(Teacher unit) {
		this.unit = unit;
		clearCaches();
		return this;
	}

	public int getId() {
		return unit.getId();
	}

	public TeacherMed setId(int id) {
		setUnit(dao.find(Teacher.class, id));
		return this;
	}

	private void clearCaches() {
		allContractsCache = null;
	}

	public List<Comment> getComments() {
		return dao.findWithNamedQuery(Comment.BY_TEACHER_ID,
				ChainMap.with("teacherId", unit.getId()));
	}

	private List<Contract> allContractsCache;

	public List<Contract> getAllContracts() {
		if (allContractsCache == null)
			allContractsCache = dao.findWithNamedQuery(
					Contract.WITH_TEACHER,
					ChainMap.with("teacherId", unit.getId()));
		List<Contract> lst = new ArrayList<Contract>(allContractsCache.size());
		for (Contract c : allContractsCache)
			lst.add(c);
		return lst;
	}

	public List<Contract> getActualContracts() {
		List<Contract> list = getAllContracts();
		for (int i = list.size() - 1; i >= 0; i--)
			if (!list.get(i).isActive())
				list.remove(i);
		return list;
	}

	public List<Client> getAllClients() {
		return null;
		// return clientMed.retainByState(unit);
	}

	public List<Client> getActiveClients() {
		// own
		// Set<Client> clients = new HashSet<Client>(10);
		// for (Contract c : getActualContracts())
		// clients.add(c.getClient());
		// return new ArrayList<Client>(clients);
		List<Client> res = clientMed.retainByActiveTeacher(unit)
				.getGroup();
		clientMed.reset();
		return res;
	}

	// contracts
	// frozen
	public List<Contract> getFrozenContracts() {
		return contractMed.setGroup(getAllContracts())
				.retainByState(ContractState.frozen).getGroup();
	}

	// canceled
	public List<Contract> getCanceledContracts() {
		return contractMed.setGroup(getAllContracts())
				.retainByState(ContractState.canceled).getGroup();
	}

	// complete
	public List<Contract> getCompleteContracts() {
		return contractMed.setGroup(getAllContracts())
				.retainByState(ContractState.complete).getGroup();
	}

	public String worksOn(String day) {
		Integer code = null;
		if (day.equals("Пн"))
			code = unit.getMonday();
		else if (day.equals("Вт"))
			code = unit.getTuesday();
		else if (day.equals("Ср"))
			code = unit.getWednesday();
		else if (day.equals("Чт"))
			code = unit.getThursday();
		else if (day.equals("Пт"))
			code = unit.getFriday();
		else if (day.equals("Сб"))
			code = unit.getSaturday();
		else if (day.equals("Вс"))
			code = unit.getSunday();

		return code == null ? "-" : dao.find(Facility.class, code).getName();
	}

	public String getName() {
		try {
			return unit.getName();
		} catch (NullPointerException e) {
			return null;
		}
	}

	public Integer[] getSchedule() {
		try {
			Integer[] sched = new Integer[7];
			sched[0] = unit.getMonday();
			sched[1] = unit.getTuesday();
			sched[2] = unit.getWednesday();
			sched[3] = unit.getThursday();
			sched[4] = unit.getFriday();
			sched[5] = unit.getSaturday();
			sched[6] = unit.getSunday();
			return sched;
		} catch (NullPointerException e) {
			return null;
		}
	}

	public String getSchoolForDay(String day) {
		try {
			Integer[] sched = getSchedule();
			day = day.toLowerCase();
			if (day.equals("пн"))
				return sched[0] == null ? null : dao.find(Facility.class,
						sched[0]).getName();
			if (day.equals("вт"))
				return sched[1] == null ? null : dao.find(Facility.class,
						sched[1]).getName();
			if (day.equals("ср"))
				return sched[2] == null ? null : dao.find(Facility.class,
						sched[2]).getName();
			if (day.equals("чт"))
				return sched[3] == null ? null : dao.find(Facility.class,
						sched[3]).getName();
			if (day.equals("пт"))
				return sched[4] == null ? null : dao.find(Facility.class,
						sched[4]).getName();
			if (day.equals("сб"))
				return sched[5] == null ? null : dao.find(Facility.class,
						sched[5]).getName();
			if (day.equals("вс"))
				return sched[6] == null ? null : dao.find(Facility.class,
						sched[6]).getName();
			return null;
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
	}

	public boolean getState() {
		try {
			return unit.isActive();
		} catch (NullPointerException npe) {
			return false;
		}
	}

	public int getLessonsComplete(Date date1, Date date2) {
		int res = eventMed.retainByTeacher(unit).retainByDatesEntry(date1, date2)
				.countEventsComplete();
		eventMed.reset();
		return res;
	}

	public int getLessonsFired(Date date1, Date date2) {
		int res = eventMed.retainByTeacher(unit).retainByDatesEntry(date1, date2)
				.countEventsFired();
		eventMed.reset();
		return res;
	}

	public int getLessonsMovedByTeacher(Date date1, Date date2) {
		int res = eventMed.retainByTeacher(unit).retainByDatesEntry(date1, date2)
				.countEventsMovedByTeacher();
		eventMed.reset();
		return res;
	}

	public int getLessonsMovedByClient(Date date1, Date date2) {
		int res = eventMed.retainByTeacher(unit).retainByDatesEntry(date1, date2)
				.countEventsMovedByClient();
		eventMed.reset();
		return res;
	}

	public int getDaysWorked(Date date1, Date date2) {
		int res = eventMed.retainByTeacher(unit).retainByDatesEntry(date1, date2)
				.retainByState(EventState.complete).countDaysInEventsGroup();
		eventMed.reset();
		return res;
	}

	public int getMoneyEarned(Date date1, Date date2) {
		int res = eventMed.retainByTeacher(unit).retainByDatesEntry(date1, date2)
				.countMoneyOfCompleteEvents();
		eventMed.reset();
		return res;
	}

	public int getMoneyEarnedForSchool(Date date1, Date date2) {
		int res = eventMed.retainByTeacher(unit).retainByDatesEntry(date1, date2)
				.retainByState(EventState.complete).countSchoolMoney();
		eventMed.reset();
		return res;
	}

	public int getMoneyEarnedForSelf(Date date1, Date date2) {
		int res = eventMed.retainByTeacher(unit).retainByDatesEntry(date1, date2)
				.retainByState(EventState.complete).countTeacherMoney();
		eventMed.reset();
		return res;
	}

	public TeacherAddition getAddition() {
		return dao.findUniqueWithNamedQuery(TeacherAddition.BY_TEACHER_ID,
				ChainMap.with("id", getId()));
	}

	private List<Teacher> teachersCache;

	public Integer countGroupSize() {
		try {
			return teachersCache.size();
		} catch (NullPointerException npe) {
			return null;
		}
	}

}
