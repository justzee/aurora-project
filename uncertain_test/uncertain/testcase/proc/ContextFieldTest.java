/*
 * Created on 2006-11-20
 */
package uncertain.testcase.proc;

import uncertain.composite.*;
//import uncertain.ocm.*;
import uncertain.proc.*;
//import uncertain.core.*;
import uncertain.testcase.core.*;
import uncertain.testcase.dbsample.*;


public class ContextFieldTest extends UncertainEngineTest {
    Table           book;
    Database        database;
    CompositeMap    dbconfig;
    Procedure       create_query;
    
    protected void setUp() throws Exception { 
        super.setUp();
        dbconfig = engine.getCompositeLoader().loadFromClassPath("uncertain.testcase.dbsample.dbschema");
        database = (Database)engine.getOcManager().createObject(dbconfig);
        assertNotNull(database);
        book = database.getTable("book");
        assertNotNull(book);
        create_query = engine.getProcedureManager().loadProcedure("uncertain.testcase.dbsample.CreateQuery");
        assertNotNull(create_query);      
    }
    
    public void testProcedureFields(){
        Field[] fields = create_query.getFields();
        assertNotNull(fields);
        assertEquals(fields.length, 6);    
        assertEquals(create_query.getInputFieldList().size(), 1);        
        
        Field f1 = fields[0];
        assertTrue(f1.isInputField());
        assertEquals(f1.getName(), "Table");
        assertEquals(f1.getType(), "uncertain.testcase.dbsample.Table");
        
        Field f2 = create_query.getReturnField();
        assertEquals(f2.getName(),"Sql");        
    }
    /*
    
    public void testActionParameters(){
        String[] actions = {"GetEntity","CreateSqlFields","CreateWhereClause","CreateSql"};
        int[] in_count = {0,0,0,3};
        int[] out_count = {1,1,1,1};
        for(int i=0; i<actions.length; i++){
            Action action = (Action)create_query.getNamedEntry(actions[i]);
            assertEquals(action.getInputFields().size(), in_count[i]);
            assertEquals(action.getReturnFields().size(), out_count[i]);
        }
        
    }
    */
    
    public void testPassingFields(){
        QueryCreator qr = new QueryCreator();
        ProcedureRunner runner = engine.createProcedureRunner(create_query);
        runner.setContextField("Table", book);
        runner.getConfiguration().addParticipant(qr);
        //create_query.run(runner);
        runner.run();
        assertEquals(qr.getTable(), book);
        assertNotNull(runner.getContext().get("Sql"));
    }

    public ContextFieldTest(String name) throws Exception {
        super(name);
    }


}
