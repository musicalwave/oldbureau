package tap.execounting.components.editors;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.BeanEditForm;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.internal.util.CaptureResultCallback;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.dal.mediators.interfaces.ContractMed;
import tap.execounting.dal.mediators.interfaces.EventMed;
import tap.execounting.dal.mediators.interfaces.TeacherMed;
import tap.execounting.entities.*;
import tap.execounting.models.selectmodels.ContractTypeIdSelectModel;
import tap.execounting.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Import(stylesheet = "context:css/addContract.css")
public class AddContract {

	@Inject
	private CRUDServiceDAO dao;
	@Inject
	private ContractMed contractMed;
	@Inject
	private TeacherMed tm;
	@Inject
	private EventMed em;
    @Inject
    private AjaxResponseRenderer renderer;
    @Inject
    private Request request;

	@InjectComponent
	private BeanEditForm editor;

    @Component
    private Zone editorZone;

	@Inject
	private ComponentResources resources;

	@Property
	@Persist
	private Contract con;

	@Property
	@Persist
	private boolean updateMode;

	// Screen properties
	@Property
	private Date eventsStartDate;
	@Property
	@Persist
	private String etype;

	private List<EventType> etypes;

	@Property
	@Persist
	private String teacher;

	private List<Teacher> teachersCache;

	@Property
	private SelectModel contractTypeIdsModel;

	private ContractMed getContractMed() {
		return contractMed;
	}

	public void setup(Contract con) {
		if (con.getTeacherId() == 0)
			teacher = "";
		else
			teacher = tm.setId(con.getTeacherId()).getName();

		etype = em.loadEventType(con.getTypeId()).getTitle();
		updateMode = true;
		this.con = con;
	}

	public void setup(Client client) {
		reset();
		con.setClientId(client.getId());
	}

	public void reset() {
		con = new Contract();
		updateMode = false;
	}

	List<String> onProvideCompletionsFromEtypes(String input) {
		List<String> res = new ArrayList<>(10);
		for (EventType e : types())
			if (e.getTitle().toLowerCase().contains(input.toLowerCase()))
				res.add(e.getTitle());
		return res;
	}

	private List<EventType> types() {
		if (etypes == null)
			etypes = dao.findWithNamedQuery(EventType.ACTUAL);
		return etypes;
	}

	List<String> onProvideCompletionsFromTeacherName(String input) {
		List<String> res = new ArrayList<>(10);
		for (Teacher t : teachers())
			if (t.getName().toLowerCase().contains(input.toLowerCase()))
				res.add(t.getName());
		return res;
	}

	private List<Teacher> teachers() {
		if (teachersCache == null)
			teachersCache = tm.getWorkingTeachers();
		return teachersCache;
	}

	private Teacher teacher() {
		for (Teacher t : tm.getAllTeachers())
			if (t.getName().equals(teacher)) return t;
		return null;
	}

	private int teacherId() {
		return teacher().getId();
	}

	private EventType type() {
		for (EventType et : types())
			if (et.getTitle().equals(etype))
				return et;
		return null;
	}

	private int typeId() {
		return type().getId();
	}

	public String getConDate() {
		return DateUtil.format("dd MMM YYYY", con.getDate());
	}

	void onValidateFromEditor() throws ValidationException {

        // Teacher check
        try {
            teacherId();
        } catch (Exception e) {
            editor.recordError(String.format("Учителя с именем %s не найдено.", teacher));
        }

        if (!editor.getHasErrors()) {
            // Contract/Teacher Schedule compatibility check
            for (int i = 1; i < 8; i++)
                if (con.getSchedule().get(i))
                    if (teacher().getScheduleDay(i) == null) {
                        editor.recordError(String.format("%s не ведет занятий в %s день недели.", teacher, i));
                    }
        }

        if (!editor.getHasErrors()) {
            // Type check
            try {
                typeId();
            } catch (Exception e) {
                editor.recordError(String.format("Типа занятий: %s не найдено.", etype));
            }
        }

        // rerender the editor's zone if any validation error occured
        // to show these errors at the top of the editor
        if(request.isXHR() && editor.getHasErrors())
        {
            setupRender();
            renderer.addRender(editorZone);
        }
    }

	Object onSuccess() {
		con.setTypeId(typeId());
		con.setTeacherId(teacherId());

		if (updateMode) dao.update(con);
        else dao.create(con);

		getContractMed().setUnit(con).doPlanEvents(eventsStartDate);

		// Today we do tricks. This code calls onExperiment in the parent
		// component. onExperiment from showContract provides us with the zone
		// body. OnExperiment from the ClientPage -- provides current page.
		CaptureResultCallback<Object> cb = new CaptureResultCallback<>();
		resources.triggerEvent("Experiment", new Object[] { con }, cb);
		return cb.getResult();
	}

	Object onTheCancel() {
		// Today we do tricks. This code calls onCancel in the parent
		// component.
		CaptureResultCallback<Object> cb = new CaptureResultCallback<>();
		resources.triggerEvent("Cancel", new Object[] { con }, cb);
		return cb.getResult();
	}

	void setupRender() {
		List<ContractType> allTypes = dao.findWithNamedQuery(ContractType.ALL);
		contractTypeIdsModel = new ContractTypeIdSelectModel(allTypes);
	}
}
