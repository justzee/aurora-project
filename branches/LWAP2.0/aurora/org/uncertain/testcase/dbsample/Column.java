/*
 * Created on 2006-11-17
 */
package uncertain.testcase.dbsample;

public class Column {
    
    public String   Name;
    public String   Type;
    public boolean  PrimaryKey = false;
    public boolean  AutoIncrement = false;
    public boolean  Required = false;
    public int      Size;
    public String   DefaultValue;
    
    public Column(){
        // do nothing
    }
    
    public Column(String name){
        this.Name = name;
    }

}
