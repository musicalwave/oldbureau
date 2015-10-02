package tap.execounting.entities

import spock.lang.Specification

import static tap.execounting.util.DateUtil.fromNowPlusDays

/**
 * User: truth0
 * Date: 3/30/13
 * Time: 12:58 PM
 */
class ClientSpec extends Specification{
    
    def 'get date returns date of the first contract'(){
        def date1 = fromNowPlusDays(-20)
        def date2 = fromNowPlusDays(-30)
        def date3 = fromNowPlusDays(-50)
        Client glen = new Client(name: 'Glen',contracts: [])
        glen.contracts << new Contract(date: date1)
        glen.contracts << new Contract(date: date2)
        glen.contracts << new Contract(date: date3)

        when: 'we ask client about his date of first contract'
        def date = glen.getDate()
        then: 'we have the date of the earliest contract'
        date.equals date3

        when: 'we reorder dates'
        def c = glen.contracts
        glen.contracts = [c[2], c[0],c[1]]
        date = glen.getDate()
        then: 'we still have date3'
        date.equals date3
    }

}
