package org.lwap.sapplugin.testcase;

/**
 * Example2.java
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2003.
 * All rights reserved.
 */
import com.sap.mw.jco.*;

/**
 * @version 1.0
 * @author  SAP AG, Walldorf
 */
public class Example2 {

  // The MySAP.com system we gonna be using
  static final String SID = "ID0";

  // The repository we will be using
  IRepository repository;

  public Example2()
  {
    try {
      // Add a connection pool to the specified system
      //    The pool will be saved in the pool list to be used
      //    from other threads by JCO.getClient(SID).
      //    The pool must be explicitely removed by JCO.removeClientPool(SID)
      JCO.addClientPool( "ID0",         // Alias for this pool
                         2,          // Max. number of connections
                         "800",       // SAP client
                         "seacat.zhou",   // userid
                         "handhand",     // password
                         "EN",        // language
                         "192.168.11.11", // host name
                         "00" );

      // Create a new repository
      //    The repository caches the function and structure definitions
      //    to be used for all calls to the system SID. The creation of
      //    redundant instances cause performance and memory waste.
      repository = JCO.createRepository("MYRepository", SID);
    }
    catch (JCO.Exception ex) {
    	ex.printStackTrace();
    }
  }

  // Retrieves and prints information about the remote system
  public void systemInfo()
  {
    try {

      // Get a function template from the repository
      IFunctionTemplate ftemplate = repository.getFunctionTemplate("RFC_SYSTEM_INFO");

      // if the function definition was found in backend system
      if(ftemplate != null) {

        // Create a function from the template
        JCO.Function function = ftemplate.getFunction();

        // Get a client from the pool
        JCO.Client client = JCO.getClient(SID);

        // We can call 'RFC_SYSTEM_INFO' directly since it does not need any input parameters
        client.execute(function);

        // The export parameter 'RFCSI_EXPORT' contains a structure of type 'RFCSI'
        JCO.Structure s = function.getExportParameterList().getStructure("RFCSI_EXPORT");

        // Use enumeration to loop over all fields of the structure
        System.out.println("System info for " + SID + ":\n" +
        				   "--------------------");

        for (JCO.FieldIterator e = s.fields(); e.hasMoreElements(); ) {
          JCO.Field field = e.nextField();
          System.out.println(field.getName() + ":\t" + field.getString());
        }//for

        System.out.println("\n\n");

        // Release the client into the pool
        JCO.releaseClient(client);
      }
      else {
	    System.out.println("Function RFC_SYSTEM_INFO not found in backend system.");
	  }
    }
	catch (Exception ex) {
	  ex.printStackTrace();
	}

  }

  // Retrieves and displays a sales order list
  public void salesOrders()
  {
	JCO.Client client = null;

    try {
      // Get a function template from the repository
      IFunctionTemplate ftemplate = repository.getFunctionTemplate("BAPI_SALESORDER_GETLIST");

      // if the function definition was found in backend system
      if(ftemplate != null) {

		// Create a function from the template
		JCO.Function function = ftemplate.getFunction();

		// Get a client from the pool
		client = JCO.getClient(SID);

		// Fill in input parameters
		JCO.ParameterList input = function.getImportParameterList();

		input.setValue("0000001200", "CUSTOMER_NUMBER"   );
		input.setValue(      "1000", "SALES_ORGANIZATION");
		input.setValue(         "0", "TRANSACTION_GROUP" );

		// Call the remote system
		client.execute(function);

		// Print return message
		JCO.Structure ret = function.getExportParameterList().getStructure("RETURN");
		System.out.println("BAPI_SALES_ORDER_GETLIST RETURN: " + ret.getString("MESSAGE"));

		// Get table containing the orders
		JCO.Table sales_orders = function.getTableParameterList().getTable("SALES_ORDERS");

		// Print results
		if (sales_orders.getNumRows() > 0) {

		  // Loop over all rows
		  do {

		    System.out.println("-----------------------------------------");

		    // Loop over all columns in the current row
		    for (JCO.FieldIterator e = sales_orders.fields(); e.hasMoreElements(); ) {
			  JCO.Field field = e.nextField();
			  System.out.println(field.getName() + ":\t" + field.getString());
		    }//for
		  } while(sales_orders.nextRow());

		}
		else {
		  System.out.println("No results found");
		}//if
      }
      else {
	    System.out.println("Function BAPI_SALESORDER_GETLIST not found in backend system.");
	  }//if
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    finally {
	  // Release the client to the pool
      JCO.releaseClient(client);
	}
  }

  protected void cleanUp() {
    JCO.removeClientPool(SID);
  }

  public static void main(String[] argv)
  {
    Example2 e = new Example2();
    e.systemInfo();
    e.salesOrders();
    e.cleanUp();
  }
}
