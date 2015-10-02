package tap.execounting.mixins;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

@Import(library = "context:/js/confirm.js")
public class Confirm {
	@Parameter(value = "Вы уверены?", defaultPrefix = BindingConstants.LITERAL)
	private String confirmMessage;

	@Parameter
	private boolean confirmEnabled;

	@Inject
	private JavaScriptSupport javaScriptSupport;

	@InjectContainer
	private ClientElement element;

	@AfterRender
	public void afterRender() {
		if (confirmEnabled)
			javaScriptSupport.addScript("new Confirm('%s', '%s');",
					element.getClientId(), confirmMessage);
	}
}
