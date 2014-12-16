package aurora.bpm.queue;

import aurora.bpm.command.Command;

public interface ICommandQueue {

	/**
	 * Inserts the specified element into this queue if it is possible to do so
	 * immediately .
	 *
	 * @param cmd
	 *            the element to add
	 * @return {@code true} if the element was added to this queue, else
	 *         {@code false}
	 * @throws ClassCastException
	 *             if the class of the specified element prevents it from being
	 *             added to this queue
	 * @throws NullPointerException
	 *             if the specified element is null and this queue does not
	 *             permit null elements
	 * @throws IllegalArgumentException
	 *             if some property of this element prevents it from being added
	 *             to this queue
	 */
	boolean offer(Command cmd) throws Exception;

	/**
	 * Retrieves and removes the head of this queue, or returns {@code null} if
	 * this queue is empty.
	 *
	 * @return the head of this queue, or {@code null} if this queue is empty
	 */
	Command poll() throws Exception;

	/**
	 * Retrieves, but does not remove, the head of this queue, or returns
	 * {@code null} if this queue is empty.
	 *
	 * @return the head of this queue, or {@code null} if this queue is empty
	 */
	Command peek() throws Exception;

	boolean isEmpty() throws Exception;

	int size() throws Exception;
	
	void setQueueId(int queueId);
	int getQueueId();

	void startListen();

	void stopListen();

}
