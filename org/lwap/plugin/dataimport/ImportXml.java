/*
 * Created on 2007-6-28
 */
package org.lwap.plugin.dataimport;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;

import org.lwap.database.TransactionFactory;

import uncertain.composite.CompositeMap;
import uncertain.proc.ProcedureRunner;

/**
 * Used together with org.lwap.plugin.dataimport.ImportXML, to implement XML import feature
 * ImportXml
 * @author Zhou Fan
 *
 */
public class ImportXml {
    
    public String LogFile;
    
    TransactionFactory  factory;
    
    /**
     * @param factory
     */
    public ImportXml(TransactionFactory factory) {
        this.factory = factory;
    }
/*
    public void onGetFileName(ProcedureRunner runner){
       runner.setContextField("FileName", FileName);         
    }
  */  
    public void onDoImport(ProcedureRunner runner) throws Exception {
       CompositeMap context = runner.getContext();
       CompositeMap action = context.getChild("action");
       if(action==null) throw new IllegalArgumentException("Must specifiy <action> in config file");
       factory.databaseAccess(action, context, context, runner);
    }
    
    public void onWriteErrorLog(ProcedureRunner runner) throws IOException {
        Throwable thr = runner.getException();
        LogFile = runner.getContext().getString("LogFile");
        if(LogFile!=null){
            FileOutputStream fos = new FileOutputStream(LogFile, true);
            PrintStream ps = new PrintStream(fos);
            ps.println(new Date()+":");
            thr.printStackTrace(ps);
            ps.println();
            ps.flush();
            fos.close();
        }
    }

}
