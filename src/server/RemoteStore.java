package server;

import service.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The interface provides process function for a RMI server to work
 */
public interface RemoteStore extends Remote {
    /**
     * The service processes the request
     *
     * @param message request to process
     * @throws RemoteException
     * @return response to the request
     */
    Message process(Message message, int id) throws RemoteException;
}