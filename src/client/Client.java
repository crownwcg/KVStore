package client;

/**
 * The interface provides necessary function for a client to work
 */
public interface Client {
    // test case
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
