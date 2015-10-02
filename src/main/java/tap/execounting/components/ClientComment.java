package tap.execounting.components;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import tap.execounting.dal.mediators.interfaces.ClientMed;

import java.util.Date;

/**
 * User: truth0
 * Date: 2/28/13
 * Time: 12:06 PM
 */
@Import(stylesheet = "context:css/components/ClientComment.css")
public class ClientComment {
    @Parameter(required = true)
    @Property
    private int clientId;
    @Inject
    private ClientMed clientMed;

    void setupRender() {
        clientMed.setUnitById(clientId);
    }

    public String getComment() {
        String comment = clientMed.getUnit().getComment();
        return comment == null ? "" : comment;
    }

    public void setComment(String comment) throws Exception {
        clientMed.setUnitById(clientId);
        if(clientMed.getUnit().getId() != clientId)
            throw new Exception("позвоните Ивану и скажите про это сообщение на странице клиента");
        clientMed.setClientComment(comment);
    }

    public Date getCommentDate() {
        return clientMed.getUnit().getCommentDate();
    }
}
