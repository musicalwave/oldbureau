package tap.execounting.dal

import org.apache.tapestry5.beanvalidator.BeanValidatorModule
import org.apache.tapestry5.hibernate.HibernateCoreModule
import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.ioc.annotations.SubModule
import org.apache.tapestry5.services.TapestryModule
import spock.lang.Specification
import tap.execounting.dal.mediators.ClientMediator
import tap.execounting.dal.mediators.ContractMediator
import tap.execounting.dal.mediators.interfaces.ClientMed
import tap.execounting.entities.Client
import tap.execounting.entities.Contract
import tap.execounting.entities.ContractType
import tap.execounting.services.AppModule

import static tap.execounting.entities.ContractType.Standard
import static tap.execounting.entities.ContractType.Trial
import static tap.execounting.util.DateUtil.*
import static tap.execounting.util.Trans.*

import static tap.execounting.data.ClientState.beginner
import static tap.execounting.data.ClientState.canceled
import static tap.execounting.data.ClientState.continuer
import static tap.execounting.data.ClientState.frozen
import static tap.execounting.data.ClientState.inactive
import static tap.execounting.data.ClientState.trial
import static tap.execounting.util.Helper.*

@SubModule([TapestryModule, BeanValidatorModule, HibernateCoreModule, HibernateModule, AppModule])
class ClientMedSpec extends Specification {

    @Inject
    ClientMed med

    ClientMediator mediator = new ClientMediator(contractMed: new ContractMediator())

    List<Client> folks

    Date lowerBound, upperBound

    // Verbal sugar
    int zero = 0, one = 1, first = 0, two = 2, second = 1, three = 3, third = 2, four = 4, fourth = 3

    // This is used when you ask date service to floor or ceil its produced date
    boolean ceil = true, floor = false
    boolean yes = true, no = false

    def answer // closure to access some computations in mehtods

    def setup() {
        med.setGroup genGuys()
    }

    def 'state of client is computed correctly'(){
        Client glen = new Client(name: 'Glen')
        when: 'client has completed trial contract, and no other'
        glen.contracts << new Contract(contractTypeId: Trial, events: genEvents(1,1), eventsNumber: 1)
        assert glen.contracts[first].complete
        med.unit = glen
        then: 'he is inactive'
        med.state.equals inactive

        when: 'client has completed trial contract, and active standard contract'
        glen.contracts << new Contract(contractTypeId: Standard, events: genEvents(10,5), eventsNumber: 10)
        then: 'he is beginner'
        med.state.equals beginner

        when: 'glen has one active standard contract and one complete standard'
        glen.contracts << new Contract(contractTypeId: Standard, events: genEvents(10,10), eventsNumber: 10)
        then: 'he is continuer'
        med.state.equals continuer
    }

    def 'became trial method is not a fool'(){
        lowerBound = fromNowPlusDays(-10)
        upperBound = fromNowPlusDays(10)
        answer = { mediator.becameTrial lowerBound, upperBound }

        when: 'user asks: "Is Glen, that new guy, without contracts, a trial student?"'
        def glen = new Client(name: 'Glen')
        mediator.unit = glen
        then: 'answer is "NNONONONO"'
        answer() .equals no

        when: 'glen have bought his first contract 15 days ago, and user asks, if he ' +
                'became trial between from -10 to 10 days.'
        glen.contracts << new Contract(contractTypeId: Trial, date: fromNowPlusDays(-15))
        then: 'answer is still no'
        answer() .equals no

        when: 'user asks if glen became trial, in period between -20 and -10 days'
        lowerBound = fromNowPlusDays(-20)
        upperBound = fromNowPlusDays(-10)
        then: 'the answer is yes'
        answer() .equals yes
    }
    def 'became trials'(){
        med.setGroup genTrialsNovicesContinuers()

        when: 'user wants those clients who became trials from 60 to 35 days ago'
        lowerBound = fromNowPlusDays(-60, floor); upperBound = fromNowPlusDays(-35, ceil);
        med.becameTrials lowerBound, upperBound
        folks = med.group
        folks.each {println it.name}

        then: 'there will be only those folks who have TRIAL contract in that ' +
                'date range. Those are: Lisa, Scott and Meg.'
        folks.size().equals three
        ['Lisa', 'Scott', 'Meg'].containsAll folks.collect { it.name }
        folks.every { fella -> fella.contracts.every { it.contractTypeId.equals Trial } }
        folks.every { fella -> fella.contracts.every { it.isBetweenDates lowerBound, upperBound } }
    }

