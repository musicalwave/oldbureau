package tap.execounting.entities

import org.apache.tapestry5.beanvalidator.BeanValidatorModule
import org.apache.tapestry5.hibernate.HibernateCoreModule
import org.apache.tapestry5.ioc.annotations.SubModule
import org.apache.tapestry5.services.TapestryModule
import spock.lang.Specification
import tap.execounting.dal.HibernateModule
import tap.execounting.data.ContractState
import tap.execounting.data.EventState
import tap.execounting.services.AppModule
import tap.execounting.util.DateUtil

import static tap.execounting.data.ContractState.active
import static tap.execounting.data.ContractState.canceled
import static tap.execounting.data.EventState.planned
import static tap.execounting.entities.ContractType.Trial
import static tap.execounting.entities.Event.FREE_FROM_SCHOOL
import static tap.execounting.entities.Event.FREE_FROM_TEACHER
import static tap.execounting.util.Helper.genContract

/**
 * User: truth0
 * Date: 3/20/13
 * Time: 8:37 PM
 */
@SubModule([TapestryModule, BeanValidatorModule, HibernateCoreModule, HibernateModule, AppModule])
class ContractSpec extends Specification {
    def "frozen state of a contract is calculated correctly" (){
        Contract con
        when: "date of freeze and unfreeze are null"
        con = new Contract()
        then: "contract is not frozen"
        !con.frozen

        when: "date of freeze and unfreeze are set up and now is between them"
        con = new Contract(
                dateFreeze: DateUtil.fromNowPlusDays(-10),
                dateUnfreeze: DateUtil.fromNowPlusDays(10))
        then: "contract is frozen"
        con.isFrozen()

        when: "date of freeze and unfreeze are ahead from now"
        con = new Contract(
                dateFreeze: DateUtil.fromNowPlusDays(10),
                dateUnfreeze: DateUtil.fromNowPlusDays(20))
        then: "contract is not frozen"
        !con.isFrozen()

        when: "date of freeze and unfreeze are gone"
        con = new Contract(
                dateFreeze: DateUtil.fromNowPlusDays(-20),
                dateUnfreeze: DateUtil.fromNowPlusDays(-5))
        then: "contract is not frozen"
        !con.isFrozen()
    }

    def "completed state of a contract is computed correctly"(){
        Contract contract
        when: "contract have X (10) lessons, and X - 1 (9) are completed"
        contract = genContract(10,9)
        then: "contract is active"
        contract.state.equals active

        when: "contract have 10 lessons, and 10 are completed"
        contract = genContract(10,10)
        then: "contract is completed"
        contract.state.equals ContractState.complete

        when: "contract is completed and canceled"
        contract = genContract(10,10)
        contract.canceled = true
        then: "contract is complete"
        contract.isCanceled()
        contract.isComplete()
        !contract.isFrozen()
        !contract.isActive()
        contract.state == ContractState.complete

        when: "contract is not completed and canceled"
        contract = genContract()
        assert contract.state == active
        contract.canceled = true
        then: 'he is still canceled'
        contract.state.equals canceled

        when: 'contract type is trial and contract is complete by lessons'
        contract = genContract(1,1)
        contract.contractTypeId = Trial
        then: 'it is complete and state equals complete'
        contract.isComplete()
        contract.state.equals ContractState.complete
    }

    def 'free lessons should not be counted as completed in standard contract'(){
        def contract = genContract 5,5
        contract .setEventsNumber 10
        def answer_is = { num -> contract.getEventsRemain() .equals num }
        def type = new EventType(title: 'Air Guitar')
        contract.events.each { it.eventType = type }

        when: 'you have an incomplete contract without free lessons'
        assert contract.events.every { !it.isFree() }
        then: 'lessons remaining equals 5'
        answer_is 5

        when: 'you add one event free from school'
        contract.events << new Event(free: FREE_FROM_SCHOOL, eventType: type)
        then: 'answer is still 5'
        answer_is 5

        when: 'you  add one event free from teacher'
        contract.events << new Event(free: FREE_FROM_TEACHER, eventType: type)
        then: 'answer is still 5'
        answer_is 5
    }

    def 'Contract::getCompleteEventsCost is computed as sum of every event price'(){
        Contract con = new Contract()
        // Useful closures
        def answer = {con.completeEventsCost}
        def Type = { title, price -> new EventType(title: title, price: price) }
        def Vent = { event_type, eState = EventState.complete ->
            new Event(eventType: event_type, state: eState) }
        def addEvent = { type, state -> con.events<<Vent(type,state) }
        def types = [
                AirGuitar: Type('Air Guitar', 1000),
                Bitching: Type('Bitching behavior', 1500),
                Jamming: Type('Jamming', 2000),
                ShoeGazing: Type('Gazing', 3000) ]
        def events = [
                Vent(types.AirGuitar),
                Vent(types.Bitching),
                Vent(types.Jamming),
                Vent(types.ShoeGazing) ]
        // Look at the balance computation rules in the contract comments
        when: 'a user needs a balance of contract'
        con.events = events
        then: 'a balance is computed from the sum of every lesson price'
        answer() .equals 7500

        when: 'you add an incomplete event'
        addEvent types.AirGuitar, planned
        addEvent types.ShoeGazing, planned
        addEvent types.ShoeGazing, planned
        then: 'nothing changes'
        answer() .equals 7500

        when: 'you add complete event'
        def e1 = Vent(types.AirGuitar)
        con.events << e1
        then: 'complete events cost will be increased'
        answer() .equals 8500

        when: 'you mark that event as free from school'
        e1.setFree Event.FREE_FROM_SCHOOL
        then: 'it shall not be summarized'
        answer() .equals 7500

        when: 'you mark last event as free from teacher'
        e1.setFree Event.FREE_FROM_TEACHER
        then: 'the same thing'
        answer() .equals 7500
    }
}
