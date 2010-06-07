/**
 * Created on: 2002-12-30 14:33:43
 * Author:     zhoufan
 */
package org.lwap.mvc;

import java.util.Iterator;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;

/**
 * apply same view set on a collection of model
 */
public class Repeater extends Layout {
    
    public static final String KEY_SIMPLE_STYLE = "SimpleStyle";
    public static final String KEY_RECORD_NUMBER_FIELD = "RecordNumberField";    
    public static final String KEY_RECORD_NUMBER_BEGIN = "RecordNumberBegin";    
/*
 *      String row_number_field = view.getString(Layout.KEY_RECORD_NUMBER_FIELD);
        if(row_number_field!=null) row_number_field = TextParser.parse(row_number_field, model);
        
        int record_num=0;

 */
    
    public class RecordNumberConfig{
        
        String    record_number_field;  
        
        long      record_number = 0;
        
        public RecordNumberConfig( CompositeMap model, CompositeMap view ){
            record_number_field = view.getString(KEY_RECORD_NUMBER_FIELD);
            if(record_number_field!=null)
                record_number_field = TextParser.parse(record_number_field, model);
            String record_number_begin = view.getString(KEY_RECORD_NUMBER_BEGIN);
            if(record_number_begin!=null){
                Object obj = TextParser.parse(record_number_begin, model);
                if(obj!=null){
                    record_number = Long.parseLong(obj.toString());
                }
            }
        }
        
        public void putField( CompositeMap record ){
            if(record_number_field!=null)
                record.putObject(record_number_field, new Long(record_number));
            record_number++;
        }
    }
    
	public class ModelBasedLayoutHandle extends HtmlLayoutHandle {

		Iterator                  model_iterator;
        RecordNumberConfig        num_config;
		
		/**
		 * @see org.lwap.mvc.HtmlLayoutHandle#createCellContent()
		 */
		public void createCellContent() throws ViewCreationException {
			CompositeMap child_model = (CompositeMap) model_iterator.next();
            num_config.putField(child_model);
			session.applyViews(child_model,view.getChilds() );
        }

		/**
		 * @see org.lwap.mvc.TabularLayout.LayoutHandle#hasMoreElements()
		 */
		public boolean hasMoreElements() {
			if( model_iterator == null)
				return false;
			else{
				return model_iterator.hasNext();
            }
		}

		/**
		 * @see org.lwap.mvc.TabularLayout.LayoutHandle#onLayoutBegin(BuildSession, CompositeMap, CompositeMap)
		 */
		public void onLayoutBegin(
			BuildSession session,
			CompositeMap model,
			CompositeMap view)
		throws ViewCreationException {
			super.onLayoutBegin(session, model, view);
			if( this.model == null) return;
			model_iterator = this.model.getChildIterator();
            num_config = new RecordNumberConfig(model,view);
		}
		
	};
	
	public class SimpleLayoutHandle implements  TabularLayout.LayoutHandle {
	    
	    Iterator 		model_iterator;
	    CompositeMap	view;
	    BuildSession	session;
        RecordNumberConfig num_config;
	    
	    public SimpleLayoutHandle(
	            BuildSession session,
				CompositeMap model,
				CompositeMap view)
	    {
	        CompositeMap m = DataBindingConvention.getDataModel(model,view);
	        if(m!=null) model_iterator = m.getChildIterator();
	        this.view = view;
	        this.session = session;
	    }
		
		public void onLayoutBegin(
				BuildSession session,
				CompositeMap model,
				CompositeMap view)
				throws ViewCreationException 
        {
            num_config = new RecordNumberConfig(model,view);
		    return;
		}
		
		public void onLayoutEnd() throws ViewCreationException{
		    return;
		}
		
		public void onRowBegin() throws ViewCreationException {
		    return;
		}
		
		public void onRowEnd() throws ViewCreationException {
			return;
		}

		public void onCreateCell() throws ViewCreationException{
			CompositeMap child_model = (CompositeMap) model_iterator.next();
            num_config.putField(child_model);
            session.applyViews(child_model,view.getChilds() );
        }
		
		public boolean hasMoreElements() {
			if( model_iterator == null)
				return false;
			else
				return model_iterator.hasNext();	
		}
		
		
	};	

	/**
	 * @see org.lwap.mvc.View#build(BuildSession, CompositeMap, CompositeMap)
	 */
	public void build(
		BuildSession session,
		CompositeMap model,
		CompositeMap view)
		throws ViewCreationException 
	{
	    TabularLayout.LayoutHandle layoutHandle = null;
	    boolean simpleStyle = view.getBoolean(KEY_SIMPLE_STYLE, false);
		if(!simpleStyle){
		    ModelBasedLayoutHandle handle = new ModelBasedLayoutHandle();
			handle.initialize(view);
			layoutHandle = handle;	
		}else{
		    layoutHandle = new SimpleLayoutHandle(session, model,view);
		}	    
		buildTabular(session,model,view,layoutHandle);
	}

	/**
	 * @see org.lwap.mvc.Layout#createDefaultHandle(CompositeMap)
	 */
	/*
	protected TabularLayout.LayoutHandle createDefaultHandle(
		CompositeMap view) {
		if(!simpleStyle){
		    ModelBasedLayoutHandle handle = new ModelBasedLayoutHandle();
			handle.initialize(view);
			return handle;	
		}else{
		    return new SimpleLayoutHandle(model,view,session);
		}
	}
	*/

	/**
	 * @see org.lwap.mvc.View#getViewName()
	 */
	public String getViewName() {
		return "repeater";
	}

}
