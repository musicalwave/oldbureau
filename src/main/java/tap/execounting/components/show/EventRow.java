package tap.execounting.components.show;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import tap.execounting.data.EventRowElement;

import java.util.List;

@Import(stylesheet="context:css/eventrow.css")
public class EventRow {
	
	@Property
	@Parameter
	private boolean displayNull;
	@Property
	@Parameter
	private boolean displaydate;
	@Property
	@Parameter
	private List<EventRowElement> elements;
	@Property
	private EventRowElement element;
	
	
	public boolean getDisplay(){
		return displayNull || element.getState()!=null;
	}
}
