package tap.execounting.pages;

import tap.execounting.services.Authenticator;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.entities.User;

@Import(stylesheet="context:css/signin.css")
public class Settings {
	@Inject
	private CRUDServiceDAO dao;

	@Inject
	private Messages messages;

	@Inject
	private Authenticator authenticator;

	@InjectPage
	private Signin signin;

	@Property
	private String password;

	@Property
	private String verifyPassword;

	@Component
	private Form settingsForm;

	public Object onSuccess() {
		if (!verifyPassword.equals(password)) {
			settingsForm.recordError(messages.get("error.verifypassword"));

			return null;
		}

		User user = authenticator.getLoggedUser();
		authenticator.logout();

		user.setPassword(password);

		dao.update(user);

		signin.setFlashMessage(messages.get("settings.password-changed"));

		return signin;
	}
}
