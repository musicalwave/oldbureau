package tap.execounting.services;

import static java.lang.System.out;

public class BroadcastingService {
	public BroadcastingService getBroadcaster() {
		return new BroadcastingService();
	}

	public void cast(String message) {
		out.println("\nBroadcast:\n" + message);
	}
}
