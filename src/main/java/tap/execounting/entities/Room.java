package tap.execounting.entities;

import javax.persistence.*;


@Entity
@NamedQueries({
	@NamedQuery(name = Room.ALL, query="Select r from Room r")
})
@Table(name="rooms")
public class Room {
	
	public static final String ALL = "Room.all";
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="room_id")
	private int roomId;
	
	private String name;
	
	public Room(){
	}
	
	public Room(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}
}
