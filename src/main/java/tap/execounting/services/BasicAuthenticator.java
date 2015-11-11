package tap.execounting.services;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;
import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.dal.ChainMap;
import tap.execounting.entities.User;
import tap.execounting.security.AuthenticationException;

public class BasicAuthenticator implements Authenticator {

	public static final String AUTH_TOKEN = "authToken";

	@Inject
	private CRUDServiceDAO crudService;

	@Inject
	private Request request;

	public void login(String username, String password)
			throws AuthenticationException {
		password = DigestUtils.md5Hex(password);
		User user = crudService.findUniqueWithNamedQuery(
				User.BY_CREDENTIALS,
				ChainMap.w("username", username)
						.and("password", password));

		if (user == null) {
			throw new AuthenticationException("The user doesn't exist");
		}

		request.getSession(true).setAttribute(AUTH_TOKEN, user);
	}

	public boolean isLoggedIn() {
		Session session = request.getSession(false);
		if (session != null) {
			return session.getAttribute(AUTH_TOKEN) != null;
		}
		return false;
	}

	public void logout() {
		Session session = request.getSession(false);
		if (session != null) {
			session.setAttribute(AUTH_TOKEN, null);
			session.invalidate();
		}
	}

	public User getLoggedUser() {
		if (isLoggedIn()) return (User) request.getSession(true).getAttribute(AUTH_TOKEN);
        else throw new IllegalStateException("The user is not logged ! ");
	}

}
