package tap.execounting.data;

import java.util.Date;

import tap.execounting.entities.Event;

public class EventRowElement {

	private Date date;

	private Event event;

	public EventRowElement(Date date, Event e) {
		this.date = date;
		event = e;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public EventState getState() {
		try {
			return event.getState();
		} catch (NullPointerException npe) {
			return null;
		}
	}

	public void setState(EventState state) {

		try {
			event.setState(state);
		} catch (NullPointerException npe) {
		}
	}

	public Event getEvent() {
		return event;
	}
}
