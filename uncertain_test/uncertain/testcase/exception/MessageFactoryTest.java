package uncertain.testcase.exception;


import java.util.Locale;

import org.xml.sax.SAXParseException;

import junit.framework.TestCase;
import uncertain.composite.CompositeLoader;
import uncertain.exception.GeneralException;
import uncertain.exception.MessageFactory;

public class MessageFactoryTest  extends TestCase {

	public void testLoadResource(){
		String resourceFileName="exceptionMessage";
		String path = MessageFactoryTest.class.getPackage().getName()+"."+resourceFileName;
		MessageFactory.loadResource(path);
		String messageCode="aurora00001";
		assertEquals("这是一个测试案例!", MessageFactory.getMessage(messageCode, null));
		MessageFactory.loadResource(path,Locale.US);
		assertEquals("this is test case.", MessageFactory.getMessage(messageCode, Locale.US,null));
		assertEquals("这是一个测试案例!", MessageFactory.getMessage(messageCode, null));
	}
	public void testCreateException(){
		CompositeLoader cl = CompositeLoader.createInstanceForOCM();
		String bmFile = "error_format";
		try{
			try {
				cl.loadFromClassPath(MessageFactoryTest.class.getPackage().getName()+"."+bmFile, "bm");
			} catch (Throwable e) {
				GeneralException ge= MessageFactory.createException("aurora00002", e, new String[]{"格式错误","格式正确"});
				throw ge;
			} 
		} catch (Throwable e) {
			assertEquals("aurora00002=>不能是格式错误，必须是格式正确.",e.getMessage());
			SAXParseException parseEx = (SAXParseException) e.getCause();
			assertEquals(11,parseEx.getLineNumber());
		}
			
	}
	public static void main(String[] args){
		(new MessageFactoryTest()).testLoadResource();
		
	}
}
