/**
 * Created on: 2004-6-11 14:47:20
 * Author:     zhoufan
 */
package uncertain.testcase.ocm;

import junit.framework.TestCase;
import java.util.*;

import uncertain.ocm.*;
import uncertain.testcase.object.*;

/**
 * 
 */
public class ClassAnalyzerTest extends TestCase {
	
	ClassAnalyzer analyzer;
	
	static String attributes[] = {"name", "canmove"};
	static Class  attrib_classes[]	={MethodAccessor.class, FieldAccessor.class};
	
	static String elements[] 
		= {"points","pointlist","points1","points2","points3", "center"};
	static Class  elements_classes[]	
		= { ArrayAccessor.class, CollectionAccessor.class, 
			CollectionAccessor.class, ArrayAccessor.class,
			ContainerAccessor.class,  MethodAccessor.class};

	/**
	 * Constructor for ClassAnalyzerTest.
	 * @param arg0
	 */
	public ClassAnalyzerTest(String arg0) {
		super(arg0);
		analyzer = new ClassAnalyzer(new OCManager());
	}

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(ClassAnalyzerTest.class);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void checkMap( Map result, String[] names, Class[] classes ){
		assertEquals(names.length, classes.length);
		assertEquals(result.size(), names.length);
		for( int i=0; i<names.length; i++){
			Object o = result.get(names[i]);
			assertNotNull(o);
			assertEquals(o.getClass(), classes[i]);
		}	
	}

	public void testAnalyze() {
		MappingRule rule = analyzer.analyze(Polygon.class);
/*
		System.out.println(rule.getAttributeMapping());
		System.out.println(rule.getElementMapping());		
*/		
		checkMap(rule.getAttributeMapping(), attributes, attrib_classes);
		checkMap(rule.getElementMapping(), elements, elements_classes);
	}

}
