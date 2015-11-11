package tap.execounting.components.grids;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import tap.execounting.entities.Event;

import java.util.List;

public class EventGrid {

	@Parameter
	@Property
	private List<Event> source;

	@Property
	private Event event;
}
