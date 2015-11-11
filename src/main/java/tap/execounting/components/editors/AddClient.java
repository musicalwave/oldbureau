package tap.execounting.components.editors;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.dal.ChainMap;
import tap.execounting.entities.Client;
import tap.execounting.entities.User;
import tap.execounting.models.selectmodels.UserSelectModel;
import tap.execounting.pages.CRUD;
import tap.execounting.services.Authenticator;

import java.util.List;

/**
 * @author truth0
 */
public class AddClient {

	@Property
	@Persist
	private boolean updateMode;

	@Property
	@Persist
	private Client client;
	
	@Component
	private Zone formaZone;
	
	// Useful, infrastructure bits of code
	@Inject
	private Messages messages;
	@Inject
	private CRUDServiceDAO dao;
    @Inject
    private Authenticator authenticator;
    @Inject
    private BeanModelSource source;
    @Inject
    private ComponentResources resources;
    @Property
    private SelectModel selectModel;

    @Property
    private BeanModel<Client> model;

	public void setup(Client c) {
		updateMode = true;
		client = c;
	}

	public void reset() {
		client = new Client();
		updateMode = false;
	}

    public void setupRender(){
        List<User> users = dao.findWithNamedQuery(User.ALL);
        selectModel = new UserSelectModel(users);
        model = source.createDisplayModel(Client.class, resources.getMessages());
        model.exclude("id",
                "return",
                "balance",
                "date",
                "firstPlannedPaymentDate",
                "firstContractDate",
                "managerName",
                "managerId",
                "firstPlannedPaymentDatePreload",
                "facilityName",
                "managerName",
                "debt");
        if(authenticator.getLoggedUser().isTop())
            model.add("managerId");
    }

	// Submit handling
	void onValidateFromForm() throws ValidationException {
		String duplicateExceptionMessageName = "addclient.error.duplicate";
		ValidationException ve = new ValidationException(messages.get(duplicateExceptionMessageName));
		
		// First -- check if for name duplication
		List<Client> clients = dao.findWithNamedQuery(Client.BY_NAME,
				ChainMap.with("name", client.getName()));
		
		// If there is no clients with such name -- it is good.
		if (clients.size() != 0) {
			// Else -- we could have two situations
			if (updateMode) {
				// If we have an update situation, and clients size more than -- something is wrong, for sure.
				if(clients.size()>1)
					throw ve;
				// If we found only one client -- CHECK ID. We should pass update, not duplicate
				if(clients.get(0).getId() != client.getId())
					throw ve;
			} else 
				// If we have creation situation, then we totally should have zero clients with such name
				throw ve;
		}
	}
	
	Object onFailure(){
		return formaZone;
	}
	
	Object onSuccess() {
		if (updateMode)
			dao.update(client);
		else
			dao.create(client);
		
		// CRUTCH alert. I don't know, any other way I could perform a page reload.
		// There should be a way, but not today.
		return CRUD.class;
	}
}
