import java.util.HashMap;


public class GossipClock {
	private HashMap<Integer, Integer> vectorClock;
	
	public GossipClock() {
		vectorClock = new HashMap<Integer, Integer>();
	}

	public HashMap<Integer, Integer> getVectorClock() {
		return vectorClock;
	}

	public void setVectorClock(HashMap<Integer, Integer> vectorClock) {
		this.vectorClock = vectorClock;
	}
	
	public void setVectorClock(int key, int value) {
		vectorClock.put(key, value);
	}
	
	public int getVectorClock(int key) {
		return vectorClock.get(key);
	}
}
