package assignment2.aufgabe2;

public class Message {

    public enum Type{
        WAKEUP,
        ECHO,
        RESULT
    }
    public final Type msgType;
    public final NodeThread from;
    public final NodeThread to;
    public final int initiatorId;
    public final Object data;

    public Message(Type msgType, NodeThread from, NodeThread to, int initiatorId, Object data) {
        this.msgType = msgType;
        this.from = from;
        this.to = to;
        this.initiatorId = initiatorId;
        this.data = data;
    }
}
