/**
 * Created on: 2002-11-25 19:34:42
 * Author:     zhoufan
 */
package org.lwap.mvc;

import java.util.LinkedList;

import uncertain.util.AdaptiveTagParser;
import uncertain.util.QuickTagParser;
import uncertain.util.TagParseHandle;

/**
 *  Data structure to hold template contents
 */
public class TemplateContent{
	
	static TemplateContent default_instance = new TemplateContent(0,null);
	
	public static final int TYPE_TEMPLATE_FRAGMENT = 0;
	public static final int TYPE_TEMPLATE_TAG = 1;
    public static final int TYPE_SERVICE_INVOKE = 2;
	
	public String  content;
	int	           type;
	
	public TemplateContent( int type, String content ){
		this.type = type;
		this.content = content;
        if(TYPE_TEMPLATE_TAG==type && content!=null)
            if(content.length()>1 && content.charAt(0) == '!'){
                this.type = TYPE_SERVICE_INVOKE;
                this.content = content.substring(1);
            }
	}
	
	public String toString(){
		return "TemplateContent(type:"+type+" \""+content+"\")";
	}
	

	public class ParseHandle implements TagParseHandle {
		
		LinkedList		content_list = new LinkedList();		
		StringBuffer buf = null;
		
		public LinkedList getContentList(){ return content_list; }

		public String  ProcessTag(int index, String tag){
			if( buf != null){
				content_list.add(new TemplateContent(TemplateContent.TYPE_TEMPLATE_FRAGMENT, buf.toString()));
				buf.setLength(0);
//  					buf = null;
			}
			content_list.add(new TemplateContent(TemplateContent.TYPE_TEMPLATE_TAG, tag));
			return null;
		}
		
		public int ProcessCharacter( int index, char ch){
			if( buf == null) buf = new StringBuffer();
			buf.append(ch);
			return -1; 
		}
		
		public StringBuffer getBuffer(){
			return buf;
		}
	};	


	//protected static AdaptiveTagParser parser   = AdaptiveTagParser.newUnixShellParser();
		
	public static LinkedList buildTemplateContent(String template){
		if( template==null) return null;
		ParseHandle handle = default_instance.new ParseHandle();
        QuickTagParser parser = new QuickTagParser();
		parser.parse(template, handle);	
		LinkedList content_list = handle.getContentList();
		
		StringBuffer buf = handle.getBuffer();
		if( buf != null) 
			if(buf.length()>0)
				content_list.add(new TemplateContent(TemplateContent.TYPE_TEMPLATE_FRAGMENT, buf.toString()));
		
		return content_list;
	
	}

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }
	
	
}
