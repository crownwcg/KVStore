package message;

import server.Server;
import service.Store;

import java.util.Map;
import java.util.Set;

public class ServerMessage extends Message {
    private Status status;      /* status */
    private long vote = -1;     /* vote number */

    private int sender;
    private Store store;
    private Map<Integer, Server> nodes;
    private Set<Integer> failedNodes;
    private ServerMessage previousVote;

    public ServerMessage() {}

    public ServerMessage(Message message) {
        super(message);
    }

    public ServerMessage(ServerMessage serverMessage) {
        super(serverMessage);
        status = serverMessage.status;
        vote = serverMessage.vote;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getVote() {
        return vote;
    }

    public void setVote(long vote) {
        this.vote = vote;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Map<Integer, Server> getNodes() {
        return nodes;
    }

    public void setNodes(Map<Integer, Server> nodes) {
        this.nodes = nodes;
    }

    public Set<Integer> getFailedNodes() {
        return failedNodes;
    }

    public void setFailedNodes(Set<Integer> failedNodes) {
        this.failedNodes = failedNodes;
    }

    public ServerMessage getPreviousVote() {
        return previousVote;
    }

    public void setPreviousVote(ServerMessage previousVote) {
        this.previousVote = previousVote;
    }
}
