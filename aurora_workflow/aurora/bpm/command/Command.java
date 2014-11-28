package aurora.bpm.command;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import uncertain.composite.JSONAdaptor;

public class Command implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5148488911190894772L;
	String action;
	CompositeMap options = new CompositeMap();

	public Command() {
	}

	public Command(String action, CompositeMap options) {
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
