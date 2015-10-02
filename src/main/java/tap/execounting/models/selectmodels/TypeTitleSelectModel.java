package tap.execounting.models.selectmodels;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.util.AbstractSelectModel;

import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.entities.EventType;

public class TypeTitleSelectModel extends AbstractSelectModel {

	List<OptionModel> options;

	public TypeTitleSelectModel(CRUDServiceDAO dao) {
		options = new ArrayList<OptionModel>();
		List<EventType> types = dao.findWithNamedQuery(EventType.ALL);
		for (EventType et : types)
			options.add(new OptionModelImpl(et.getTitle(), et.getTitle()));
		HashSet<String> set = new HashSet<String>();
		for (EventType et : types) {
			try {
				String ex = et.getTitle().split(" ")[0].trim();
				if (ex.charAt(ex.length() - 1) == ':')
					ex = ex.substring(0, ex.length() - 1).trim();
				set.add(ex);
			} catch (Exception e) {
				System.out.println("Exception occured in data.TypeTitleSelectModel");
				e.printStackTrace();
			}
		}
		for (String s : set)
			options.add(new OptionModelImpl(s, s));
	}

	public List<OptionGroupModel> getOptionGroups() {
		return null;
	}

	public List<OptionModel> getOptions() {
		return options;
	}
}
