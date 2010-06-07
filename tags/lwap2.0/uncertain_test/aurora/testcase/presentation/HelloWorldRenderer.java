/*
 * Created on 2009-7-14
 */
package aurora.testcase.presentation;

import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

/**
 * Using template to create UI content 
 * @author Zhou Fan
 */
public class HelloWorldRenderer {
    
    public void onCreateViewContent( BuildSession session, ViewContext view_context ){
        
        // 从view中获取color属性
        CompositeMap view = view_context.getView();
        String color = view.getString("color");

        // 从model中获取需要显示的字段
        CompositeMap model = view_context.getModel();
        String field = view.getString("field");
        String greeting = model.getString(field); 
        
        // 将动态内容放入ViewContext的Map中，后面将用于替换模版中的同名标记
        Map content_map = view_context.getMap();
        content_map.put("color", color);
        content_map.put("value", greeting);
    }

}
