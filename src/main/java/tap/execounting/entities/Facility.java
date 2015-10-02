package tap.execounting.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.tapestry5.beaneditor.NonVisual;

import tap.execounting.entities.interfaces.Deletable;

@Entity
@NamedQueries({
		@NamedQuery(name = Facility.ALL, query = "from Facility"),
		@NamedQuery(name = Facility.ACTUAL, query = "from Facility where deleted = false"),
		@NamedQuery(name = Facility.BY_FACILITY_NAME, query = "Select s from Facility s where s.name = :facilityName") })
@Table(name = "facilities")
public class Facility implements Deletable {

	public static final String ALL = "Facility.all";
	public static final String BY_FACILITY_NAME = "Facility.byFacilityName";
	public static final String ACTUAL = "Facility.actual";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "facility_id")
	private int facilityId;

	@Column(nullable = false, unique = true)
	private String name;

	@NonVisual
	private boolean deleted;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "facilities_rooms", joinColumns = { @JoinColumn(name = "facility_id") }, inverseJoinColumns = { @JoinColumn(name = "room_id") })
	private List<Room> rooms = new ArrayList<Room>(0);

	public Facility() {
	}

	public Facility(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addRoom() {
		rooms.add(new Room(this.name));
	}

	public List<Room> getRooms() {
		return rooms;
	}

	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}

	public int getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(int facilityId) {
		this.facilityId = facilityId;
	}

	public int getRoomsNumber() {
		return rooms.size();
	}

	public void setRoomsNumber(int number) {
		while (getRoomsNumber() < number)
			rooms.add(new Room(getName() + " #" + getRoomsNumber() + 1));
		while (getRoomsNumber() > number)
			rooms.remove(rooms.size() - 1);
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}
