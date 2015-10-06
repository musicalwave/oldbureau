package tap.execounting.pages;

import java.util.ArrayList;
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

import org.hibernate.*;

import org.hibernate.criterion.Restrictions;
import org.hibernate.type.IntegerType;
import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.dal.mediators.interfaces.EventMed;
import tap.execounting.data.EventState;
import tap.execounting.models.selectmodels.FacilitySelectModel;
import tap.execounting.models.selectmodels.RoomSelectModel;
import tap.execounting.models.selectmodels.TeacherSelectModel;
import tap.execounting.models.selectmodels.TypeTitleSelectModel;
import tap.execounting.entities.Event;
import tap.execounting.entities.Facility;
import tap.execounting.entities.Teacher;

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


    private Criteria getEventListCriteria()
    {
        Criteria criteria = session.createCriteria(Event.class);

        if(date1 != null)
            criteria.add(Restrictions.ge("date", date1));

        if(date2 != null)
            criteria.add(Restrictions.le("date", date2));

        if(teacherId != null)
            criteria.add(Restrictions.eq("hostId", teacherId));

        if (state != null)
        {
            if(state == 6)
                criteria.add(Restrictions.disjunction().
                        add(Restrictions.eq("state", EventState.complete.getCode())).
                        add(Restrictions.eq("state", EventState.failedByClient.getCode())));
            else
                criteria.add(Restrictions.eq("state", state));
        }

        if(facilityId != null)
            criteria.add(Restrictions.eq("facilityId", facilityId));

        if(roomId != null)
            criteria.add(Restrictions.eq("roomId", roomId));

        if(typeId != null)
            criteria.add(Restrictions.eq("typeId", typeId));

        return criteria;
    }


    private SQLQuery createStatQueryFromSP(String spName){

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

        setStatQueryParams(query);

        return query;
    }

    private void setStatQueryParams(Query query) {
        query.setParameter("event_teacher_id", teacherId);
        query.setParameter("event_facility_id", facilityId);
        query.setParameter("event_room_id", roomId);
        query.setParameter("event_type_id", typeId);
        query.setParameter("event_state", state);
        query.setParameter("event_left_date", date1);
        query.setParameter("event_right_date", date2);
    }

    public List<Event> getEvents() {

        Criteria criteria = getEventListCriteria();

        if(criteria != null)
        {
            List eventList = criteria.list();
            if(eventList != null)
                return eventList;
        }

        return new ArrayList<Event>();
    }

    public int getMoney() {

        Query query = createStatQueryFromSP("getMoney").
                        addScalar("money", new IntegerType());
        return (int)query.uniqueResult();
    }

    public int getPercentedMoney() {
        return percent != null ? (getMoney() * percent / 100) : 0;
    }

    public int getShare() {

        Query query = createStatQueryFromSP("getSchoolShare").
                addScalar("money", new IntegerType());

        return  (int)query.uniqueResult();
    }


    public int getTeacherShare() {
        Query query = createStatQueryFromSP("getTeacherShare").
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
