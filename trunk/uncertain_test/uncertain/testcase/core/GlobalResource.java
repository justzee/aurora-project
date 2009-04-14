/*
 * Created on 2006-12-13
 */
package uncertain.testcase.core;

import uncertain.core.*;

/**
 * This class is used to test initializing global instances in
 * UncertainEngine 
 * @author Zhou Fan
 *
 */
public class GlobalResource implements IGlobalInstance {
    
    UncertainEngine uncertainEngine;

    /**
     * @param uncertainEngine
     */
    public GlobalResource(UncertainEngine uncertainEngine) {
        this.uncertainEngine = uncertainEngine;
    }

    public void onInitialize(){
        uncertainEngine.getObjectSpace().registerInstance(this);
    }
}
