/*
 * Created on 2006-11-17
 */
package uncertain.testcase.dbsample;
import java.util.LinkedList;

public class ForeignKey {
    
    String          foreignTable;
    LinkedList      reference_list = new LinkedList();
    
    public ForeignKey(){
    }
    
    /**
     * @param foreignTable
     */
    public ForeignKey(String foreignTable) {
        super();
        this.foreignTable = foreignTable;
    }


    /**
     * @return the foreignTable
     */
    public String getForeignTable() {
        return foreignTable;
    }

    /**
     * @param foreignTable the foreignTable to set
     */
    public void setForeignTable(String foreignTable) {
        this.foreignTable = foreignTable;
    }
    
    public void addReference(Reference r){
        reference_list.add(r);
    }
    

}
