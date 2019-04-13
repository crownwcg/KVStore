package message;

public class ClientMessage extends Message {
    public ClientMessage(Message message) {
        super(message);
    }

    public ClientMessage(String key, String value, Operation ope) {
        this.key = key;
        this.value = value;
        this.ope = ope;
    }

    private int clientId;       /* client id */

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }
}
