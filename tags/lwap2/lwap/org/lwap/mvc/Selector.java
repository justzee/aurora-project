/**
 * Created on: 2002-11-20 19:18:40
 * Author:     zhoufan
 */
package org.lwap.mvc;

import java.util.Collection;
import java.util.Iterator;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;

/**
 * <std:selector Test="model_access_path">
 * 		<case Value="Value1">
 * 			<view1/>
 * 		</case>
 * 		<case Value="Value2">
 * 			<view2/>
 * 		</case>
 * </std:selector>
 */
public class Selector implements View {
	
	static final String VIEW_NAME = "selector";
	
	static final String KEY_TEST = "Test";
	static final String KEY_TEST_CLASS = "TestClass";
	static final String KEY_VALUE = "Value";
	static final String CASE = "case";			
	
//	static HashMap decider_cache = new HashMap();

	/**
	 * @see org.lwap.mvc.View#build(BuildSession, CompositeMap, CompositeMap)
	 */
	public void build(
		BuildSession session,
		CompositeMap model,
		CompositeMap view)
		throws ViewCreationException 
	{
		CompositeMap select_model = DataBindingConvention.getDataModel(model,view);
		if(select_model == null) return;
		String 		 test_field = view.getString(KEY_TEST);
//		String		 test_class = 
		if( test_field == null) throw new ViewCreationException("selector: No test field specified");
		Object obj = select_model.getObject(test_field);
		Iterator it = view.getChildIterator();
		if( it == null) throw new ViewCreationException("selector:No case found");

		Collection child_views = null;
		while( it.hasNext()){
			CompositeMap child = (CompositeMap)it.next();
//			if( CASE.equals(view.getName()) ) throw new ViewCreationException("selector:not a 'case' element:"+child.getName());
			Object test_value = child.get(KEY_VALUE);

			if( test_value == null) {
				child_views = child.getChilds();
				break;
			}
			else{ 
				if( obj == null) continue;
				String vl = test_value.toString();
				
				if( "*".equals(vl) )
					if( obj != null ){
						child_views = child.getChilds();
						break;
					}
				
				vl = TextParser.parse(vl, model);
				if( vl.equals(obj.toString())){
					child_views = child.getChilds();
					break;				
				}
			}
			
		}
		
		if( child_views != null) session.applyViews(select_model,child_views);
	}

	/**
	 * @see org.lwap.mvc.View#getViewName()
	 */
	public String getViewName() {
		return VIEW_NAME;
	}

}
