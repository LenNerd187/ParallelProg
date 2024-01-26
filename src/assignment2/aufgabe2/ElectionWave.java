package assignment2.aufgabe2;

public class ElectionWave {

    public ElectionWave(Node initiator, Node wokeUpFrom) {
        this.initiator = initiator;
        this.wokeUpFrom = wokeUpFrom;
    }

    Node initiator;
    Node wokeUpFrom;
    boolean sentWakeups = false;
    boolean sentEcho = false;



}
