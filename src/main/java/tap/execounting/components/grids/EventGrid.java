package tap.execounting.components.grids;

import java.util.List;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.entities.Client;
import tap.execounting.entities.Event;
import tap.execounting.entities.EventType;
import tap.execounting.entities.Facility;
import tap.execounting.entities.Room;
import tap.execounting.entities.Teacher;

public class EventGrid {

	@Parameter
	@Property
	private List<Event> source;

	@Property
	private Event event;

	@Inject
	private CRUDServiceDAO dao;

	public String getHostName() {
		return dao.find(Teacher.class, event.getHostId()).getName();
	}

	public String getFacilityTitle() {
		return dao.find(Facility.class, event.getFacilityId()).getName();
	}

	public String getRoomTitle() {
		return dao.find(Room.class, event.getRoomId()).getName();
	}

	public String getState() {
		return event.getState().toString();
	}

	public String getTypeTitle() {
		return dao.find(EventType.class, event.getTypeId()).getTitle();
	}

	public int getMoney() {
		EventType et = dao.find(EventType.class, event.getTypeId());
		if (event.getClients().size() > 0)
			return et.getPrice() * event.getClients().size();
		return et.getPrice();
	}
	
	public String getClients(){
		StringBuilder sb = new StringBuilder();
		List<Client> lc = event.getClients();
		
		for(int i = 0;i<lc.size();i++){
			sb.append(lc.get(i).getName());
			if(i<lc.size()-1) sb.append(", ");
		}
		return sb.toString();
	}
}
