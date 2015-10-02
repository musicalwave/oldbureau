package tap.execounting.services;

import java.util.Comparator;

import tap.execounting.entities.Contract;

public class ContractByClientNameComparator implements Comparator<Contract> {

	@Override
	public int compare(Contract c1, Contract c2) {
		return c1.getClient().getName().compareTo(c2.getClient().getName());
	}

}
