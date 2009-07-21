package com.aurora.ui;

import java.io.IOException;
import java.util.Map;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import uncertain.composite.CompositeMap;
import uncertain.ocm.ISingleton;

/**
 * 带Input类型的组件基类.
 * 
 * @version $Id: Field.java v 1.0 2009-7-20 上午11:29:55 znjqolf Exp $
 * @author <a href="mailto:znjqolf@126.com">vincent</a>
 * 
 * notBlank:是否为空  true|false
 * readOnly:是否只读  true|false
 * emptyText:输入提示文字	String
 * className:额外样式名	String
 * style:样式描述			String
 */
public class Field {
	
	public static int idIndex = 1;
	
	protected static final String PROPERTITY_NOTBLANK = "notBlank";
	protected static final String PROPERTITY_READONLY = "readOnly";
	protected static final String PROPERTITY_EMPTYTEXT = "emptyText";
	protected static final String PROPERTITY_CLASSNAME = "className";
	protected static final String PROPERTITY_STYLE = "style";
	protected static final String PROPERTITY_VALUE = "value";
	
	protected static final String WRAP_CSS = "wrapClass";
	
	protected static final String CLASSNAME_WRAP = "item-wrap";
	protected static final String CLASSNAME_NOTBLANK = "item-notBlank";
	protected static final String CLASSNAME_READONLY = "item-readOnly";
	protected static final String CLASSNAME_EMPTYTEXT = "item-emptyText";
	
	protected String getJavaScript(BuildSession session, ViewContext context, String javascript) {
		boolean b = session.includeResource(javascript);
		if (!b) {
			String js = session.getResourceUrl(javascript);
			return "<script language='javascript' type='text/javascript' src='"+ js + "'></script>";
		}else{
			return "";			
		}
	}

	protected String getStyleSheet(BuildSession session, ViewContext context,String style) {
		boolean b = session.includeResource(style);
		if (!b) {
			String href = session.getResourceUrl(style);
			return "<link type='text/css' rel='stylesheet' href='" + href+ "'></link>";
		}else{
			return "";
		}
	}
	
	/**
	 * 加载Aurora组件库以及样式文件
	 * 
	 * @param session
	 * @param context
	 * @throws IOException
	 */
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		StringBuffer css = new StringBuffer();
		StringBuffer js = new StringBuffer();
		css.append(getStyleSheet(session, context, "core/Aurora.css"));
		if(!"".equals(css.toString()))
		context.getContextMap().put("css",css.toString());
		
		js.append(getJavaScript(session, context, "core/ext-core.js"));
		js.append(getJavaScript(session, context, "core/Aurora.js"));
		js.append(getJavaScript(session, context, "core/Field.js"));
		if(!"".equals(js.toString()))
		context.getContextMap().put("script",js.toString());
	}

	public void onCreateViewContent(BuildSession session, ViewContext context) {
		CompositeMap view = context.getView();
		Map map = context.getMap();
		
		/** 包装样式 **/
		map.put(WRAP_CSS, CLASSNAME_WRAP);
		
		/** 额外样式名 **/
		String className = view.getString(PROPERTITY_CLASSNAME, "");
//		map.put(PROPERTITY_CLASSNAME, className);
		if(!"".equals(className)) {
			String wrapClass = (String)map.get(WRAP_CSS);
			wrapClass += " " + className;
			map.put(WRAP_CSS, wrapClass);
		}
		
		
		/** 样式 **/
		String style = view.getString(PROPERTITY_STYLE, "");
		if(!"".equals(style)) {
			map.put(PROPERTITY_STYLE, "style='"+style+"'");
		}
		
		/** 是否为空 **/
		String notBlank = view.getString(PROPERTITY_NOTBLANK, "false");
		map.put(PROPERTITY_NOTBLANK, notBlank);
		if("true".equals(notBlank)) {
			String wrapClass = (String)map.get(WRAP_CSS);
			wrapClass += " " + CLASSNAME_NOTBLANK;
			map.put(WRAP_CSS, wrapClass);
		}
		
		/** 是否只读 **/
		String readOnly = view.getString(PROPERTITY_READONLY,"false");
		if("true".equals(readOnly)) {
			String wrapClass = (String)map.get(WRAP_CSS);
			wrapClass += " "+CLASSNAME_READONLY;
			map.put(WRAP_CSS, wrapClass);
			map.put(PROPERTITY_READONLY, "readonly");
		}
		
		/** id name 属性 **/
		String id = view.getString("id");
		if("".equals(id)) id= "aid-"+(idIndex++);
		map.put("id", id);
		String name = view.getString("name");
		if("".equals(name)) name= "aname-"+(idIndex++);
		map.put("name", name);
		
		/** 文本提示 **/
		String emptyText = view.getString(PROPERTITY_EMPTYTEXT,"");
		if(!"".equals(emptyText)) {
			String wrapClass = (String)map.get(WRAP_CSS);
			wrapClass += " " + CLASSNAME_EMPTYTEXT;
			map.put(WRAP_CSS, wrapClass);
			map.put(PROPERTITY_VALUE, emptyText);
		}
		
		/** 值 **/
		String value = view.getString(PROPERTITY_VALUE,"");
		if(!"".equals(emptyText)) {
			String wrapClass = (String)map.get(WRAP_CSS);
			wrapClass += " " + CLASSNAME_EMPTYTEXT;
			map.put(WRAP_CSS, wrapClass);
			map.put(PROPERTITY_VALUE, emptyText);
		}
	}
}
