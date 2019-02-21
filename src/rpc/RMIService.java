package rpc;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The interface provides process function for a RMI server to work
 */
public interface RMIService extends Remote {
    /**
     * The service processes the request
     *
     * @param request request to process
     * @throws RemoteException
     * @return response to the request
     */
    String process(String request, int id) throws RemoteException;
}
