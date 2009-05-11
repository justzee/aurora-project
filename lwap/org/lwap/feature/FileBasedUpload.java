/*
 * Created on 2007-7-26
 */
package org.lwap.feature;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.lwap.application.WebApplication;
import org.lwap.database.DBUtil;

import uncertain.composite.CompositeMap;
import uncertain.core.ConfigurationError;
import uncertain.event.EventModel;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.proc.ProcedureRunner;

public class FileBasedUpload extends UploadFileHandle {
    
    public static final String KEY_FILE_UPLOAD_PATH = "file-upload-path";
    
    String upload_path;
    
    public FileBasedUpload(WebApplication app) {
        super();
        upload_path = app.getApplicationConfig().getString(KEY_FILE_UPLOAD_PATH);
        if(upload_path==null) throw new ConfigurationError("[FileBasedUpload]:Must set 'file-upload-path' in application.xml");
        //System.out.println("upload to "+upload_path);
    }
    
    public File getFullPath(String attachment_id) throws Exception {
        Calendar c = Calendar.getInstance();
        String year = Integer.toString(c.get(Calendar.YEAR));
        String month = Integer.toString(c.get(Calendar.MONTH));
        File root = new File(upload_path);
        File dir = new File(root, year);
        if(!dir.exists()) 
            dir.mkdir();
        dir = new File(dir, month);
        if(!dir.exists())
            dir.mkdir();
        return new File(dir, attachment_id);
    }

    public long writeBLOB( Connection conn,InputStream instream,String aid) 
    throws Exception    {
        Statement stmt = null;
        try{
            long size = 0;
            int b;
            //Write file to disk
            FileOutputStream fos ;
            File file = getFullPath(aid);
            fos = new FileOutputStream(file);
            while(( b = instream.read())>=0){
                fos.write(b);
                size++;
            }
            fos.close();
            // Update attachment record
            stmt = conn.createStatement();
            stmt.executeUpdate("update fnd_atm_attachment a set a.full_path = '"+file.getPath()+"' where a.attachment_id = "+aid);
            conn.commit();
            return size;
        }finally{
            DBUtil.closeStatement(stmt);
        }
    }
    
    public int onBuildOutputContent(ProcedureRunner runner) throws Exception{
        
       if(isSave) return EventModel.HANDLE_NORMAL;
        CompositeMap    context = runner.getContext();
        ILogger logger = LoggingContext.getLogger(context, WebApplication.LWAP_APPLICATION_LOGGING_TOPIC);
        model = service.getModel();

        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        ReadableByteChannel rbc = null;
        WritableByteChannel wbc = null;
        OutputStream os = null;
        FileInputStream is = null;
        
        try{
            conn = service.getConnection();
            pst = conn.prepareStatement("select m.full_path from fnd_atm_attachment m where m.attachment_id=" + service.getParameters().get("attachment_id"));
            rs = pst.executeQuery();
            if(!rs.next()) return EventModel.HANDLE_NO_SAME_SEQUENCE;
            String path = rs.getString(1);
            
            if (path != null) {
                CompositeMap ft = model.getChild("FILE-TYPE");
                if(ft==null){
                    logger.warning("Can't get file type from database record");
                    return EventModel.HANDLE_NO_SAME_SEQUENCE;
                }
                String mime_type = ft.getString("MIME_TYPE");
                HttpServletResponse response = service.getResponse();
                if(mime_type!=null){
                    response.setContentType(mime_type);
                }else{
                    logger.warning("Can't get file mime type");
                }
                int sz = ft.getInt("FILE_SIZE",0);
                String file_name = ft.getString("FILE_NAME");
                if(sz>0)
                    response.setContentLength(sz);
                if(file_name!=null)
                response.addHeader("Content-Disposition", 
                        "attachment; filename="+file_name);

                os = response.getOutputStream();
                is = new FileInputStream(path);
                rbc = Channels.newChannel(is);
                wbc = Channels.newChannel(os);
                //System.out.println("buffer size:"+Buffer_size);
                ByteBuffer buf = ByteBuffer.allocate(Buffer_size);
                int size=-1;
                while( (size = rbc.read(buf))>0){
                    buf.position(0);
                    wbc.write(buf);
                    buf.clear();
                    os.flush();
                }
            }

        } finally{
            try{
                DBUtil.closeConnection(conn);
                DBUtil.closeResultSet(rs);
                DBUtil.closeStatement(pst);
                if(rbc!=null) rbc.close();
                if(pst!=null) pst.close();
                if(is!=null) is.close();
                if(os!=null) 
                    os.close();
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        return EventModel.HANDLE_NO_SAME_SEQUENCE;
    }
    
}
