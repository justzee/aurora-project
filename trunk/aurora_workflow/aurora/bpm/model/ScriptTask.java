/*
 * Created on 2014-8-27 上午12:55:38
 * $Id$
 */
package aurora.bpm.model;

import aurora.bpm.define.Activity;
import aurora.bpm.define.IProcessInstancePath;

public class ScriptTask extends Activity {
    
    @Override
    public void arrive(IProcessInstancePath path) {
        // execute script defined
        System.out.println("script executed");
    }
    
    @Override
    public void validate(){
        
    }

}
