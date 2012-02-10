/*
 * Created on 2011-7-12 下午12:50:12
 * $Id$
 */
package aurora.database.actions;

import java.sql.ResultSet;

import uncertain.composite.CompositeMap;
import aurora.database.IResultSetConsumer;
import aurora.database.IResultSetProcessor;

/**
 * Save ResultSet into context instead of fetch resultset at once.
 * Implements IResultSetConsumer for compatibility
 */
public class ResultSetSaver implements IResultSetConsumer, IResultSetProcessor {
    
    CompositeMap        context;
    String              path;

    /**
     * @param context
     * @param path
     */
    public ResultSetSaver(CompositeMap context, String path) {
        this.context = context;
        this.path = path;
    }

    public void processResultSet(ResultSet rs) {
        context.put(path, rs);
    }

    public void begin(String root_name) {
        throw new IllegalStateException("Should not be called");
    }

    public void newRow(String row_name) {
 
    }

    public void loadField(String name, Object value) {

    }

    public void endRow() {

    }

    public void end() {

    }

    public void setRecordCount(long count) {

    }

    public Object getResult() {
        return null;
    }

}
