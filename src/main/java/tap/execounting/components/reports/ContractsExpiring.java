package tap.execounting.components.reports;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.data.GridPagerPosition;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import tap.execounting.dal.mediators.interfaces.ClientMed;
import tap.execounting.dal.mediators.interfaces.ContractMed;
import tap.execounting.data.EventState;
import tap.execounting.entities.Comment;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Event;

import java.util.Date;
import java.util.List;

public class ContractsExpiring {
    // Screen fields
    @Parameter @Property
    private GridPagerPosition pagerPosition = GridPagerPosition.BOTH;
    @Parameter("1000") @Property
    private int rows;
    @Property
    private BeanModel<Contract> model;
    @Inject
    private ComponentResources resources;
    @Inject
    private BeanModelSource beanModelSource;
    @Inject
    private ContractMed med;
    @Inject
    private ClientMed clientMed;
    @Property
    private Contract contract;

    void setupRender(){
        if (model == null) {
            model = beanModelSource.createDisplayModel(Contract.class,
                    resources.getMessages());
            model.exclude(
                    "active",
                    "balance",
                    "canceled",
                    "clientId",
                    "complete",
                    "completeEventsCost",
                    "contractTypeId",
                    "date",
                    "dateFreeze",
                    "dateUnfreeze",
                    "discount",
                    "eventsNumber",
                    "eventsRemain",
                    "false",
                    "frozen",
                    "gift",
                    "giftMoney",
                    "lessonsNumber",
                    "money",
                    "moneyPaid",
                    "paid",
                    "singleEventCost",
                    "state",
                    "teacherId",
                    "typeId");

            model.add("name", null);
            model.add("info", null);
            model.add("lastEvent", null);

            model.reorder("name", "info", "lastEvent");
        }
    }
    public List<Contract> getSource(){
        return med.reset().retainExpiring(1).getGroup();
    }

    /**
     * How much events are left, and how much events has been already planned
     * @return
     */
    public String getInfo() {
        StringBuilder builder = new StringBuilder();
        if(contract.isComplete())
            throw new IllegalArgumentException("why is there completed contract");

        builder.append(contract.getEventType().getTitle() + ": ");
        builder.append(contract.getEventsRemain());
        if (contract.getEventsScheduled().size() > 0)
            builder.append(" и " + contract.getEventsScheduled().size()
                    + " уже запланировано");
        return builder.toString();
    }

    public Date getLastEventDate(){
        for(Event e : contract.getEvents())
            if(e.getState()== EventState.planned)
                return e.getDate();
        return null;
    }
    public Date getCommentDate(){
        Comment c = clientMed.setUnit(contract.getClient()).getComment();
        return c == null ? null : c.getDate();
    }
    public String getComment(){
        Comment c = clientMed.setUnit(contract.getClient()).getComment();
        return c == null ? "" : c.getText();
    }
}
