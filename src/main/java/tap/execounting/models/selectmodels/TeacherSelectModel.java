package tap.execounting.models.selectmodels;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.util.AbstractSelectModel;
import tap.execounting.entities.Teacher;

import java.util.ArrayList;
import java.util.List;

public class TeacherSelectModel extends AbstractSelectModel {

	private List<OptionModel> options = new ArrayList<OptionModel>(3);

	public TeacherSelectModel(List<Teacher> teachers) {
		for (Teacher t : teachers)
			options.add(new OptionModelImpl(t.getName(), t.getId()));
	}

	public List<OptionGroupModel> getOptionGroups() {
		return null;
	}

	public List<OptionModel> getOptions() {
		return options;
	}
}
