package service;

/**
 * The interface provides necessary function for a service to work
 */
public interface Service {
    /**
     * The service processes the request
     *
     * @param request request to process
     * @return response to the request
     */
    String process(String request);
}
