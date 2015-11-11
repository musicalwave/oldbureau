package tap.execounting.components.grids;

import java.util.List;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;

import tap.execounting.components.editors.AddClient;
import tap.execounting.components.show.SmartIcon;
import tap.execounting.dal.mediators.interfaces.ClientMed;
import tap.execounting.entities.Client;
import tap.execounting.entities.Teacher;
import tap.execounting.security.AuthorizationDispatcher;

public class ClientGrid {
	@Inject
	private BeanModelSource beanModelSource;
	@Inject
	private ComponentResources componentResources;
	@InjectComponent
	private Zone ezone;
	@Inject
	@Property
	private AuthorizationDispatcher dispatcher;

	@InjectComponent
	private AddClient editor;
	@Property
	private boolean editorActive;
	@Property
	private BeanModel<Client> model;
	@Property
	private Client unit;
	@Property
	@Persist
	private String nameFilter;
	
	@Inject
	private ClientMed clientMed;

	public List<Client> getSource() {
		if (nameFilter != null && nameFilter.length() > 2)
			clientMed.retainByName(nameFilter);
		return clientMed.getGroup(true);
	}

	Object onActionFromEdit(Client c) {
		// AUTHORIZATION MOUNT POINT EDIT
		if (dispatcher.canEditClients()) {
			editorActive = true;
			editor.setup(c);
		}
		return ezone.getBody();
	}

	Object onActionFromAdd() {
		// AUTHORIZATION MOUNT POINT CREATE
		if (dispatcher.canCreateClients()) {
			editorActive = true;
			editor.reset();
		}
		return ezone.getBody();
	}

	void onDelete(Client c) {
		// AUTH moved to the mediator 
		clientMed.delete(c);
	}

	void setupRender() {
		if (model == null) {
			model = beanModelSource.createDisplayModel(Client.class,
					componentResources.getMessages());
			model.exclude("return",
					"balance",
					"state",
					"firstContractDate",
					"studentInfo",
					"firstPlannedPaymentDate",
					"date",
					"id",
					"managerId",
					"firstPlannedPaymentDatePreload",
					"facilityName",
					"managerName",
					"debt");
			model.add("teachers", null);
			model.add("Action", null);
			model.add("deleted");
			model.reorder("deleted");
		}
	}

	public String getTeachers() {
		StringBuilder sb = new StringBuilder();
		List<Teacher> ts = unit.getCurrentTeachers();
		for (int i = 0; i < ts.size(); i++) {
			sb.append(ts.get(i).getName());
			if (i < ts.size() - 1)
				sb.append(", ");
		}
		return sb.toString();
	}

	public String getIconType() {
		return unit.isDeleted() ? SmartIcon.DELETED : "";
	}
}
