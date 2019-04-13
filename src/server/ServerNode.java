package server;

import message.ClientMessage;
import message.Message;
import message.ServerMessage;
import service.Log;
import service.Store;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * Node server of replica
 */
public class ServerNode implements Server {
    private int port = 9000;            /* port number */
    private Store store = new Store();  /* service of the server */
    private Map<Integer, Server> nodes = new HashMap<>();        /* peer servers' port numbers */
    private Set<Integer> failedNodes = new HashSet<>();
    private ServerMessage previousVote = new ServerMessage();

    public ServerNode() {}

    /**
     * Construtor with ports array, first element is the port number of the server,
     * the followings are other servers' port numbers
     *
     * @param ports port numbers
     */
    public ServerNode(int[] ports) {
        port = ports[0];

        // initiate servers connection at first
        PriorityQueue<ServerMessage> pq = new PriorityQueue<>((i, j) -> (int) (j.getPreviousVote().getVote() - i.getPreviousVote().getVote()));
        for (int i = 1; i < ports.length; i++) {
            if (nodes.containsKey(ports[i]) || nodes.get(ports[i]) == null) {
                connect(ports[i]);
                try {
                    ServerMessage recoverRequest = new ServerMessage();
                    recoverRequest.setSender(ports[0]);
                    recoverRequest.setStatus(Message.Status.RECOVER);
                    ServerMessage serverMessage = nodes.get(ports[i]).process(recoverRequest);
                    if (serverMessage.getStore() != null) {
                        pq.add(serverMessage);
                    }
                } catch(Exception e){
                    Log.exceptionThrown(e, "The server on port:" + port + " is down.");
                    failedNodes.add(port);
                }
            }
        }

        if (!pq.isEmpty()) {
            ServerMessage recover = pq.poll();
            store = recover.getStore();
            for (int port : recover.getNodes().keySet()) {
                if (port != this.port) {
                    nodes.put(port, recover.getNodes().get(port));
                }
            }
            for (int port : recover.getFailedNodes()) {
                if (port != this.port) {
                    failedNodes.add(port);
                }
            }
            previousVote = recover.getPreviousVote();
        }
    }

    private void connect(int port) {
        Log.info("initilize the connection to the server on port:" + port);
        try {
            // Looking up the registry for the remote object
            Server node = (Server) LocateRegistry.getRegistry("localhost", port).lookup("Server");
            Log.registryFound("localhost", port, "Server");
            nodes.put(port, node);
        } catch (Exception e) {
            Log.exceptionThrown(e, "connection to localhost:" + port + " failed");
        }
    }

    /**
     * The service processes the request
     *
     * @param clientMessage request to process
     * @return response to the request
     */
    @Override
    public ClientMessage process(ClientMessage clientMessage) {
        Log.info("process " + clientMessage.getOpe() + " operation for " + clientMessage.getClientId()
                + " with key: " + clientMessage.getKey()
                + (clientMessage.getOpe() == Message.Operation.PUT ? " and value: " + clientMessage.getValue() : ""));
        return clientMessage.getOpe() == Message.Operation.GET ? get(clientMessage) : update(clientMessage);
    }

    /**
     * Process server message from other servers
     *
     * @param serverMessage message from other servers
     * @return response to the server
     */
    @Override
    public ServerMessage process(ServerMessage serverMessage) {
        // if it's a message requested for recovering
        switch (serverMessage.getStatus()) {
            case PREPARED:
                Log.info("Prepared proposal requested from server on port:" + serverMessage.getSender());
                if (serverMessage.getVote() > previousVote.getVote()) {
                    serverMessage.setStatus(Message.Status.PROMISE);
                    serverMessage.setValue(previousVote.getValue());
                    previousVote.setVote(serverMessage.getVote());
                }
                break;
            case ACCEPTED:
                Log.info("Accepted proposal requested from server on port:" + serverMessage.getSender());
                if (serverMessage.getVote() == previousVote.getVote()) {
                    previousVote.setValue(serverMessage.getValue());
                    if (serverMessage.getOpe() == Message.Operation.PUT) {
                        store.put(serverMessage.getKey(), serverMessage.getValue());
                    } else if (serverMessage.getOpe() == Message.Operation.DELETE) {
                        store.delete(serverMessage.getKey());
                    }
                }
            case RECOVER:
                Log.info("Recover requested from server on port:" + serverMessage.getSender());
                if (!nodes.containsKey(serverMessage.getSender())) {
                    nodes.put(serverMessage.getSender(), null);
                }
                failedNodes.remove(serverMessage.getSender());
                serverMessage.setStore(store.duplicate());
                serverMessage.setNodes(new HashMap<>(nodes));
                serverMessage.setFailedNodes(new HashSet<>(failedNodes));
                serverMessage.setPreviousVote(new ServerMessage(previousVote));
                break;
            default:
                break;
        }

        return serverMessage;
    }

