package service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The Store class is a service that provides a key-value store
 */
public class Store implements Serializable {
    private static final long serialVersionUID = 20120734325400L;
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

    public Store(Store store) {
        this.store = new HashMap<>(store.store);
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
    public Store duplicate() {
        return new Store(this);
    }

}
