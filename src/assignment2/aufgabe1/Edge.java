package assignment2.aufgabe1;

public class Edge {
    public final String node1;
    public final String node2;

    public Edge(String node1, String node2) {
        this.node1 = node1;
        this.node2 = node2;
    }

    public String toString(){
        return node1 + "---" + node2;
    }
}
