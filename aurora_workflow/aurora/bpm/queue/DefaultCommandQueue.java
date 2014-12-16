package aurora.bpm.queue;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import uncertain.logging.ILogger;
import uncertain.logging.ILoggerProvider;
import aurora.bpm.command.Command;
import aurora.bpm.command.CommandRegistry;
import aurora.bpm.command.ICommandExecutor;

public class DefaultCommandQueue implements ICommandQueue, Runnable {
	private final Object readLock = new Object();
	private final Object writeLock = new Object();

	private LinkedBlockingQueue<Command> queue;

	private boolean listening = false;

	protected CommandRegistry cr;

	private int queueId = 0;

	protected ILogger logger;

	public DefaultCommandQueue() {
		super();
	}

	public DefaultCommandQueue(CommandRegistry cr,
			ILoggerProvider loggerProvider) {
		this();
		if (cr == null)
			throw new RuntimeException(
					"DefaultCommandQueue init failed.(CommandRegistry is null)");
		this.cr = cr;
		this.logger = loggerProvider.getLogger("command-queue");
		queue = new LinkedBlockingQueue<Command>();
	}

	@Override
	public boolean offer(Command cmd) throws Exception {
		synchronized (writeLock) {
			return queue.offer(cmd);
		}
	}

	@Override
	public Command poll() throws Exception {
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

	@Override
	public void startListen() {
		listening = true;
		Executors.newFixedThreadPool(1).execute(this);
		logger.config("command-queue-" + queueId + " start listen.");
	}

	@Override
	public void stopListen() {
		listening = false;
		logger.config("command-queue-" + queueId + " stop listen.");
	}

	@Override
	public void run() {
		while (listening) {
			try {
				Command cmd = poll();
				ICommandExecutor exec = cr.findExecutor(cmd.getAction());
				exec.execute(cmd);
			} catch (Throwable e) {
				logger.log(Level.SEVERE, "exception occurred on command-queue-"
						+ queueId, e);
				e.printStackTrace();
			}
		}
		System.out.println("stop listen");
	}

	public int getQueueId() {
		return queueId;
	}

	public void setQueueId(int queueId) {
		this.queueId = queueId;
	}

}
