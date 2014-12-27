/*
 * Created on 2014年12月23日 下午8:41:54
 * $Id$
 */
package pipe.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import pipe.base.IEndPoint;

public class LogReturnProcessor implements IEndPoint {

    FileWriter writer;

    public LogReturnProcessor() throws IOException {
    }

    @Override
    public void process(Object data) {
        LogRecord r = (LogRecord) data;
        r.content += " [processed by " + Thread.currentThread().getName() + "]\r\n";
        try {
            writer.write(r.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // System.out.println(r.toString());
    }

    public void start() {
        try {
            writer = new FileWriter(new File("/Users/zhoufan/logs/output"
                    + new Date() + ".log"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void stop() {
        try {
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
