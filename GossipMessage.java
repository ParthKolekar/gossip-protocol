
public class GossipMessage {
	private GossipClock clock;
	private String message;
	public GossipClock getClock() {
		return clock;
	}
	public void setClock(GossipClock clock) {
		this.clock = clock;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public GossipMessage(byte[] bytes) {
		// TODO Make this from messages
	}
}
