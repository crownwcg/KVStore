package rpc;

import client.Client;
import service.Service;

import java.rmi.registry.LocateRegistry;
import java.util.Scanner;
import java.util.logging.Level;

/**
 * The RMIlient class is a client connected to the server using RMI
 */
public class RMIClient implements Client {
    private String hostname;       /* hostname of the server */
    private int port = -1;         /* port number of the server */
    private RMIService service;    /* service of the server */

    /**
     * Set hostname
     * @param hostname hostname of the server
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * Set port number
     * @param port port number of the server
     */
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    /**
     * Connect to the specific server
     */
    public void connect() {
        try {
            // Looking up the registry for the remote object
            service = (RMIService) LocateRegistry.getRegistry(hostname, port).lookup("RMIServer");
            Service.logger.log(Level.INFO,"registry found");
        } catch (Exception e) {
            Service.logger.log(Level.WARNING, e.getClass() + ": registry failed");
        }
    }

    @Override
    /**
     * Send message to the server and get response
     *
     * @param msg msg to send
     * @return response string from the server
     */
    public String send(String msg) {
        String response = "";
        try {
            response = service.process(msg, this.hashCode());
            Service.logger.log(Level.INFO,"response received: " + response);
        } catch (Exception e) {
            Service.logger.log(Level.WARNING,e.getClass() + ": unable to get message");
        }
        return response;
    }

    @Override
    /**
     * Close the client
     */
    public void close() {

    }

    /**
     * Local test for RMI client
     */
    public static void main(String[] args) {
        String host = "localhost";
        int port = 9000;
        if (args.length == 2) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }

        RMIClient client = new RMIClient();
        client.setHostname(host);
        client.setPort(port);
        client.connect();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Please input your request: ");
            String request = scanner.nextLine();
            client.send(request);
        }
    }
}
