package tap.execounting.data;

public class TeacherInfo {
	private String name;
	private int scheduledEvents;
	private int completeEvents;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getScheduledEvents() {
		return scheduledEvents;
	}
	public void setScheduledEvents(int scheduledEvents) {
		this.scheduledEvents = scheduledEvents;
	}
	public int getCompleteEvents() {
		return completeEvents;
	}
	public void setCompleteEvents(int completeEvents) {
		this.completeEvents = completeEvents;
	}
}
