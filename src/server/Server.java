package server;

import message.ClientMessage;
import message.ServerMessage;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
    /**
     * The service processes the request
     *
     * @param clientMessage request to process
     * @throws RemoteException remote exception
     * @return response to the request
     */
    ClientMessage process(ClientMessage clientMessage) throws RemoteException;

    /**
     * Process server message from other servers
     *
     * @param serverMessage message from other servers
     * @throws RemoteException remote exception
     * @return response to the server
     */
    ServerMessage process(ServerMessage serverMessage) throws RemoteException;
}