package uncertain.proc;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.datatype.ConvertionException;
import uncertain.datatype.DataType;
import uncertain.datatype.DataTypeRegistry;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.exception.ConfigurationFileException;
import uncertain.ocm.AbstractLocatableObject;

public class Argument extends AbstractLocatableObject {
	private String type;
	private String value;
	private String path;
	private CompositeMap config;
	private Object objectValue;
	private Class classType;

	public void onInitialize(CompositeMap context){
		if(path==null && value ==null){
			throw BuiltinExceptionFactory.createOneAttributeMissing(this, "path,value");
		}
		if(path != null && value !=null){
			throw BuiltinExceptionFactory.createConflictAttributesExcepiton(this, "path,value");
		}
		if(context != null){
			type = TextParser.parse(type, context);
			if(value != null)
				objectValue = TextParser.parse(value, context);
			else
				objectValue = context.getObject(path);
		}
		try {
			DataType dt = DataTypeRegistry.getInstance().getDataType(type);
			if(dt == null){
				throw BuiltinExceptionFactory.createDataTypeUnknown(this, type);
			}
			classType = dt.getJavaType();
			objectValue = dt.convert(objectValue);
		} catch (ConvertionException e) {
			throw new ConfigurationFileException("uncertain.exception.convertion_exception", new Object[]{objectValue,classType}, e, this);
		}
		
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public void validConfig(){
		
	}
	public void beginConfigure(CompositeMap config) {
		this.config = config;
	}
	public void endConfigure() {
	}
	public Object getObjectValue() {
		return objectValue;
	}
	public void setObjectValue(Object objectValue) {
		this.objectValue = objectValue;
	}
	public Class getClassType() {
		return classType;
	}
	public void setClassType(Class classType) {
		this.classType = classType;
	}
	
}
