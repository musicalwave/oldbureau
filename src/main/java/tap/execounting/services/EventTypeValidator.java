package tap.execounting.services;

import org.apache.tapestry5.Field;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.ioc.MessageFormatter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.FormSupport;
import org.apache.tapestry5.validator.AbstractValidator;
import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.entities.EventType;

import java.util.List;

public class EventTypeValidator extends AbstractValidator<Void, String> {

	@Inject
	private CRUDServiceDAO dao;

	private final static String messagekey = "eventtype-not-found";
	public final static String name = "eventtype";

	protected EventTypeValidator(Void constraintType, String valueType,
			String messageKey) {
		super(null, String.class, messagekey);
	}

	public EventTypeValidator() {
		super(null, String.class, messagekey);
	}

	public void render(Field field, Void constraintValue,
			MessageFormatter formatter, MarkupWriter writer,
			FormSupport formSupport) {
		// formSupport.addValidation(field, jsname, message(field.getLabel(),
		// formatter),null);
	}

	public void validate(Field field, Void contraint,
			MessageFormatter formatter, String value)
			throws ValidationException {
		if (eventTypeNotFound(value))
			throw new ValidationException(message(value, formatter));
	}

	private boolean eventTypeNotFound(String value) {
		List<EventType> types = dao.findWithNamedQuery(EventType.ACTUAL);
		for (EventType t : types)
			if (t.getTitle().equals(value))
				return false;
		return true;
	}

	private String message(String value, MessageFormatter formatter) {
		return formatter.format(value);
	}

}
