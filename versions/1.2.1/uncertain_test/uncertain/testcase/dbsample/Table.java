/*
 * Created on 2006-11-17
 */
package uncertain.testcase.dbsample;

import java.util.*;

public class Table {

    String          name;
    TreeMap         column_map = new TreeMap();
    TreeMap         fk_map = new TreeMap();
    TreeMap         index_map = new TreeMap();
    
    public Table(){

    }
    
    public Table(String name){
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    public void addColumn(Column c){
        column_map.put(c.Name, c);
    }
    
    public void addForeignKey(ForeignKey k){
        fk_map.put(k.getForeignTable(), k);
    }
    
    public void addIndex(Index i){
        index_map.put(i.getName(), i);
    }
    
    // =========== Here we provide some convenient methods to access table config
    
    public Column[] getColumnArray(){
        Object[] o = column_map.values().toArray();
        Column[] c = new Column[o.length];
        System.arraycopy(o,0, c, 0, o.length);
        return c;
    }
    
    public Column getColumn(String name){
        return (Column)column_map.get(name);
    }
    
    public ForeignKey[] getForeignKeyArray(){
        Object[] o = fk_map.values().toArray();
        ForeignKey[] c = new ForeignKey[o.length];
        System.arraycopy(o,0, c, 0, o.length);
        return c;
    }
    
    public Index[] getIndexArray(){
        Object[] o = index_map.values().toArray();
        Index[] c = new Index[o.length];
        System.arraycopy(o,0, c, 0, o.length);
        return c;
    }    
    
}
