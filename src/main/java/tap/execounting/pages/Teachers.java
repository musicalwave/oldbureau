package tap.execounting.pages;

import java.util.List;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.annotations.Inject;

import org.apache.tapestry5.services.BeanModelSource;
import tap.execounting.dal.mediators.interfaces.TeacherMed;
import tap.execounting.entities.Teacher;

@Import(stylesheet = {"context:css/datatable.css",
                      "context:css/teachers.css"})
public class Teachers {

	@Inject
	private TeacherMed teacherMediator;

	@Inject
	private BeanModelSource source;

    @Inject
	private ComponentResources resources;

	private BeanModel<Teacher> model;

	void pageLoaded() {
        if(model == null) {
            model = source.createDisplayModel(Teacher.class, resources.getMessages());
            model.include("name");
        }
	}

	public List<Teacher> getWorkingTeachers() {
		return teacherMediator.getWorkingTeachers();
	}

	public Teacher getTeacher() {
		return teacherMediator.getUnit();
	}

	public void setTeacher(Teacher teacher) {
		teacherMediator.setUnit(teacher);
	}

	public BeanModel<Teacher> getModel(){
		return model;
	}
}