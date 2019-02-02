package client;

import service.Service;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.logging.Level;

/**
 * The UDPClient class is a client connected to the server using UDP
 */
public class UDPClient implements Client {
    private DatagramSocket socket;      /* datagram socket of the client */
    private InetAddress address;        /* address of the server */
    private int port = -1;              /* port number of the server */
    private byte[] buf = new byte[256]; /* UDP packet */

    /**
     * Set hostname
     * @param hostname hostname of the server
     */
    public void setAddress(String hostname) {
        try {
            this.address = InetAddress.getByName(hostname);
        } catch (Exception e) {
            Service.logger.log(Level.WARNING,e.getClass() + ": wrong hostname");
        }
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
            socket = new DatagramSocket();
            Service.logger.log(Level.INFO,"initiate the socket");
        } catch (Exception e) {
            Service.logger.log(Level.WARNING,e.getClass() + ": unable to initiate the socket.");
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
        if (address == null || port == -1) {
            Service.logger.log(Level.SEVERE,"no address or port.");
            return "";
        }

        // send request
        byte[] msgBytes = msg.getBytes();
        DatagramPacket request = new DatagramPacket(msgBytes, msgBytes.length, address, port);

        try {
            socket.send(request);
            // use a timeout mechanism to deal with an unresponsive server
            socket.setSoTimeout(2000);
            Service.logger.log(Level.INFO,"send request: " +
                    new String(request.getData(), 0, request.getLength()) +
                    " to <address:" + address + ">" + "<port:" + port + ">");
        } catch (Exception e) {
            Service.logger.log(Level.WARNING,e.getClass() + ": unable to send request.");
        }

        // get response
        DatagramPacket response = new DatagramPacket(buf, buf.length);
        try {
            socket.receive(response);
        } catch (Exception e) {
            Service.logger.log(Level.WARNING,e.getClass() + ": unable to receive response.");
            return  "";
        }

        String received = new String(response.getData(), 0, response.getLength());
        Service.logger.log(Level.INFO,"response received: " + received);
        return received;
    }

    @Override
    /**
     * Close the client
     */
    public void close() {
        if (socket != null) {
            socket.close();
        }
    }

    /**
     * Local test for UDP client
     */
    public static void main(String[] args) {
        String host = "localhost";
        int port = 8080;
        if (args.length == 2) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }

        for (String str : msgs) {
            UDPClient client = new UDPClient();
            client.setAddress(host);
            client.setPort(port);
            client.connect();
            client.send(str);
            client.close();
        }
    }
}
