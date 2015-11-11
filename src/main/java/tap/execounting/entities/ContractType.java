package tap.execounting.entities;

import org.apache.tapestry5.beaneditor.NonVisual;

import javax.persistence.*;

/**
 * Types for contract
 * Six types of contract exists:
 * 	Standard -- usual contract. Client pays to the school, school pays to the teacher
 * 	Tuleneva -- old, deprecated type.
 * 	Special -- as a standard, but this does not go to the official payroll
 * 	Trial -- when client tries school
 * 	Free from school -- Client does not pays to school, but school pays to teacher.
 * 	Free from teacher -- Client does not pays to school, school does not pays to teacher.
 * @author truth0
 */
@Entity
@Table(name = "contract_types")
@NamedQueries({ @NamedQuery(name = ContractType.ALL, query = "Select ct from ContractType ct") })
public class ContractType {

	public static final String ALL = "ContractType.all";
	public static final int Trial = 3;
	@Deprecated
	public static final int Tuleneva = 2;
	public static final int Special = 5;
	public static final int Standard = 1;
	public static final int FreeFromTeacher = 4;
	public static final int FreeFromSchool = 6;

	@Id
	@Column(name = "contract_type_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String title;
	
	@NonVisual
	private boolean deleted;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}
