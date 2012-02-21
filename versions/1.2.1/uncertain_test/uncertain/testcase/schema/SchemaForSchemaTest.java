/*
 * Created on 2009-8-28
 */
package uncertain.testcase.schema;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.schema.Element;
import uncertain.schema.IQualifiedNamed;
import uncertain.schema.Schema;
import uncertain.schema.SchemaConstant;
import uncertain.schema.SchemaManager;

public class SchemaForSchemaTest extends TestCase {

    static final String[] elementNames = { "complexType","element","attribute","array","extension","featureClass", "category" };
    static final String[] element_attribs = {"maxOccurs", "minOccurs", "type", "editor", "ref", "category", "name"};
    static final String[] element_arrays = {"attributes","elements", "arrays", "extensions", "classes"};
    
    static QualifiedName schemaQName(String name ){
        return new QualifiedName(SchemaConstant.SCHEMA_NAMESPACE, name);
    }
    
    static Set buildQNameSet( List lst ){
        Set s = new HashSet();
        for(Iterator it = lst.iterator(); it.hasNext(); ){
            IQualifiedNamed qn = (IQualifiedNamed)it.next();
            s.add(qn.getQName());
        }
        return s;
    }
    
    static void checkNameList( String[] required_names, List qname_list, boolean with_namespace ){
        Set s = buildQNameSet(qname_list);
        for(int i=0; i<required_names.length; i++){
            QualifiedName qname = with_namespace?schemaQName(required_names[i]): new QualifiedName(required_names[i]);
            if(!s.contains(qname))
                throw new AssertionFailedError("required QName "+qname+" not found");
        }
    }
    
    Schema  schema;
    Map     elementMap = new HashMap();
    

    public SchemaForSchemaTest(String name) {
        super(name);
    }
    
    public void testElements(){
        Element element = schema.getElement(schemaQName("element"));
        
        List attribs = element.getAllAttributes();
        checkNameList(element_attribs, attribs, false);
        
        List arrays = element.getAllArrays();
        checkNameList(element_arrays, arrays, true);
        
    }

    public void testAttribute(){
    	
		CompositeMap schemaCm = new CompositeMap(null,SchemaConstant.SCHEMA_NAMESPACE,"schema");
		CompositeMap attributes = new CompositeMap(null,SchemaConstant.SCHEMA_NAMESPACE,"attributes");
		schemaCm.addChild(attributes);
		System.out.println("schemaCm:\n"+schemaCm.toXML());
		
        Element element = schema.getSchemaManager().getElement(attributes);
        assertNotNull(element);
        
        CompositeMap elementCm = new CompositeMap(null,SchemaConstant.SCHEMA_NAMESPACE,"element");
        CompositeMap newAttributes = new CompositeMap(null,SchemaConstant.SCHEMA_NAMESPACE,"attributes");
        elementCm.addChild(newAttributes);
        System.out.println("elementCm:\n"+elementCm.toXML());
        
        Element newElement = schema.getSchemaManager().getElement(newAttributes);
//        assertNull(newElement);
        assertNotNull(newElement);
    }
    
    protected void setUp() throws Exception {
        schema = SchemaManager.getSchemaForSchema();
        assertNotNull(schema);
        for(int i=0; i<elementNames.length; i++){
            Element elm = schema.getElement( schemaQName(elementNames[i]));
            if(elm==null)
                throw new AssertionFailedError("Can't get element "+elementNames[i]);
            elementMap.put(elementNames[i], elm);            
        }        
    }

}
