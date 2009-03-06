/**
 * Created on: 2002-12-2 18:08:57
 * Author:     zhoufan
 */
package org.lwap.ui.testcase;

import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

import org.lwap.mvc.DataBindingConvention;
import org.lwap.ui.SelectiveField;
import org.lwap.ui.SelectiveFieldImpl;
import org.lwap.ui.UIAttribute;
import org.lwap.validation.InputParameterImpl;

import uncertain.composite.CompositeMap;

/**
 * 
 */
public class SelectiveFieldImplTest extends TestCase {

	CompositeMap	context;	
	CompositeMap	model;
	CompositeMap	options;
	CompositeMap	select;
	
	SelectiveField fld;

	/**
	 * Constructor for SelectiveFieldImplTest.
	 * @param arg0
	 */
	public SelectiveFieldImplTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		context = new CompositeMap("model");
		
		model = new CompositeMap("employee");
		model.put("status", new Integer(0));
		
		context.addChild(model);
		
		options = new CompositeMap("employee_status");
		for(int i=1; i<=5; i++){
			CompositeMap op =  options.createChild("status");
			op.put("Name", "status"+i);
			op.put("Value",new Integer(i));
		}
		
		context.addChild(options);
		
		select = InputParameterImpl.createParameter("status",Integer.class,true,new Integer(0)) ;
		CompositeMap view_option = new CompositeMap("option");
		view_option.put(UIAttribute.ATTRIB_PROMPT,"--NONE--");
		view_option.put(DataBindingConvention.KEY_DATAVALUE , "0");
		select.addChild(view_option);

//		System.out.println(context.toXML());
		fld = SelectiveFieldImpl.createSelectiveField(select);
		fld.setDataModel("employee");
		fld.setDataField("@status");

		fld.setDataSource("employee_status");
		fld.setDisplayField("@Name");
		fld.setValueField("@Value");

		fld.bindModel(context);		
		fld.bindDataSource(context);
		
		fld.setValue("1");
		
		
//		System.out.println(select.toXML());
		
	}
	
	public void testOptionBinding(){
		Collection ops = fld.getBindedOptions();
		assertEquals( ops.size(), 5);
		Iterator it = ops.iterator();
		while( it.hasNext()){
			CompositeMap op = (CompositeMap)it.next();
			String prompt = fld.getBindedOptionPrompt(op);
			Object vl = fld.getBindedOptionValue(op);
//			System.out.println("vl:"+vl);
			assertTrue( vl instanceof Integer);
			int value = ((Integer)vl).intValue();
			assertEquals( prompt, "status"+value);
		}		
	}
	
	public void testParseViewOptions(){
		Iterator it = fld.getOptions().iterator();
		while( it.hasNext()){
			CompositeMap option = (CompositeMap) it.next();
			Object value = fld.getOptionValue(option);
			assertTrue( value instanceof Integer);
		}
	}
	
	public void testGetValue(){
		Integer vl = (Integer)fld.getValue();
		assertEquals( vl.intValue(), 1);
		
	}
	
	public void testGetSelectedPrompt(){
		for( int i=0; i<10; i++)
		assertEquals( fld.getSelectedPrompt(), "status1");
	}
	
	
	public void testForEach(){
		
		SelectiveField.SelectOptionProcessor processor = new SelectiveField.SelectOptionProcessor(){
			
			public boolean processOption( Object value, String prompt, boolean is_select){
				assertEquals( value.getClass(), Integer.class);
				if( is_select && value != null)
				assertEquals( value, fld.getValue());
				return true;
			}
			
			
		};
		
	}
	

}
