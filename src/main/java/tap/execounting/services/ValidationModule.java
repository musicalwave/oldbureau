package tap.execounting.services;

import org.apache.tapestry5.Validator;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.services.FieldValidatorSource;

public class ValidationModule {

	@Contribute(FieldValidatorSource.class)
	public static void addEventTypeValidator(
			MappedConfiguration<String, Validator<Void, String>> configuration) {
		configuration.add(EventTypeValidator.name, new EventTypeValidator());
	}
}
