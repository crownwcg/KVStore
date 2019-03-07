package service;

import java.io.Serializable;

/**
 * The clss Message defines the message information
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 20120731125400L;

    /* Define operation type of the message: get, put, or delete */
    public enum Operation {
        GET,
        PUT,
        DELETE
    }

    /* Define status type of the message:
        prepared to be committed,
        committed,
        aborted by server
     */
    public enum Status {
        PREPARED,
        COMMITTED,
        ABORTED
    }

    /* Define result type of the message:
        failed: operation is committed with some issues
        success: operation is committed successfully
     */
    public enum Result {
        FAILED,
        SUCCESS
    }

    /* Define status type of the message:
        sent by server,
        sent by client
     */
    public enum Type {
        SERVER,
        CLIENT
    }

    private String key;         /* key string */
    private String value;       /* value string */
    private Operation ope;      /* operation type */
    private Status status = Status.PREPARED;    /* status type */
    private Result result = Result.SUCCESS;     /* result type */
    private Type type = Type.CLIENT;            /* message type */

    /**
     * Constructor with key, value, and operation type
     *
     * @param key key string
     * @param value value string
     * @param ope operation type
     */
    public Message(String key, String value, Operation ope) {
        this.key = key;
        this.value = value;
        this.ope = ope;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Operation getOpe() {
        return ope;
    }

    public void setOpe(Operation ope) {
        this.ope = ope;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String toString() {
        return ope + " " + key + (value == null ? "" : " " + value) + " has " + result;
    }
}
