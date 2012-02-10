/**
 * Created on: 2002-12-30 11:22:57
 * Author:     zhoufan
 */
package org.lwap.mvc;

import java.io.IOException;

import org.lwap.ui.UIAttribute;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;

abstract class HtmlLayoutHandle extends DynamicObject
implements TabularLayout.LayoutHandle {
	
		protected java.io.Writer		out;
		protected BuildSession		session;
		protected CompositeMap		model;
		protected CompositeMap		view;
		protected CompositeMap		origin_model;
		
		public abstract void createCellContent() throws ViewCreationException ;

		public abstract boolean hasMoreElements();

		public void onLayoutBegin(BuildSession session, 
									CompositeMap model,	
									CompositeMap view) 
		throws ViewCreationException {
		    this.model = DataBindingConvention.getDataModel(model,view);
		    if(this.model==null) origin_model = model;
		    else origin_model = this.model;
		    this.view = view;
		    this.session = session;
		    
		    this.out = session.getWriter();
			try{    
			    //if(out!=null)throw new NullPointerException();
				out.write("<table border=\"0\"");
				if( view.containsKey(UIAttribute.ATTRIB_WIDTH))
					out.write(" width=\"" + view.getString(UIAttribute.ATTRIB_WIDTH)+"\"");
				out.write(">");
			} catch(IOException ex){
				throw new ViewCreationException(ex);
			}				
		}
		
		public void onLayoutEnd() throws ViewCreationException{
			try{
			    out.flush();
			    //out.write("<!-- end layout table -->");
				out.write("</table>");
				out.flush();
				//out.close();
			} catch(IOException ex){
				throw new ViewCreationException(ex);
			}				
		}
		
		public void onRowBegin() throws ViewCreationException{
			try{			
				out.write("<tr>");
			} catch(IOException ex){
				throw new ViewCreationException(ex);
			}				
		}
		
		public void onRowEnd() throws ViewCreationException{
			try{		
			    out.flush();
			    //out.write("<!-- end layout row -->");
			    out.write("</tr>");				
			} catch(IOException ex){
				throw new ViewCreationException(ex);
			}				
		}
		

		public void onCreateCell() throws ViewCreationException{
			StringBuffer buf = new StringBuffer();
			AttributeBuilder.createAttrib(buf,getObjectContext(),UIAttribute.ATTRIB_WIDTH, Layout.KEY_CELL_WIDTH);
			AttributeBuilder.createAttrib(buf,getObjectContext(),UIAttribute.ATTRIB_HEIGHT, Layout.KEY_CELL_HEIGHT);
			AttributeBuilder.createAttrib(buf,getObjectContext(),UIAttribute.ATTRIB_ALIGN, Layout.KEY_CELL_ALIGN);
            AttributeBuilder.createAttrib(buf,getObjectContext(),"valign", Layout.KEY_CELL_VALIGN);
            AttributeBuilder.createAttrib(buf,getObjectContext(),"class", Layout.KEY_CELL_STYLE);
			try{
				out.write("<td");
				out.write(buf.toString());
				out.write('>');
				if( hasMoreElements() ) 
					createCellContent();
				out.flush();
				//out.write("<!-- end layout td -->");
				out.write("</td>");
			} catch(IOException ex){
				throw new ViewCreationException(ex);
			}
			
		}	
		
			
};