    /**
     * Process get operation
     *
     * @param clientMessage request to process
     * @return response to the request
     */
    private ClientMessage get(ClientMessage clientMessage) {
        String value = store.get(clientMessage.getKey());
        clientMessage.setResult(value == null ? Message.Result.FAILED : Message.Result.SUCCESS);
        clientMessage.setValue(value);
        return clientMessage;
    }

    /**
     * Process updates (put and delete)
     *
     * @param clientMessage request to process
     * @return response to the request
     */
    private ClientMessage update(ClientMessage clientMessage) {
        // phase1: prepare
        // get response from other servers
        Log.info("multicasting to other servers");
        ServerMessage serverMessage = new ServerMessage(clientMessage);
        serverMessage.setSender(port);
        long vote = previousVote.getVote() + port % (nodes.size() + 1) + 1;
        serverMessage.setVote(vote);
        serverMessage.setStatus(Message.Status.PREPARED);
        List<ServerMessage> serverMessages = new ArrayList<>();
        for (int port : nodes.keySet()) {
            System.out.println(port);
            if (nodes.get(port) == null) {
                connect(port);
            }
        }

        int total = 0;  // total alive servers
        Set<Integer> ports = new HashSet<>(nodes.keySet());
        for (int port : ports) {
            Server node = nodes.get(port);
            try {
                ServerMessage response = node.process(serverMessage);
                serverMessages.add(response);
                total++;
            } catch (Exception e) {
                Log.exceptionThrown(e, "The server on port:" + port + " is down.");
                failedNodes.add(port);
                nodes.remove(port);
            }
        }
        // check if response is promised
        int count = 0;  // count of promising servers
        for (ServerMessage msg : serverMessages) {
            if (msg.getStatus() == Message.Status.PROMISE) {
                count++;
            }
        }
        if (total != 0 && count <= total / 2) {
            // abort this proposal
            clientMessage.setResult(Message.Result.FAILED);
            return clientMessage;
        }

        // phase2: propose
        Log.info("multicasting to commit the update");
        ServerMessage proposal = new ServerMessage(serverMessage);
        proposal.setStatus(Message.Status.ACCEPTED);
        proposal.setSender(port);
        if (clientMessage.getOpe() == Message.Operation.PUT) {
            for (ServerMessage msg : serverMessages) {
                if (msg.getValue() != null && msg.getVote() > proposal.getVote()) {
                    proposal.setValue(msg.getValue());
                    proposal.setVote(msg.getVote());
                }
            }
        }
        // multicast the proposal
        proposal.setVote(vote);
        ports = new HashSet<>(nodes.keySet());
        for (int port : ports) {
            Server node = nodes.get(port);
            try {
                node.process(proposal);
            } catch (Exception e) {
                Log.exceptionThrown(e, "The server on port:" + port + " is down.");
                failedNodes.add(port);
                nodes.remove(port);
            }
        }

        previousVote.setVote(vote);
        if (clientMessage.getOpe() == Message.Operation.PUT) {
            store.put(clientMessage.getKey(), clientMessage.getValue());
        } else {
            store.delete(serverMessage.getKey());
        }
        return new ClientMessage(serverMessage);
    }

    /**
     * Start the server
     */
    private void start() {
        try {
            Server server = (Server) UnicastRemoteObject.exportObject(this, port);
            Log.info("remote object exported");

            Registry registry = LocateRegistry.createRegistry(port);
            Log.info("rmi registry created");

            registry.bind("Server", server);
            Log.info("server ready in port:" + port + " using name \'Server\'");
        } catch (Exception e) {
            Log.exceptionThrown(e, "unable to initialize the server");
        }
    }

    /**
     * Local test for server node
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please input port numbers of servers");
        }

        int[] ports = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            try {
                ports[i] = Integer.parseInt(args[i]);
            } catch (Exception e) {
                System.out.println("Please input 5 integers");
            }
        }

        new ServerNode(ports).start();
    }
}
