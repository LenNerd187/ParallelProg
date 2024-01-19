package assignment2.aufgabe1;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class EchoNode extends NodeAbstract{

    private Node wokeUpFrom;        //the node that woke this up
    private int messageCounter = 0; //number of messages (wakeup oder echo) this node got
    private final HashSet<Edge> knownEdges = new HashSet<Edge>();
    public EchoNode(String name, boolean initiator) {
        super(name, initiator);
    }

    @Override
    public void run() {
        for (Node node:neighbours) {
            node.hello(this);
        }

        if (initiator){
            initiateWakeups();
        }else{
            waitForFirstWakeup();
            wakeUpNeighbours();
        }

        waitForAllMessages();

        if (initiator){
            System.out.println("Initiator got all messages back - all done :-)");
            printResult(knownEdges);
        }else {
            System.out.println("Node " + name + " received calls from all neighbours - sending echo now");
            wokeUpFrom.echo(this, knownEdges);
        }

    }
    private void wakeUpNeighbours(){
        System.out.println("Node " + name + " sending wakeup messages...");
        for (Node neighbour : neighbours){
            if (neighbour != wokeUpFrom){
                neighbour.wakeup(this);
            }
        }
    }
    private synchronized void waitForFirstWakeup(){
        try {
            System.out.println("Node " + name + " going to sleep... waiting for first wakeup call");
            wait();
        } catch (InterruptedException e) {}
        System.out.println("Node " + name + " woke up!");
    }

    private synchronized void waitForAllMessages(){
        while (messageCounter < neighbours.size() ) {
            try {
                System.out.println("Node " + name + " still waiting for messages  going back to sleep");
                wait();
            } catch (InterruptedException e) {}
        }
    }

    private void initiateWakeups(){
        if (initiator == false){
            return;
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}
        for (Node neighbour : neighbours){
            neighbour.wakeup(this);
        }
    }

    @Override
    public void hello(Node neighbour) {
        if (neighbours.contains(neighbour) == false){
            EchoNode echoNode = (EchoNode) neighbour;
            System.out.println("ERROR: Node " + name + " doesnt know its neighbour " + echoNode.name);
        }
    }


    @Override
    public synchronized void wakeup(Node neighbour) {
        System.out.println("Node " + name + " wakeup called");
        messageCounter++;
        if (wokeUpFrom == null){
            wokeUpFrom = neighbour;
        }
        notify();   //TODO: compiler reordering?? notify darf erst ausgeführt nachdem wokeUpFrom gesetzt ist - ist beim Testen nie passiert
    }

    @Override
    public synchronized void echo(Node neighbour, Object data) {
        messageCounter++;
        EchoNode echoNeighbour = (EchoNode) neighbour;
        HashSet<Edge> dataSet = (HashSet<Edge>) data;
        if(dataSet != null){
            knownEdges.addAll(dataSet);
        }
        knownEdges.add(new Edge(name, echoNeighbour.name));
        notify();
    }

    @Override
    public void setupNeighbours(Node... neighbours) {
        this.neighbours.clear();
        this.neighbours.addAll(List.of(neighbours));
    }

    private void printResult(HashSet<Edge> edges){
        String result = "";
        for(Edge edge : edges){
            result += edge.toString() + "\n";
        }
        System.out.println(result);
    }
}
