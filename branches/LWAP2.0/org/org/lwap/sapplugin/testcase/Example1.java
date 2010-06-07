package org.lwap.sapplugin.testcase;

/**
 * Example1.java - call the rfc module 'STFC_CONNECTION' with custom definition
 * of function metadata. Please notice, the communication with static
 * metadata interface definitions is dangerous. The inconsistencies in the
 * interface definitions may cause corrupted data, errors while communication or
 * even application crashes. In Example2 you can see, how to avoid these problems.
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2003.
 * All rights reserved.
 */
import com.sap.mw.jco.*;

/**
 * Example1 - start a simple call with static metadata definition
 *
 * @version 1.0
 * @author  SAP AG, Walldorf
 */
public class Example1 {

  public static void main(String[] argv)
  {
    JCO.Client client = null;

    try {

      // Print the version of the underlying JCO library
      System.out.println("\n\nVersion of the JCO-library:\n" +
                             "---------------------------\n" + JCO.getMiddlewareVersion());

      // Create a client connection to a dedicated R/3 system
      client = JCO.createClient( "000",       // SAP client
                				 "johndoe",   // userid
                				 "*****",     // password
                				 "EN",        // language
                				 "appserver", // host name
                				 "00" );      // system number

      // Open the connection
      client.connect();

      // Get the attributes of the connection and print them

      JCO.Attributes attributes = client.getAttributes();
      System.out.println("Connection attributes:\n" +
                         "----------------------\n" + attributes);
      boolean is_backend_unicode = attributes.getPartnerCodepage().equals("4102") ||
                                   attributes.getPartnerCodepage().equals("4103");

      // Create metadata definition of the input parameter list
      JCO.MetaData input_md = new JCO.MetaData("INPUT");
      input_md.addInfo("REQUTEXT", JCO.TYPE_CHAR, 255, 255 * (is_backend_unicode? 2 : 1 ),
                        -1, 0, null, null, 0, null, null);

      // Create the input parameter list from the metadata object
      JCO.ParameterList input = JCO.createParameterList(input_md);

      // Set the first (and only) input parameter
      input.setValue("This is my first JCo example.", "REQUTEXT");

      // Create metadata definition of the output parameter list
      JCO.MetaData output_md = new JCO.MetaData("OUTPUT");

      // Specify the parameters types of the function will be returned
      output_md.addInfo("ECHOTEXT", JCO.TYPE_CHAR, 255, 255 * (is_backend_unicode? 2 : 1 ),
                         -1, 0, null, null, 0, null, null);
      output_md.addInfo("RESPTEXT", JCO.TYPE_CHAR, 255, 255 * (is_backend_unicode? 2 : 1 ),
                         -1, 0, null, null, 0, null, null);

      // Create the output parameter list from the metadata object
      JCO.ParameterList output = JCO.createParameterList(output_md);

      // Call the function
      client.execute("STFC_CONNECTION", input, output);

      // Print the result
      System.out.println("The function 'STFC_CONNECTION' returned the following parameters:\n" +
                         "-----------------------------------------------------------------");
      for (int i = 0; i < output.getFieldCount(); i++) {
          System.out.println("Name: " +  output.getName(i) + " Value: " + output.getString(i));
      }//for

      // All done
      System.out.println("\n\nCongratulations! It worked.");
    }
    catch (Exception ex) {
      System.out.println("Caught an exception: \n" + ex);
    }
    finally {
        // do not forget to close the client connection
        if (client != null) client.disconnect();
    }
  }
}
