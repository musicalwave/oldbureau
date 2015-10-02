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
 * Date: 6/6/13
 * Time: 8:15 AM
 */
@Access(level = TOP)
public class ClientPhone {
    @Inject
    ClientMed med;

    @Property
    private Client client;

    public List<Client> getClients(){
        return med.sortByName().getGroup();
    }
}
