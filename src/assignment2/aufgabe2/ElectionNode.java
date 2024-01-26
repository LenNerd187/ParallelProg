package assignment2.aufgabe2;

import assignment2.aufgabe1.Edge;

import java.util.*;

public class ElectionNode extends Node {

    private final HashSet<Edge> knownEdges = new HashSet<>();
    private int highestSentInitiator = -1;
    private final boolean wantsLeader;
    private int highestEchoSent = -1;

    private boolean running = true;

    //Shared Data
    private Node wokeUpFrom;        //the node that woke this up
    private int messageCounter = 0; //number of messages (wakeup oder echo) this node got
    private int highestKnownId;
    private int highestKnownInitiator = -1;
    private Node resultReceivedFrom = null;
    private boolean resultSent = false;



    public ElectionNode(int id, boolean wantsLeader) {
        super(id);
        this.highestKnownId = id;
        this.wantsLeader = new Random().nextBoolean();
        //this.wantsLeader = wantsLeader;
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
                        //printResult(knownEdges);
                        resultReceivedFrom = this;
                        return;
                    } else {
                        System.out.println("Node " + id + " received calls from all neighbours - sending echo now");
                        messagesToSent.add(new Message(Message.Type.ECHO, this, wokeUpFrom, highestKnownInitiator, highestKnownId, knownEdges));
                    }
                    highestEchoSent = highestKnownInitiator;
                }
            }
        }
        sendMessages(messagesToSent);
    }

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
                for (Node node:neighbours) {
                    if (node != wokeUpFrom) {
                        messagesToSent.add(new Message(Message.Type.WAKEUP, this, node, highestKnownInitiator, highestKnownId, null));
                    }
                }
            }
        }
        sendMessages(messagesToSent);
    }




    private void sendResultMessages(){
        LinkedList<Message> messagesToSent = new LinkedList<>();
        synchronized (this){
            if(resultReceivedFrom != null && resultSent == false) {
                for (Node node : neighbours) {
                    if (node != resultReceivedFrom) {
                        messagesToSent.add(new Message(Message.Type.RESULT, this, node, highestSentInitiator, highestKnownId, null));
                    }
                }
                resultSent = true;
                running = false;
            }
        }
        sendMessages(messagesToSent);
    }


    @Override
    public void run() {
        runInitialize();

        while(running) {

            //Loop: sleep -> wakeup -> spread wakeups -> check messageCounter -> repeat
            //if we didnt already received a relevant wakeup
            //wait for wakeup or election start timer?
            if (wantsLeader && wokeUpFrom == null) {
                waitForMessages(200);
            } else {
                waitForMessages(-1);
            }


            synchronized (this){
                if (wokeUpFrom == null) {
                    //we woke up because we want to initiate a wave
                    System.out.println("Node " + id + " initiating election wave");
                    highestKnownInitiator = id;
                    wokeUpFrom = this;
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

    //NOT synchronized
    //sending messages by acquiring receiving Nodes Lock and writing Shared Data
    private void sendMessages(List<Message> messages){
        while(messages.size() > 0){
            Message message = messages.removeFirst();
            switch (message.msgType){
                case WAKEUP :
                    message.to.wakeup(message.from, message.initiatorId);
                    break;
                case ECHO :
                    message.to.echo(message.from, message.data, message.initiatorId, message.value);
                    break;
                case RESULT:
                    message.to.result (message.from, message.value);
                    break;
            }
        }
    }



    public synchronized void wakeup(Node neighbour, int initiatorId) {
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


    public synchronized void result(Node neighbour, int value){
        System.out.println("Node " + id + " received result(" + value + ") from Node " + neighbour.id);
        highestKnownId = value;
        resultReceivedFrom = neighbour;
    }

    public synchronized void echo(Node neighbour, Object data, int initiatorId, int highestVote) {
        if (initiatorId == highestKnownInitiator){
            System.out.println("Node " + id + " received echo(" + initiatorId + ") from Node " + neighbour.id);
            highestKnownId = Math.max(highestVote, highestKnownId);
            messageCounter++;
            HashSet<Edge> dataSet = (HashSet<Edge>) data;
            if (dataSet != null) {
                knownEdges.addAll(dataSet);
            }
            knownEdges.add(new Edge(Integer.toString(id), Integer.toString(neighbour.id)));
            notifyAll();
        }
    }

    private synchronized void waitForMessages(long timeoutMillis){
        try {
            //only if there is no wakeup wave to send
            //and there is no echo message to send
            if ((highestKnownInitiator <= highestSentInitiator) &&
                    (messageCounter < neighbours.size()))  {
                if (timeoutMillis > 0) {
                    wait(200);
                } else {
                    wait();
                }
            }
        } catch (InterruptedException e) {}
    }
    private void runInitialize(){
        try {
            Thread.sleep(50);
        } catch (InterruptedException ex) {}
        for (Node node : neighbours) {
            node.hello(this);
        }
        try {
            Thread.sleep(50);
        } catch (InterruptedException ex) {}
    }
    public void hello(Node neighbour) {
        if (neighbours.contains(neighbour) == false) {
            ElectionNode node = (ElectionNode) neighbour;
            System.out.println("ERROR: Node " + id + " doesnt know its neighbour " + node.id);
        }
    }
}
