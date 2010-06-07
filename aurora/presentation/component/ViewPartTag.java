/*
 * Created on 2009-5-15
 */
package aurora.presentation.component;

import java.io.Writer;
import java.util.logging.Level;

import uncertain.composite.CompositeMap;
import uncertain.logging.ILogger;
import uncertain.util.template.ITagContent;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

public class ViewPartTag implements ITagContent {


    BuildSession        mBuildSession;
    ViewContext         mViewContext;    
    CompositeMap        mView;

    public ViewPartTag(BuildSession buildSession, ViewContext viewContext, CompositeMap view ) 
    {
        this.mBuildSession = buildSession;
        this.mViewContext = viewContext;
        this.mView = view;
    }

    public String getContent( CompositeMap context ) {
        try{
            Writer out = mBuildSession.getWriter();
            out.flush();
            mBuildSession.buildView(mViewContext.getModel(), mView );
            out.flush();
            return null;
        }catch(Exception ex){
            ILogger logger = mBuildSession.getLogger();
            logger.log(Level.SEVERE, "Error when building view ", ex);
            logger.severe("view config:" + mView.toXML());
            return null;
        }
    }

}
