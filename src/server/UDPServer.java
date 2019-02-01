package server;

import service.PlainService;
import service.Service;
import service.Store;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * The UDPServer class is a server using TCP
 */
public class UDPServer implements Server {
    private DatagramSocket socket;          /* server socket */
    private int port = -1;                  /* port number */
    private Service service;                /* service of the server */
    private byte[] buf = new byte[1000];    /* UDP packet */

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
        // handle uninitialized socket
        if (port == -1) {
            PlainService.log("no set up for server port");
            return;
        }

        // create the server socket
        try {
            socket = new DatagramSocket(port);
            PlainService.log("server listens to " + port);
        } catch (Exception e) {
            PlainService.log(e.getClass() + ": unable to start the server.");
        }

        // get request and response to the client
        while (true) {
            DatagramPacket request = new DatagramPacket(buf, buf.length);

            try {
                socket.receive(request);
            } catch (Exception e) {
                PlainService.log(e.getClass() + ": unable to receive request.");
            }

            InetAddress address = request.getAddress();
            int port = request.getPort();

            PlainService.log(
                    "receive request from client: " +
                    new String(request.getData(), 0, request.getLength()) +
                    " of length " + request.getLength() +
                    " <address:" + request.getAddress() + ">" +
                    "<port:" + request.getPort() + ">");

            byte[] res = service == null ? "No service available".getBytes() : service.process(new String(request.getData(), 0, request.getLength())).getBytes();
            DatagramPacket response = new DatagramPacket(res, res.length, address, port);

            try {
                socket.send(response);
                PlainService.log(
                        "send response from server: " +
                        new String(response.getData(), 0, response.getLength()) +
                        " of length " + response.getLength() +
                        " <address:" + response.getAddress() + ">" +
                        "<port:" + response.getPort() + ">");
            } catch(Exception e) {
                PlainService.log(e.getClass() + ": unable to send response.");
            }
        }
    }

    @Override
    /**
     * Stop the server
     */
    public void stop() {
        socket.close();
    }

    /**
     * Local test for UDP server
     */
    public static void main(String[] args) {
        int port = 8000;
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        }

        UDPServer server = new UDPServer();
        server.setPort(port);
        server.setService(new Store());
        server.start();
    }
}
