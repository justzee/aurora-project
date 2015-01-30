package aurora.bpm.queue;

import java.util.List;

import redis.clients.jedis.Jedis;
import uncertain.logging.ILoggerProvider;
import aurora.bpm.command.Command;
import aurora.bpm.command.CommandRegistry;

public class RedisBasedCommandQueue extends DefaultCommandQueue {
	/**
	 * operation will be blocked,so we need two clients
	 */
	Jedis clientPush, clientPop;
	String ip = "127.0.0.1";
	int port = 6379;

	public RedisBasedCommandQueue(CommandRegistry cr,
			ILoggerProvider loggerProvider) {
		super();
		if (cr == null)
			throw new RuntimeException(
					"RedisBasedCommandQueue init failed.(CommandRegistry is null)");
		this.cr = cr;
		this.logger = loggerProvider.getLogger("command-queue");
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getIp() {
		return ip;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	@Override
	public boolean offer(Command cmd) throws Exception {
		clientPush.lpush(getListKey(), cmd.toString());
		return true;
	}

	private String getListKey() {
		return "COMMAND-QUEUE-" + getQueueId();
	}

	@Override
	public Command poll() throws ClassNotFoundException, Exception {
		List<String> returns = clientPop.brpop(getListKey(), "0");
		return Command.parseFromString(returns.get(1));
	}

	@Override
	public Command peek() {
		return null;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public int size() {
		return 0;
	}

	public void connect() {
		super.connect();
		clientPush = new Jedis(ip, port);
		clientPop = new Jedis(ip, port);
		System.out.printf("Redis server connected on %s:%d(queue id:%d)\n", ip,
				port, getQueueId());
	}

	public void disconnect() {
		super.disconnect();
		clientPop.close();
		clientPush.close();
	}

}
