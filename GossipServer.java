import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
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
		System.err.println("test hearGossip");
		System.err.println(bytes);
	}
	
	private static void processGossip(String message) throws RemoteException {
		// TODO Send this string to two remote hearGossip methods
		String firstServer = Integer.toString(serverID);
		try {
			GossipInterface clientStub = (GossipInterface) registry.lookup(firstServer);
			clientStub.hearGossip("".getBytes());
		} catch (NotBoundException e) {
			System.err.println("Not bound method : " + firstServer);
		}
		
		String secondServer = Integer.toString(serverID);
		try {
			GossipInterface clientStub = (GossipInterface) registry.lookup(secondServer);
			clientStub.hearGossip("".getBytes());
		} catch (NotBoundException e) {
			System.err.println("Not bound method : " + secondServer);
		}
	}
		
	public static void main(String[] args) {
	
		if (args.length != 3) {
			System.err.println("Invalid Usage");
			System.exit(-1);
		}
		
		interval = 10;
		
		serverID = Integer.parseInt(args[0]);
		totalServers = Integer.parseInt(args[1]);
		inputFileName = args[2];
		
		String name = Integer.toString(serverID);
		try {
			registry = LocateRegistry.getRegistry("localhost");
			GossipInterface stub = (GossipInterface) UnicastRemoteObject.exportObject(new GossipServer());
			registry.rebind(name, stub);
		} catch (RemoteException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(inputFileName));
			for (String line; (line = br.readLine()) != null;) {
				processGossip(line);
				TimeUnit.SECONDS.sleep(interval);
			}
			br.close();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		while(true);
		
	}
}
