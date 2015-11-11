package tap.execounting.components.editors;

import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.dal.ChainMap;
import tap.execounting.entities.Facility;
import tap.execounting.entities.Teacher;
import tap.execounting.models.selectmodels.FacilitySelectModel;
import tap.execounting.pages.CRUD;

import java.util.List;

public class AddTeacher {

	@Persist
	@Property
	private boolean updateMode;

	@Persist
	@Property
	private Teacher teacher;

	@Property
	private SelectModel facilitySelect;

	@Component
	private Zone formaZone;

	// Useful bits
	@Inject
	private CRUDServiceDAO dao;
	@Inject
	private Messages messages;

	public void setup(Teacher t) {
		teacher = t;
		updateMode = true;
	}

	public void reset() {
		teacher = new Teacher();
		updateMode = false;
	}

	void onValidateFromForm() throws ValidationException {
		// Submit handling
		String duplicateExceptionMessageName = "addteacher.error.duplicate";
		ValidationException ve = new ValidationException(
				messages.get(duplicateExceptionMessageName));

		// First -- check if for name duplication
		List<Teacher> teachers = dao.findWithNamedQuery(Teacher.BY_NAME,
				ChainMap.with("name", teacher.getName()));

		// If there is no teachers with such name -- it is good.
		if (teachers.size() != 0) {
			// Else -- we could have two situations
			if (updateMode) {
				// If we have an update situation, and teachers size more than
				// --
				// something is wrong, for sure.
				if (teachers.size() > 1)
					throw ve;
				// If we found only one client -- CHECK ID. We should pass
				// update, not duplicate
				if (teachers.get(0).getId() != teacher.getId())
					throw ve;
			} else
				// If we have creation situation, then we totally should have
				// zero teachers with such name
				throw ve;
		}
	}

	Object onFailure() {
		return formaZone;
	}

	Object onSuccess() {
		if (updateMode)
			dao.update(teacher);
		else
			dao.create(teacher);

		return CRUD.class;
	}

	void onPrepareForRender() {
		List<Facility> facilities = dao.findWithNamedQuery(Facility.ACTUAL);
		facilitySelect = new FacilitySelectModel(facilities);
	}
}
