package tap.execounting.dal

import org.apache.tapestry5.beanvalidator.BeanValidatorModule
import org.apache.tapestry5.hibernate.HibernateCoreModule
import org.apache.tapestry5.ioc.annotations.SubModule
import org.apache.tapestry5.services.TapestryModule
import spock.lang.Specification
import tap.execounting.dal.mediators.EventMediator
import tap.execounting.dal.mediators.interfaces.EventMed
import tap.execounting.services.AppModule

import static tap.execounting.util.Helper.*

/**
 * User: truth0
 * Date: 3/21/13
 * Time: 2:43 PM
 */
@SubModule([TapestryModule, BeanValidatorModule, HibernateCoreModule, HibernateModule, AppModule])
class EventMedSpec extends Specification{

    static EventMed med

    def setupSpec(){
        med = new EventMediator(dao: new HibernateCrudServiceDAO(session: genSessionFactory().openSession()))
    }

    def 'sort by date works like a boss'(){
        when: 'user sorts all events by date'
        med.sortByDate false
        def len = med.group.size()
        then: 'we have a lot of events and they are sorted'
        println "sort by date\namount of events: $len"
        (1..(len-1)).every { !med.group[it-1].date.before(med.group[it].date) }
    }

    def 'count school money'(){
        // Count school money
    }

    def 'count teacher money'(){

    }
}
