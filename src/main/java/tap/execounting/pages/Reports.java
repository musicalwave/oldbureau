package tap.execounting.pages;

import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.dal.ChainMap;
import tap.execounting.dal.mediators.interfaces.ClientMed;
import tap.execounting.entities.Comment;

import java.util.Date;
import java.util.List;

@Import(
        library = {
                "context:js/jquery-1.8.3.min.js",
                "context:js/comments.js"},
        stylesheet = {
                "context:css/datatable.css",
                "context:css/reports.css",
                "context:css/comments.css"})
public class Reports {

    // Activation context
    // Screen fields
    // Generally useful bits and pieces
    @Inject
    private ClientMed clientMed;
    @Inject
    private CRUDServiceDAO dao;

    // Page stuff
    @InjectPage
    private ClientPage clientPage;

    // The code
    public JSONObject onAJpoll(@RequestParameter("timeStamp") long timestamp) {
        JSONObject js = new JSONObject("{'status':'ok'}");
        List<Comment> list = dao.findWithNamedQuery(Comment.CLIENT_AFTER_DATE,
                ChainMap.with("date", new Date(timestamp)));
        if (list.size() > 0) {
            JSONArray jr = new JSONArray();
            for (Comment c : list)
                jr.put(new JSONObject("id", c.getEntityId() + "", "comment", c
                        .getText(), "timeStamp", c.getDate().getTime() + ""));
            js.put("updates", jr);
        }
        return js;
    }

    public JSONObject onAJ(@RequestParameter("id") int id,
                           @RequestParameter("comment") String text,
                           @RequestParameter("timeStamp") long timeStamp) {

        clientMed.setUnitById(id).comment(text, timeStamp);
        JSONObject js = new JSONObject("{'status':'ok'}");
        return js;
    }

    // getters
    ClientPage onDetails(int clientId) {
        clientPage.setup(clientMed.setUnitById(clientId).getUnit());
        return clientPage;
    }

    @Property
    @Persist
    private boolean switchPages;


    public String getPagerPosition() {
        return switchPages ? "both" : "none";
    }

    public int getRows() {
        return switchPages ? 20 : 100000;
    }
}
