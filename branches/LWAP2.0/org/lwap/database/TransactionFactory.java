/*
 * Created on 2006-12-7
 */
package org.lwap.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.sql.DataSource;

import org.lwap.application.WebApplication;
import org.lwap.controller.MainService;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import uncertain.event.Configuration;
import uncertain.proc.IEntry;
import uncertain.proc.Procedure;
import uncertain.proc.ProcedureRunner;

public class TransactionFactory {

    public static final String KEY_CURRENT_CONNECTION = "_instance.java.sql.Connection";

    DataSource data_source;

    CompositeLoader composite_loader;

    UncertainEngine uncertainEngine;

    /**
     * @return the uncertainEngine
     */
    public UncertainEngine getUncertainEngine() {
        return uncertainEngine;
    }

    /**
     * @param uncertainEngine
     *            the uncertainEngine to set
     */
    public void setUncertainEngine(UncertainEngine uncertainEngine) {
        this.uncertainEngine = uncertainEngine;
    }

    public DataSource getDataSource() {
        return data_source;
    }

    public CompositeLoader getCompositeLoader() {
        return composite_loader;
    }

    /**
     * @param data_source
     *            A <code>javax.sql.DataSource</code> instance
     * @param composite_loader
     *            CompositeLoader to load transaction config
     */
    public TransactionFactory(DataSource data_source,
            CompositeLoader composite_loader) {
        this.data_source = data_source;
        this.composite_loader = composite_loader;
    }

    /**
     * perform database access
     * 
     * @param access_def
     *            a CompositeMap whose childs contain access config
     * @param params
     *            CompositeMap for input parameter
     * @param target
     *            CompositeMap to hold return values
     */
    public void databaseAccess(Connection conn, CompositeMap access_def,
            CompositeMap params, CompositeMap target, boolean commit)
            throws SQLException {
        if (access_def == null)
            throw new IllegalArgumentException("database access config is null");
        Collection childs = access_def.getChilds();
        if (childs != null)
            try {
                conn.setAutoCommit(false);
                DatabaseAccess.execute(childs, conn, params, target);
                if (commit)
                    conn.commit();
            } catch (SQLException ex) {
                if (conn != null)
                    try {
                        if (!conn.isClosed())
                            conn.rollback();
                    } catch (SQLException new_ex) {
                    }
                throw ex;
            }

    }

    public void databaseAccess(CompositeMap access_def, CompositeMap params,
            CompositeMap target) throws SQLException {
        Connection conn = null;
        try {
            conn = data_source.getConnection();
            databaseAccess(conn, access_def, params, target, true);
        } finally {
            if (conn != null)
                DBUtil.closeConnection(conn);
        }
    }

    public void databaseAccess(String file_name, CompositeMap params,
            CompositeMap target) throws IOException, SQLException {
        CompositeMap access_def = null;
        try {
            access_def = composite_loader.load(file_name);
        } catch (Exception se) {
            throw new IOException(se.getMessage());
        }
        if (access_def == null)
            throw new IllegalArgumentException("can't load file " + file_name);
        databaseAccess(access_def, params, target);
    }

    public void databaseAccess(CompositeMap access_def, CompositeMap params,
            CompositeMap target, ProcedureRunner runner) throws Exception {
        if (access_def == null)
            throw new IllegalArgumentException("database access config is null");
        Iterator it = access_def.getChildIterator();
        Connection conn = null;
        Procedure proc = null;
        if (it != null)
            try {
                conn = data_source.getConnection();
                conn.setAutoCommit(false);
                runner.getContext().put(MainService.KEY_CURRENT_CONNECTION,
                        conn);
                proc = new Procedure(uncertainEngine.getOcManager());
                while (it.hasNext()) {
                    CompositeMap item = (CompositeMap) it.next();
                    DatabaseAccess da = DatabaseAccess.getInstance(item);
                    if (da != null) {
                        DatabaseEntry entry = new DatabaseEntry(da, conn,
                                params, target, null);
                        entry.beginConfigure(item);
                        proc.addEntry(entry);
                    } else {
                        Object inst = uncertainEngine.getOcManager()
                                .createObject(item);
                        if (inst == null) {
                            proc.addChild(item);
                        } else {
                            if (inst instanceof IEntry) {
                                proc.addEntry((IEntry) inst);
                            }
                        }
                    }
                }
                runner.call(proc);
                if (runner.getException() != null) {
                    // runner.getException().printStackTrace();
                    throw (Exception) runner.getException();
                }
                conn.commit();
            } catch (Exception ex) {
                if (conn != null)
                    try {
                        if (!conn.isClosed())
                            conn.rollback();
                    } catch (SQLException new_ex) {
                    }
                throw ex;
            } finally {
                runner.getContext().put(MainService.KEY_CURRENT_CONNECTION,
                        null);
                if (conn != null)
                    try {
                        conn.close();
                    } catch (Exception ex) {
                    }
                if (proc != null)
                    proc.clear();
            }

    }

