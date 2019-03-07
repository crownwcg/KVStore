package service;

import java.util.HashMap;
import java.util.Map;

/**
 * The Store class is a service that provides a key-value store
 */
public class Store {
    private Map<String, String> store;    /* the store */

    /**
     * Default constructor
     */
    public Store() {
        store = new HashMap<>();
        store.put("a", "a");
        store.put("b", "b");
        store.put("c", "c");
        store.put("d", "d");
        store.put("e", "e");
    }

    /**
     * Construcotr for copy
     *
     * @param store store map
     */
    private Store(Map<String, String> store) {
        this.store = new HashMap<>(store);
    }

    /**
     * The service processes the request
     *
     * @param message request to process
     * @return response to the request
     */
    public synchronized Message process(Message message) {
        // handle PUT/GET/DELETE request
        switch (message.getOpe()) {
            case GET:
                message.setValue(get(message.getKey()));
                message.setResult(message.getValue() == null ? Message.Result.FAILED : Message.Result.SUCCESS);
                break;
            case PUT:
                message.setResult(put(message.getKey(), message.getValue()) ? Message.Result.SUCCESS : Message.Result.FAILED);
                break;
            case DELETE:
                message.setResult(delete(message.getKey()) ? Message.Result.SUCCESS : Message.Result.FAILED);
                break;
        }
        message.setStatus(Message.Status.COMMITTED);
        return message;
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

    /**
     * @return a copy of this store
     */
    public Store localCache() {
        return new Store(store);
    }

}
