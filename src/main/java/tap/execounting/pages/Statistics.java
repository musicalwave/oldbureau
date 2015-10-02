package tap.execounting.pages;

import java.util.Date;
import java.util.List;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;

import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.dal.mediators.interfaces.EventMed;
import tap.execounting.models.selectmodels.FacilitySelectModel;
import tap.execounting.models.selectmodels.RoomSelectModel;
import tap.execounting.models.selectmodels.TeacherSelectModel;
import tap.execounting.models.selectmodels.TypeTitleSelectModel;
import tap.execounting.entities.Event;
import tap.execounting.entities.Facility;
import tap.execounting.entities.Teacher;

import static tap.execounting.util.DateUtil.*;

@Import(library = "context:/js/updateEffects.js", stylesheet = {
		"context:css/datatable.css", "context:css/filtertable.css",
		"context:css/stattable.css" })
public class Statistics {

	// Code Helpers
	@Inject
	private CRUDServiceDAO dao;
	@Inject
	private Request request;
	@Inject
	private AjaxResponseRenderer renderer;
	@Inject
	private EventMed eventMed;

	// Page Components
	@Property
	private TeacherSelectModel teacherSelect;
	@Property
	private FacilitySelectModel facilitySelect;
	@Property
	private RoomSelectModel roomSelect;
	@Property
	private TypeTitleSelectModel typeSelect;
	@Component
	private Zone roomZone;
	@Component
	private Zone resultZone;
	@Component
	private Zone statZone;

	// Page Properties
	@Property
	@Persist
	private Integer facilityId;
	@Property
	@Persist
	private Integer teacherId;
	@Property
	@Persist
	private String typeId;
	@Property
	@Persist
	private Integer roomId;
	@Property
	@Persist
    // IF Event state equals 6 - than it is paid
	private Integer state;
	@Property
	@Persist
	private Date date1;
	@Property
	@Persist
	private Date date2;
	@Property
	@Persist
	private Integer percent;

	private List<Event> eventsCache;

	public List<Event> getEvents() {
		if (eventsCache != null)
			return eventsCache;//.subList(0, eventsCache.size());
        eventMed.reset();

        if(date2!=null) ceil(date2);
        eventMed.retainByDatesEntry(date1, date2);
        eventMed.retainByTeacherId(teacherId);
        if(state != null && state == 6) eventMed.retainPaidEvents();
        else eventMed.retainByStateCode(state);
        eventMed.retainByFacilityId(facilityId);
        eventMed.retainByRoomId(roomId);
        eventMed.retainByEventTitleContaining(typeId);
        eventsCache = eventMed.getGroup();

		return getEvents();
	}

	public int getMoney() {
		return eventMed.setGroup(getEvents()).countMoney();
	}

	public int getPercentedMoney() {
		return percent != null ? (getMoney() * percent / 100) : 0;
	}

	// school share
	public int getShare() {
		return eventMed.setGroup(getEvents()).countSchoolMoney();
	}

	
	public int getTeacherShare() {
		return eventMed.setGroup(getEvents()).countTeacherMoney();
	}

	Object onValueChangedFromFacilityId(Integer facId) {
		facilityId = facId;
		roomSelect = facilityId == null ? new RoomSelectModel(null)
				: new RoomSelectModel(dao.find(Facility.class, facilityId));
		return roomZone.getBody();
	}

	void onSubmitFromFilterForm() {
		if (request.isXHR())
			renderer.addRender(resultZone).addRender(statZone);
	}

	void onPrepareForRender() {
		List<Teacher> teachers = dao.findWithNamedQuery(Teacher.ALL);
		teacherSelect = new TeacherSelectModel(teachers);
		List<Facility> facilities = dao.findWithNamedQuery(Facility.ALL);
		facilitySelect = new FacilitySelectModel(facilities);

		roomSelect = facilityId == null ? new RoomSelectModel(null)
				: new RoomSelectModel(dao.find(Facility.class, facilityId));
		typeSelect = new TypeTitleSelectModel(dao);
	}
}
