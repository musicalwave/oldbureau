package tap.execounting.models.selectmodels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.util.AbstractSelectModel;

import tap.execounting.entities.Facility;
import tap.execounting.entities.Room;

public class RoomSelectModel extends AbstractSelectModel {

	List<OptionModel> options = new ArrayList<OptionModel>();

	public RoomSelectModel(Facility f) {
		if (f != null) {
			Room[] rs = new Room[f.getRoomsNumber()];
			f.getRooms().toArray(rs);
			Arrays.sort(rs, new Comparator<Room>() {
                public int compare(Room o1, Room o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });
			for (Room r : rs) {
				options.add(new OptionModelImpl(r.getName(), r.getRoomId()));
			}
		}
	}

	public List<OptionGroupModel> getOptionGroups() {
		return null;
	}

	public List<OptionModel> getOptions() {
		return options;
	}
}