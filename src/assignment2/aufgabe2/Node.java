package assignment2.aufgabe2;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This interface defines the methods of each node participating in the echo
 * algorithm. These methods can be called by the neighbours of a node.
 */
public abstract class Node extends Thread {
	protected final int id;
	protected final Set<Node> neighbours = new HashSet<Node>();

	public Node(int id) {
		super(Integer.toString(id));
		this.id = id;
	}

	public void setupNeighbours(Node... neighbours) {
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
	public abstract void hello(Node neighbour);

	/**
	 * Incoming "wakeup" message from a neighbour.
	 * 
	 * @param neighbour
	 */
	public abstract void wakeup(Node neighbour, int initiatorId);
	/**
	 * Incoming "echo" message from a neighbour. The neighbour can also send some
	 * data with this echo message. The data object might e.g. contain information
	 * about edges of the spanning tree known so far; if not needed, set data to
	 * null.
	 * 
	 * @param neighbour
	 * @param data
	 */
	public abstract void echo(Node neighbour, Object data, int initiatorId, int highestVote);

	public abstract void result(Node neighbour, int value);


}
