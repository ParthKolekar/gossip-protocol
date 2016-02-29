import java.rmi.Remote;
import java.rmi.RemoteException;


public interface GossipInterface extends Remote {
	void hearGossip(byte[] bytes) throws RemoteException;
}
