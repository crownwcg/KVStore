package server;

import client.Client;
import service.Log;
import service.Message;
import service.Store;

import java.util.ArrayList;
import java.util.List;

/**
 * The RemoteStoreImp is implementation of RemoteStore interface
 */
public class RemoteStoreImp implements RemoteStore {
    private Store store,    /* key value store */
            cache;          /* local cache for key value store */
    private List<Integer> ports;                        /* other distributed server node's port */
    private List<Client> clients = new ArrayList<>();   /* client instance connecting other server node */

    /**
     * Constructor with store and ports
     *
     * @param store store service
     * @param ports other servers' port numbers
     */
    public RemoteStoreImp(Store store, List<Integer> ports) {
        this.store = store;
        this.ports = new ArrayList<>(ports);
    }

    @Override
    /**
     * The service processes the request
     *
     * @param message request to process
     * @return response to the request
     */
    public Message process(Message message, int id) {
        Log.info("process " + message.getOpe() + " operation for " + id
                + " with key: " + message.getKey()
                + (message.getOpe() == Message.Operation.PUT ? " and value: " + message.getValue() : ""));
        Message response = message.getOpe() == Message.Operation.GET ? get(message) : update(message);
        return response;
    }

    /**
     * Process get operation
     *
     * @param message request to process
     * @return response to the request
     */
    private Message get(Message message) {
        return store.process(message);
    }

    /**
     * Process updates (put and delete)
     *
     * @param message request to process
     * @return response to the request
     */
    private Message update(Message message) {
        // initiate servers connection at first
        if (clients.size() != ports.size()) {
            Log.info("initilize the server group");
            unionServers();
        }

        // if message received from client, do multicast
        if (message.getType() == Message.Type.CLIENT) {
            message.setType(Message.Type.SERVER);

            // get response from other servers
            Log.info("multicasting to other servers");
            List<Message> messages = new ArrayList<>();
            for (Client c : clients) {
                messages.add(c.send(message));
            }

            // check if any response is aborted
            for (Message msg : messages) {
                if (msg.getStatus() == Message.Status.ABORTED) {
                    Log.warning("message is aborted by a server");
                    message.setStatus(Message.Status.ABORTED);
                    Log.info("aborting the message");
                    for (Client c : clients) {
                        c.send(message);
                    }
                    return message;
                }
            }

            // no abortion, commit updates
            Log.info("multicasting to commit the update");
            message.setStatus(Message.Status.COMMITTED);
            for (Client c : clients) {
                c.send(message).toString();
            }
            return store.process(message);
        }

        // if message is sent from a server
        // prepared: do local cache for operation
        // committed: commit the update
        // aborted: clear cache
        switch (message.getStatus()) {
            case PREPARED:
                cache = store.localCache();
                Log.info("cache the update");
                return cache.process(message);
            case COMMITTED:
                store = cache;
                cache = null;
                Log.info("commit the update");
                break;
            case ABORTED:
                cache = null;
                Log.info("abort the update");
                break;
        }
        return message;
    }

    /**
     * Union servers using rmi
     */
    private void unionServers() {
        clients = new ArrayList<>();
        for (int port : ports) {
            Client client = new Client("localhost", port);
            client.connect();
            clients.add(client);
        }
    }
}
