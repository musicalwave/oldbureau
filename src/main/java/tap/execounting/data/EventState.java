package tap.execounting.data;

public enum EventState {
	planned(0, "Запланировано"), complete(1, "Состоялось"), failedByClient(3,
			"Сгорело"), movedByTeacher(4, "Перенос : педагог"), movedByClient(
			5, "Перенос : клиент");

	private int code;
	private String translation;

	private EventState(int code, String translation) {
		this.code = code;
		this.translation = translation;
	}

	public int getCode() {
		return this.code;
	}

	public static EventState fromCode(int code) {
		switch (code) {
		case 0:
			return planned;
		case 1:
			return complete;
		case 3:
			return failedByClient;
		case 4:
			return movedByTeacher;
		case 5:
			return movedByClient;
		default:
			throw new IllegalArgumentException("unknown state code");
		}
	}

	@Override
	public String toString() {
		return this.translation;
	}
}
