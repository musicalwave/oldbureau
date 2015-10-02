package tap.execounting.pages;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import tap.execounting.annotations.Access;
import tap.execounting.dal.mediators.interfaces.ClientMed;
import tap.execounting.entities.Client;

import java.util.List;

import static tap.execounting.security.AccessLevel.TOP;

/**
 * User: t00
 * Date: 5/25/13
 * Time: 4:47 PM
 */
@Access(level = TOP)
public class ClientsBalance {
    @Inject
    ClientMed med;

    @Property
    private Client client;

    public List<Client> getClients(){
        return med.sortByName().getGroup();
    }
}
