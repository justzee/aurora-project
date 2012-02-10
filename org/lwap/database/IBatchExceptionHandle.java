/*
 * Created on 2007-6-21
 */
package org.lwap.database;

import java.sql.SQLException;

import uncertain.composite.CompositeMap;

/**
 * 
 * IBatchExceptionHandle
 * @author Zhou Fan
 *
 */
public interface IBatchExceptionHandle {
    
    /**
     * Handle a exception on batch update
     * @param row number of row in batch source list, starting from 0
     * @param param input parameter that causes this exception
     * @param exp SQLException thrown
     * @return true if this exception will be handled, so this batch update is considered success;
     * false if the total batch update is failure
     */
    public boolean handleException(int row, CompositeMap param, SQLException exp );
    
    public void batchBegin();
    
    public void batchEnd();

}
