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
import org.hibernate.type.DateType;
import org.hibernate.type.TimestampType;
import tap.execounting.entities.Contract;
import java.util.List;

public class ContractsExpiring {
    // Screen fields
    @Parameter @Property
    private GridPagerPosition pagerPosition = GridPagerPosition.BOTH;
    @Parameter("1000") @Property
    private int rows;
    @Property
    private BeanModel<Contract> model;
    @Property
    private Contract contract;

    @Inject
    private ComponentResources resources;
    @Inject
    private BeanModelSource beanModelSource;
    @Inject
    Session session;

    void setupRender(){
        if (model == null) {
            model = beanModelSource.createDisplayModel(
                        Contract.class,
                        resources.getMessages());

            model.include("clientName",
                          "info",
                          "lastScheduledEventDate",
                          "lastScheduledEventFacility",
                          "comment");

            model.get("info").sortable(false);
            model.get("comment").sortable(false);
        }
    }
    public List<Contract> getSource(){
        Query query = session.createSQLQuery("CALL getExpiringContracts()").
                addScalar("id").
                addScalar("clientId").
                addScalar("clientName").
                addScalar("clientCommentDate", new TimestampType()).
                addScalar("clientCommentText").
                addScalar("info").
                addScalar("lastScheduledEventDate", new DateType()).
                addScalar("lastScheduledEventFacility").
                setResultTransformer(new AliasToBeanResultTransformer(Contract.class));
        return query.list();
    }
}
