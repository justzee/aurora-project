package aurora.bpm.command;

import java.io.ByteArrayInputStream;

import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import uncertain.composite.JSONAdaptor;

public class Command {
	String action;
	CompositeMap options = new CompositeMap();
	int queueId;

	public Command() {
		super();
	}

	public Command(String action) {
		this();
		setAction(action);
	}

	public Command(String action, CompositeMap options) {
		this();
		setAction(action);
		setOptions(options);
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public CompositeMap getOptions() {
		return options;
	}

	public void setOptions(CompositeMap options) {
		if (options == null)
			throw new NullPointerException("Command options can't be null.");
		this.options = options;
	}

	@Override
	public String toString() {
		return "[" + action + ","
				+ JSONAdaptor.toJSONObject(options).toString() + "]";
	}

	public int getQueueId() {
		return queueId;
	}

	public void setQueueId(int queueId) {
		this.queueId = queueId;
	}

	/**
	 * action equals AND option equals too
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Command))
			return false;
		Command cmd = (Command) obj;
		return eq(action, cmd.getAction()) && eq(options, cmd.getOptions());
	}

	private boolean eq(Object o1, Object o2) {
		if (o1 == null)
			return o2 == null;
		return o1.equals(o2);
	}

	/**
	 * if the string can not be parsed, an empty command(no action ,no options)
	 * will be returned
	 * 
	 * @param str
	 * @return
	 */
	public static Command parseFromString(String str) {
		Command cmd = new Command();
		try {
			int s = str.indexOf(',');
			cmd.setAction(str.substring(1, s));
			ByteArrayInputStream bais = new ByteArrayInputStream(str.substring(
					s + 1, str.length() - 1).getBytes("UTF-8"));
			JSONObject obj = JSONAdaptor.createJSONObject(bais);
			bais.close();
			cmd.setOptions(JSONAdaptor.toMap(obj));
		} catch (Exception e) {
			System.out.println("invalid command:" + str);
			e.printStackTrace();
		}
		return cmd;
	}
}
