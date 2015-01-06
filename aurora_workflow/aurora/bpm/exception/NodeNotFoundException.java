package aurora.bpm.exception;

public class NodeNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2989328741181509786L;
	String node_id;
	String process_code;
	String process_version;

	public NodeNotFoundException(String node_id, String process_code,
			String process_version) {
		super("Can not find node by id:" + node_id + " in process define:["
				+ process_code + "," + process_version + "]");
		this.node_id = node_id;
		this.process_code = process_code;
		this.process_version = process_version;
	}

}
