package assignment2.aufgabe2;

public class Message {

    enum Type{
        WAKEUP,
        ECHO,
        RESULT
    }
    public final Type msgType;
    public final Node from;
    public final Node to;
    public final int initiatorId;
    public final int value;
    public final Object data;

    public Message(Type msgType, Node from, Node to, int initiatorId, int value, Object data) {
        this.msgType = msgType;
        this.from = from;
        this.to = to;
        this.initiatorId = initiatorId;
        this.value = value;
        this.data = data;
    }
}
