package server;

import service.Service;

/**
 * The interface provides necessary function for a server to work
 */
public interface Server {

    /**
     * Set port number
     * @param port port number of the server
     */
    void setPort(int port);

    /**
     * Set the service
     * @param service service of the server
     */
    void setService(Service service);

    /**
     * Start the server
     */
    void start();

    /**
     * Stop the server
     */
    void stop();
}
