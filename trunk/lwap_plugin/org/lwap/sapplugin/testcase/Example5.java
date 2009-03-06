package org.lwap.sapplugin.testcase;


/**
 * Example5.java - Simple implementation of an (external RFC) server. This example is based on
 * static metadata with non-unicode layout, so the calls only from non-unicode systems can be
 * handled.
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2003.
 * All rights reserved.
 */
import com.sap.mw.jco.*;
import java.util.*;

/**
 * @version 1.0
 * @author  SAP AG, Walldorf
 */

//******************************************************************************
public class Example5 implements JCO.ServerExceptionListener, JCO.ServerStateChangedListener {
//******************************************************************************

  /**
   *  Implementation of our own repository.
   *  Just dummy extend the basic repository that comes with the JCO package
   */
  static public class Repository extends JCO.BasicRepository implements IRepository {

    /**
     * Creates a new empty repository
     */
    public Repository(String name)
    {
      super(name);
    }
  }

  /** The repository we gonna be using */
  protected static IRepository repository;

  static {

    repository = new Repository("TestRepository");

    // non-unicode definition of functions. The server with this repository can
    // dispatch calls only from non-unicode systems

    //------------------------------------------------------------------------------
    //  Add function 'STFC_CONNECTION'
    //------------------------------------------------------------------------------
    JCO.MetaData fmeta = new JCO.MetaData("STFC_CONNECTION");
    fmeta.addInfo("REQUTEXT", JCO.TYPE_CHAR, 255,   0,  0, JCO.IMPORT_PARAMETER, null);
    fmeta.addInfo("ECHOTEXT", JCO.TYPE_CHAR, 255,   0,  0, JCO.EXPORT_PARAMETER, null);
    fmeta.addInfo("RESPTEXT", JCO.TYPE_CHAR, 255,   0,  0, JCO.EXPORT_PARAMETER, null);
    repository.addFunctionInterfaceToCache(fmeta);

    //------------------------------------------------------------------------------
    //  Add function 'STFC_STRUCTURE'
    //------------------------------------------------------------------------------
    fmeta = new JCO.MetaData("STFC_STRUCTURE");
    fmeta.addInfo("IMPORTSTRUCT", JCO.TYPE_STRUCTURE, 144, 0, 0, JCO.IMPORT_PARAMETER, "RFCTEST");
    fmeta.addInfo("ECHOSTRUCT",   JCO.TYPE_STRUCTURE, 144, 0, 0, JCO.EXPORT_PARAMETER, "RFCTEST");
    fmeta.addInfo("RESPTEXT",     JCO.TYPE_CHAR,      255, 0, 0, JCO.EXPORT_PARAMETER,  null    );
    fmeta.addInfo("RFCTABLE",     JCO.TYPE_TABLE,     144, 0, 0, 0,                    "RFCTEST");
    repository.addFunctionInterfaceToCache(fmeta);

    //------------------------------------------------------------------------------
    // Add the structure RFCTEST to the structure cache
    //------------------------------------------------------------------------------
    JCO.MetaData smeta  = new JCO.MetaData("RFCTEST");
    smeta.addInfo("RFCFLOAT",  JCO.TYPE_FLOAT,  8,  0, 0);
    smeta.addInfo("RFCCHAR1",  JCO.TYPE_CHAR,   1,  8, 0);
    smeta.addInfo("RFCINT2",   JCO.TYPE_INT2,   2, 10, 0);
    smeta.addInfo("RFCINT1",   JCO.TYPE_INT1,   1, 12, 0);
    smeta.addInfo("RFCICHAR4", JCO.TYPE_CHAR,   4, 13, 0);
    smeta.addInfo("RFCINT4",   JCO.TYPE_INT,    4, 20, 0);
    smeta.addInfo("RFCHEX3",   JCO.TYPE_BYTE,   3, 24, 0);
    smeta.addInfo("RFCCHAR2",  JCO.TYPE_CHAR,   2, 27, 0);
    smeta.addInfo("RFCTIME",   JCO.TYPE_TIME,   6, 29, 0);
    smeta.addInfo("RFRDATE",   JCO.TYPE_DATE,   8, 35, 0);
    smeta.addInfo("RFCDATA1",  JCO.TYPE_CHAR,   50,43, 0);
    smeta.addInfo("RFCDATA2",  JCO.TYPE_CHAR,   50,93, 0);
    repository.addStructureDefinitionToCache(smeta);
  }

  /**
   *  Implementation of my own server
   */
  static public class Server extends JCO.Server {

