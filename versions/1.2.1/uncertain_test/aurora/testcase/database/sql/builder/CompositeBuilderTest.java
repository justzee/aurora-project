/*
 * Created on 2010-5-26 下午03:28:38
 * $Id$
 */
package aurora.testcase.database.sql.builder;

import java.util.Collection;
import java.util.Iterator;

import aurora.database.sql.CompositeStatement;
import aurora.database.sql.InsertStatement;
import aurora.database.sql.UpdateStatement;
import aurora.service.validation.Parameter;

public class CompositeBuilderTest extends AbstractSqlBuilderTest {


    public CompositeBuilderTest(String name) {
        super(name);
    }
    
    public static UpdateStatement createUpdateStatement(){
        UpdateStatement update = new UpdateStatement("EMP_HT");
        update.addUpdateField("DATE_FROM", "sysdate");
        update.addUpdateField("DATE_TO", "FND_SYS.GET_END_DATE");
        update.getWhereClause().addCondition("deptno=${@deptno}");
        return update;
    }
    
    public void testCompositeStatementBuild()
        throws Exception
    {
        InsertStatement ins = InsertBuilderTest.createInsertSelect();
        UpdateStatement update = createUpdateStatement();
        CompositeStatement comp = new CompositeStatement();
        comp.addStatement(ins);
        comp.addStatement(update);
        String sql = mRegistry.getSql(comp);

        String ins_sql = mRegistry.getSql(ins);
        assertContains(sql, ins_sql);
        
        String update_sql = mRegistry.getSql(update);
        assertContains(sql, update_sql);
        assertContains(sql, "BEGIN");
        assertContains(sql, "END");
        
        System.out.println(sql);
      
    }
    
    boolean isExists( String param_name, Collection p ){
        Iterator it = p.iterator();
        while(it.hasNext()){
            Parameter param = (Parameter)it.next();
            if(param_name.equals(param.getName()))
                return true;
        }
        return false;
    }
    
    public void testGetParameter(){
        InsertStatement ins = InsertBuilderTest.createInsertSelect();
        ins.addParameter( Parameter.createInputParameter("P1", "String") );
        ins.addParameter(Parameter.createOutputParameter("P2", "String"));
        
        UpdateStatement update = createUpdateStatement();
        update.addParameter( Parameter.createInputParameter("P1", "String") );
        update.addParameter(Parameter.createInputParameter("P3", "String"));
        
        CompositeStatement comp = new CompositeStatement();
        comp.addStatement(ins);
        comp.addStatement(update);
        
        /*
         * case1: 2 child both have parameters, parent haven't 
         */
        Collection params = comp.getParameters(); 
        assertEquals(params.size(), 3);
        assertTrue(isExists("P1",params));
        assertTrue(isExists("P2",params));
        assertTrue(isExists("P3",params));
        
        /*
         * case2: childs and parent all have parameter
         */
        comp.addParameter(Parameter.createInputParameter("P1", "String"));
        comp.addParameter(Parameter.createInputParameter("P2", "String"));
        comp.addParameter(Parameter.createInputParameter("P3", "String"));
        comp.addParameter(Parameter.createInputParameter("P4", "String"));
        params = comp.getParameters();
        assertEquals(params.size(), 4);
        assertTrue(isExists("P1",params));
        assertTrue(isExists("P2",params));
        assertTrue(isExists("P3",params));
        assertTrue(isExists("P4",params));
        

        InsertStatement ins1 = InsertBuilderTest.createInsertSelect();
        ins1.addParameter( Parameter.createInputParameter("P1", "String") );
        ins1.addParameter(Parameter.createOutputParameter("P5", "String"));
        
        UpdateStatement update1 = createUpdateStatement();
        update1.addParameter( Parameter.createInputParameter("P2", "String") );
        update1.addParameter(Parameter.createInputParameter("P6", "String"));  

        /*
         * case3: cascade composite
         */
        CompositeStatement child_comp = new CompositeStatement();
        child_comp.addStatement(ins1);
        child_comp.addStatement(update1);
        comp.addStatement(child_comp);
        
        params = comp.getParameters(); 
        assertEquals(params.size(), 6);
        assertTrue(isExists("P1",params));
        assertTrue(isExists("P2",params));
        assertTrue(isExists("P3",params));    
        assertTrue(isExists("P4",params));
        assertTrue(isExists("P5",params));
        assertTrue(isExists("P6",params));   
        
        String sql = mRegistry.getSql(comp);
        //System.out.println(sql);
    }
    


}
