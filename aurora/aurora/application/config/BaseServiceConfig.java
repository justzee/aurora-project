/*
 * Created on 2009-9-15 下午06:34:13
 * Author: Zhou Fan
 */
package aurora.application.config;

import aurora.application.Namespace;
import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;

public class BaseServiceConfig extends DynamicObject {

    public static final String KEY_PARAMETER = "parameter";
    private static final String KEY_INIT_PROCEDURE = "init-procedure";
    
    public static BaseServiceConfig createServiceConfig(CompositeMap map) {
        BaseServiceConfig cfg = new BaseServiceConfig();
        cfg.initialize(map);
        return cfg;
    }

    public CompositeMap getInitProcedureConfig() {
        return object_context.getChild(KEY_INIT_PROCEDURE);
    }
    
    public CompositeMap getParameterConfig(){
        return object_context.getChild(KEY_PARAMETER);
    }
    
    public void addInitProcedureAction( CompositeMap config ){
        CompositeMap init_config = getInitProcedureConfig();
        if(init_config==null){
            init_config = object_context.createChild(KEY_INIT_PROCEDURE);
            init_config.setNameSpaceURI(Namespace.AURORA_FRAMEWORK_NAMESPACE);
        }
        init_config.addChild(config);
    }

}
