package uncertain.testcase.exception;

import java.io.IOException;
import java.util.Locale;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import junit.framework.TestCase;
import uncertain.composite.CompositeLoader;
import uncertain.exception.GeneralException;
import uncertain.exception.MessageFactory;

public class MessageFactoryTest extends TestCase {

	public void testLoadResource() {
		String resourceFileName = "exceptionMessage";
		String path = MessageFactoryTest.class.getPackage().getName() + "." + resourceFileName;
		MessageFactory.loadResource(path);
		String messageCode = "aurora00001";
		assertEquals("这是一个测试案例!", MessageFactory.getMessage(messageCode, null));
		MessageFactory.loadResource(path, Locale.US);
		assertEquals("this is test case.", MessageFactory.getMessage(messageCode, Locale.US, null));
		assertEquals("这是一个测试案例!", MessageFactory.getMessage(messageCode, null));
	}

	
	public void testCreateException() {
		CompositeLoader cl = CompositeLoader.createInstanceForOCM();
		String notExistFile = "not_Exist";
		String errorFormatFile = "error_format";
		try {
			LoadCompositeMap(notExistFile);
		} catch (Throwable e) {
			assertEquals("异常代码：aurora00002 not_Exist.bm文件不存在!", e.getMessage());
		}
		try {
			LoadCompositeMap(errorFormatFile);
		} catch (Throwable e) {
		    assertEquals("异常代码：aurora00004 error_format.bm文件格式不正确，请检查第11行第69列!", e.getMessage());
		}
		
	}
	

	private void LoadCompositeMap(String fileName) {
		CompositeLoader cl = CompositeLoader.createInstanceForOCM();
		try {
			cl.loadFromClassPath(MessageFactoryTest.class.getPackage().getName() + "." + fileName, "bm");
		} catch (IOException e) {
			GeneralException ge = MessageFactory.createException("aurora00002", e, new String[] { fileName + ".bm" });
			throw ge;
		} catch (SAXException e) {
			GeneralException ge = null;
			if (e instanceof SAXParseException) {
				SAXParseException parseEx = (SAXParseException) e;
				ge = MessageFactory.createException("aurora00004", e, new String[] { fileName + ".bm",
						String.valueOf(parseEx.getLineNumber()), String.valueOf(parseEx.getColumnNumber()) });
			} else {
				ge = MessageFactory.createException("aurora00003", e, new String[] { fileName + ".bm" });
			}
			throw ge;
		}
	}

	
}
