package tap.execounting.models.selectmodels;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.util.AbstractSelectModel;
import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.entities.EventType;

import java.util.ArrayList;
import java.util.List;

public class TypeSelectModel extends AbstractSelectModel {

	List<OptionModel> options;

	public TypeSelectModel(CRUDServiceDAO dao) {
		options = new ArrayList<OptionModel>();
		List<EventType> types = dao.findWithNamedQuery(EventType.ALL);
		for (EventType et : types)
			options.add(new OptionModelImpl(et.getTitle(), et.getId()));
	}

	public List<OptionGroupModel> getOptionGroups() {
		return null;
	}

	public List<OptionModel> getOptions() {
		return options;
	}
}
