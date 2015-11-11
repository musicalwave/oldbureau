package tap.execounting.components;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import tap.execounting.dal.mediators.interfaces.EventMed;
import tap.execounting.data.EventState;
import tap.execounting.entities.Event;

import java.util.Date;

public class EventMover {

	@Inject
	private EventMed med;
	@Property
	@Persist
	private Event event;

	@Persist
	private EventState newState;
	@Property
	private Date newDate;
	@Property
	// 0 - for schedule transfer
	// 1 - for dated transfer
	private int transferType;
	@Inject
	private Messages messages;

	public boolean getOnDateTest() {
		return transferType == 1;
	}

	public void setup(Event e, EventState newState) {
		this.event = e;
		this.newState = newState;
		this.newDate = e.getDate();
	}

	boolean onSuccess() throws IllegalAccessException {
		med.setUnit(event);
		med.move(newState, newDate, transferType);
		return false;
	}
}
