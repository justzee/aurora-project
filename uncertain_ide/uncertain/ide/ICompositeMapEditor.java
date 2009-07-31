/*
 * Created on 2009-7-29
 */
package uncertain.ide;

import uncertain.composite.CompositeMap;
import uncertain.schema.ISchemaManager;

public interface ICompositeMapEditor {
    
    public void setSchemaManager( ISchemaManager manager );
    
    public ISchemaManager getSchemaManager();
    
    public void setData( CompositeMap data );
    
    public CompositeMap getData();
    
    public void refresh();

}
