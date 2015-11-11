package tap.execounting.entities;

import javax.persistence.*;

@Entity
@Table(name = "teacher_addition")
@NamedQueries({
	@NamedQuery(name = TeacherAddition.BY_TEACHER_ID, query = "from TeacherAddition where teacherId=:id")
})
public class TeacherAddition {
	public static final String BY_TEACHER_ID="TeacherAddition.byTeacherId";
	
	@Id
	@Column(name = "addition_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "teacher_id", unique = true, nullable = false)
	private int teacherId;

	private String field_1;
	private String field_2;
	private String field_3;
	private String field_4;
	private String field_5;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(int teacherId) {
		this.teacherId = teacherId;
	}

	public String getField_1() {
		return field_1;
	}

	public void setField_1(String field_1) {
		this.field_1 = field_1;
	}

	public String getField_2() {
		return field_2;
	}

	public void setField_2(String field_2) {
		this.field_2 = field_2;
	}

	public String getField_3() {
		return field_3;
	}

	public void setField_3(String field_3) {
		this.field_3 = field_3;
	}

	public String getField_4() {
		return field_4;
	}

	public void setField_4(String field_4) {
		this.field_4 = field_4;
	}

	public String getField_5() {
		return field_5;
	}
	
	public void setField_5(String field_5) {
		this.field_5 = field_5;
	}
}
