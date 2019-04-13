package client;

import message.ClientMessage;
import server.Server;
import service.Log;
import message.Message;

import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

/**
 * The RMIlient class is a client connected to the server using RMI
 */
public class Client {
    private String hostname = "localhost";      /* hostname of the server */
    private int port = 9000;                    /* port number of the server */
    private Server server;                      /* remote store object */

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
    private void connect() {
        try {
            // Looking up the registry for the remote object
            server = (Server) LocateRegistry.getRegistry(hostname, port).lookup("Server");
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
    private Message send(ClientMessage msg) {
        msg.setClientId(this.hashCode());
        Message response = msg;
        try {
            response = server.process(msg);
            Log.info("response received from " + hostname + ":" + port + " Operation status: " + response.getResult());
        } catch (Exception e) {
            Log.exceptionThrown(e,"unable to get response");
        }
        return response;
    }

    private static ClientMessage formMessage(String s) {
        String[] request = s.split(",");
        if (request.length != 2 && request.length != 3) {
            System.out.println(request.length);
            System.out.println("Please check your format of input: " + s);
            return null;
        }

        ClientMessage msg = new ClientMessage(request[1], request.length == 3 ? request[2] : "", Message.Operation.GET);
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
            ClientMessage request = formMessage(scanner.nextLine());
            if (request != null) {
                Message response = client.send(request);
                Log.info(response.toString());
            }
        }
    }
}