    def 'became novice is no fool at all'(){
        lowerBound = fromNowPlusDays(-20)
        upperBound = fromNowPlusDays(-5)
        answer = { mediator.becameNovice lowerBound, upperBound }

        when: 'user asks "Did our dear Glen became novice from 20 to 5 days ago",' +
                'while Glen does not have contracts at all'
        def glen = new Client(name: 'Glen')
        mediator.unit = glen
        then: 'the answer is no'
        answer() .equals no

        when: 'glen has one contract out of range'
        def contract = new Contract(contractTypeId: Standard, date: fromNowPlusDays(-25))
        glen.contracts .add contract
        then:
        answer() .equals no

        when: 'glen has contract with the range'
        contract.date = fromNowPlusDays(-15)
        then:
        answer() .equals yes

        when: 'the only contract is trial'
        contract .setContractTypeId Trial
        then:
        answer() .equals no

        when: 'Glen has another contract 25 days ago'
        contract .setContractTypeId Standard
        glen.contracts .add new Contract(contractTypeId: Standard, date: fromNowPlusDays(-25))
        then: 'he became novice 25 days ago, but not in 20 to 5'
        answer() .equals no

        when: 'earliest contract is trial'
        glen.contracts [second] .contractTypeId = Trial
        then: 'he is novice now'
        answer() .equals yes
    }
    def 'became novices'(){
        med .setGroup genTrialsNovicesContinuers()

        when: 'user want to see only those clients who became novices from 40 to 10 days ago'
        lowerBound = fromNowPlusDays(-40, floor); upperBound = fromNowPlusDays(-10,ceil);
        med.becameNovices lowerBound, upperBound
        folks = med.group

        then: 'there will be only those who have FIRST STANDARD contract in that ' +
                'period. Those are: Scott, Mike and Meg. They also will have only' +
                'one contract.'
        ['Scott', 'Mike', 'Meg'].containsAll folks.collect {it.name}
        folks.size().equals three
        folks.every { fella -> fella.contracts[first].isBetweenDates lowerBound, upperBound }
        folks.every { fella -> fella.contracts[first].contractTypeId.equals Standard }
        folks.every { fella -> fella.contracts.size().equals one }

        when: 'user want to see only those clients who became novices from 50 to 10 days ago'
        med.setGroup genTrialsNovicesContinuers()
        lowerBound = fromNowPlusDays(-50, floor)
        med.becameNovices lowerBound, upperBound
        folks = med.group

        then: 'we will have also the Mick'
        ['Scott', 'Mike', 'Meg', 'Mick'].containsAll folks.collect {it.name}
        folks.size().equals four
        folks.every { fella -> fella.contracts[first].isBetweenDates lowerBound, upperBound }
        folks.every { fella -> fella.contracts[first].contractTypeId.equals Standard }
        folks.every { fella -> fella.contracts.size().equals one }
    }

    def 'became continuer method is no fool'(){
        lowerBound = fromNowPlusDays(-20)
        upperBound = fromNowPlusDays(-5)
        answer = { mediator .becameContinuer lowerBound, upperBound }
        def contract = new Contract(date: fromNowPlusDays(-10), contractTypeId: Trial)
        def glen = new Client()

        when: 'Glen does not has any contract'
        mediator.unit = glen
        then: 'he is not continuer'
        answer() .equals no

        when: 'Glen has trial contract'
        glen.contracts .add contract
        then: 'He is not continuer'
        answer() .equals no

        when: 'the contract is standard'
        contract .contractTypeId = Standard
        then: 'Glen is a novice, not a continuer'
        answer() .equals no

        when: 'Glen has second standard contract'
        glen.contracts << new Contract(contractTypeId: Standard, date: fromNowPlusDays(-25))
        then: 'He has second standard contract between these dates.'
        answer() .equals yes
    }
    def 'became continuers'(){
        med.setGroup genTrialsNovicesContinuers()

        when: 'user want to see those who became continuers from 40 to 10 days ago'
        lowerBound = fromNowPlusDays(-40, floor)
        upperBound = fromNowPlusDays(-10, ceil)
        med.becameContinuers lowerBound, upperBound
        folks = med.group

        then: 'he will see Meg and Mick. They will have only SECOND and following ' +
                'standard contracts, which also will be filtered by period of 40  ' +
                'to 10 days ago.'
        ['Meg','Mick'].containsAll folks.collect {it.name}
        folks.size().equals two
        folks.every { man ->
            man.contracts.every { contract ->
                contract.isBetweenDates lowerBound, upperBound
                contract.contractTypeId.equals Standard }}
    }

