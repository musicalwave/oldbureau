package tap.execounting.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.SelectModelVisitor;
import org.apache.tapestry5.internal.OptionModelImpl;

public class StringValueSelectModel implements SelectModel {
	private final List<OptionModel> options;

	public StringValueSelectModel(Map<Integer, String> strings) {
		this.options = new ArrayList<OptionModel>();

		for (Integer i : strings.keySet()) {
			options.add(new OptionModelImpl(strings.get(i), i));
		}
	}

	public List<OptionModel> getOptions() {
		return options;
	}

	public List<OptionGroupModel> getOptionGroups() {
		return null;
	}

	public void visit(final SelectModelVisitor visitor) {
	}
}
