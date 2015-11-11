package tap.execounting.entities;

import org.apache.tapestry5.beaneditor.NonVisual;

import javax.persistence.*;
import java.util.Date;

/**
 * This class does not support interface tap.execounting.util.entities.interfaces.Deletable, since
 * some comments certainly should be removed.
 * 
 * @author truth0
 * 
 */
@Entity
@Table(name = "comment")
@NamedQueries({
		@NamedQuery(name = Comment.ALL, query = "from Comment"),
		@NamedQuery(name = Comment.BY_TEACHER_ID, query = "from Comment where code=0 and entityId=:teacherId"),
		@NamedQuery(name = Comment.BY_CLIENT_ID, query = "from Comment where code=1 and entityId=:id"),
		@NamedQuery(name = Comment.CLIENT_AFTER_DATE, query = "from Comment where code=1 and date >= :date") })
public class Comment {
	public static final String ALL = "Comment.all";
	public static final String BY_TEACHER_ID = "Comment.byTeacherId";
	public static final String BY_CLIENT_ID = "Comment.byClientId";
	public static final String CLIENT_AFTER_DATE = "Comment.clientAfterDate";

	public static final int TeacherCode = 0;
	public static final int ClientCode = 1;

	// service properties
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@NonVisual
	private int id;
	@NonVisual
	private int code;
	@NonVisual
	private int entityId;
	@NonVisual
	private boolean deleted;
	// business properties
	private String text;
	@NonVisual
	private Date date;
	@NonVisual
	private int userId;

	public Comment() {
	}

	public Comment(int code, int userId, int entityId) {
		this.code = code;
		this.userId = userId;
		this.entityId = entityId;
	}

	public void append(Date date, String text) {
		this.date = date;
		this.text = text;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getEntityId() {
		return entityId;
	}

	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}
