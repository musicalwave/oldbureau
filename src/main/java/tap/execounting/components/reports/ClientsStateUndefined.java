package tap.execounting.components.reports;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.data.GridPagerPosition;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import tap.execounting.dal.mediators.interfaces.ClientMed;
import tap.execounting.dal.mediators.interfaces.EventMed;
import tap.execounting.data.ClientState;
import tap.execounting.entities.Client;
import tap.execounting.entities.Comment;
import tap.execounting.entities.Event;
import tap.execounting.util.DateUtil;

import java.util.Date;
import java.util.List;

public class ClientsStateUndefined {
    // Useful bits
    @Inject
    private ComponentResources resources;
    @Inject
    private ClientMed med;
    @Inject
    private EventMed eventMed;
    @Inject
    private BeanModelSource beanModelSource;
    @Inject
    private Messages messages;

    // Screen fields
    @Property
    private BeanModel<Client> model;
    @Property
    private Client client;

    // Screen fields
    @Parameter @Property
    private GridPagerPosition pagerPosition = GridPagerPosition.BOTH;
    @Parameter("1000") @Property
    private int rows;

    public void setupRender(){
        // subscription ended
        if (model == null) {
            model = beanModelSource.createDisplayModel(Client.class,
                    resources.getMessages());
            model.add("lastEventDate", null);
            model.add("info", null);
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


    public List<Client> getUndefinedClients() {
        List<Client> list = med.reset().retainByState(ClientState.inactive).sortByLastEventDate().getGroup(true);
        return list;
    }
    public String getLastEventDate(){
        if (client.getContracts().size() == 0)
            return messages.get("no-contracts");
        Event lastEvent = null;
        try {

            lastEvent = eventMed.retainByClientIdAndDates(client.getId(),
                    DateUtil.fromNowPlusDays(-31),
                    DateUtil.fromNowPlusDays(1)).lastByDate();
            return DateUtil.format("dd.MM.YY", lastEvent.getDate());

        } catch (IndexOutOfBoundsException e) {
            return messages.format("no-events", DateUtil.format("d M Y", DateUtil.fromNowPlusDays(-31)));
        }
    }

    // Used to display the date of the latest event
    public String getInfo() {
        // TODO optimize that by caching recent events
        if (client.getContracts().size() == 0)
            return messages.get("no-contracts");
        Event lastEvent = null;
        try {
            lastEvent = eventMed.lastByDate();
        } catch (IndexOutOfBoundsException e) {
            return messages.format("no-events", DateUtil.format("d M Y", DateUtil.fromNowPlusDays(-31)));
        } finally {
            eventMed.reset();
        }
        return lastEvent.getEventType().getTitle();
    }

    public String getComment() {
        Comment c = med.setUnit(client).getComment();
        return c == null ? "" : c.getText();
    }

    public Date getCommentDate() {
        Comment c = med.setUnit(client).getComment();
        return c == null ? null : c.getDate();
    }
}
