package assignment2.aufgabe1.package.echo;

import java.util.HashSet;
import java.util.Set;

/**
 * Abstract implementation of the Node interface.
 */
public abstract class NodeAbstract extends Thread implements Node {

	/** Name of this node */
	protected final String name;

	/** Is this node the initiator of the echo algorithm? */
	protected final boolean initiator;

	/**
	 * Collection of known neighbours of this node; only the methods of the
	 * neighbours in this collection can be called.
	 */
	protected final Set<Node> neighbours = new HashSet<Node>();

	/** Abstract constructor of a node */
	public NodeAbstract(String name, boolean initiator) {
		super(name);
		this.name = name;
		this.initiator = initiator;
	}

	/**
	 * Method to set up the list of initially known neighbours; the setup must be
	 * complete in all nodes before any echo thread is started!
	 * 
	 * Be aware that the neighbour relationship is symmetric (if node "a" has node
	 * "b" as its neighbour, also node "b" must have node "a" as its neighbour)!
	 * Therefore, call method <code>hello</code> of each neighbour here.
	 */
	public abstract void setupNeighbours(Node... neighbours);

	/** Utility method to print this node in a readable way */
	@Override
	public String toString() {
		return name;
	}

}
