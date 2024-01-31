package assignment2.aufgabe2;

import assignment2.aufgabe1.Edge;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public abstract class NodeThread extends Thread {
	protected boolean running = true;
	protected final int id;
	protected final Set<NodeThread> neighbours = new HashSet<NodeThread>();

	public NodeThread(int id) {
		super(Integer.toString(id));
		this.id = id;
	}

	public void setupNeighbours(NodeThread... neighbours) {
		this.neighbours.clear();
		this.neighbours.addAll(List.of(neighbours));
	}

	/**
	 * Greetings from a neighbour. Before starting the echo algorithm this message
	 * is called within <code>setupNeighbours</code> once by every node for all its
	 * initially known neighbours.
	 * 
	 * The main purpose is that <code>this</code> node gets the chance to add the
	 * calling <code>neighbour</code> to its own list of neighbours (just in case
	 * the neighbour was previously unknown to this node although the neighbour
	 * itself knows this node).
	 * 
	 * @param neighbour
	 */
	public abstract void hello(NodeThread neighbour);

	/**
	 * Incoming "wakeup" message from a neighbour.
	 * 
	 * @param neighbour
	 */
	public abstract void wakeup(NodeThread neighbour, int initiatorId);
	/**
	 * Incoming "echo" message from a neighbour. The neighbour can also send some
	 * data with this echo message. The data object might e.g. contain information
	 * about edges of the spanning tree known so far; if not needed, set data to
	 * null.
	 * 
	 * @param neighbour
	 * @param data
	 */
	public abstract void echo(NodeThread neighbour, Object data, int initiatorId);

	public abstract void result(NodeThread neighbour, int value);


	protected void printResult(HashSet<Edge> edges){
		String result = "";
		for(Edge edge : edges){
			result += edge.toString() + "\n";
		}
		System.out.println(result);
	}

}
