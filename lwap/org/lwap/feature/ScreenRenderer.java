/*
 * Created on 2009-5-14
 */
package org.lwap.feature;

import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import org.lwap.controller.MainService;

import uncertain.composite.CompositeMap;
import uncertain.event.EventModel;
import uncertain.event.RuntimeContext;
import uncertain.logging.ILogger;
import uncertain.proc.ProcedureRunner;
import aurora.presentation.BuildSession;
import aurora.presentation.PresentationManager;

public class ScreenRenderer {
    
    /**
     * @param prtManager
     */
    public ScreenRenderer(PresentationManager prtManager) {
        super();
        mPrtManager = prtManager;
    }

    PresentationManager         mPrtManager;
    MainService                 mService;
    CompositeMap                mContext;
    CompositeMap                mScreen;
    
    public int onCreateView( ProcedureRunner runner ){
        mContext = runner.getContext(); 
        mService = MainService.getServiceInstance(mContext);
        mScreen = mService.getServiceConfig().getChild("screen");
        if( mScreen != null ){
            mScreen.setName("html-page");
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
        //HttpServletRequest request = mService.getRequest();
        HttpServletResponse response = mService.getResponse();
        
        Writer out = response.getWriter();
        BuildSession session = mPrtManager.createSession(out);
        RuntimeContext  rtc = RuntimeContext.getInstance(mContext);
        ILogger logger = (ILogger)rtc.getInstanceOfType(ILogger.class);
        session.setLogger(logger);
        session.buildView(mService.getModel(), mScreen);
        out.flush();
        
        return EventModel.HANDLE_NO_SAME_SEQUENCE;
        
    }

}
