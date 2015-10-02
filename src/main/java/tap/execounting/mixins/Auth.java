package tap.execounting.mixins;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

@Import(library = "context:/js/authorize.js")
public class Auth {
	@Parameter(value = "Вы не можете выполнить данную операцию", defaultPrefix = BindingConstants.MESSAGE)
	private String authMessage;

	@Parameter(value = "true" , defaultPrefix = BindingConstants.PROP)
	private boolean authEnabled;

	@Inject
	private JavaScriptSupport scriptSupport;

	@InjectContainer
	private ClientElement element;

	@AfterRender
	public void afterRender() {
		if (authEnabled)
			scriptSupport.addScript("new Authorize('%s', '%s');",
					element.getClientId(), authMessage);
	}
}
