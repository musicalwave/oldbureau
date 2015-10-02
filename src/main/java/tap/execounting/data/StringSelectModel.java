package tap.execounting.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.SelectModelVisitor;
import org.apache.tapestry5.internal.OptionModelImpl;

public class StringSelectModel implements SelectModel {
	private final List<String> strings;

	public StringSelectModel(final List<String> strings) {
		this.strings = strings;
	}

	public List<OptionModel> getOptions() {
		final List<OptionModel> options = new ArrayList<OptionModel>();

		for (final String string : this.strings) {
			options.add(new OptionModelImpl(string));
		}

		return options;
	}

	public List<OptionGroupModel> getOptionGroups() {
		return null;
	}

	public void visit(final SelectModelVisitor visitor) {
	}
}
