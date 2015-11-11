package tap.execounting.dal

import org.apache.tapestry5.beanvalidator.BeanValidatorModule
import org.apache.tapestry5.hibernate.HibernateCoreModule
import org.apache.tapestry5.ioc.annotations.SubModule
import org.apache.tapestry5.services.TapestryModule
import spock.lang.Specification
import tap.execounting.dal.mediators.PaymentMediator
import tap.execounting.dal.mediators.interfaces.PaymentMed
import tap.execounting.services.AppModule
import tap.execounting.util.DateUtil

import javax.inject.Inject

import static tap.execounting.util.Helper.genPayments

/**
 * User: truth0
 * Date: 3/21/13
 * Time: 2:42 PM
 */
@SubModule([TapestryModule, BeanValidatorModule, HibernateCoreModule, HibernateModule, AppModule])
class PaymentMedSpec extends Specification {

    @Inject
    PaymentMed paymentMed

    def "filters are fine"(){
        paymentMed = new PaymentMediator()

        when: "you have a lot of random payments, and you want to retain only scheduled"
        paymentMed.group = genPayments(1000, 500)
        paymentMed.retainByState(true)
        then: "there are only scheduled payments, indeed"
        paymentMed.group.every { pay -> pay.scheduled }

        when: "you have a lot of random payments, and you want to retain only completed"
        paymentMed.group = genPayments(1000, 500)
        paymentMed.retainByState(false)
        then: "there are only completed payments, indeed"
        paymentMed.group.every { pay -> !pay.scheduled }

        when: "you call retain to keep only scheduled, while you have only completed"
        assert paymentMed.group.size() == 500
        paymentMed.retainByState(true)
        then: "you won't have any payments"
        paymentMed.group.size() == 0

        when: "you count return from 10 payments with amount 1000"
        paymentMed.group = genPayments(10,10)
        then: "you get 10k return"
        paymentMed.countReturn(DateUtil.fromNowPlusDays(-1), DateUtil.fromNowPlusDays(1)) == 10000
    }
}
