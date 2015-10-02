package tap.execounting.components.reports;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.data.GridPagerPosition;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import tap.execounting.dal.mediators.interfaces.ClientMed;
import tap.execounting.entities.Client;
import tap.execounting.entities.Comment;

import java.util.Date;
import java.util.List;

/**
 * User: truth0
 * Date: 2/24/13
 * Time: 5:05 PM
 */
public class ClientsDebtors {
    @Parameter @Property
    private GridPagerPosition pagerPosition = GridPagerPosition.BOTH;
    @Parameter("1000") @Property
    private int rows;
    // Useful bits
    @Inject
    private ComponentResources resources;
    @Inject
    private BeanModelSource beanModelSource;
    @Inject
    private ClientMed clientMed;

    // Screen fields
    @Property
    private BeanModel<Client> model;
    @Property
    private Client client;

    void setupRender(){
        if (model == null) {
            model = beanModelSource.createDisplayModel(Client.class,
                    resources.getMessages());
            model.add("debt", null);
            model.add("comment", null);
            model.exclude(
                    "balance",
                    "canceled",
                    "date",
                    "firstContractDate",
                    "firstPlannedPaymentDate",
                    "id",
                    "managerId",
                    "phoneNumber",
                    "return",
                    "state",
                    "studentInfo");
        }
    }

    public String getComment() {
        Comment c = clientMed.setUnit(client).getComment();
        return c == null ? "" : c.getText();
    }

    public Date getCommentDate() {
        Comment c = clientMed.setUnit(client).getComment();
        return c == null ? null : c.getDate();
    }
    public List<Client> getDebtors() {
        return clientMed.reset().retainDebtors().sortByName().getGroup(true);
    }

    public String getDebt() {
        return client.getBalance() * (-1) + "";
    }

}
