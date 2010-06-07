/**
 * Example7.java
 */

package org.lwap.sapplugin.testcase;

import com.sap.mw.jco.*;

/**
 * Example of a JCo server with transaction processing
 * This is an example of the simplest implementation
 * of a JCo dual server. It uses dynamic repositories
 * for each of the servers.
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2003.
 * All rights reserved.
 *
 * @version  1.1
 * @author   SAP AG, Walldorf
 */

//******************************************************************************
public class Example7 implements JCO.ServerExceptionListener, JCO.ServerErrorListener {
//******************************************************************************

	/**
	 *  This is the actual Server (Listener) object
	 */
	static public class Server extends JCO.Server {

		/**
		 *  Simple constructor. Just call superclass to initialize everything
		 *  properly.
		 */
		public Server(String gwhost, String gwserv, String program_id, IRepository repos)
		{
			super(gwhost, gwserv, program_id, repos);
		}

		/**
		 *  This function will be invoked when a transactional RFC is being called from a
		 *  SAP R/3 system. The function has to store the TID in permanent storage and return <code>true</code>.
		 *  The method has to return <code>false</code> if the a transaction with this ID has already
		 *  been process. Throw an exception if anything goes wrong. The transaction processing will be
		 *  aborted thereafter.<b>
		 *  Derived servers must override this method to actually implement the transaction ID management.
		 *  @param tid the transaction ID
		 *  @return <code>true</code> if the ID is valid and not in use otherwise, <code>false</code> otherwise
		 */
		protected boolean onCheckTID(String tid)
		{
			return true;
		}

		/**
		 *  This function will be called after the <em>local</em> transaction has been completed.
		 *  All resources assiciated with this TID can be released.<b>
		 *  Derived servers must override this method to actually implement the transaction ID management.
		 *  @param tid the transaction ID
		 */
		protected void onConfirmTID(String tid)
		{
		}

		/**
		 *  This function will be called after <em>all</em> RFC functions belonging to a certain transaction
		 *  have been successfully completed. <b>
		 *  Derived servers can override this method to locally commit the transaction.
		 *  @param tid the transaction ID
		 */
		protected void onCommit(String tid)
		{
		}

		/**
		 *  This function will be called if an error in one of the RFC functions belonging to
		 *  a certain transaction has occurred.<b>
		 *  Derived servers can override this method to locally rollback the transaction.
		 *  @param tid the transaction ID
		 */
		protected void onRollback(String tid)
		{
		}

		/**
		 *  Called upon an incoming requests
		 */
		protected void handleRequest(JCO.Function function)
		{
			// Process incoming requests
			if (function.getName().equals("STFC_CONNECTION")) {
				// Do your processing here

				// For now we just dump the function to a HTML file
				// which can be viewed nicely in a browser
				function.writeHTML(function.getName() + ".html");
			}

			// This will cause a short-dump in R/3 that indicates that we cannot
			// handle the request.
			else {
				// Otherwise
				throw new JCO.AbapException("NOT_SUPPORTED","This service is not implemented by the external server");
			}
		}
	}

	/**
	 *  Called if an exception was thrown anywhere in our server
	 */
	public void serverExceptionOccurred(JCO.Server srv, Exception ex)
	{
		System.out.println("Exception in Server " + srv.getProgID() + ":\n" + ex);
		ex.printStackTrace();
	}

	/**
	 *  Called if an error was thrown anywhere in our server
	 */
	public void serverErrorOccurred(JCO.Server srv, Error err)
	{
		System.out.println("Error in Server " + srv.getProgID() + ":\n" + err);
		err.printStackTrace();
	}

	// System IDs of the system that we gonna using be for dictionary calls
	String POOL_A = "SYSTEM_A";
	String POOL_B = "SYSTEM_B";

	// The server objects that actually handles the request
	int MAX_SERVERS = 2;
	Server servers[] = new Server[MAX_SERVERS];

	/**
	 *  Constructor. Creates a client pool, the repository and a server.
	 */
	public Example7()
	{

		IRepository repository;

		// Add a connection pool to a remote R/3 system A.
		// We will use this connected to dynamically
		// request dictionary information for incoming function calls.
		// !!! Please, fill in the necessary login and system parameters !!!
		JCO.addClientPool(POOL_A,3,"000","user","password","EN","system_a","01");

		// Create repository for System A
		repository = JCO.createRepository("SYSTEM_A", POOL_A );

		// Create a new server and register it with system A
		servers[0] = new Server("SystemA", "sapgw01", "SERVER_A", repository);

		// Add a connection pool to a remote R/3 system B.
		// We will use this connected to dynamically
		// request dictionary information for incoming function calls.
		// !!! Please, fill in the necessary login and system parameters !!!
		JCO.addClientPool(POOL_B,3,"000","user","password","EN","system_b","02");

		// Create repository for system B
		repository = JCO.createRepository("SYSTEM_B", POOL_B );

		// Create a new server and register it with system B
		servers[1] = new Server("SystemB", "sapgw02", "SERVER_B", repository);

		// Register ourselves such that we get exceptions from the servers
		JCO.addServerExceptionListener(this);

		// Register ourselves such that we get errors from the servers
		JCO.addServerErrorListener(this);
	}

	/**
	 *  Start the server
	 */
	public void startServers()
	{
		try {
			for (int i = 0; i < MAX_SERVERS; i++) servers[i].start();
		}
		catch (Exception ex) {
			System.out.println("Could not start servers !\n" + ex);
		}//try
	}

	/**
	 *  Simple main program driver
	 */
	public static void main(String[] argv)
	{
		Example7 obj = new Example7();
		obj.startServers();
	}
}

