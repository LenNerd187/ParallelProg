package assignment2.aufgabe2;

import java.util.ArrayList;

public class ElectionDemo {


    public static void main(String[] args) throws InterruptedException {
        while (true) {
            System.out.println("Creating Network....");
            NodeThread[] nodes = createCircleNetwork(10);
            System.out.println("Network created n = " + nodes.length);

            for (NodeThread node : nodes) {
                node.start();
            }
            for (NodeThread node : nodes) {
                node.join();
                //System.out.println("Node " + node.id + " joined Main Thread");
            }
            Thread.sleep(1000);

        }
    }

    /**
     * Erstellt einen Graphen mit 6 Knoten
     * Es gibt Schleifen, Knoten mit 2 Nachbarn und Knoten mit 3 Nachbarn
     * Der Graph sieht aus wie eine 8
     * @return ein Array mit allen Knoten. Index im Array stimmt mit ID des Knotens überein
     */
    private static NodeThread[] create8Network(){
        NodeThread[] nodes = new NodeThread[6];
        nodes[0] = new ElectionNode(0);
        nodes[1] = new ElectionNode(1);
        nodes[2] = new ElectionNode(2);
        nodes[3] = new ElectionNode(3);
        nodes[4] = new ElectionNode(4);
        nodes[5] = new ElectionNode(5);

        nodes[0].setupNeighbours(nodes[1], nodes[3]);
        nodes[1].setupNeighbours(nodes[0], nodes[2], nodes[4]);
        nodes[2].setupNeighbours(nodes[1], nodes[5]);
        nodes[3].setupNeighbours(nodes[0], nodes[4]);
        nodes[4].setupNeighbours(nodes[3], nodes[5], nodes[1]);
        nodes[5].setupNeighbours(nodes[4], nodes[2]);
        return nodes;
    }




    /**
     * Erstellt einen Baum mit 6 Knoten
     * Knoten 0 ist die Wurzel des Baumes, Knoten 2 und 5 sind Blätter
     * @return ein Array mit allen Knoten. Index im Array stimmt mit ID des Knotens überein
     */
    private static NodeThread[] createTreeNetwork(){
        NodeThread[] nodes = new NodeThread[6];
        nodes[0] = new ElectionNode(0);
        nodes[1] = new ElectionNode(1);
        nodes[2] = new ElectionNode(2);
        nodes[3] = new ElectionNode(3);
        nodes[4] = new ElectionNode(4);
        nodes[5] = new ElectionNode(5);

        nodes[0].setupNeighbours(nodes[1], nodes[3]);
        nodes[1].setupNeighbours(nodes[0], nodes[2]);
        nodes[2].setupNeighbours(nodes[1]);
        nodes[3].setupNeighbours(nodes[0], nodes[4]);
        nodes[4].setupNeighbours(nodes[3], nodes[5]);
        nodes[5].setupNeighbours(nodes[4]);
        return nodes;
    }

    /**
     * Erstellt einen vollständigen Graphen mit n Knoten
     * Alle Knoten sind mit allen Knoten verbunden
     * @param n Anzahl der Knoten
     * @return ein Array mit allen Knoten. Index im Array stimmt mit ID des Knotens überein
     */
    private static NodeThread[] createCompleteNetwork(int n){
        NodeThread[] nodes = new NodeThread[n];
        for (int i = 0; i < n; i++){
            nodes[i] = new ElectionNode(i);
        }

        for (int i = 0; i < n; i++){
            ArrayList<NodeThread> neighbours = new ArrayList<>();
            for (int j = 0; j < n; j++){
                if (i != j){
                    neighbours.add(nodes[j]);
                }
            }
            nodes[i].setupNeighbours(neighbours.toArray(new NodeThread[0]));
        }
        return nodes;
    }

    /**
     * Erstellt einen Kreis Graphen mit n Knoten
     * Alle Knoten sind mit genau 2 Nachbarn verbunden
     * @param n Anzahl der Knoten
     * @return ein Array mit allen Knoten. Index im Array stimmt mit ID des Knotens überein
     */
    private static NodeThread[] createCircleNetwork(int n){
        NodeThread[] nodes = new NodeThread[n];
        for (int i = 0; i < n; i++){
            nodes[i] = new ElectionNode(i);
        }

        for (int i = 0; i < n; i++){
            NodeThread nodeA = nodes[i];
            NodeThread nodeB = nodes[(i+1) % nodes.length];
            NodeThread nodeC = nodes[(i+2) % nodes.length];
            nodeB.setupNeighbours(nodeA);
            nodeB.setupNeighbours(nodeC);
        }
        return nodes;
    }

    /**
     * Erstellt einen Kreis Graphen mit n Knoten
     * Alle Knoten sind mit genau 2 Nachbarn verbunden
     * @param n Anzahl der Knoten
     * @return ein Array mit allen Knoten. Index im Array stimmt mit ID des Knotens überein
     */
    private static NodeThread[] createLineNetwork(int n){

        NodeThread[] nodes = new NodeThread[n];
        for (int i = 0; i < nodes.length; i++){
            nodes[i] = new ElectionNode(i);
        }

        nodes[0].setupNeighbours(nodes[1]);
        for (int i = 0; i < nodes.length - 2; i++){
            NodeThread nodeA = nodes[i];
            NodeThread nodeB = nodes[(i+1) ];
            NodeThread nodeC = nodes[(i+2) ];
            nodeB.setupNeighbours(nodeA, nodeC);
        }
        nodes[nodes.length-1].setupNeighbours(nodes[nodes.length-2]);
        return nodes;
    }
    /**
     * Erstellt einen Wald Graphen mit n Knoten
     * Alle Knoten stehen alleine, es gibt keine Kanten
     * @param n Anzahl der Knoten
     * @return ein Array mit allen Knoten. Index im Array stimmt mit ID des Knotens überein
     */
    private static NodeThread[] createForestNetwork(int n){

        NodeThread[] nodes = new NodeThread[n];
        for (int i = 0; i < nodes.length; i++){
            nodes[i] = new ElectionNode(i);
        }
        return nodes;
    }
}
