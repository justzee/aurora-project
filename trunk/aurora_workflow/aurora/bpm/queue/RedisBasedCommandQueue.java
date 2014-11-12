package aurora.bpm.queue;

import java.io.IOException;
import java.util.List;

import redis.clients.jedis.Jedis;
import uncertain.core.ILifeCycle;
import aurora.bpm.command.Command;

public class RedisBasedCommandQueue implements ICommandQueue {
	static String LIST_KEY = "COMMAND_QUEUE";
	/**
	 * operation will be blocked,so we need two clients
	 */
	Jedis clientPush, clientPop;
	String ip = "127.0.0.1";
	int port = 6379;

	public RedisBasedCommandQueue() {

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
	public boolean offer(Command cmd) throws IOException {
		clientPush.lpush(LIST_KEY, cmd.toString());
		return true;
	}

	@Override
	public Command poll() throws ClassNotFoundException, Exception {
		List<String> returns = clientPop.brpop(LIST_KEY, "0");
		return Command.parseFromString(returns.get(1));
	}

	@Override
	public Command peek() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void connect() {
		clientPush = new Jedis(ip, port);
		clientPop = new Jedis(ip, port);
		System.out.println("Redis server connected.");
	}

}
