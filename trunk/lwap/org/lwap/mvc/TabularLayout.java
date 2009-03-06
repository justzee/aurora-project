/**
 * Created on: 2002-12-30 10:47:22
 * Author:     zhoufan
 */
package org.lwap.mvc;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;


/**
 * Perform basic tasks for tabular layout
 * 
 */
public class TabularLayout {
	
	public interface LayoutHandle {
		
		public void onLayoutBegin(BuildSession session, 
									CompositeMap model,	
									CompositeMap view) throws ViewCreationException;
		
		public void onLayoutEnd() throws ViewCreationException;
		
		public void onRowBegin() throws ViewCreationException;
		
		public void onRowEnd() throws ViewCreationException;

		public void onCreateCell() throws ViewCreationException;
		
		public boolean hasMoreElements();
		
		
	};

	public static final int COLUMN_COUNT_UNLIMITED = -1;
	
	int				column_count;
	LayoutHandle		handle;
	
	public TabularLayout(int col_count, LayoutHandle handle){
		this.column_count = col_count;
		this.handle = handle;
	}
	
	public void layout( BuildSession session, CompositeMap model,	CompositeMap view)
					 	throws ViewCreationException 
	{
		
		//if( model == null) return;
        
		handle.onLayoutBegin(session, model, view);

		if( column_count == COLUMN_COUNT_UNLIMITED){
			handle.onRowBegin();
			while( handle.hasMoreElements()){
                handle.onCreateCell();
            }
			handle.onRowEnd();
		} else {
			while( handle.hasMoreElements()){
				handle.onRowBegin();
				for( int n=0; n<column_count; n++){
					handle.onCreateCell();
				}
				handle.onRowEnd();
			}
		}

		handle.onLayoutEnd();
	}

}