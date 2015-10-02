package tap.execounting.components.grids;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;

import tap.execounting.components.editors.AddEventType;
import tap.execounting.components.show.SmartIcon;
import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.entities.EventType;
import tap.execounting.security.AuthorizationDispatcher;

public class TypeGrid {
	@Inject
	private BeanModelSource beanModelSource;
	@Inject
	private ComponentResources componentResources;
	@Inject
	private CRUDServiceDAO dao;
	@InjectComponent
	private Zone ezone;
	@Inject
	@Property
	private AuthorizationDispatcher dispatcher;

	@InjectComponent
	private AddEventType editor;
	@Property
	private boolean editorActive;
	@Property
	private BeanModel<EventType> model;
	@Property
	private EventType unit;

	public List<EventType> getSource() {
		List<EventType> types = dao.findWithNamedQuery(EventType.ALL);
		Collections.sort(types, new Comparator<EventType>() {

			@Override
			public int compare(EventType o1, EventType o2) {
				if (o1.isDeleted()) {
					if (o2.isDeleted())
						return o1.getTitle().compareTo(o2.getTitle());
					return +1;
				} else {
					if (o2.isDeleted())
						return -1;
					return o1.getTitle().compareTo(o2.getTitle());
				}
			}

		});
		return types;
	}

	Object onActionFromEdit(EventType c) {
		// AUTHORIZATION MOUNT POINT
		if (dispatcher.canEditEventTypes()) {
			editorActive = true;
			editor.setup(c);
		}
		return ezone.getBody();
	}

	Object onActionFromAdd() {
		// AUTHORIZATION MOUNT POINT
		if (dispatcher.canCreateEventTypes()) {
			editorActive = true;
			editor.reset();
		}
		return ezone.getBody();
	}

	void onDelete(EventType c) {
		// AUTHORIZATION MOUNT POINT
		if (dispatcher.canDeleteEventTypes())
			dao.delete(EventType.class, c.getId());
	}

	void setupRender() {
		if (model == null) {
			model = beanModelSource.createDisplayModel(EventType.class,
					componentResources.getMessages());
			model.exclude("id", "typeTitle");
			model.add("Action", null);
			model.add("deleted");
			model.reorder("deleted");
		}
	}

	public String getIconType() {
		return unit.isDeleted() ? SmartIcon.DELETED : "";
	}
}
