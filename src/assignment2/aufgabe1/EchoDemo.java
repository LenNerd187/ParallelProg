package assignment2.aufgabe1;

public class EchoDemo {

    public static void main(String[] args) throws InterruptedException {
        while (true) {
            EchoNode[] nodes = new EchoNode[6];
            nodes[0] = new EchoNode("a", true);
            nodes[1] = new EchoNode("b", false);
            nodes[2] = new EchoNode("c", false);
            nodes[3] = new EchoNode("d", false);
            nodes[4] = new EchoNode("e", false);
            nodes[5] = new EchoNode("f", false);

            nodes[0].setupNeighbours(nodes[1], nodes[3]);
            nodes[1].setupNeighbours(nodes[0], nodes[2], nodes[4]);
            nodes[2].setupNeighbours(nodes[1]);
            nodes[3].setupNeighbours(nodes[0], nodes[4]);
            nodes[4].setupNeighbours(nodes[3], nodes[5], nodes[1]);
            nodes[5].setupNeighbours(nodes[4]);

            for (EchoNode node : nodes) {
                node.start();
            }
            for (EchoNode node : nodes) {
                node.join();
            }
        }
    }
}
