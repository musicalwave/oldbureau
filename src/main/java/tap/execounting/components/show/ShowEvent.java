package tap.execounting.components.show;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.components.editors.AddEvent;
import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Event;
import tap.execounting.entities.EventType;
import tap.execounting.entities.Facility;
import tap.execounting.entities.Room;
import tap.execounting.security.AuthorizationDispatcher;

public class ShowEvent {

	@Parameter
	@Property
	private Event event;

	@Inject
	private CRUDServiceDAO dao;
	@Inject
	private AuthorizationDispatcher dispatcher;

	@InjectComponent
	private Zone bodyZone;

	@Property
	private boolean updateMode;

	@InjectComponent
	private AddEvent editor;

	Object onEdit(Event e) {
		// AUTHORIZATION MOUNT POINT EVENT EDIT
		if (dispatcher.canEditEvents()) {
			editor.setup(e, true);
			updateMode = true;
		}
		return bodyZone.getBody();
	}

	public String getType() {
		String title;
		try {
			title = dao.find(EventType.class, event.getTypeId()).getTitle();
		} catch (Exception e) {
			title = "не выбран";
		}
		return title;
	}

	public String getFacilityName() {
		return dao.find(Facility.class, event.getFacilityId()).getName();
	}

	public String getRoomName() {
		return dao.find(Room.class, event.getRoomId()).getName();
	}

	public String getNames() {
		StringBuilder sb = new StringBuilder();
		for (Contract c : event.getContracts())
			sb.append(c.getClient().getName() + ", ");
		sb.delete(sb.length() - 2, sb.length());
		return sb.toString();
	}

	public String getDate() {
		Format formatter = new SimpleDateFormat("dd MMMM  HH:mm");
		return formatter.format(event.getDate());
	}

	public int getMoney() {
		int m1 = dao.find(EventType.class, event.getTypeId()).getPrice();
		int c = event.getClients().size();
		if (c > 1)
			m1 *= c;
		return m1;
	}

	public String getClients() {
		StringBuilder sb = new StringBuilder();
		List<Client> lc = event.getClients();

		for (int i = 0; i < lc.size(); i++) {
			sb.append(lc.get(i).getName());
			if (i < lc.size() - 1)
				sb.append(", ");
		}
		return sb.toString();
	}

    Object onEditorClosed(Event event) {
        updateMode = false;
        this.event = event;
        return bodyZone.getBody();
    }
}
