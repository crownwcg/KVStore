package service;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The calss Log is for logging
 */
public class Log {
    private static final Logger logger = Logger.getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);

    /**
     * Log with level info
     *
     * @param s message
     */
    public static void info(String s) {
        logger.log(Level.INFO, s);
    }

    /**
     * Log with level warning
     *
     * @param s message
     */
    public static void warning(String s) {
        logger.log(Level.WARNING, s);
    }

    /**
     * Log that registry is found
     *
     * @param hostname hostname
     * @param port port number
     * @param name name of registry
     */
    public static void registryFound(String hostname, int port, String name) {
        info("registry " + name + " found in " + hostname + " of port " + port);
    }

    /**
     * Log that an exception is thrown
     *
     * @param e exception class
     * @param msg message
     */
    public static void exceptionThrown(Exception e, String msg) {
        e.printStackTrace();
        warning(e.getClass() + ": " + msg);
    }
}
