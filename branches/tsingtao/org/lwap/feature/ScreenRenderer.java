/*
 * Created on 2009-5-14
 */
package org.lwap.feature;

import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import org.lwap.controller.MainService;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.event.EventModel;
import uncertain.event.RuntimeContext;
import uncertain.logging.ILogger;
import uncertain.proc.ProcedureRunner;
import aurora.database.service.DatabaseServiceFactory;
import aurora.presentation.BuildSession;
import aurora.presentation.PresentationManager;
import aurora.presentation.ViewContext;

public class ScreenRenderer {
    
    public static final String SCREEN = "screen";
    public static final String HTML_PAGE = "html-page";

    /**
     * @param prtManager
     */
    public ScreenRenderer(PresentationManager prtManager, DatabaseServiceFactory fact ) {
        super();
        mPrtManager = prtManager;
        mServiceFactory = fact;
    }

    PresentationManager         mPrtManager;
    MainService                 mService;
    CompositeMap                mContext;
    CompositeMap                mScreen;
    DatabaseServiceFactory      mServiceFactory;
    
    public int onCreateView( ProcedureRunner runner ){
        mContext = runner.getContext(); 
        mService = MainService.getServiceInstance(mContext);
        mScreen = mService.getServiceConfig().getChild(SCREEN);
        if( mScreen != null ){
            mScreen.setName(HTML_PAGE);
            mContext.addChild(mScreen);
            mService.setViewOutput(true);
            return EventModel.HANDLE_NO_SAME_SEQUENCE;
        }
        else
            return EventModel.HANDLE_NORMAL;
    }
    
    public int onBuildOutputContent( ProcedureRunner runner )
        throws Exception
    {
        if( mScreen==null ) return EventModel.HANDLE_NORMAL;
        HttpServletResponse response = mService.getResponse();
        response.setContentType("text/html;charset=utf-8");
        Writer out = response.getWriter();
        BuildSession session = mPrtManager.createSession(out);
        
        ViewContext view_context = session.createNamedViewContext( new QualifiedName(null, HTML_PAGE));
        RuntimeContext  rtc = RuntimeContext.getInstance(mContext);

        ILogger logger = (ILogger)rtc.getInstanceOfType(ILogger.class);
        session.setLogger(logger);
        session.buildView(mService.getModel(), mScreen);
        out.flush();
        
        return EventModel.HANDLE_NO_SAME_SEQUENCE;
        
    }

}
