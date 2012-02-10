/**
 * Created on: 2002-11-28 13:35:02
 * Author:     zhoufan
 * Modify On 2003-01-15   By  Hu Jian
 * Add 'file'   input type="file"
 */
package org.lwap.ui.web;

import org.lwap.ui.InputFieldImpl;

import uncertain.composite.CompositeMap;
/**
 *
 */
public class TextEdit extends InputFieldImpl {

	public static TextEdit createTextEdit( CompositeMap view_def){
		TextEdit edit = new TextEdit();
		edit.initialize(view_def);
		return edit;
	}

	public static final String KEY_PASSWORD = "Password";
    public static final String KEY_FILE = "File";

	public boolean isPassword(){
		return getBoolean( KEY_PASSWORD, false);
	}

        public boolean isFile(){
                return getBoolean( KEY_FILE, false);
        }

	public void setPassword(boolean pwd){
		putBoolean( KEY_PASSWORD,pwd);
	}

        public void setFile(boolean file){
                putBoolean( KEY_FILE,file);
        }

}
