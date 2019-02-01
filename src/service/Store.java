package service;

import java.util.HashMap;
import java.util.Map;

/**
 * The Store class is a service that provides a key-value store
 */
public class Store implements Service {
    private Map<String, String> store = new HashMap<>();    /* the store */

    @Override
    /**
     * The service processes the request
     *
     * @param request request to process
     * @return response to the request
     */
    public String process(String request) {
        String[] msg = request.split(",");
        String ope = msg[0].toUpperCase();

        // handle PUT/GET/DELETE request
        if (ope.equals("PUT")) {
            if (msg.length != 3) {
                return "received acknowledging PUT with an invalid KEY, VALUE pair";
            }
            String key = msg[1], value = msg[2];
            put(key, value);
            return "succeed to PUT " + key + " " + value;
        } else if (ope.equals("GET")) {
            if (msg.length != 2) {
                return "received acknowledging GET with an invalid request format";
            }
            if (get(msg[1]) == null) {
                return "KEY doesn't exist";
            }
            return "succeed to get " + "\'" + get(msg[1]) + "\' of KEY " + msg[1];
        } else if (ope.equals("DELETE")) {
            if (msg.length != 2) {
                return "received acknowledging DELETE with an invalid request format";
            }
            if (!delete(msg[1])) {
                return "KEY doesn't exist";
            }
            return "succeed to delete KEY " + msg[1];
        }

        return "received unknown request: " + request;
    }

    /**
     * Put the key-value pair to the store
     *
     * @param key key
     * @param value value
     * @return true if key doesn't exist and otherwise
     */
    public boolean put(String key, String value) {
        boolean contains = store.containsKey(key);
        store.put(key, value);
        return !contains;
    }

    /**
     * Get value of key
     *
     * @param key key
     * @return value of the key or null
     */
    public String get(String key) {
        return store.containsKey(key) ? store.get(key) : null;
    }

    /**
     * Delete key
     *
     * @param key key
     * @return true if key exists and otherwise
     */
    public boolean delete(String key) {
        boolean contains = store.containsKey(key);
        store.remove(key);
        return contains;
    }

}
