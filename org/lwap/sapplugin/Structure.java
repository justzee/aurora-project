package org.lwap.sapplugin;

import com.sap.mw.jco.JCO;

import uncertain.composite.CompositeMap;
import uncertain.logging.ILogger;

public class Structure {
    
    public static final String IMPORT = "import";
    public static final String EXPORT = "export";    
    
    /** Name of Structure */
    public String   Name;    
    
    public String   Target;
    
    /**
     * Type of Structure, 'export' or 'import'
     */
    public String   Type = IMPORT;   
    
    ILogger              logger;    
    
    boolean Dump = true;
    
    FieldMapping[]  field_mappings;   
    
    public void setLogger( ILogger logger ){
        this.logger = logger;
    }
    
    public void setFieldMappings(FieldMapping[] m){
        field_mappings = m;       
    }
    
    public FieldMapping[] getFieldMappings(){
        return field_mappings;
    }
    
    public boolean isImport(){
        return IMPORT.equalsIgnoreCase(Type);
    }   
    
    public JCO.Structure getJCOStructure(JCO.ParameterList list){
        try{
            JCO.Structure structure = list.getStructure(Name);
            if(structure==null) 
                throw new IllegalArgumentException("Structure '"+Name+"' doesn't exist");
            return structure;
        }catch(Throwable t){
            throw new IllegalArgumentException("Can't get Structure '"+Name+"':"+t.getMessage());
        }
    }
   
    public void fillJCOStructure(JCO.Structure structure,  CompositeMap context){
    	if(Dump){
            logger.info("ABAP Structure " + Name);
        }
    	if(field_mappings!=null){
			for(int i=0;i<field_mappings.length;i++){
				FieldMapping fieldMapping=field_mappings[i];
				Object value=context.getObject(fieldMapping.Source_field);
				if(value==null)
					value=fieldMapping.Value;
				structure.setValue(value, fieldMapping.Name);
				if(Dump){
		            logger.info(fieldMapping.Name+" -> " + value);
		        }
			}  
    	}
    	if(Dump){
            logger.info("\r\nStructure transfered");
        }		    
    }
  
    public CompositeMap fillCompositeMap(JCO.Structure records, CompositeMap result){    	
    	for(int i=0;i<records.getFieldCount();i++){
    		Object value=records.getValue(i);    		
    		result.put(records.getField(i), value);
    	}        
        return result;
    }       
}
