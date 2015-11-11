package tap.execounting.models.selectmodels;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.util.AbstractSelectModel;
import tap.execounting.entities.ContractType;

import java.util.ArrayList;
import java.util.List;

public class ContractTypeIdSelectModel extends AbstractSelectModel {

	private List<ContractType> types;
	
	public ContractTypeIdSelectModel(List<ContractType> types) {
		this.types = types; 
	}
	
	public List<OptionGroupModel> getOptionGroups() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<OptionModel> getOptions() {
		List<OptionModel> options = new ArrayList<OptionModel>();
		for(ContractType type : types) options.add(new OptionModelImpl(type.getTitle(), type.getId()));
		
		return options;
	}

}
