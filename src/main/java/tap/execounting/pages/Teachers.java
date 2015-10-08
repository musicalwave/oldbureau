package tap.execounting.pages;

import java.util.List;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.annotations.Inject;

import org.apache.tapestry5.services.BeanModelSource;
import tap.execounting.dal.mediators.interfaces.TeacherMed;
import tap.execounting.entities.Teacher;

public class Teachers {

	@Inject
	private TeacherMed tMed;

	@Inject
	private BeanModelSource source;

    @Inject
	private ComponentResources resources;

	private BeanModel<Teacher> model;

	void pageLoaded() {
		model = source.createDisplayModel(Teacher.class, resources.getMessages());
		model.include("name");
		model.get("name").label("Преподаватель");
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

	public BeanModel<Teacher> getModel(){
		return model;
	}

}