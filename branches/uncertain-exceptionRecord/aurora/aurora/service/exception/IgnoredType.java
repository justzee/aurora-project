package aurora.service.exception;

import uncertain.core.ConfigurationError;

public class IgnoredType {
	private String name;
	public void setName(String name){
		try {
			Class.forName(name);
		} catch (ClassNotFoundException e) {
			throw new ConfigurationError(e);
		}
		this.name = name;
	}
	public String getName(){
		return name;
	}
}
