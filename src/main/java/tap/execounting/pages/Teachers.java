package tap.execounting.pages;

import java.util.List;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.mediators.interfaces.TeacherMed;
import tap.execounting.entities.Teacher;

@Import(stylesheet = "context:/css/teacherslist.css")
public class Teachers {

	@Inject
	private TeacherMed tMed;

	@InjectPage
	private TeacherPage page;

	@SuppressWarnings("unused")
	private Object onActionFromTLink(Teacher context) {
		page.setup(context);
		return page;
	}

	public List<Teacher> getAll() {
		return tMed.getWorkingTeachers();
	}

	public Teacher getUnit() {
		return tMed.getUnit();
	}

	public void setUnit(Teacher unit) {
		tMed.setUnit(unit);
	}
}