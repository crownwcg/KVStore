package message;

import java.io.Serializable;

/**
 * The clss Message defines the message information
 */
public abstract class Message implements Serializable {
    private static final long serialVersionUID = 20120731125400L;

    String key;         /* key string */
    String value;       /* value string */
    Operation ope;      /* operation type */
    Result result = Result.SUCCESS;     /* result type */

    Message() {}

    Message(Message message) {
        this.key = message.getKey();
        this.value = message.getValue();
        this.ope = message.getOpe();
        this.result = message.getResult();
    }

    /* Define operation type of the message: get, put, or delete */
    public enum Operation {
        GET,
        PUT,
        DELETE
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
        prepared to be committed,
        committed,
        aborted by server
     */
    public enum Status {
        PREPARED,
        PROMISE,
        ACCEPTED,
        RECOVER
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

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return ope + " " + key + " " + value + " " + result;
    }
}
