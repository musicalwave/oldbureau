package tap.execounting.components.editors;


import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.entities.Facility;

public class AddFacility {

	@Persist
	@Property
	private boolean updateMode;

	@Inject
	private CRUDServiceDAO dao;

	@Persist
	private Facility facility;

	public void setup(Facility f) {
		facility = f;
		updateMode = true;
	}

	public void reset() {
		updateMode = false;
		facility = new Facility();
	}

	void onSuccess() {
		if (updateMode) {
			dao.update(facility);
		} else {
			dao.create(facility);
		}
	}

	public Facility getFacility() {
		return facility;
	}

	public void setFacility(Facility facility) {
		this.facility = facility;
	}
}
