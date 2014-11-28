package aurora.bpm.command;

public class OptionMissingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -126355483283026651L;

	public OptionMissingException(String optName) {
		super(optName + " missing.");
	}

}
