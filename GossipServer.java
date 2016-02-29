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

import com.ds.gossip.GossipRequestProto.GossipRequest;

public class GossipServer implements GossipInterface {
	private static GossipClock clock;
	private static long interval;
    private static long setupTime;
    private static long processTime;
	private static Registry registry;
	private static int serverID;
	private static int totalServers;
	private static String inputFileName;
	
	@Override
	public void hearGossip(byte[] bytes) {
        try {
            GossipRequest gossipMessage = GossipRequest.parseFrom(bytes);
            String message = gossipMessage.getMsg();
            GossipRequest.Clock gossipClock = gossipMessage.getClock();
        
            int messageServerID = gossipClock.getServerID();
            int messageClock = gossipClock.getTimeval();

            if (clock.getVectorClock(messageServerID) >= messageClock) {
                System.out.println("Reject " + message);
                return;
            } 

            clock.setVectorClock(messageServerID, messageClock);
            System.out.println("Accept " + message);

            try {
                TimeUnit.SECONDS.sleep(processTime);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }

            processGossip(message);

        } catch (Exception e) { 
            e.printStackTrace();
            System.exit(-1);
        }
	}
	
	private static void processGossip(String message) throws RemoteException {
		GossipRequest.Builder gossipMessage = GossipRequest.newBuilder();
		gossipMessage.setMsg(message);
		GossipRequest.Clock.Builder clockMessage = GossipRequest.Clock.newBuilder();
		clockMessage.setTimeval(clock.getVectorClock(serverID));
		clockMessage.setServerID(serverID);
		gossipMessage.setClock(clockMessage);
		byte[] serializedMessage = gossipMessage.build().toByteArray();
		
		int serverID1 = ThreadLocalRandom.current().nextInt(1, totalServers + 1);
		String firstServer = Integer.toString(serverID1);
		try {
			GossipInterface clientStub = (GossipInterface) registry.lookup(firstServer);
			clientStub.hearGossip(serializedMessage);
		} catch (NotBoundException e) {
			System.err.println("Not bound method : " + firstServer);
		}
		
		int serverID2 = ThreadLocalRandom.current().nextInt(1, totalServers + 1);		
		String secondServer = Integer.toString(serverID2);
		try {
			GossipInterface clientStub = (GossipInterface) registry.lookup(secondServer);
			clientStub.hearGossip(serializedMessage);
		} catch (NotBoundException e) {
			System.err.println("Not bound method : " + secondServer);
		}
	}
		
	public static void main(String[] args) {
		
		interval = 2;
        setupTime = 2;
        processTime = 2;

		clock = new GossipClock();
		
		serverID = Integer.parseInt(args[0]);
		totalServers = Integer.parseInt(args[1]);
		inputFileName = args[2];
		
		for (int i = 1; i <= totalServers; i++) {
			clock.setVectorClock(i, -1);
		}
		
        try {
    	    TimeUnit.SECONDS.sleep(setupTime);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

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
                TimeUnit.SECONDS.sleep(processTime);
				processGossip(line);
                clock.setVectorClock(serverID, clock.getVectorClock(serverID) + 1);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
