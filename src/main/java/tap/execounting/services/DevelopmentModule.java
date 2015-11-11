package tap.execounting.services;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestHandler;
import org.apache.tapestry5.services.Response;
import org.slf4j.Logger;

import java.io.IOException;

public class DevelopmentModule {
	public static void contributeApplicationDefaults(
			MappedConfiguration<String, Object> configuration) {
		configuration.add(SymbolConstants.APPLICATION_VERSION, "4.0.1-dev");
	}

	public RequestFilter buildTimingFilter(final Logger log) {
		return new RequestFilter() {
			public boolean service(Request request, Response response,
					RequestHandler handler) throws IOException {
				long startTime = System.currentTimeMillis();

				try {
					return handler.service(request, response);
				} finally {
					long elapsed = System.currentTimeMillis() - startTime;
					log.info(String.format("Request time: %d ms", elapsed));
				}
			}
		};
	}

	public void contributeRequestHandler(
			OrderedConfiguration<RequestFilter> configuration,
			@Local RequestFilter filter) {
		// Each contribution to an ordered configuration has a name, When
		// necessary, you may
		// set constraints to precisely control the invocation order of the
		// contributed retainByState
		// within the pipeline.

		configuration.add("Timing", filter);
	}
}
