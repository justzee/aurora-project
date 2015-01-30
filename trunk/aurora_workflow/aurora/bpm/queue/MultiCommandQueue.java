package aurora.bpm.queue;

import java.util.ArrayList;

import uncertain.core.ILifeCycle;
import uncertain.ocm.IObjectCreator;
import uncertain.ocm.IObjectRegistry;

public class MultiCommandQueue implements ILifeCycle {

	private String serverType = "redis";// "jvm"
	private int pollSize = 4;

	/**
	 * for redis server only
	 */
	private String ip = "127.0.0.1";
	/**
	 * for redis server only
	 */
	private int port = 6379;

	private ArrayList<ICommandQueue> queuePool = new ArrayList<ICommandQueue>();
	private IObjectRegistry ior;

	public MultiCommandQueue(IObjectRegistry ior) {
		this.ior = ior;
	}

	public String getServerType() {
		return serverType;
	}

	public void setServerType(String serverType) {
		if ("jvm".equalsIgnoreCase(serverType)
				|| "redis".equalsIgnoreCase(serverType)) {
			this.serverType = serverType;
		} else
			throw new IllegalArgumentException(serverType + " is not legal.");
	}

	public ICommandQueue getCommandQueue(int queueId) {
		return queuePool.get(queueId);
	}

	public int getPollSize() {
		return pollSize;
	}

	public void setPollSize(int pollSize) {
		this.pollSize = pollSize;
	}

	@Override
	public boolean startup() {
		try {
			for (int i = 0; i < pollSize; i++) {
				ICommandQueue cq = createQueue(i);
				queuePool.add(cq);
				cq.startListen();
			}
			System.out.println(queuePool.size());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private ICommandQueue createQueue(int id) throws Exception {
		if ("jvm".equalsIgnoreCase(serverType)) {
			ICommandQueue cq = (ICommandQueue) ((IObjectCreator) ior)
					.createInstance(DefaultCommandQueue.class);
			cq.setQueueId(id);
			return cq;
		}
		if ("redis".equalsIgnoreCase(serverType)) {
			RedisBasedCommandQueue cq = (RedisBasedCommandQueue) ((IObjectCreator) ior)
					.createInstance(RedisBasedCommandQueue.class);
			cq.setQueueId(id);
			cq.setIp(ip);
			cq.setPort(port);
			cq.connect();
			return cq;
		}
		return null;
	}

	@Override
	public void shutdown() {
		for (ICommandQueue cq : queuePool)
			cq.stopListen();
		queuePool.clear();
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
