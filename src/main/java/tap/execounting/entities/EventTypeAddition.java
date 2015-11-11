package tap.execounting.entities;

import org.apache.tapestry5.beaneditor.NonVisual;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "event_type_additions")
@NamedQueries({
		@NamedQuery(name = EventTypeAddition.BY_EVENT_TYPE_ID, query = "from EventTypeAddition where eventTypeId = :eventTypeId"),
		@NamedQuery(name = EventTypeAddition.PROBATION_BY_EVENT_TYPE_ID, query = "from EventTypeAddition where eventTypeId = :eventTypeId and additionCode = "
				+ EventTypeAddition.PROBATION_ADJUSTMENT) })
public class EventTypeAddition {
	public static final String BY_EVENT_TYPE_ID = "EventTypeAddition.byEventTypeId";
	public static final String PROBATION_BY_EVENT_TYPE_ID = "EventTypeAddition.probationByEventTypeId";
	public static final int PROBATION_ADJUSTMENT = 0;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@NonVisual
	@Column(name="addition_id")
	private Integer id;

	@Column(name = "event_type_id")
	@NonVisual
	@NotNull
	private int eventTypeId;

	@NonVisual
	@NotNull
	@Column
	private int additionCode;

	@Min(value = 1)
	@NotNull
	private int additionValue;

	private int additionClause;

	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getEventTypeId() {
		return eventTypeId;
	}

	public void setEventTypeId(int eventTypeId) {
		this.eventTypeId = eventTypeId;
	}

	public int getAdditionCode() {
		return additionCode;
	}

	public void setAdditionCode(int additionCode) {
		this.additionCode = additionCode;
	}

	public int getAdditionValue() {
		return additionValue;
	}

	public void setAdditionValue(int additionValue) {
		this.additionValue = additionValue;
	}

	public int getAdditionClause() {
		return additionClause;
	}

	public void setAdditionClause(int additionClause) {
		this.additionClause = additionClause;
	}
}
