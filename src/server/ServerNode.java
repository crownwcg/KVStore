package server;

import service.Store;

/**
 * Node server of replica
 */
public class ServerNode extends Server {
    /**
     * Construtor with ports array, first element is the port number of the server,
     * the followings are other servers' port numbers
     *
     * @param ports port numbers
     */
    public ServerNode(int[] ports) {
        super(ports[0], new Store());
        for (int i =  1; i < ports.length; i++) {
            this.ports.add(ports[i]);
        }
    }

    /**
     * Local test for server node
     */
    public static void main(String[] args) {
        if (args.length != 5) {
            System.out.println("Please input 5 port numbers of servers");
        }

        int[] ports = new int[5];
        for (int i = 0; i < 5; i++) {
            try {
                ports[i] = Integer.parseInt(args[i]);
            } catch (Exception e) {
                System.out.println("Please input 5 integers");
            }
        }

        new ServerNode(ports).start();
    }
}
