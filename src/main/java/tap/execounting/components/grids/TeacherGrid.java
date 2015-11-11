package tap.execounting.components.grids;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import tap.execounting.components.editors.AddTeacher;
import tap.execounting.components.show.SmartIcon;
import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.entities.Facility;
import tap.execounting.entities.Teacher;
import tap.execounting.security.AuthorizationDispatcher;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TeacherGrid {
	@Inject
	private BeanModelSource beanModelSource;
	@Inject
	private ComponentResources componentResources;
	@Inject
	private CRUDServiceDAO dao;
	@InjectComponent
	private Zone tzone;
	@Inject
	@Property
	private AuthorizationDispatcher dispatcher;

	@InjectComponent
	private AddTeacher editor;
	@Property
	private boolean editorActive;
	@Property
	private BeanModel<Teacher> model;
	@Property
	private Teacher unit;

	public Facility facility(Integer id) {
		if (id == null)
			return null;
		return dao.find(Facility.class, id);
	}

	public String state(Teacher t) {
		return t.isActive() ? "да" : "нет";
	}

	public List<Teacher> getSource() {
		List<Teacher> teachers = dao.findWithNamedQuery(Teacher.ALL);
		Collections.sort(teachers, new Comparator<Teacher>() {

			@Override
			public int compare(Teacher o1, Teacher o2) {
				if (o1.isDeleted()) {
					if (o2.isDeleted())
						return o1.getName().compareTo(o2.getName());
					return +1;
				} else {
					if (o2.isDeleted())
						return -1;
					return o1.getName().compareTo(o2.getName());
				}
			}

		});
		return teachers;
	}

	Object onActionFromEdit(Teacher c) {
		// AUTHORIZATION MOUNT POINT
		if (dispatcher.canEditTeachers()) {
			editorActive = true;
			editor.setup(c);
		}
		return tzone;
	}

	Object onActionFromAdd() {
		// AUTHORIZATION MOUNT POINT
		if (dispatcher.canCreateTeachers()) {
			editorActive = true;
			editor.reset();
		}
		return tzone;
	}

	void onDelete(Teacher c) {
		// AUTHORIZATION MOUNT POINT
		if (dispatcher.canDeleteTeachers())
			dao.delete(Teacher.class, c.getId());
	}

	void setupRender() {
		if (model == null) {
			model = beanModelSource.createDisplayModel(Teacher.class,
					componentResources.getMessages());
			model.add("Action", null);

			model.exclude("id");
			for (String pn : model.getPropertyNames())
				model.get(pn).sortable(false);
			model.get("name").sortable(true);
			model.add("deleted");
			model.reorder("deleted");
		}
	}

	public String getIconType() {
		return unit.isDeleted() ? SmartIcon.DELETED : "";
	}
}
