/*
 * Created on 2009-7-29
 */
package uncertain.ide;

import uncertain.composite.CompositeMap;
import uncertain.schema.ISchemaManager;

public class AbstractCompositeMapEditor implements ICompositeMapEditor {
    
    CompositeMap    mData;
    ISchemaManager  mSchemaManager;
    
    public CompositeMap getData() {
        return mData;
    }
    
    public void setData(CompositeMap data) {
        mData = data;
    }
    
    public ISchemaManager getSchemaManager() {
        return mSchemaManager;
    }
    
    public void setSchemaManager(ISchemaManager schemaManager) {
        mSchemaManager = schemaManager;
    }
    
    public void refresh(){
        // do nothing
    }

}
