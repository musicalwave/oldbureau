package tap.execounting.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import tap.execounting.entities.User;
import tap.execounting.pages.Index;
import tap.execounting.services.Authenticator;

/**
 * Layout component for pages of application.
 */
@Import(stylesheet = { "context:css/style.css", "context:css/home.css" }, library = { "context:js/SpacerResizer.js" })
public class Layout {
	@Property
	private String pageName;

	@Property
	@Parameter(required = true, defaultPrefix = BindingConstants.LITERAL)
	private String pageTitle;

	@Inject
	private ComponentResources resources;

	@Inject
	private Authenticator authenticator;

	public String getClassForPageName() {
		return resources.getPageName().equalsIgnoreCase(pageName) ? "current_page_item"
				: null;
	}

	public User getUser() {
		return authenticator.isLoggedIn() ? authenticator.getLoggedUser()
				: null;
	}

	@Log
	public Object onActionFromLogout() {
		authenticator.logout();
		return Index.class;
	}

	public String cssForLi(String link) {
		link = link.toLowerCase();
		String ptitle = pageTitle.toLowerCase();

		return ptitle.equals(link) ? "activeMenuItem" : "";
	}

	// @Import(stylesheet = { "context:/layout/datepicker.css" })
	void afterRender() {
	}
}
