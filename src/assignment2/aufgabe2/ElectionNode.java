package assignment2.aufgabe2;

import assignment2.aufgabe1.Edge;

import java.util.*;

public class ElectionNode extends NodeThread {

    private final HashSet<Edge> knownEdges = new HashSet<>();
    private int highestSentInitiator = -1;
    private int highestEchoSent = -1;
    private boolean resultSent = false; //nicht unbedingt benötigt, wenn resultSend = true  wird running auf false gesetzt und der Thread terminiert


    //################ Shared Data ########################################################################
    private NodeThread wokeUpFrom;        //the node that woke this up
    private int messageCounter = 0; //number of messages (wakeup oder echo) this node got
    private int highestKnownInitiator = -1; //wird beim Empfangen der wakeup Nachrichten aktualisiert
    private NodeThread resultReceivedFrom = null;
    //#####################################################################################################


    public ElectionNode(int id) {
        super(id);
    }

    /**
     * Thread Run Methode:
     * Main Loop: sleep -> wakeup/initiate wakeups -> spread wakeups? -> send echos? -> send result? -> repeat
     * Der ElectionNode-Thread läuft in einer Loop:
     * 1. Schlafen legen
     * 2. Aufgeweckt werden (von Nachricht oder timeout)
     * 3. Wenn wir nicht von einer Election Welle aufgeweckt wurden:
     *      Auswürfeln ob wir eine Election Welle auslösen wollen
     * 4. Prüfen ob wir Wakeup Nachrichten versenden wollen
     * 4. Prüfen ob wir eine Echo Nachricht versenden wollen
     * 4. Prüfen ob wir eine Result Nachricht weiterleiten wollen
     * 5. Loop
     */
    @Override
    public void run() {
        runInitialize();
        System.out.println("Node " + id + " initialized.");

        while(running) {
            //Loop: sleep -> wakeup -> spread wakeups? -> send echo? -> send result? -> repeat

            waitForMessages(100);

            synchronized (this){
                if (wokeUpFrom == null) {
                    //uns hat noch keine Welle erreicht. Der Timer hat uns geweckt. Wollen wir Leader werden?
                    boolean wantsInitiator = new Random().nextBoolean();
                    if (wantsInitiator){
                        System.out.println("Node " + id + " initiating election wave");
                        highestKnownInitiator = id;
                        wokeUpFrom = this;
                    }
                }
            }

            //wakeups to spread??
            sendWakeupMessages();

            //send echo?
            sendEchoMessages();

            //Send resultMessages?
            sendResultMessages();

        } // Main Loop

    } // run Method


    /**
     * im kritischen Bereich wird überprüft ob wir eine neue stärkere Wakeup Welle erhalten haben
     * evtl werden Wakeup-Nachrichten erstellt - das "verschicken" an die Nachbarn ist NICHT mehr im kritischen Bereich
     * um die inkrementelle Anforderung der Locks zu umgehen
     */
    private void sendWakeupMessages(){
        List<Message> messagesToSent = new ArrayList<>();
        synchronized (this){
            if (highestKnownInitiator > highestSentInitiator){
                highestSentInitiator = highestKnownInitiator;
                for (NodeThread node:neighbours) {
                    if (node != wokeUpFrom) {
                        messagesToSent.add(new Message(Message.Type.WAKEUP, this, node, highestKnownInitiator, null));
                    }
                }
            }
        }
        sendMessages(messagesToSent);
    }


    /**
     * im kritischen Bereich wird überprüft ob wir alle Nachrichten bekommen haben und noch kein Echo zu dieser Welle verschickt haben
     * evtl wird eine Echo Nachricht erstellt - das "verschicken" an den Nachbarn ist NICHT mehr im kritischen Bereich
     * um die inkrementelle Anforderung der Locks zu umgehen
     */
    private void sendEchoMessages(){
        LinkedList<Message> messagesToSent = new LinkedList<>();
        synchronized (this){
            if (messageCounter == neighbours.size()) {
                if (highestKnownInitiator > highestEchoSent) {
                    if (this.wokeUpFrom == this) {
                        System.out.println("Initiator got all messages back - we can send the Result now");
                        printResult(knownEdges);
                        resultReceivedFrom = this;
                        return;
                    } else {
                        System.out.println("Node " + id + " received calls from all neighbours - sending echo now");
                        messagesToSent.add(new Message(Message.Type.ECHO, this, wokeUpFrom, highestKnownInitiator, knownEdges));
                    }
                    highestEchoSent = highestKnownInitiator;
                }
            }
        }
        sendMessages(messagesToSent);
    }


    /**
     * im kritischen Bereich wird überprüft ob wir ein Ergebnis haben welches wir noch nicht versendet haben
     * evtl werden Result-Nachrichten erstellt - das "verschicken" an die Nachbarn ist NICHT mehr im kritischen Bereich
     * um die inkrementelle Anforderung der Locks zu umgehen
     */
    private void sendResultMessages(){
        LinkedList<Message> messagesToSent = new LinkedList<>();
        synchronized (this){
            if(resultReceivedFrom != null && resultSent == false) {
                for (NodeThread node : neighbours) {
                    if (node != resultReceivedFrom) {
                        messagesToSent.add(new Message(Message.Type.RESULT, this, node, highestSentInitiator, null));
                    }
                }
                resultSent = true;
                running = false;
            }
        }
        sendMessages(messagesToSent);
    }

