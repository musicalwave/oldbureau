package tap.execounting.data;

import tap.execounting.entities.Event;

import java.util.Date;

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
