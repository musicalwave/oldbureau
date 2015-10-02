package tap.execounting.components.show;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.dal.ChainMap;
import tap.execounting.entities.Comment;
import tap.execounting.entities.User;

public class ShowComment {
	@Parameter(required = true)
	@Property
	private Comment comment;

	@Parameter
	@Property
	private boolean showDeleted;

	@Inject
	private CRUDServiceDAO dao;

	public boolean getShow() {
		return !comment.isDeleted() || showDeleted;
	}

	public String getUsername() {
		User u = dao.findUniqueWithNamedQuery(User.BY_ID, ChainMap.with("userId", comment.getUserId()));
		return u.getUsername();
	}
}
