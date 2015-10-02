package tap.execounting.dal.mediators;

import org.apache.tapestry5.ioc.ServiceBinder;

import tap.execounting.dal.mediators.interfaces.ClientMed;
import tap.execounting.dal.mediators.interfaces.ContractMed;
import tap.execounting.dal.mediators.interfaces.EventMed;
import tap.execounting.dal.mediators.interfaces.PaymentMed;
import tap.execounting.dal.mediators.interfaces.TeacherMed;




public class MediatorModule {
	public static void bind(ServiceBinder binder) {
		binder.bind(ContractMed.class, ContractMediator.class);
		binder.bind(ClientMed.class, ClientMediator.class);
		binder.bind(PaymentMed.class, PaymentMediator.class);
		binder.bind(EventMed.class, EventMediator.class);
		binder.bind(TeacherMed.class, TeacherMediator.class);
	}

}
