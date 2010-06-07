package org.lwap.sapplugin.testcase;
import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;

public class SapCurrencyAmount {
    // The MySAP.com system we gonna be using

  
    public static String getLocalCurrencyAmount(String SID,String userid,String password,String ip,String lang,String mandt,String date,String foreign_currency,String foreign_amount,String local_currency,String type_of_rate)    {
    //String SID = "DEV"; //DEV
    IRepository repository=null;
    
        try {
            // Add a connection pool to the specified system
            //    The pool will be saved in the pool list to be used
            //    from other threads by JCO.getClient(SID).
            //    The pool must be explicitely removed by JCO.removeClientPool(SID)
            JCO.addClientPool(SID,         // Alias for this pool
                                                10,          // Max. number of connections
                                                mandt,       // SAP client
                                                userid,   // userid
                                                password,     // password
                                                lang,        // language
                                                ip, // host name
                                                "00" );

            // Create a new repository
            //    The repository caches the function and structure definitions
            //    to be used for all calls to the system SID. The creation of
            //    redundant instances cause performance and memory waste.
            repository = JCO.createRepository("MYRepository", SID);
        }
        catch (JCO.Exception ex) {
            System.out.println("Caught an exception: \n" + ex);
        }

        JCO.Client client = null;
      // boolean iErr = false;
      //float localamount;
    String localamount;
        try {
            // Get a function template from the repository
            IFunctionTemplate ftemplate = repository.getFunctionTemplate("ZCONVERT_TO_LOCAL_CURRENCY");

            // if the function definition was found in backend system
            if(ftemplate != null) {

                // Create a function from the template
                JCO.Function function = ftemplate.getFunction();

                // Get a client from the pool
                client = JCO.getClient(SID);
                //
                JCO.ParameterList input  = function.getImportParameterList();
                JCO.ParameterList output = function.getExportParameterList();
  
                //  String s_client  = input.getStructure("CLIENT");
                //  JCO.Structure s_client=null;
                input.setValue(mandt,"CLIENT");
                input.setValue(date,"DATE");
                input.setValue(foreign_amount,"FOREIGN_AMOUNT");
                input.setValue(foreign_currency,"FOREIGN_CURRENCY");
                input.setValue(local_currency,"LOCAL_CURRENCY");
                input.setValue(type_of_rate,"TYPE_OF_RATE");
              
                // Call the remote system
                client.execute(function);

        /*      s_client.clear();
                s_foreign_amount.clear();
                s_foreign_currency.clear();
                s_local_currency.clear();
*/
                //localamount= Float.parseFloat(output.getString("LOCAL_AMOUNT")+"");
        localamount= output.getString("LOCAL_AMOUNT") ;
                JCO.removeClientPool(SID);

              return localamount;
            }
            else {
                System.out.println("Function ZCONVERT_TO_LOCAL_CURRENCY not found in backend system.");
            }//if
        }

        catch (Exception ex) {
            System.out.println("Caught an exception: \n" + ex);
        }
        finally {
            // Release the client to the pool
            JCO.releaseClient(client);
        }
    return "0";
    }

  public static void main(String[] argv)
    {
        System.out.println("result is :"+
        SapCurrencyAmount.getLocalCurrencyAmount("DEV","handlyx","handlyx","192.168.0.240","EN","400","20060502","USD","200","CNY","M"));   
    }
}


