package assignment2.aufgabe2;

import java.util.ArrayList;

public class ElectionDemo {


    public static void main(String[] args) throws InterruptedException {
        if (true) {
            Node[] nodes = create8Network();

            for (Node node : nodes) {
                node.start();
            }
            for (Node node : nodes) {
                node.join();
            }
            Thread.sleep(2000);
        }
    }

    private static Node[] create8Network(){
        Node[] nodes = new Node[6];
        nodes[0] = new ElectionNode(0,true);
        nodes[1] = new ElectionNode(1,false);
        nodes[2] = new ElectionNode(2,false);
        nodes[3] = new ElectionNode(3,true);
        nodes[4] = new ElectionNode(4,false);
        nodes[5] = new ElectionNode(5,false);

        nodes[0].setupNeighbours(nodes[1], nodes[3]);
        nodes[1].setupNeighbours(nodes[0], nodes[2], nodes[4]);
        nodes[2].setupNeighbours(nodes[1], nodes[5]);
        nodes[3].setupNeighbours(nodes[0], nodes[4]);
        nodes[4].setupNeighbours(nodes[3], nodes[5], nodes[1]);
        nodes[5].setupNeighbours(nodes[4], nodes[2]);
        return nodes;
    }
    private static Node[] createTreeNetwork(){
        Node[] nodes = new Node[6];
        nodes[0] = new ElectionNode(0,true);
        nodes[1] = new ElectionNode(1,false);
        nodes[2] = new ElectionNode(2,false);
        nodes[3] = new ElectionNode(3,true);
        nodes[4] = new ElectionNode(4,false);
        nodes[5] = new ElectionNode(5,false);

        nodes[0].setupNeighbours(nodes[1], nodes[3]);
        nodes[1].setupNeighbours(nodes[0], nodes[2]);
        nodes[2].setupNeighbours(nodes[1]);
        nodes[3].setupNeighbours(nodes[0], nodes[4]);
        nodes[4].setupNeighbours(nodes[3], nodes[5]);
        nodes[5].setupNeighbours(nodes[4]);
        return nodes;
    }
    private static Node[] createCompleteNetwork(int n){
        Node[] nodes = new Node[n];
        for (int i = 0; i < n; i++){
            nodes[i] = new ElectionNode(i,true);
        }

        for (int i = 0; i < n; i++){
            ArrayList<Node> neighbours = new ArrayList<>();
            for (int j = 0; j < n; j++){
                if (i != j){
                    neighbours.add(nodes[j]);
                }
            }
            nodes[i].setupNeighbours(neighbours.toArray(new Node[0]));
        }
        return nodes;
    }

}
