/**
 * Created on: 2004-6-15 19:04:36
 * Author:     zhoufan
 */
package uncertain.testcase.ocm;

import java.io.*;
//import java.util.*;

import junit.framework.TestCase;
import uncertain.composite.*;
import uncertain.ocm.*;
import uncertain.testcase.object.*;
import uncertain.testcase.dbsample.*;
import java.util.*;

/**
 * 
 */
public class OCManagerTest extends TestCase {
	
	OCManager			oc_manager;
	CompositeMap		person_map;
    CompositeMap        database_map;

	/**
	 * Constructor for OCManagerTest.
	 * @param arg0
	 */
	public OCManagerTest(String arg0) {
		super(arg0);
		oc_manager = new OCManager();
		oc_manager.getClassRegistry().registerPackage("uncertain.testcase.object", "uncertain.testcase.object");
        oc_manager.getClassRegistry().registerPackage("uncertain.testcase.dbsample", "uncertain.testcase.dbsample");        
		oc_manager.addListener(new LoggingListener());
	}

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(OCManagerTest.class);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
        InputStream stream = Person.class.getClassLoader().getResourceAsStream("uncertain/testcase/object/PersonTest.xml");
		person_map = OCManager.defaultParser().parseStream(stream);
        System.out.println(person_map.toXML());
		stream.close();
        
        stream = Database.class.getClassLoader().getResourceAsStream("uncertain/testcase/dbsample/dbschema.xml");
        database_map = OCManager.defaultParser().parseStream(stream);
        stream.close();

    }

	/**
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCreateObject() {
		Person person = (Person)oc_manager.createObject(person_map);
		assertNotNull(person);
		assertTrue(person.Assigned);
		assertNotNull(person.Workmates);
		assertEquals(person.Workmates.length, 2);
		assertEquals(person.Workmates[0].BORNPLACE, "Beijing");
		
		ContactInfo c = person.getContactInfo();
		assertNotNull(c);
		assertEquals(c.getExtraInfo().size(),2);
		//System.out.println(c.getExtraInfo());
		
		assertEquals(person.getName().toString(), "Frank,Lee");
		assertEquals(person.getContactInfo().getEmail(), "frank.lee@msn.com");
		assertEquals(person.getContactInfo().getPhone(), 16300);
		assertEquals(person.getContactInfo().getIcq(), 12222);		
		assertEquals(person.getContactInfo().getMobile(), new Integer(12345));
		//System.out.println(person);
	}
    
    public void testCreateDatabase(){
        // load Database
        Database database = (Database)oc_manager.createObject(database_map);
        assertNotNull(database);
        // check tables
        Collection tables = database.getTables();
        assertNotNull(tables);
        assertEquals(tables.size(), 2);
        checkForTable(database,"author",3,0,0);
        checkForTable(database,"book",4,1,1);
        // check column
        Table book = database.getTable("book");
        Column book_id = book.getColumn("book_id");
        assertNotNull(book_id);
        assertTrue(book_id.AutoIncrement);
        assertTrue(book_id.PrimaryKey);
        assertTrue(book_id.Required);
        assertEquals(book_id.Type,"INTEGER");
    }

    long doOCMCreate(int objects){
        long t = System.currentTimeMillis();
        oc_manager.setEventEnable(false);
        for(int i=0; i<objects; i++){
            Database database = (Database)oc_manager.createObject(database_map);
        }
        t = System.currentTimeMillis()-t; 
        return t;
    }
    
    
    long doNativeCreate(int objects){
        long t = System.currentTimeMillis();
        oc_manager.setEventEnable(false);
        for(int i=0; i<objects; i++){
            Database database = new Database();
            for(int n=0; n<2; n++){
            Table table = new Table("t"+i);
            for(int m=0; m<4; m++){
                Column column = new Column("column"+m);
                column.Size=50;
                String pk = m==0?"true":"false";
                column.PrimaryKey = Boolean.getBoolean(pk);
                column.AutoIncrement = Boolean.getBoolean(pk);
                column.Type="VARCHAR";
                table.addColumn(column);
            }
            ForeignKey k = new ForeignKey("fk1");
            k.addReference(new Reference());
            table.addForeignKey(k);
            Index id = new Index("i1");
            id.addIndexColumn( new IndexColumn("c1"));
            table.addIndex(id);
            database.addTable(table);
            }
        }
        t = System.currentTimeMillis()-t; 
        return t;
    }
    /*
    public void _testEfficiency(){
        int objects = 1000;
        long t1 = doNativeCreate(objects);
        System.out.println("total time for native creating "+objects+" objects:"+t1);
        System.out.println("Avg time for each obj:"+((double)t1)/objects);
        long t2 = doOCMCreate(objects);
        System.out.println("total time for OCM creating "+objects+" objects:"+t2);
        System.out.println("Avg time for each obj:"+((double)t2)/objects);
        System.out.println("Efficiency is "+(double)t2/t1+" times");
    }
    
    public void testEfficiency2() throws IOException {
        FileWriter f = new FileWriter("c:/result.csv");
        PrintWriter out = new PrintWriter(f);
        out.println("objects,Efficiency rate");
        for( int i=100; i<5000; i+=100){
            long t1 = doNativeCreate(i);
            long t2 = doOCMCreate(i);
            out.println(i+","+(double)t2/t1);
        }
        f.close();
        System.out.println("Done.");
    }
    */
    public void checkForTable(Database database, String table, int columns, int indexes, int fks){
        Table t = database.getTable(table);        
        assertNotNull(t);
        assertEquals(t.getColumnArray().length, columns);
        assertEquals(t.getIndexArray().length, indexes);
        assertEquals(t.getForeignKeyArray().length, fks);
        
    }

}
