package tap.execounting.components.reports;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.data.GridPagerPosition;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import tap.execounting.dal.mediators.interfaces.ClientMed;
import tap.execounting.dal.mediators.interfaces.PaymentMed;
import tap.execounting.entities.Client;
import tap.execounting.entities.Comment;
import tap.execounting.entities.Payment;

import java.util.*;

/**
 * User: truth0
 * Date: 2/24/13
 * Time: 5:02 PM
 */
public class ClientsSoonPayments {
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

    void setupRender() {
        if (model == null) {
            model = beanModelSource.createDisplayModel(Client.class, resources.getMessages());
            model.add("paymentsInfo", null);
            model.add("comment", null);
            model.exclude(
                    "balance",
                    "canceled",
                    "date",
                    "firstContractDate",
                    "id",
                    "managerId",
                    "managerName",
                    "phoneNumber",
                    "return",
                    "state",
                    "studentInfo");
        }
    }

    public List<Client> getSource() {
        return clientMed.reset().retainBySoonPayments(14).getGroup(true);
    }

    public String getComment() {
        Comment c = clientMed.setUnit(client).getComment();
        return c == null ? "" : c.getText();
    }

    public Date getCommentDate() {
        Comment c = clientMed.setUnit(client).getComment();
        return c == null ? null : c.getDate();
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

    // App bits
    @Inject
    PaymentMed med;
    @Inject
    ClientMed clientMed;

    // Tapestry bits
    @Inject
    ComponentResources resources;
    @Inject
    BeanModelSource beanModelSource;
}
