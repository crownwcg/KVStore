package server;

import service.Service;
import service.Store;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

/**
 * The TCPServer class is a server using TCP
 */
public class TCPServer implements Server, Runnable {
    private int port = -1;                  /* port number */
    private ServerSocket serverSocket;      /* server socket */
    private Service service;                /* service of the server */
    private ExecutorService threadPool = Executors.newFixedThreadPool(10);

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
     */
    public void execute(Socket socket) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String request = reader.readLine();
            Service.logger.log(Level.INFO,"request received: " + request);
            String response = service.process(request);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write(response);
            writer.newLine();
            writer.flush();
            reader.close();
            writer.close();
            Service.logger.log(Level.INFO,"response sent: " + response);
        } catch (Exception e) {
            Service.logger.log(Level.WARNING,e.getClass() + ": unable to get response");
        }
    }

    @Override
    /**
     * Start the server
     */
    public void start() {
        // handle uninitialized socket
        if (port == -1) {
            Service.logger.log(Level.WARNING,"no port set");
            return;
        }

        // create the server socket
        try {
            serverSocket = new ServerSocket(port);
            Service.logger.log(Level.INFO, "server listens to " + port);
        } catch (Exception e) {
            Service.logger.log(Level.WARNING,e.getClass() + ": unable to start the server");
        }

        // accept the connection
        while (true) {
            try {
                Service.logger.log(Level.INFO,"server sleeps");
                Thread.sleep(1000);
                Service.logger.log(Level.INFO,"server awakes");
            } catch (Exception e) {
                Service.logger.log(Level.WARNING,e.getClass() + ": thread sleep error.");
            }

            try {
                Socket socket = serverSocket.accept();
                socket.setSoTimeout(10000);
                this.threadPool.execute(() -> {
                    execute(socket);
                });
            } catch (Exception e) {
                Service.logger.log(Level.WARNING,e.getClass() + ": unable to connect to the client");
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
                Service.logger.log(Level.INFO,"server closed");
            } catch (Exception e) {
                Service.logger.log(Level.WARNING,"unable to close the socket");
            }
        }
    }

    @Override
    public void run() {
        start();
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
        server.setService(Store.getInstance());
        new Thread(server).start();
    }
}