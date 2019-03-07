package server;

import service.Log;
import service.Store;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * The Server class is a server using RMI
 */
public class Server {
    private int port = 9000;            /* port number */
    private Store store = new Store();  /* service of the server */
    private List<Integer> ports = new ArrayList<>();        /* peer servers' port numbers */

    /**
     * Default constructor
     */
    public Server() {}

    /**
     * Constructor with port number and store
     *
     * @param port port number of the server
     * @param store service of the server
     */
    public Server(int port, Store store) {
        this.port = port;
        this.store = store;
    }

    public void setDistributedPorts(List<Integer> ports) {
        this.ports = ports;
    }

    /**
     * Start the server
     */
    public void start() {
        try {
            RemoteStoreImp rmiServiceImp = new RemoteStoreImp(store, ports);
            RemoteStore service = (RemoteStore) UnicastRemoteObject.exportObject(rmiServiceImp, port);
            Log.info("remote object exported");

            Registry registry = LocateRegistry.createRegistry(port);
            Log.info("rmi registry created");

            registry.bind("Server", service);
            Log.info("server ready in port:" + port + " using name \'Server\'");
        } catch (Exception e) {
            Log.exceptionThrown(e, "unable to initialize the server");
            return;
        }
    }

    /**
     * Local test for RMI server
     */
    public static void main(String[] args) {
        Server server = new Server();
        if (args.length == 1) {
            try {
                int port = Integer.parseInt(args[1]);
                server = new Server(port, new Store());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Please check the format of port number");
            }
        }
        server.start();
    }
}