    /**
     * Beim Versenden hat der Sende-Thread den Lock des Empfänger-Node aber NICHT den Lock des Sende-Node
     * um die inkrementelle Anforderung der Locks zu umgehen
     *
     * @param messages Liste mit Nachrichten zum versenden
     */
    private void sendMessages(List<Message> messages){
        while(messages.size() > 0){
            Message message = messages.removeFirst();
            switch (message.msgType){
                case WAKEUP :
                    message.to.wakeup(message.from, message.initiatorId);
                    break;
                case ECHO :
                    message.to.echo(message.from, message.data, message.initiatorId);
                    break;
                case RESULT:
                    message.to.result(message.from, message.initiatorId);
                    break;
            }
        }
    }


    /**
     * Erhalten einer Wakeup Nachricht. Synchronized: Der sendende Thread hält den Lock vom empfangenden Node
     * @param neighbour Der Nachbarknoten der diese wakeup Nachricht verschickt
     *
     * @param initiatorId Die ID des Initiatorknoten, die Wakeupwellen mit höherer ID überschreiben die mit kleinerer ID
     *
     */
    public synchronized void wakeup(NodeThread neighbour, int initiatorId) {
        if (wokeUpFrom == null){
            System.out.println("Node " + id + " first wakeup wave("+ initiatorId  +  ") called from " + neighbour.id);
            //first wakeup message received
            wokeUpFrom = neighbour;
            highestKnownInitiator = initiatorId;
            messageCounter = 1;
            notifyAll();
        }else{
            if (initiatorId > highestKnownInitiator){
                System.out.println("Node " + id + " new wakeup wave("+ initiatorId  +  ") called from " + neighbour.id);
                //we received a stronger wakeup wave -> overwrite
                wokeUpFrom = neighbour;
                highestKnownInitiator = initiatorId;
                messageCounter = 1;
                notify();
            }else if (initiatorId == highestKnownInitiator){
                System.out.println("Node " + id + " same wakeup("+ initiatorId  +  ") called from " + neighbour.id);
                //wakeup message from our current strongest wave
                messageCounter++;
                notifyAll();
            }
        }
    }

    /**
     * Erhalten einer Echo Nachricht. Synchronized: Der sendende Thread hält den Lock vom empfangenden Node
     * @param neighbour Der Nachbarknoten der diese wakeup Nachricht verschickt
     * @param data evtl Daten die im Netzwerk verteil werden müssen (Spannender Baum?)
     * @param initiatorId Die ID des Initiatorknoten, die Wakeupwellen mit höherer ID überschreiben die mit kleinerer ID
     */
    public synchronized void echo(NodeThread neighbour, Object data, int initiatorId) {
        if (initiatorId == highestKnownInitiator){
            System.out.println("Node " + id + " received echo(" + initiatorId + ") from Node " + neighbour.id);
            messageCounter++;
            HashSet<Edge> dataSet = (HashSet<Edge>) data;
            if (dataSet != null) {
                knownEdges.addAll(dataSet);
            }
            knownEdges.add(new Edge(Integer.toString(id), Integer.toString(neighbour.id)));
            notifyAll();
        }
    }

    /**
     * Erhalten einer Result Nachricht. Synchronized: Der sendende Thread hält den Lock vom empfangenden Node
     * @param neighbour Der Nachbarknoten der diese Result Nachricht verschickt
     * @param initiatorId Das Ergebnis: Die höchste bekannte Initiator-ID
     */
    public synchronized void result(NodeThread neighbour, int initiatorId){
        //System.out.println("Node " + id + " received result(" + initiatorId + ") from Node " + neighbour.id);
        resultReceivedFrom = neighbour;
    }


    /**
     * Thread legt sich schlafen und wartet auf ein Notify oder auf timeoutMillis
     * @param timeoutMillis timeout in Millisekunden
     */
    private synchronized void waitForMessages(long timeoutMillis){
        try {
            //only if there is no wakeup wave to send
            //and there is no echo message to send
            //and there is no result message to send
            if ((highestKnownInitiator <= highestSentInitiator) &&
                    (messageCounter < neighbours.size()) &&
                    (resultReceivedFrom != null && resultSent == false)) {
                if (timeoutMillis > 0) {
                    wait(timeoutMillis);
                } else {
                    wait();
                }
            }
        } catch (InterruptedException e) {}
    }

    /**
     * Hello-Nachrichten an alle bekannten Nachbarn senden.
     * Das Set neighbours wird synchronized kopiert und dann aber außerhalb des
     * synchronized Blocks versendet um die inkrementelle Anforderung der Locks zu umgehen
     */
    private void runInitialize(){
        Set<NodeThread> tempNeighbours;
        synchronized (this) {
            tempNeighbours = new HashSet<>(neighbours);
        }
        for (NodeThread node:tempNeighbours) {
            node.hello(this);
        }
    }


    /**
     * Erhalten einer Hello Nachricht. Synchronized: Der sendende Thread hält den Lock vom empfangenden Node
     * @param neighbour Der Nachbarknoten der diese wakeup Nachricht verschickt
     */
    public synchronized void hello(NodeThread neighbour) {
        if (neighbours.contains(neighbour) == false){
            neighbours.add(neighbour);
            System.out.println("Node " + id + " added unknown Neighbour " + neighbour.id);
        }
    }
}
