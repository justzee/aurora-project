/*
 * Created on 2006-11-17
 */
package uncertain.testcase.dbsample;

import java.util.LinkedList;

public class Index {
    
    String          name;
    LinkedList      index_column_list= new LinkedList();
    
    public Index(){
         
    }
    

    /**
     * @param name
     */
    public Index(String name) {
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
    
    public void addIndexColumn(IndexColumn c){
        index_column_list.add(c);
    }

}
