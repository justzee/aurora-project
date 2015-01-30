package aurora.bpm.queue;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import redis.clients.jedis.exceptions.JedisConnectionException;
import uncertain.logging.ILogger;
import uncertain.logging.ILoggerProvider;
import aurora.bpm.command.Command;
import aurora.bpm.command.CommandRegistry;
import aurora.bpm.command.ExceptionLoggerExecutor;
import aurora.bpm.command.ICommandExecutor;
import aurora.bpm.engine.BpmnEngine;

public class DefaultCommandQueue implements ICommandQueue, Runnable {
	private final Object readLock = new Object();
	private final Object writeLock = new Object();

	private LinkedBlockingQueue<Command> queue;

	private boolean listening = false;

	protected CommandRegistry cr;

	private int queueId = 0;

	protected ILogger logger;
	private ScheduledExecutorService threadPool;
	private ArrayList<IQueueListener> listeners = new ArrayList<IQueueListener>();

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
		this.logger = loggerProvider.getLogger(BpmnEngine.TOPIC);
		queue = new LinkedBlockingQueue<Command>();
	}
	
	public void addQueueListener(IQueueListener listener) {
		listeners.add(listener);
	}
	
	public void fireOnCommand(Command cmd) {
		for(IQueueListener ql:listeners)
			ql.onCommand(getQueueId(),cmd);
	}
	
	public void fireOnException(Throwable thr,Command cmd) {
		for(IQueueListener ql:listeners)
			ql.onException(getQueueId(),thr,cmd);
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

	public boolean isListening() {
		return listening;
	}

	@Override
	public void startListen() {
		connect();
		listening = true;
		threadPool = Executors.newScheduledThreadPool(1);
		threadPool.scheduleWithFixedDelay(this, 0, 1, TimeUnit.MILLISECONDS);
		logger.config("command-queue-" + queueId + " start listen.");
	}

	@Override
	public void stopListen() {
		listening = false;
		logger.config("command-queue-" + queueId + " stop listen.");
		threadPool.shutdownNow();
		disconnect();
	}

	@Override
	public void run() {
		Command cmd = null;
		Throwable thr = null;
		try {
			cmd = poll();
			//fireOnCommand(cmd);
			ICommandExecutor exec = cr.findExecutor(cmd.getAction());
			cmd.getOptions().put(QUEUE_ID, getQueueId());
			exec.execute(cmd);
		} catch (JedisConnectionException e) {
			if (listening) {
				logger.log(Level.SEVERE, "exception occurred on command-queue-"
						+ queueId, e);
				e.printStackTrace();
				thr = e;
			} else {
				// this exception caused by engine shutdown,ignore it.
			}
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "exception occurred on command-queue-"
					+ queueId, e);
			e.printStackTrace();
			thr = e;
		} finally {
			if (thr != null) {
				cmd.getOptions().put("EXCEPTION",thr);
				try {
					cr.findExecutor(ExceptionLoggerExecutor.TYPE).execute(cmd);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public int getQueueId() {
		return queueId;
	}

	public void setQueueId(int queueId) {
		this.queueId = queueId;
	}

	public void connect() {

	}

	public void disconnect() {
	}

}
