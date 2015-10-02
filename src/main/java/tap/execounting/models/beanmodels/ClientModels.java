package tap.execounting.models.beanmodels;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import tap.execounting.entities.Client;
import tap.execounting.services.Authenticator;

/**
 * User: truth0
 * Date: 3/31/13
 * Time: 4:54 PM
 */
public class ClientModels {
    @Inject
    private BeanModelSource modelSource;
    @Inject
    private ComponentResources comRes;
    @Inject
    private Authenticator auth;

    BeanModel<Client> getEditModel(){
        BeanModel<Client> model = modelSource.createDisplayModel(Client.class, comRes.getMessages());
        model.exclude("id", "return","balance","date","firstPlannedPaymentDate",
                "firstContractDate","managerName","managerId");
        if(auth.getLoggedUser().isTop())
            model.add("managerId");
        return model;}
}
