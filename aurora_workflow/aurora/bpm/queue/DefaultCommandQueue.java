package aurora.bpm.queue;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import aurora.bpm.command.Command;

public class DefaultCommandQueue implements ICommandQueue {
	private final Object readLock = new Object();
	private final Object writeLock = new Object();

	private LinkedBlockingQueue<Command> queue;

	public DefaultCommandQueue() {
		queue = new LinkedBlockingQueue<Command>();
		System.out.println(getClass().getSimpleName() + " instance created.");
	}

	@Override
	public boolean offer(Command cmd) {
		synchronized (writeLock) {
			return queue.offer(cmd);
		}
	}

	@Override
	public Command poll() throws InterruptedException {
		synchronized (readLock) {
			return queue.poll(Long.MAX_VALUE, TimeUnit.DAYS);
		}
	}

	@Override
	public Command peek() {
		return queue.peek();
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public int size() {
		return queue.size();
	}

}
