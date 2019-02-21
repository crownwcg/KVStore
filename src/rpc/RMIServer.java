package rpc;

import server.Server;
import service.Service;
import service.Store;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;

/**
 * The RMIServer class is a server using RMI
 */
public class RMIServer implements Server {
    private int port = -1;      /* port number */
    private Service service;    /* service of the server */

    @Override
    /**
     * Set port number
     * @param port port number of the server
     */
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    /**
     * Set the service
     * @param service service of the server
     */
    public void setService(Service service) {
        this.service = service;
    }

    @Override
    /**
     * Start the server
     */
    public void start() {
        try {
            RMIStore store = new RMIStore(service);
            RMIService service = (RMIService) UnicastRemoteObject.exportObject(store, port);
            Service.logger.log(Level.INFO,"remote object exported");
            Registry registry = LocateRegistry.createRegistry(port);
            Service.logger.log(Level.INFO,"rmi registry created");
            registry.bind("RMIServer", service);
            Service.logger.log(Level.INFO,"server ready using name \'RMIServer\'");
        } catch (Exception e) {
            Service.logger.log(Level.WARNING,e.getClass() + ": unable to initialize the server");
            return;
        }
    }

    @Override
    /**
     * Stop the server
     */
    public void stop() {

    }

    /**
     * Local test for RMI server
     */
    public static void main(String[] args) {
        int port = 9000;
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        }

        RMIServer server = new RMIServer();
        server.setPort(port);
        server.setService(Store.getInstance());
        server.start();
    }
}
