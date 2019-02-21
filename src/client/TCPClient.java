package client;

import service.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;

/**
 * The TCPClient class is a client connected to the server using TCP
 */
public class TCPClient implements Client {
    private String hostname;    /* hostname of the server */
    private int port = -1;      /* port number of the server */
    private Socket socket;      /* client socket */

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
        // handle uninitialized client
        if (port == -1 || hostname == null) {
            Service.logger.log(Level.SEVERE,"no port or host set up");
            return;
        }

        // create client socket
        try {
            socket = new Socket(hostname, port);
            // use a timeout mechanism to deal with an unresponsive server
            socket.setSoTimeout(10000);
            Service.logger.log(Level.INFO,"connect to <address:" + hostname + ">" + "<port:" + port + ">");
        } catch (Exception e) {
            Service.logger.log(Level.WARNING,"cannot connect to <address:" + hostname + ">" + "<port:" + port + ">");
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
        // handle uninitialized client socket
        if (socket == null) {
            Service.logger.log(Level.SEVERE,"socket is not connected");
            return "";
        }

        // get response
        String response = "";
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write(msg);
            writer.newLine();
            writer.flush();
            Service.logger.log(Level.INFO, "send request: " + msg);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            response = reader.readLine();
            Service.logger.log(Level.INFO,"response received: " + response);
            reader.close();
            writer.close();
        } catch (Exception e) {
            Service.logger.log(Level.WARNING,e.getClass() + ": unable to send message");
        }
        return response;
    }

    @Override
    /**
     * Close the client
     */
    public void close() {
        if (socket != null) {
            try {
                socket.close();
                Service.logger.log(Level.INFO,"socket closed");
            } catch (Exception e) {
                Service.logger.log(Level.WARNING,e.getClass() + ": unable to close the socket");
            }
        }
    }

    /**
     * Local test for TCP client
     */
    public static void main(String[] args) {
        String host = "localhost";
        int port = 8000;
        if (args.length == 2) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }

        for (String str : msgs) {
            TCPClient client = new TCPClient();
            client.setHostname(host);
            client.setPort(port);
            client.connect();
            client.send(str);
            client.close();
        }
    }
}
