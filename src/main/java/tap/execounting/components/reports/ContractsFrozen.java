package tap.execounting.components.reports;


import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.data.GridPagerPosition;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.hibernate.NonUniqueResultException;
import tap.execounting.dal.mediators.interfaces.ClientMed;
import tap.execounting.dal.mediators.interfaces.ContractMed;
import tap.execounting.data.ClientState;
import tap.execounting.data.ContractState;
import tap.execounting.entities.Client;
import tap.execounting.entities.Comment;
import tap.execounting.entities.Contract;

import java.util.Date;
import java.util.List;

public class ContractsFrozen {
    @Parameter
    @Property
    private GridPagerPosition pagerPosition = GridPagerPosition.BOTH;
    @Parameter("1000")
    @Property
    private int rows;
    // Useful bits
    @Inject
    private ComponentResources resources;
    @Inject
    private BeanModelSource beanModelSource;
    @Inject
    private ContractMed med;
    @Inject
    private ClientMed clientMed;

    // Screen fields
    @Property
    private BeanModel<Contract> model;
    @Property
    private Contract contract;


    void setupRender() {
        if (model == null) {
            model = beanModelSource.createDisplayModel(Contract.class,
                    resources.getMessages());
            model.add("name", null);
            model.exclude(
                    "active",
                    "balance",
                    "canceled",
                    "clientId",
                    "complete",
                    "completeEventsCost",
                    "contractTypeId",
                    "date",
                    "discount",
                    "eventsNumber",
                    "eventsRemain",
                    "false",
                    "frozen",
                    "gift",
                    "giftMoney",
                    "id",
                    "lessonsNumber",
                    "money",
                    "moneyPaid",
                    "paid",
                    "singleEventCost",
                    "state",
                    "teacherId",
                    "typeId");
            model.reorder("name", "dateFreeze", "dateUnfreeze");
        }
    }

    public List<Contract> getSource() {
        med.reset();
        med.retainByState(ContractState.frozen);
        med.sortByClientName();
        return med.getGroup(true);
    }

    public String getComment() {
        Comment c = clientMed.setUnit(contract.getClient()).getComment();
        return c == null ? "" : c.getText();
    }

    public Date getCommentDate() throws Exception {
        try {
            Comment c = clientMed.setUnit(contract.getClient()).getComment();
            return c == null ? null : c.getDate();
        } catch (NonUniqueResultException ex) {
            throw new Exception("Double comment for client: " + contract.getClientId());
        }
    }
}
