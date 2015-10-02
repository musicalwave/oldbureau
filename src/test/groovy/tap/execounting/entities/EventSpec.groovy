package tap.execounting.entities

import org.apache.tapestry5.beanvalidator.BeanValidatorModule
import org.apache.tapestry5.hibernate.HibernateCoreModule
import org.apache.tapestry5.ioc.annotations.SubModule
import org.apache.tapestry5.services.TapestryModule
import spock.lang.Specification
import tap.execounting.dal.CRUDServiceDAO
import tap.execounting.dal.HibernateCrudServiceDAO
import tap.execounting.dal.HibernateModule
import tap.execounting.services.AppModule

import static tap.execounting.entities.Event.FREE_FROM_SCHOOL
import static tap.execounting.entities.Event.FREE_FROM_TEACHER
import static tap.execounting.entities.Event.NOT_FREE
import static tap.execounting.util.Helper.genSessionFactory

/**
 * User: truth0
 * Date: 3/17/13
 * Time: 3:30 PM
 */
@SubModule([TapestryModule, BeanValidatorModule, HibernateCoreModule, HibernateModule, AppModule])
class EventSpec extends Specification{

    EventType type = new EventType(
            price: 2000,
            shareTeacher: 1000,
            title: 'guitar')
    Event event = new Event(eventType: type)
    def setFree = {freeType->event.setFree freeType}
    def teacherMoneyIs = {amount -> event.getTeacherMoney() .equals amount}
    def schoolMoneyIs = {amount -> event.getSchoolMoney() .equals amount}

    CRUDServiceDAO dao;

    def 'free status computation'(){
        def e = new Event()

        when: 'event is not free'
        e.setFree NOT_FREE
        then: 'every free method will return false'
        e.isFree() == false
        e.isFreeFromSchool() == false
        e.isFreeFromTeacher() == false

        when: 'event is free from school'
        event.setFree FREE_FROM_SCHOOL
        then: 'isFreeFromSchool will return true'
        event.isFreeFromSchool() == true
        event.isFree() == true
        event.isFreeFromTeacher() == false

        when: 'event is free from teacher'
        event.setFree FREE_FROM_TEACHER
        then: 'is free from teacher will return true'
        event.isFree() == true
        event.isFreeFromSchool() == false
        event.isFreeFromTeacher() == true
    }

    def "queries are working"(){
        dao = new HibernateCrudServiceDAO(session: genSessionFactory().openSession())
        def map = ['clientId' : 2]
        List<Event> events

        when: "user is asking events by client id"
        events = dao.findWithNamedQuery(Event.BY_CLIENT_ID, map)
        then: "query works"
        events.size() > 0
        events.each {println it.contracts[0].client.name}

        when: "user is asking events by client id and date"
        map.date1 = Date.parse('dd M yyyy', '05 02 2013')
        map.date2 = Date.parse('dd M yyyy', '07 04 2013')
        events = dao.findWithNamedQuery(Event.BY_CLIENT_ID_AND_DATES, map)
        then: "query returns events filtered by date and client id"
        events.each {println it.contracts[0].client.name + " " + it.date}
        events.every { it.date <= map.date2 }
        events.every { it.date >= map.date1 }
    }

    def 'event returns money like a boss'(){
        when: 'event is not free'
        setFree NOT_FREE
        then: 'it returns 1000 for school and 1000 for teacher'
        teacherMoneyIs 1000
        schoolMoneyIs 1000

        when: 'event is free from school'
        setFree FREE_FROM_SCHOOL
        then: 'it should return 0 as school money'
        schoolMoneyIs 0
        teacherMoneyIs 1000

        when: 'event is free from teacher'
        setFree FREE_FROM_TEACHER
        then: 'it should return 0 for school and 0 for teacher'
        schoolMoneyIs 0
        teacherMoneyIs 0
    }
}
