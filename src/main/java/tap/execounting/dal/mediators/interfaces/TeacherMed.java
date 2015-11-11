package tap.execounting.dal.mediators.interfaces;

import tap.execounting.entities.*;

import java.util.Date;
import java.util.List;

public interface TeacherMed {
//unit methods
	//getters:
	
	//unit
	public Teacher getUnit();
	public TeacherMed setUnit(Teacher unit);
	
	//id
	public int getId();
	//this method sets the unit itself
	public TeacherMed setId(int id);
	
	//name
	public String getName();
	
	//schedule
	public Integer[] getSchedule();
	public String getSchoolForDay(String day);
	
	//state
	public boolean getState();
	
	//comments
	public List<Comment> getComments();
	
	//clients
		//all
	public List<Client> getAllClients();
		//active
	public List<Client> getActiveClients();
	//contracts
		//frozen
	public List<Contract> getFrozenContracts();
		//Canceled
	public List<Contract> getCanceledContracts();
		//Complete
	public List<Contract> getCompleteContracts();
	
	//lessons
		//complete
	public int getLessonsComplete(Date date1, Date date2);
		//failed
	public int getLessonsFired(Date date1, Date date2);
		//failed by teacher
	public int getLessonsMovedByTeacher(Date date1, Date date2);
		//failed by client
	public int getLessonsMovedByClient(Date date1, Date date2);
	//days worked
	public int getDaysWorked(Date date1, Date date2);
	//money earned
	public int getMoneyEarned(Date date1, Date date2);
		//for school
	public int getMoneyEarnedForSchool(Date date1, Date date2);
		//for self
	public int getMoneyEarnedForSelf(Date date1, Date date2);
	
	//addition
	public TeacherAddition getAddition();
	
//group methods
	public List<Teacher> getAllTeachers();
	public List<Teacher> getWorkingTeachers();
	
	//count:
	public Integer countGroupSize();
}
