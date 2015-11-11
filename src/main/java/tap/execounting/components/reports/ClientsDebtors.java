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
import org.hibernate.type.IntegerType;
import org.hibernate.type.TimestampType;
import tap.execounting.entities.Client;

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
    private Session session;

    // Screen fields
    @Property
    private BeanModel<Client> model;
    @Property
    private Client client;

    void setupRender(){
        if (model == null) {
            model = beanModelSource.createDisplayModel(
                        Client.class,
                        resources.getMessages());

            model.include("name", "managerName", "debt");
            model.add("comment", null);
            model.get("comment").sortable(false);
        }
    }

    public List<Client> getDebtors() {
        Query query = session.createSQLQuery("CALL getDebtors()").
                addScalar("id").
                addScalar("name").
                addScalar("comment").
                addScalar("commentDate", new TimestampType()).
                addScalar("managerName").
                addScalar("debt", new IntegerType()).
                setResultTransformer(new AliasToBeanResultTransformer(Client.class));
        return query.list();
    }
}