    def 'retain clients who have scheduled payments within 14 days'() {
        when:
        med.retainBySoonPayments(14)
        folks = med.group

        then:
        folks.each { println it.name }
        folks.size().equals two
        folks.any {man -> man.name == 'Glen'}
        folks.any {man -> man.name == 'Greg'}
    }

    def 'client state is computed correctly'() {
        med = new ClientMediator(contractMed: new ContractMediator())
        when: "client has zero contracts"
        med.unit = genClient(0,0)
        then: "client is inactive"
        med.state == inactive

        when: "client has 5 finshed contracts"
        med.unit = genClient(5,5)
        then: "client is inactive"
        med.state == inactive

        when: "client has 4 completed contracts and one active"
        med.unit = genClient()
        then: "he is continuer (instead of being simply active)"
        med.state == continuer

        when: "client has 4 completed contracts and one frozen"
        med.unit = genClient(4,4)
        def frozen_contract = new Contract(
                eventsNumber: 10,
                dateFreeze: fromNowPlusDays(-5),
                dateUnfreeze: fromNowPlusDays(5)
        )
        med.unit.contracts << frozen_contract
        then: "he is frozen"
        frozen_contract.isFrozen()
        med.hasFrozenContracts()
        med.state == frozen

        when: "man has 3 active contract and he is canceled"
        med.unit = genClient(3,0)
        med.unit.canceled = true
        then: "he is canceled"
        med.state == canceled

        when: "man has one active standard contract"
        med.unit = genClient(1,0)
        then: "he is beginner"
        med.state == beginner

        when: "man has more than one standard contract, and one of them is active"
        med.unit == genClient(2, 1)
        med.unit.contracts = genContracts(2,1)
        assert med.unit.contracts.size() == 2
        then: "he is continuer"
        med.state.equals continuer

        when: "man has 2 trial contracts, and at least one of them is active"
        med.unit = new Client(name: "Glen")
        med.unit.contracts << new Contract(
                                contractTypeId: ContractType.Trial,
                                events: genEvents(3,2),
                                eventsNumber: 3)
        med.unit.contracts << new Contract(
                                contractTypeId: ContractType.Trial,
                                events: genEvents(3,3),
                                eventsNumber: 3)
        then: "he is still trial"
        med.state.equals trial
    }

    def "contracts to clients"(){
        List<Contract> contracts = med.group.collect { it.contracts[0] }
        when: "contracts are being converted to clients"
        def clients = contractsToClients(contracts)
        then: "clients are the same"
        ['Glen', 'Mark', 'Greg', 'Jack'].each { out -> clients.any { it.name == out } }
        clients.size().equals four
    }

    def 'clients to contracts'(){
        when:
        def contracts = med.group.collectMany { it.contracts }
        then:
        contracts.every { it instanceof Contract }
    }

    def "retain by name"(){
        when: "user filters clients by name"
        med.retainByName 'glen'
        then: "there is only Glen"
        med.group[0].name.equals 'Glen'
        med.group.size().equals one

        when: "user filters by another name"
        med.retainByName 'greg'
        then: "there is no clients at all"
        med.group.size().equals zero
    }

    def "retain by date of first contract"(){
        med.setGroup genGuys()
        when: 'user filters clients by date of first contract'
        def date1 = fromNowPlusDays(-33); floor date1;
        // TODO include lower and upper borders the filter
        // TODO check DateService
        def date2 = fromNowPlusDays(-20); ceil date2
        med.retainByDateOfFirstContract date1, date2
        then: 'there are Mark and Jack'
        med.group.any { it.name.equals 'Jack' }
        med.group.any { it.name.equals 'Mark' }
        med.group.size().equals 2

        when: 'user filters clients by date of first contract'
        date1 = fromNowPlusDays(-16); floor date1
        date2 = fromNowPlusDays(-14); ceil date2
        med.setGroup genGuys()
        med.retainByDateOfFirstContract date1, date2
        then: 'there are Greg and Glen'
        med.group.any { it.name.equals 'Greg' }
        med.group.any { it.name.equals 'Glen' }
        med.group.size().equals two
    }

    def "retain by active teacher"(){
        def teachers = genCustomTeachers()
        assert med.group.size() == four
        when: 'user filters clients by the teacher of their active contracts'
        med.retainByActiveTeacher(teachers[first]) // teacher Ivan
        then: 'there are only Glen and Jack'
        med.group.collect { it.name }.containsAll(['Glen', 'Jack'])

        when: 'user filters clients by the teacher of their active contracts'
        med.setGroup genGuys()
        med.retainByActiveTeacher(teachers[third]) // teacher Spock
        then: 'there are Mark, Jack and Greg'
        med.group.collect { it.name }.containsAll(['Mark', 'Greg', 'Jack'])
    }


}
