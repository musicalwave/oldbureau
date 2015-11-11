package tap.execounting.components.editors;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.entities.Comment;
import tap.execounting.services.Authenticator;

import java.util.Date;

public class AddComment {

	@Inject
	private Authenticator authenticator;

	@Inject
	private CRUDServiceDAO dao;

	@Property
	private String commentText;

	@Persist
	private Comment comment;

	@Parameter(required = true)
	private int code;
	@Parameter(required = true)
	private int entityId;

	void onActivate() {
		comment = new Comment(code, getUserId(), entityId);
	}

	void onSuccess() {
		Comment comment = this.comment == null ? new Comment(code, getUserId(),
				entityId) : this.comment;
		comment.append(new Date(), commentText);
		dao.create(comment);
	}

	private int getUserId() {
		int userId = 0;
		try {
			userId = authenticator.getLoggedUser().getId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userId;
	}
}
