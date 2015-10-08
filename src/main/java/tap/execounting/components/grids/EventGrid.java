package tap.execounting.components.grids;

import java.util.List;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

import tap.execounting.entities.Event;

public class EventGrid {

	@Parameter
	@Property
	private List<Event> source;

	@Property
	private Event event;
}
