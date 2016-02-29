import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


public class GossipServer implements GossipInterface {
	private GossipMessage currentMessage;
	private static long interval;
	private static Registry registry;
	private static int serverID;
	private static int totalServers;
	private static String inputFileName;
	
	@Override
	public void hearGossip(byte[] bytes) throws RemoteException {
		// TODO Deserialize a ProtoBuf from the serializedMessage
		// TODO Compare clocks
		// TODO if clock is older, then discard
		// TODO else, accept and run processGossip() after a timer
		//if (currentMessage.getClock() != null) {
			
		//}
		currentMessage = new GossipMessage(bytes);
		System.err.println(serverID);
		System.err.println(new String(bytes));
	}
	
	private static void processGossip(String message) throws RemoteException {
		int serverID1 = ThreadLocalRandom.current().nextInt(1, totalServers + 1);
		String firstServer = Integer.toString(serverID1);
		try {
			GossipInterface clientStub = (GossipInterface) registry.lookup(firstServer);
			clientStub.hearGossip(message.getBytes());
		} catch (NotBoundException e) {
			System.err.println("Not bound method : " + firstServer);
		}
		
		int serverID2 = ThreadLocalRandom.current().nextInt(1, totalServers + 1);		
		String secondServer = Integer.toString(serverID2);
		try {
			GossipInterface clientStub = (GossipInterface) registry.lookup(secondServer);
			clientStub.hearGossip(message.getBytes());
		} catch (NotBoundException e) {
			System.err.println("Not bound method : " + secondServer);
		}
	}
		
	public static void main(String[] args) {
		
		interval = 2;
		
		serverID = Integer.parseInt(args[0]);
		totalServers = Integer.parseInt(args[1]);
		inputFileName = args[2];
		
		String name = Integer.toString(serverID);
		try {
			registry = LocateRegistry.getRegistry();
			GossipInterface stub = (GossipInterface) UnicastRemoteObject.exportObject(new GossipServer(), 0);
			registry.rebind(name, stub);
		} catch (RemoteException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(inputFileName));
			for (String line; (line = br.readLine()) != null;) {
				TimeUnit.SECONDS.sleep(interval);
				processGossip(line);
			}
			br.close();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
