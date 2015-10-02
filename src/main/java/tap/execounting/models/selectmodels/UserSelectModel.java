package tap.execounting.models.selectmodels;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.util.AbstractSelectModel;
import tap.execounting.entities.User;

import java.util.ArrayList;
import java.util.List;

public class UserSelectModel extends AbstractSelectModel {

	private List<OptionModel> options = new ArrayList();

	public UserSelectModel(List<User> users) {
		for (User u : users)
			options.add(new OptionModelImpl(u.toString(), u.getId()));
	}

	public List<OptionGroupModel> getOptionGroups() {
		return null;
	}

	public List<OptionModel> getOptions() {
		return options;
	}
}
