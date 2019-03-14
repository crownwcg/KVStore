package client;

import service.Log;
import service.Message;
import server.RemoteStore;

import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

/**
 * The RMIlient class is a client connected to the server using RMI
 */
public class Client {
    private String hostname = "localhost";      /* hostname of the server */
    private int port = 9000;                    /* port number of the server */
    private RemoteStore remoteStore;            /* remote store object */

    public Client() {}

    /**
     * Constructor with hostname and port
     *
     * @param hostname hostname of the server
     * @param port port number of the server
     */
    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    /**
     * Connect to the specific server
     */
    public void connect() {
        try {
            // Looking up the registry for the remote object
            remoteStore = (RemoteStore) LocateRegistry.getRegistry(hostname, port).lookup("Server");
            Log.registryFound(hostname, port, "Server");
        } catch (Exception e) {
            Log.exceptionThrown(e, "connection to " + hostname + ":" + port + " failed");
        }
    }

    /**
     * Send message to the server and get response
     *
     * @param msg msg to send
     * @return response string from the server
     */
    public Message send(Message msg) {
        Message response = msg;
        try {
            response = remoteStore.process(msg, this.hashCode());
            Log.info("response received from " + hostname + ":" + port + " Operation status: " + response.getStatus());
        } catch (Exception e) {
            Log.exceptionThrown(e,"unable to get response");
        }
        return response;
    }

    private static Message formMessage(String s) {
        String[] request = s.split(",");
        if (request == null || (request.length != 2 && request.length != 3)) {
            System.out.println(request.length);
            System.out.println("Please check your format of input: " + s);
            return null;
        }

        Message msg = new Message(request[1], request.length == 3 ? request[2] : "", Message.Operation.GET);
        switch (request[0].toLowerCase()) {
            case "get":
                break;
            case "put":
                msg.setOpe(Message.Operation.PUT);
                break;
            case "delete":
                msg.setOpe(Message.Operation.DELETE);
                break;
            default:
                System.out.println("Unknow operation: please input put or get or delete");
                return  null;
        }
        return msg;
    }

    /**
     * Local test for RMI client
     */
    public static void main(String[] args) {
        Client client = new Client();
        if (args.length == 2) {
            try {
                int port = Integer.parseInt(args[1]);
                client = new Client(args[0], port);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Please check the format of port number");
            }
        }

        client.connect();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Please input your request: format: Operation,key(,value)");
            Message request = formMessage(scanner.nextLine());
            if (request != null) {
                Message response = client.send(request);
                Log.info(response.toString());
            }
        }
    }
}