    /** execute with ProcedureRunner so other types action can be run */
    public void databaseAccess(ProcedureRunner runner, CompositeMap access_def,
            CompositeMap params, CompositeMap target) throws ServletException {
        boolean dump = access_def.getBoolean("Dump", false);
        CompositeMap context = runner.getContext();
        MainService service = MainService.getServiceInstance(context);
        WebApplication application = (WebApplication) service.getApplication();
        Configuration configuration = runner.getConfiguration();
        Iterator it = access_def.getChildIterator();
        Connection conn = null;
        Procedure proc = null;

        if (it != null)
            try {

                conn = data_source.getConnection();
                conn.setAutoCommit(false);

                context.put(KEY_CURRENT_CONNECTION, conn);
                proc = new Procedure(uncertainEngine.getOcManager());
                boolean rp = application.isRecordRerformance();
                PerformanceRecorder recorder = application
                        .getPerformanceRecorder();
                while (it.hasNext()) {
                    CompositeMap item = (CompositeMap) it.next();
                    if (dump) {
                        System.out.println("[DatabaseAccess] Inspecting "
                                + item.toXML());
                    }

                    DatabaseAccess da = DatabaseAccess.getInstance(item);
                    if (da != null) {
                        if (dump) {
                            System.out.println("[DatabaseAccess] Got entry "
                                    + da.getClass().getName());
                        }

                        if (rp) {
                            da.setOwner(service.getServiceName());
                            da.setPerformanceRecorder(recorder);
                        }
                        DatabaseEntry entry = new DatabaseEntry(da, conn,
                                params, target, service);
                        proc.addEntry(entry);
                    } else {
                        Object inst = configuration.getInstance(item);
                        if (inst == null) {
                            if (dump) {
                                System.out
                                        .println("[DatabaseAccess] Adding component as child CompositeMap");
                            }
                            proc.addChild(item);
                        } else {
                            if (inst instanceof IEntry) {
                                if (dump) {
                                    System.out
                                            .println("[DatabaseAccess] Adding entry"
                                                    + inst.getClass().getName());
                                }
                                proc.addEntry((IEntry) inst);
                            } else
                                uncertainEngine.getLogger().warning(
                                        "Unknown configuration: "
                                                + item.toXML());
                        }
                    }
                }
                runner.call(proc);
                if (runner.getLatestException() == null) {
                    // System.out.println("commited trasaction");
                    conn.commit();
                }
            } catch (Throwable ex) {
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException sex) {
                        sex.printStackTrace();
                    }
                }
                DBUtil.closeConnection(conn);
                throw new ServletException(ex);
            } finally {
                // runner.fireEvent("ConnectionClose", new Object[]{conn} );
                DBUtil.closeConnection(conn);
                context.remove(KEY_CURRENT_CONNECTION);
                if (proc != null)
                    proc.clear();
            }

    }

    public void execute(String file_name, CompositeMap params)
            throws IOException, SQLException {
        CompositeMap target = new CompositeMap();
        databaseAccess(file_name, params, target);
    }

    public CompositeMap query(String file_name, CompositeMap params)
            throws IOException, SQLException {
        CompositeMap target = new CompositeMap();
        databaseAccess(file_name, params, target);
        return target;
    }

    public CompositeMap query(DatabaseQuery query, CompositeMap parameters)
            throws SQLException {
        CompositeMap result = new CompositeMap("records");
        Connection conn = null;
        try {
            conn = data_source.getConnection();
            query.execute(conn, parameters == null ? result : parameters,
                    result);
        } finally {
            if (conn != null)
                DBUtil.closeConnection(conn);
        }
        return result;
    }

}
