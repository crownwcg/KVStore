package client;

import service.PlainService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

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
            PlainService.log("no port or host set up");
            return;
        }

        // create client socket
        try {
            socket = new Socket(hostname, port);
            // use a timeout mechanism to deal with an unresponsive server
            socket.setSoTimeout(2000);
            PlainService.log("connect to <address:" + hostname + ">" + "<port:" + port + ">");
        } catch (Exception e) {
            PlainService.log("cannot connect to <address:" + hostname + ">" + "<port:" + port + ">");
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
            PlainService.log("socket is not connected");
            return "";
        }

        // get response
        String response = "";
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write(msg);
            writer.newLine();
            writer.flush();
            PlainService.log("send request: " + msg);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            response = reader.readLine();
            PlainService.log("response received: " + response);
            reader.close();
            writer.close();
        } catch (Exception e) {
            PlainService.log(e.getClass() + ": unable to send message");
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
                PlainService.log("socket closed");
            } catch (Exception e) {
                PlainService.log(e.getClass() + ": unable to close the socket");
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

        String[] msgs = new String[]{
                "PUT,a,1",
                "GET,a",
                "PUT,b,2",
                "PUT,c,3",
                "DELETE,a",
                "PUT,a,4",
                "PUT,d,4",
                "GET,a",
                "GET,b",
                "GET,c",
                "GET,d",
                "DELETE,a",
                "DELETE,b",
                "DELETE,c",
                "DELETE,d"
        };
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
