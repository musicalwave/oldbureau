package tap.execounting.models.selectmodels;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.util.AbstractSelectModel;

public class BooleanSelectModel extends AbstractSelectModel {

	private List<OptionModel> options;

	public BooleanSelectModel() {
		options = new ArrayList<OptionModel>();
		options.add(new OptionModelImpl("проведено", "true"));
		options.add(new OptionModelImpl("не проведено", "false"));
	}

	public List<OptionGroupModel> getOptionGroups() {
		return null;
	}

	public List<OptionModel> getOptions() {
		return options;
	}

}
