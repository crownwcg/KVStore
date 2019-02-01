package service;

import java.sql.Timestamp;

/**
 * The PlainService class provides some static service
 */
public class PlainService implements Service {
    @Override
    /**
     * The service processes the request
     *
     * @param request request to process
     * @return response to the request
     */
    public String process(String request) {
        return "Plain Service";
    }

    /**
     * Log messgae to the console with timestamp
     *
     * @param msg msg to log
     */
    public static void log(String msg) {
        System.out.println(new Timestamp(System.currentTimeMillis()) + ": " + msg);
    }
}
