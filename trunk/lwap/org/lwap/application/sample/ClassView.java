/**
 * Created on: 2002-11-11 21:52:48
 * Author:     zhoufan
 */
package org.lwap.application.sample;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lwap.application.BaseService;

import uncertain.composite.CompositeMap;

public class ClassView extends BaseService {

  // 重载父类的createModel方法，创建该Service的Model
  public void createModel(HttpServletRequest request, HttpServletResponse response) 
  throws  IOException,ServletException
  {
  	// 从URL参数获取需要显示的java类的名称
  	String cls_name = request.getParameter("class");
  	if( cls_name == null) return;

	//在Model中创建一个名为"class"的CompositeMap，并在其中创建一个名为"method-list"的CompositeMap
  	CompositeMap class_model = super.getModel().createChild(null,null,"class");
  	class_model.put("name",cls_name);
  	CompositeMap method_list = class_model.createChild(null,null,"method-list");
  	
  	
  	//获取该class的实例
  	Class cls = null;
  	try{
  		cls = Class.forName(cls_name);
  	}catch(Throwable ex){
  		throw new ServletException(ex);
  	}
   	
   		//获取该类直接声明的所有方法
  	Method[] methods = cls.getDeclaredMethods();
  	for(int i=0; i<methods.length; i++){
  			// 跳过静态的或非公有的方法
  			int md = methods[i].getModifiers();
  			if( Modifier.isStatic(md) || !Modifier.isPublic(md)) continue;
  			
  			//对每个符合条件的方法，创建一个CompositeMap，并将该方法的主要属性放进该Map
  			CompositeMap method = new CompositeMap(null,null,"method");
  			method.put("access", Modifier.toString(md) );  			
  			method.put("return", methods[i].getReturnType().getName());
  			
  			Class[] params = methods[i].getParameterTypes();  			
  			StringBuffer param_declare = new StringBuffer();
  			for( int n=0; n<params.length; n++){
  				if( n>0) param_declare.append(',');
  				param_declare.append(params[n].getName());
  			}
  			method.put("declare", methods[i].getName() +'(' + param_declare + ')' );
  			
  			//将创建的CompositeMap作为子Map添加到method-list中
  			method_list.addChild(method);
  	}
  	
  	//System.out.println( getModel().toXML());
  	
  	/*
  	 * 循环完毕后，Model的结构为
  	 * <model>
  	 *    <class name="java.lang.String">
  	 *         <method access="public" return="int" declare="length()" />
  	 *         ...
  	 *    </class>
  	 * </mode>
  	 */
  		
  }
}
