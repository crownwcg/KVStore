package service;

import java.util.logging.Logger;

/**
 * The interface provides necessary function for a service to work
 */
public interface Service {
    Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * The service processes the request
     *
     * @param request request to process
     * @return response to the request
     */
    String process(String request);
}
