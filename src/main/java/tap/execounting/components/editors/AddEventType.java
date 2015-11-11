package tap.execounting.components.editors;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.dal.ChainMap;
import tap.execounting.entities.EventType;
import tap.execounting.entities.EventTypeAddition;

public class AddEventType {

	@Property
	@Persist
	private boolean updateMode;

	@Inject
	private CRUDServiceDAO dao;

	@Property
	@Persist
	private EventType etype;

	@Property
	private EventTypeAddition probationAddition;
	@Property
	private boolean probationLauncher;

	public void setup(EventType et) {
		updateMode = true;
		etype = et;
	}

	public void reset() {
		etype = new EventType();
		updateMode = false;
	}

	void onSuccess() {
		if (updateMode) {
			dao.update(etype);
		} else {
			dao.create(etype);
		}
		if (probationLauncher) {
			if (probationAddition.getId() == null) {
				probationAddition.setEventTypeId(etype.getId());
				dao.create(probationAddition);
			} else
				dao.update(probationAddition);
		} else if (probationAddition.getId() != null) {
			dao.delete(EventTypeAddition.class, probationAddition.getId());
		}
	}

	void onPrepare() {
		probationAddition = dao.findUniqueWithNamedQuery(
				EventTypeAddition.PROBATION_BY_EVENT_TYPE_ID, ChainMap
						.with("eventTypeId", etype.getId()));

		if (probationAddition == null) {
			probationAddition = new EventTypeAddition();
			probationAddition
					.setAdditionCode(EventTypeAddition.PROBATION_ADJUSTMENT);
		} else
			probationLauncher = true;
	}

}
