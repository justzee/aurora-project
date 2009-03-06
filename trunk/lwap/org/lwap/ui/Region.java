/**
 * Created on: 2002-11-13 13:56:51
 * Author:     zhoufan
 */
package org.lwap.ui;

/**
 * basic attribute for a widget that occupies certain region in screen
 */
public interface Region {
	
	public int getLeft();
	
	public int getTop();
	
	public int getWidth();
	
	public int getHeight();
	
	public void setLeft(int left);
	
	public void setTop(int top);
	
	public void setWidth(int width);
	
	public void setHeight(int height);

}
