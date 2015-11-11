package tap.execounting.components.reports;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.data.GridPagerPosition;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.type.TimestampType;
import tap.execounting.dal.mediators.interfaces.ContractMed;
import tap.execounting.dal.mediators.interfaces.PaymentMed;
import tap.execounting.entities.Client;
import tap.execounting.entities.Payment;

import java.util.List;

/**
 * User: truth0
 * Date: 2/24/13
 * Time: 5:02 PM
 */
public class ClientsSoonPayments {
    // App bits
    @Inject
    Session session;
    @Inject
    PaymentMed med;

    // Tapestry bits
    @Inject
    ComponentResources resources;
    @Inject
    BeanModelSource beanModelSource;

    @Parameter
    @Property
    private GridPagerPosition pagerPosition = GridPagerPosition.BOTH;
    @Parameter("1000")
    @Property
    private int rows;

    // Screen fields
    @Property
    private BeanModel<Client> model;
    @Property
    private Client client;
    // Page components
    @Property
    private Payment loopPayment;

    @Inject
    private ContractMed contractMed;

    void setupRender() {
        if (model == null) {
            model = beanModelSource.createDisplayModel(
                        Client.class,
                        resources.getMessages());

            model.include("name",
                          "firstPlannedPaymentDatePreload",
                          "facilityName");

            model.add("plannedPayments", null);
            model.add("comment", null);

            model.get("plannedPayments").sortable(false);
            model.get("comment").sortable(false);
        }
    }

    public List<Client> getSource() {
        Query query = session.createSQLQuery("CALL getClientsWithPendingPayments()").
                addScalar("id").
                addScalar("name").
                addScalar("comment").
                addScalar("commentDate", new TimestampType()).
                addScalar("firstPlannedPaymentDatePreload").
                addScalar("facilityName").
                setResultTransformer(new AliasToBeanResultTransformer(Client.class));

        List<Client> clientList = query.list();

        // load clients' contracts
        // to get contracts' payments which are
        // loaded implicitly by Hibernate
        for(Client client : clientList)
            client.setContracts(contractMed.reset().retainByClient(client).getGroup());

        return clientList;
    }

    /**
     * Handles event triggered by submitting the edit, in child MiniPayment
     * @param paymentId
     * @return
     */
    boolean onRefreshMiniPaymentZone(int paymentId){
        loopPayment = med.getUnitById(paymentId);
        return true;
    }
}
