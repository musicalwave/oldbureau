package tap.execounting.components.grids;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import tap.execounting.components.editors.AddFacility;
import tap.execounting.components.show.SmartIcon;
import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.entities.Facility;
import tap.execounting.security.AuthorizationDispatcher;

import java.util.List;

public class FacilityGrid {
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
	private AddFacility editor;
	@Property
	private boolean editorActive;
	@Property
	private BeanModel<Facility> model;
	@Property
	private Facility unit;

	public List<Facility> getSource() {
		return dao.findWithNamedQuery(Facility.ALL);
	}

	Object onActionFromEdit(Facility c) {
		// AUTHORIZATION MOUNT POINT
		if (dispatcher.canEditFacilities()) {
			editorActive = true;
			editor.setup(c);
		}
		return ezone.getBody();
	}

	Object onActionFromAdd() {
		// AUTHORIZATION MOUNT POINT
		if (dispatcher.canCreateFacilities()) {
			editorActive = true;
			editor.reset();
		}
		return ezone.getBody();
	}

	void onDelete(Facility c) {
		// AUTHORIZATION MOUNT POINT
		if (dispatcher.canDeleteFacilities())
			dao.delete(Facility.class, c.getFacilityId());
	}

	void setupRender() {
		if (model == null) {
			model = beanModelSource.createDisplayModel(Facility.class,
					componentResources.getMessages());
			model.add("Action", null);
			model.exclude("facilityId");
			model.add("deleted");
			model.reorder("deleted");
		}
	}
	public String getIconType(){
		return unit.isDeleted() ? SmartIcon.DELETED : "";
	}
}
