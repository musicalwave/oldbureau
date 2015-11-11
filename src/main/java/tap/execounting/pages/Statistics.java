package tap.execounting.pages;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.type.IntegerType;
import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.entities.Event;
import tap.execounting.entities.Facility;
import tap.execounting.entities.Teacher;
import tap.execounting.models.selectmodels.FacilitySelectModel;
import tap.execounting.models.selectmodels.RoomSelectModel;
import tap.execounting.models.selectmodels.TeacherSelectModel;
import tap.execounting.models.selectmodels.TypeTitleSelectModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Import(library = "context:/js/updateEffects.js",
        stylesheet = {
        "context:css/datatable.css",
        "context:css/filtertable.css",
        "context:css/stattable.css"})

public class Statistics {

    // Code Helpers
    @Inject
    private Session session;
    @Inject
    private CRUDServiceDAO dao;
    @Inject
    private Request request;
    @Inject
    private AjaxResponseRenderer renderer;

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
    private Integer typeId;
    @Property
    @Persist
    private Integer roomId;
    @Property
    @Persist
    // IF Event state equals 6 - then it is paid
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

    private List<Event> cachedEvents;

    private SQLQuery createEventQueryFromSP(String spName){
        String queryString = new StringBuilder().
                append("CALL ").
                append(spName).
                append("(").
                append(":event_teacher_id, ").
                append(":event_facility_id, ").
                append(":event_room_id, ").
                append(":event_type_id, ").
                append(":event_state, ").
                append(":event_left_date, ").
                append(":event_right_date").
                append(");").toString();

        SQLQuery query = session.createSQLQuery(queryString);

        setEventQueryParams(query);

        return query;
    }

    private void setEventQueryParams(Query query) {
        query.setParameter("event_teacher_id", teacherId);
        query.setParameter("event_facility_id", facilityId);
        query.setParameter("event_room_id", roomId);
        query.setParameter("event_type_id", typeId);
        query.setParameter("event_state", state);
        query.setParameter("event_left_date", date1);
        query.setParameter("event_right_date", date2);
    }

    public List<Event> getEvents() {
        if(cachedEvents == null) {
            Query query = createEventQueryFromSP("getEvents").
                    addScalar("id").
                    addScalar("teacherName").
                    addScalar("facilityName").
                    addScalar("roomName").
                    addScalar("date").
                    addScalar("stateName").
                    addScalar("comment").
                    addScalar("typeName").
                    addScalar("price", new IntegerType()).
                    addScalar("clientNames").
                    setResultTransformer(new AliasToBeanResultTransformer(Event.class));
            cachedEvents = query.list();

            if (cachedEvents != null)
                return cachedEvents;
            else
                return new ArrayList<>();
        }
        else
            return cachedEvents;
    }

    public int getMoney() {
        Query query = createEventQueryFromSP("getMoney").
                        addScalar("money", new IntegerType());
        return (int)query.uniqueResult();

    }

    public int getPercentedMoney() {
        return percent != null ? (getMoney() * percent / 100) : 0;
    }

    public int getShare() {
        Query query = createEventQueryFromSP("getSchoolShare").
                addScalar("money", new IntegerType());

        return  (int)query.uniqueResult();
    }


    public int getTeacherShare() {
        Query query = createEventQueryFromSP("getTeacherShare").
                addScalar("money", new IntegerType());

        return (int)query.uniqueResult();
    }

    Object onValueChangedFromFacilityId(Integer facId) {
        facilityId = facId;
        roomSelect = facilityId == null ?
                     new RoomSelectModel(null) :
                     new RoomSelectModel(dao.find(Facility.class, facilityId));
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

        roomSelect = facilityId == null ?
                     new RoomSelectModel(null) :
                     new RoomSelectModel(dao.find(Facility.class, facilityId));

        typeSelect = new TypeTitleSelectModel(dao);
    }
}
