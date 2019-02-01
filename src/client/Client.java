package client;

/**
 * The interface provides necessary function for a client to work
 */
public interface Client {
    /**
     * Connect to the specific server
     */
    void connect();

    /**
     * Send message to the server and get response
     *
     * @param msg msg to send
     * @return response string from the server
     */
    String send(String msg);

    /**
     * Close the client
     */
    void close();
}
