package aurora.bpm.engine;

import java.util.logging.Level;

import aurora.bpm.command.Command;
import aurora.bpm.command.CommandRegistry;
import aurora.bpm.model.DefinitionFactory;
import aurora.bpm.queue.ICommandQueue;
import aurora.bpm.queue.IQueueListener;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.sqlje.core.IInstanceManager;
import uncertain.core.ILifeCycle;
import uncertain.logging.ILogger;
import uncertain.logging.ILoggerProvider;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectCreator;
import uncertain.ocm.IObjectRegistry;

public class BpmnEngine extends AbstractLocatableObject implements ILifeCycle,
		IQueueListener {
	public static final String TOPIC = "BPMN-ENGINE";
	private ILoggerProvider loggerProvider;
	private ICommandQueue[] queues;
	private IObjectRegistry objRegistry;

	public BpmnEngine(IObjectRegistry ior, IInstanceManager instManager,
			ILoggerProvider loggerProvider) throws Exception {
		super();
		this.objRegistry = ior;
		this.loggerProvider = loggerProvider;
		ILogger logger = loggerProvider.getLogger(TOPIC);
		IObjectCreator ioc = (IObjectCreator) ior;
		try {
			ior.registerInstance(ioc.createInstance(DefinitionFactory.class));
			ior.registerInstance(ioc.createInstance(ExecutorContext.class));
			ior.registerInstance(ioc.createInstance(CommandRegistry.class));
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
		ior.registerInstanceOnce(getClass(), this);

	}

	public void setQueues(ICommandQueue[] queues) {
		this.queues = queues;
	}

	public ICommandQueue[] getQueues() {
		return queues;
	}

	public ICommandQueue getQueueById(int queueId) {
		return queues[queueId];
	}

	@Override
	public boolean startup() {
		ILogger logger = loggerProvider.getLogger(TOPIC);
		if (queues == null || queues.length == 0) {
			logger.severe("No command queue available.");
			throw new RuntimeException("No command queue available.");
		}
		for (int i = 0; i < queues.length; i++) {
			queues[i].addQueueListener(this);
			queues[i].setQueueId(i);
			queues[i].startListen();
		}
		logger.info("process engine startup success.");
		return true;
	}

	@Override
	public void shutdown() {
		for (ICommandQueue cq : queues)
			cq.stopListen();
		ExecutorContext ec = (ExecutorContext) objRegistry
				.getInstanceOfType(ExecutorContext.class);
		if (ec != null)
			ec.destory();
	}

	@Override
	public void onCommand(int queue_id, Command cmd) {

	}

	@Override
	public void onException(int queue_id, Throwable thr, Command cmd) {
		Long instance_id = cmd == null ? -1L : cmd.getOptions().getLong(
				"instance_id", -1);
		Long user_id = cmd == null ? -1L : cmd.getOptions().getLong("user_id",
				-1);

	}

}
