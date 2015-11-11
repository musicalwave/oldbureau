package tap.execounting.pages;

import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.entities.Event;
import tap.execounting.entities.Facility;
import tap.execounting.entities.Teacher;

import java.util.List;

@Import(stylesheet = "context:css/datatable.css")
public class Schedules {

	@Inject
	private CRUDServiceDAO dao;

	@Property
	private Teacher teacher;
	@Property
	private Facility facility;
	@Property
	@Persist
	private boolean schoolMode;
	@Inject
	private Request request;
	@Component
	private Zone showZone;
	@InjectPage
	private SchoolSchedule schoolPage;

	@InjectPage
	private TeacherSchedule ts;

	public List<Event> getAllEvents() {
		return dao.findWithNamedQuery(Event.ALL);
	}

	public List<Teacher> getAllTeachers() {
		return dao.findWithNamedQuery(Teacher.ALL);
	}

	public List<Facility> getFacilities() {
		return dao.findWithNamedQuery(Facility.ALL);
	}

	public Facility facility(Integer id) {
		if (id == null)
			return null;
		return dao.find(Facility.class, id);
	}

	Object onActionFromFacilitySelected(Facility f) {
		schoolPage.setup(f);
		return schoolPage;
	}

	Object onActionFromTeacherLink(Teacher t) {
		ts.setup(t);
		return ts;
	}

	Object onViewModeChanged() {
		schoolMode = !schoolMode;
		return request.isXHR() ? showZone.getBody() : null;
	}
}