    /**
     *  Create an instance of my own server
     *  @param gwhost the gateway host
     *  @param gwserv the gateway service number
     *  @param progid the program id
     *  @param repository the repository used by the server to lookup the definitions of an inc
     */
    public Server(String gwhost, String gwserv, String progid, IRepository repository)
    {
      super(gwhost,gwserv,progid,repository);
    }

    /**
     *  Not really necessary to override this function but for demonstration purposes...
     */
    protected JCO.Function getFunction(String function_name)
    {
      JCO.Function function = super.getFunction(function_name);
      return function;
    }

    /**
     *  Not really necessary to override this method but for demonstration purposes...
     */
    protected boolean checkAuthorization(String function_name, int authorization_mode,
        String authorization_partner, byte[] authorization_key)
    {
      /* Simply allow everyone to invoke the services */
      return true;
    }

    /**
     *  Overrides the default method.
     *  Can handle only the two functions STFC_CONNECTION and STFC_STRUCTURE
     */
    protected void handleRequest(JCO.Function function)
    {
      JCO.ParameterList input  = function.getImportParameterList();
      JCO.ParameterList output = function.getExportParameterList();
      //JCO.ParameterList tables = function.getTableParameterList();

      System.out.println("handleRequest(" + function.getName() + ")");

      if (function.getName().equals("STFC_CONNECTION")) {
        output.setValue(input.getString("REQUTEXT"),"ECHOTEXT");
        output.setValue("This is a response from Example5.java","RESPTEXT");
      }
      else if (function.getName().equals("STFC_STRUCTURE")) {
        JCO.Structure sin  = input.getStructure("IMPORTSTRUCT");
        JCO.Structure sout = (JCO.Structure)sin.clone();
        try {
          System.out.println(sin);
        }
        catch (Exception ex) {
          System.out.println(ex);
        }
        output.setValue(sout,"ECHOSTRUCT");
        output.setValue("This is a response from Example5.java","RESPTEXT");
      }//if
    }
  }

  /** List of servers */
  JCO.Server srv[] = new JCO.Server[2];

  /**
   *  Constructor
   */
  public Example5()
  {
    // Yes, we're interested in server exceptions
    JCO.addServerExceptionListener(this);

    // And we also want to know when the server(s) change their states
    JCO.addServerStateChangedListener(this);
  }

  /**
   *  Start the server
   */
  public void startServers()
  {
    // Server 1 listens for incoming requests from system 1
    // (Change gateway host, service, and program ID according to your needs)
    srv[0] = new Server("gwhost1","gwserv00","JCOSERVER01",repository);
    // Server 2 listens for incoming requests from system 2
    // (Change gateway host, service, and program ID according to your needs)
    srv[1] = new Server("gwhost2","gwserv00","JCOSERVER02",repository);

    for (int i = 0; i < srv.length; i++) {
      try {
        srv[i].setTrace(true);
        srv[i].start();
      }
      catch (Exception ex) {
        System.out.println("Could not start server " + srv[i].getProgID() + ":\n" + ex);
      }//try
    }//for
  }

  /**
   *  Simply prints the text of the exception and a stack trace
   */
  public void serverExceptionOccurred(JCO.Server server, Exception ex)
  {
    System.out.println("Exception in server " + server.getProgID() + ":\n" + ex);
    ex.printStackTrace();
  }

  /**
   *  Simply prints server state changes
   */
  public void serverStateChangeOccurred(JCO.Server server, int old_state, int new_state)
  {
    System.out.print("Server " + server.getProgID() + " changed state from [");
    if ((old_state & JCO.STATE_STOPPED    ) != 0) System.out.print(" STOPPED ");
    if ((old_state & JCO.STATE_STARTED    ) != 0) System.out.print(" STARTED ");
    if ((old_state & JCO.STATE_LISTENING  ) != 0) System.out.print(" LISTENING ");
    if ((old_state & JCO.STATE_TRANSACTION) != 0) System.out.print(" TRANSACTION ");
    if ((old_state & JCO.STATE_BUSY       ) != 0) System.out.print(" BUSY ");

    System.out.print("] to [");
    if ((new_state & JCO.STATE_STOPPED    ) != 0) System.out.print(" STOPPED ");
    if ((new_state & JCO.STATE_STARTED    ) != 0) System.out.print(" STARTED ");
    if ((new_state & JCO.STATE_LISTENING  ) != 0) System.out.print(" LISTENING ");
    if ((new_state & JCO.STATE_TRANSACTION) != 0) System.out.print(" TRANSACTION ");
    if ((new_state & JCO.STATE_BUSY       ) != 0) System.out.print(" BUSY ");
    System.out.println("]");
  }

  public static void main(String[] argv)
  {
    Example5 obj = new Example5();
    obj.startServers();
  }
}
