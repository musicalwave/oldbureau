package tap.execounting.security;

public enum Operation {
	create((byte) 0), read((byte) 1), update((byte) 2), delete((byte) 3);

	private byte code;

	private Operation(byte code) {
		this.code = code;
	}

	public byte getCode() {
		return code;
	}

	public static Operation fromCode(byte code) {
		switch (code) {
		case 0:
			return create;
		case 1:
			return read;
		case 2:
			return update;
		case 3:
			return delete;
		default:
			throw new IllegalArgumentException("Wrong operation code");
		}
	}
}
