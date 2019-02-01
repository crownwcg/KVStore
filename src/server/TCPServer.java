package server;

import service.PlainService;
import service.Service;
import service.Store;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The TCPServer class is a server using TCP
 */
public class TCPServer implements Server {
    private int port = -1;                  /* port number */
    private ServerSocket serverSocket;      /* server socket */
    private Service service;                /* service of the server */

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

    /**
     * Process the request received from the socket
     *
     * @param socket the listening socket
     */
    public void execute(Socket socket) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String request = reader.readLine();
            PlainService.log("request received: " + request);
            String response = service == null ? "No service available" : service.process(request);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write(response);
            writer.newLine();
            writer.flush();
            reader.close();
            writer.close();
            PlainService.log("response sent: " + response);
        } catch (Exception e) {
            PlainService.log(e.getClass() + ": unable to get response");
        }
    }

    @Override
    /**
     * Start the server
     */
    public void start() {
        // handle uninitialized socket
        if (port == -1) {
            PlainService.log("no port set");
            return;
        }

        // create the server socket
        try {
            serverSocket = new ServerSocket(port);
            PlainService.log("server listens to " + port);
        } catch (Exception e) {
            PlainService.log(e.getClass() + ": unable to start the server");
        }

        // accept the connection
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                socket.setSoTimeout(2000);
                execute(socket);
                socket.close();
            } catch (Exception e) {
                PlainService.log(e.getClass() + ": unable to connect to the client");
            }
        }
    }

    @Override
    /**
     * Stop the server
     */
    public void stop() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
                PlainService.log("server closed");
            } catch (Exception e) {
                PlainService.log("unable to close the socket");
            }
        }
    }

    /**
     * Local test for TCP server
     */
    public static void main(String[] args) {
        int port = 8000;
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        }

        TCPServer server = new TCPServer();
        server.setPort(port);
        server.setService(new Store());
        server.start();
    }
}