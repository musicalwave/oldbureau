package tap.execounting.components.editors;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.internal.util.CaptureResultCallback;
import org.apache.tapestry5.ioc.annotations.Inject;
import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.data.StringSelectModel;
import tap.execounting.entities.*;
import tap.execounting.models.selectmodels.FacilitySelectModel;
import tap.execounting.models.selectmodels.RoomSelectModel;
import tap.execounting.models.selectmodels.TeacherSelectModel;
import tap.execounting.models.selectmodels.TypeSelectModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AddEvent {

	@Property
	@Persist
	private boolean updateMode;
	@Inject
	private ComponentResources resources;

	@Inject
	private CRUDServiceDAO dao;

	@Property
	@Persist
	private Event event;

	// Screen properties
	@Property
	private boolean freeFromTeacher;
	@Property
	private boolean freeFromSchool;

	@Property
	private SelectModel facilitySelect;

	@Property
	private SelectModel teacherSelect;

	@Property
	private SelectModel roomSelect;

	@Property
	private SelectModel typeSelect;

	@InjectComponent
	private Zone roomZone;

	private String etype;

	public String getEtype() {
		return event.getTypeId() == 0 ? "" : dao.find(EventType.class,
				event.getTypeId()).getTitle();
	}

	@Persist
	private List<String> clientNames;

	public List<String> getClientNames() {
		return clientNames;
	}

	public void setClientNames(List<String> clients) {
		clientNames = clients;
	}

	public void setEtype(String etype) {
		this.etype = etype;
	}

	public void setup(Event e) {
		roomSelect = new RoomSelectModel(dao.find(Facility.class,
				e.getFacilityId()));

		if (!updateMode && event != null) {
			e.setFacilityId(event.getFacilityId());
			e.setRoomId(event.getRoomId());
		}
		event = e;
	}

	public void setup(Event e, boolean update) {
		updateMode = update;
		setup(e);
	}

	public void setup(Teacher t) {
		updateMode = false;
		event = new Event();
		event.setHostId(t.getId());
		event.setDate(new Date());
	}

	public void setup(Facility f) {
		updateMode = false;
		event = new Event();
		event.setFacilityId(f.getFacilityId());
		event.setDate(new Date());
	}

	public void reset() {
		event = new Event();
		event.setDate(new Date());
		updateMode = false;
	}

	EventType checkType() {
		EventType eventType = null;
		List<EventType> etypes = dao.findWithNamedQuery(EventType.ALL);
		for (EventType et : etypes)
			if (et.getTitle().equals(etype)) {
				eventType = et;
				break;
			}

		if (eventType == null)
			throw new IllegalArgumentException("Тип занятий " + etype
					+ " не найден");
		return eventType;
	}

	Client findClient(String name) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);
		Client c = dao.findUniqueWithNamedQuery(Client.BY_NAME, params);
		if (c == null)
			throw new IllegalArgumentException("Клиент с именем '" + name
					+ "' не найден");
		return c;
	}

    //TODO here is some logic to update and move to onValidate method
	Object onSuccess() {
		EventType eventType = checkType();

		event.setTypeId(eventType.getId());

		for (String name : clientNames) {
			// the procedure standard for each client follows
			if (name != null && name.length() > 3) {
				Client c = findClient(name);

				// source dependent from updatemode
				List<Contract> source = updateMode ? c.getContracts() : c
						.getActiveContracts();

				// event type check for compatibility with existing contracts of
				// the client
				boolean typeMatch = false;
				List<Contract> candidates = new ArrayList<Contract>();
				for (Contract con : source) {
					// typeMatch = con.getTypeId() == evet.getId(); // strict
					// type_id check;
					typeMatch = con.getEventType().getTypeTitle()
							.equals(eventType.getTypeTitle()); // soft check

					if (event.haveContract(con)) {
						if (!typeMatch) {
							throw new IllegalArgumentException(
									String.format(
											"Ошибка: сохраняемое занятие для клиента %s, имеет тип (%s), отличный "
													+ "от типа договора (%s) к которому оно прикреплено. Если вы хотите перекинуть это занятие"
													+ "в другой договор - "
													+ "то сначала удалите его из текущего.",
											name, eventType.getTitle(), con
													.getEventType().getTitle()));
						}
						candidates.clear();
						break;
					}

					if (typeMatch) {
						candidates.add(con);
					}
				}
				if (candidates.size() > 0) {
					sort(candidates); // by date ascending
					event.addContract(candidates.get(0));
				}
				if (!typeMatch)
					throw new IllegalArgumentException(String.format(
							"По клиенту %s договора на %s не найдено", name,
							eventType.getTitle()));

			}
		}
		if(freeFromSchool)
			event.setFree(Event.FREE_FROM_SCHOOL);
		else if(freeFromTeacher)
			event.setFree(Event.FREE_FROM_TEACHER);
		else event.setFree(Event.NOT_FREE);

		if (updateMode) {
			dao.update(event);
		} else {
			dao.create(event);
		}
		return onTheCancel();
	}

	private void sort(List<Contract> list) {
		for (int i = 0; i < list.size() - 1; i++)
			for (int j = list.size() - 1; j > i; j--) {
				Contract icon = list.get(i);
				Contract jcon = list.get(j);
				if (icon.getDate().after(jcon.getDate())) {
					list.set(j, icon);
					list.set(i, jcon);
				}
			}
	}

	// Event handlers community
	Object onTheCancel() {
		CaptureResultCallback<Object> capturer = new CaptureResultCallback<Object>();
		resources.triggerEvent("InnerUpdate", new Object[] { event }, capturer);
        resources.triggerEvent("EditorClosed", new Object[] { event }, capturer);
		return capturer.getResult();
	}

	Object onValueChangedFromFacilityId(int facilityId) {
		event.setFacilityId(facilityId);
		Facility f = dao.find(Facility.class, facilityId);
		roomSelect = new RoomSelectModel(f);

		return roomZone.getBody();
	}

	void onPrepareForRender() {
		clientNames = new ArrayList<String>();
		if (event.getClients() != null)
			for (Client c : event.getClients())
				clientNames.add(c.getName());

		initSelectModels();
		// roomSelect
		if (event == null) {
			roomSelect = new RoomSelectModel(dao.find(Facility.class,
					(Integer) facilitySelect.getOptions().get(0).getValue()));
		} else {
			if (event.getFacilityId() == 0)
				roomSelect = new RoomSelectModel(
						dao.find(Facility.class, (Integer) facilitySelect
								.getOptions().get(0).getValue()));
			else
				roomSelect = new RoomSelectModel(dao.find(Facility.class,
						event.getFacilityId()));
		}

		typeSelect = new TypeSelectModel(dao);
		if (!updateMode)
			event.setComment("");
		if(event.isFreeFromSchool())
			freeFromSchool=true;
		else if(event.isFreeFromTeacher())
			freeFromTeacher=true;
	}

	private void initSelectModels() {
		List<Teacher> teachers = dao.findWithNamedQuery(Teacher.WORKING);
        teachers.add(dao.find(Teacher.class, event.getHostId()));
		teacherSelect = new TeacherSelectModel(teachers);
		List<Facility> facilities = dao.findWithNamedQuery(Facility.ACTUAL);
		facilitySelect = new FacilitySelectModel(facilities);
	}

	List<String> onProvideCompletionsFromEventTypes(String starts) {
		List<String> res = new ArrayList<String>();
		starts = starts.toLowerCase();

		for (EventType etype : etypes()) {
			if (etype.getTitle().toLowerCase().contains(starts))
				res.add(etype.getTitle());
		}

		return res;
	}

	SelectModel onProvideCompletionsFromClientField(String starts) {

		List<String> res = dao.findWithNamedQuery(Client.ALL_NAMES);
		starts = starts.toLowerCase();

		for (int i = res.size() - 1; i >= 0; i--)
			if (!res.get(i).toLowerCase().contains(starts))
				res.remove(i);
		return new StringSelectModel(res);
	}

	/**
	 * Validates input from form
	 */
	void onValidateFromForm() {
		// BOTH FREE SWITCHES should be never on together.
		if (freeFromSchool && freeFromTeacher)
			throw new IllegalArgumentException(
					"\nЭЭЭ!! Можно только одну галочку! (Поле бесплатно)\n");
		
		for (EventType et : etypes())
			if (et.getTitle().equals(etype)) {
				event.setTypeId(et.getId());
				return;
			}
		String message = "Не найден тип занятий с названием \"" + etype + "\"";
		throw new IllegalArgumentException(message);
	}

	private List<EventType> etypes() {
		return dao.findWithNamedQuery(EventType.ALL);
	}
}
