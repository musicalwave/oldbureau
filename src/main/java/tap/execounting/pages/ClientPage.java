package tap.execounting.pages;

import java.util.Date;
import java.util.List;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.components.editors.AddContract;
import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;
import tap.execounting.security.AuthorizationDispatcher;
import tap.execounting.util.DateUtil;

@Import(stylesheet="context:css/stattable.css")
public class ClientPage {

	@Property
	@Persist
	private Client client;
	@Inject
	private CRUDServiceDAO dao;
	@Inject
	private AuthorizationDispatcher dispatcher;
	private boolean editorActive;
	@Property
	@Persist
	private boolean editorActiveAlways;
	@Component
	private Zone ezone;
	@Persist
	@Property
	private boolean constantEventEditor;

	@InjectComponent
	private AddContract editor;
	@Property
	private Contract contract;
	@Property
	private boolean mode;

	// page events
	void onActivate(int clientId) {
		this.client = dao.find(Client.class, clientId);
	}

	int onPassivate() {
		return client.getId();
	}

	void onPrepare() {
		client = dao.find(Client.class, client.getId());
		setup(client);
	}
	
	Object onExperiment(Contract con){
		return this;
	}
	
	Object onCancel(Contract con){
		return this;
	}

	Object onAddContract() {
		// AUTHORIZATION MOUNT POINT CONTRACT CREATE
		if (dispatcher.canCreateContracts())
			editorActive = true;
		return ezone;
	}

	// setup
	public void setup(Client c) {
		client = c;
		c.getContracts().size();
		editor.setup(c);
	}

	// page properties
	public List<Contract> getContracts() {
		return client.getContracts(false);
	}

	public String getFirstContractDate() {
		if (getContracts().size() == 0)
			return "договоров по данному клиенту нет в базе";
		Date d = getContracts().get(0).getDate();
		for (Contract c : client.getContracts()) {
			if (c.getDate().compareTo(d) < 0)
				d = c.getDate();
		}
		return DateUtil.format("dd MMM YY", d);
	}

	public String getCurrentTeachersInfo() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < client.getCurrentTeachers().size(); i++) {
			sb.append(client.getCurrentTeachers().get(i).getName());
			if (i < client.getCurrentTeachers().size() - 1)
				sb.append(", ");
		}
		return sb.toString();
	}

	public boolean getEditorActive() {
		return editorActive || editorActiveAlways;
	}

}