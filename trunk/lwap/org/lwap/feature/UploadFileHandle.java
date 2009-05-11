/*
 * Created on 2005-11-28
 */
package org.lwap.feature;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oracle.sql.BLOB;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.lwap.application.WebApplication;
import org.lwap.controller.ControllerProcedures;
import org.lwap.controller.FormController;
import org.lwap.controller.IController;
import org.lwap.controller.MainService;
import org.lwap.database.DBUtil;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.core.ConfigurationError;
import uncertain.event.Configuration;
import uncertain.event.EventModel;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.proc.IFeature;
import uncertain.proc.ProcedureRunner;

/**
 * FileUpload
 * 
 * @author Zhou Fan
 * 
 */
public class UploadFileHandle implements IFeature, IController {

    private static final String PROMPT_UPLOAD_FILE_SIZE_EXCEED = "prompt.upload.file_size_exceed";

    public static final String PROMPT_UPLOAD_UNKNOWN_FILE_TYPE = "prompt.upload.unknown_file_type";

    public int Max_upload_size = 10000000;

    public int Max_memory_size = 10000000;

    public int Buffer_size = 500 * 1024;

    public String Encoding = "utf-8";
    
    public String SqlFile = "fnd_upload_file_type.data";

    boolean isSave = false;

    MainService service;

    FileItem fileItem;

    FormController formController;

    CompositeMap form_config;

    CompositeMap params;

    CompositeMap model;

    //Logger logger;

    HttpServletRequest request;

    public UploadFileHandle() {
        //logger = l;
    }

    public void setUsage(String u) {
        if ("save".equalsIgnoreCase(u))
            isSave = true;
        else
            isSave = false;
        // System.out.println("isSave:"+isSave);
    }

    public String getUsage() {
        return isSave ? "save" : "show";
    }

    public String getProcedureName() {
        // return
        // isSave?ControllerProcedures.FORM_POST:ControllerProcedures.BASE_SERVICE;
        return ControllerProcedures.FORM_POST;
    }

    public void setServiceInstance(MainService service_inst) {
        service = service_inst;
    }

    public int detectAction(HttpServletRequest request, CompositeMap context) {
        this.request = request;
        if (isSave && "post".equalsIgnoreCase(request.getMethod())) {
            return IController.ACTION_DETECTED;
        }
        return IController.ACTION_NOT_DETECTED;
    }

    /**
     * get attached form controller
     */
    public int attachTo(CompositeMap config, Configuration procConfig) {
        CompositeMap svcConfig = config.getRoot();
        if (isSave) {
            form_config = CompositeUtil.findChild(svcConfig, "form", "Name",
                    "UPLOAD_FORM");
            if (form_config == null)
                throw new ConfigurationError(
                        "can't find 'UPLOAD_FORM' in config");
            formController = (FormController) procConfig.getFeatureInstance(
                    form_config, FormController.class);
            if (formController == null)
                throw new ConfigurationError(
                        "file-upload should be configured after form element");
        }
        return IFeature.NORMAL;
    }

    public void onValidateInput(ProcedureRunner runner) throws Exception {
        
        // CompositeMap    params = runner.getContext().getChild("parameter");
        // Create a factory for disk-based file items
        FileItemFactory factory = new DiskFileItemFactory();
        // Create a new file upload handler
        ServletFileUpload up = new ServletFileUpload(factory);

        // FileUpload up = new FileUpload();
        up.setSizeMax(Max_upload_size);
        up.setHeaderEncoding(Encoding);
        // up.setHeaderEncoding(request.getCharacterEncoding());
        // up.setSizeMax(Max_memory_size);
        // up.setRepositoryPath(".");
        if (service == null)
            service = MainService.getServiceInstance(runner.getContext());
        model  = service.getModel();
        params = service.getParameters();
        
        List items = null;
        try {
            items = up.parseRequest(service.getRequest());
        } catch (FileUploadException ex) {
            ex.printStackTrace();
            formController.getForm().setErrorPrompt(null,
                    service.getLocalizedString(PROMPT_UPLOAD_FILE_SIZE_EXCEED));
            FormController
                    .setParameterValid(service.getServiceContext(), false);
            return;
        }
        Iterator i = items.iterator();
        while (i.hasNext()) {
            fileItem = (FileItem) i.next();
            // System.out.println(fileItem.getFieldName()+","+fileItem.getName());
            if (fileItem.getName() == null)
                continue;
            if (fileItem.isFormField()) {
                String name = fileItem.getFieldName();
                String value = fileItem.getString(Encoding);
                params.put(name, value);
            } else {
                File file = new File(fileItem.getName());
                String file_name = file.getName();
                // params.put("FILE_NAME",new
                // String(file_name.getBytes(),"utf-8"));
                params.put("FILE_NAME", file_name);
                // System.out.println("utf-8:"+new
                // String(file_name.getBytes(),"utf-8"));
                // System.out.println("iso8859_1:"+new
                // String(file_name.getBytes("iso8859_1"),"utf-8"));
                params.put("size", new Long(fileItem.getSize()));
                service.databaseAccess(this.SqlFile, params, model);                
                break;
            }
        }
        Object code = model.getObject("/model/FILEUPLOAD/@TYPE_CODE");
        if (code == null) {
            formController
                    .getForm()
                    .setErrorPrompt(
                            null,
                            service
                                    .getLocalizedString(PROMPT_UPLOAD_UNKNOWN_FILE_TYPE));
            FormController
                    .setParameterValid(service.getServiceContext(), false);
        } else {
            FormController.setParameterValid(service.getServiceContext(), true);
        }
    }

    public long writeBLOB(Connection conn, InputStream instream, String aid)
            throws Exception {

        conn.setAutoCommit(false);
        long size = 0;

        Statement st = null;
        ResultSet rs = null;
        OutputStream outstream = null;

        try {
            st = conn.createStatement();
            int b = st
                    .executeUpdate("update fnd_atm_attachment t set t.content = empty_blob() where t.attachment_id="
                            + aid);
            st.execute("commit");

            rs = st
                    .executeQuery("select content from fnd_atm_attachment t where t.attachment_id = "
                            + aid + " for update");
            if (!rs.next())
                throw new IllegalArgumentException("attachment_id not set");
            BLOB blob = ((oracle.jdbc.driver.OracleResultSet) rs).getBLOB(1);
            rs.close();

            if (blob == null) {
                System.out
                        .println("Warning: can't update fnd_atm_attachment.content for recrd "
                                + aid);
                return 0;
            }
            outstream = blob.getBinaryOutputStream(0);            
            int chunk = blob.getChunkSize();
            byte[] buff = new byte[chunk];
            int le;
            while ((le = instream.read(buff)) != -1) {
                outstream.write(buff, 0, le);
                size += le;
            }
            outstream.close();
            st.execute("commit");
            st.close();
            instream.close();
            conn.commit();
            return size;
        } finally {
            DBUtil.closeResultSet(rs);
            DBUtil.closeStatement(st);
        }

    }

    public void postDoAction(ProcedureRunner runner) throws Exception {
        if (fileItem == null || !isSave)
            return;
        CompositeMap context = runner.getContext();
        Object aid = service.getModel().getObject("/model/FILEUPLOAD/@RESULT");

        if (aid == null)
            throw new IllegalStateException("can't get attachment record id");
        Connection conn = null;
        try {
            conn = service.getConnection();
            InputStream in = fileItem.getInputStream();
            long size = writeBLOB(conn, in, aid.toString());
            fileItem.delete();
            params.put("success", "true");
        } finally {
            if (conn != null)
                conn.close();
        }
    }

    private String toUtf8String(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ((c >= 0) && (c <= 255)) {
                sb.append(c);
            } else {
                byte[] b;
                try {
                    b = Character.toString(c).getBytes("utf-8");
                } catch (Exception ex) {
                    System.out.println(ex);
                    b = new byte[0];
                }
                for (int j = 0; j < b.length; j++) {
                    int k = b[j];
                    if (k < 0) {
                        k += 256;
                    }
                    sb.append("%" + Integer.toHexString(k).toUpperCase());
                }
            }
        }
        return sb.toString();
    }

    public int onBuildOutputContent(ProcedureRunner runner) throws Exception {

        if (isSave)
            return EventModel.HANDLE_NORMAL;
        CompositeMap context = runner.getContext();
        ILogger logger = LoggingContext.getLogger(context, WebApplication.LWAP_APPLICATION_LOGGING_TOPIC);
        model = service.getModel();

        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        ReadableByteChannel rbc = null;
        WritableByteChannel wbc = null;
        OutputStream os = null;
        InputStream is = null;

        try {
            conn = service.getConnection();
            pst = conn
                    .prepareStatement("select m.content from fnd_atm_attachment m where m.attachment_id="
                            + service.getParameters().get("attachment_id"));
            rs = pst.executeQuery();
            if (!rs.next())
                return EventModel.HANDLE_NO_SAME_SEQUENCE;
            Blob content = rs.getBlob(1);
            if (content != null) {
                CompositeMap ft = model.getChild("FILE-TYPE");
                if (ft == null) {
                    logger.warning("Can't get file type from database record");
                    return EventModel.HANDLE_NO_SAME_SEQUENCE;
                }
                String mime_type = ft.getString("MIME_TYPE");
                HttpServletResponse response = service.getResponse();
                response.setLocale(java.util.Locale.CHINESE);
                if (mime_type != null) {
                    response.setContentType(mime_type);
                } else {
                    logger.warning("Can't get file mime type");
                }
                int sz = ft.getInt("FILE_SIZE", 0);
                String file_name = ft.getString("FILE_NAME");
                if (sz > 0)
                    response.setContentLength(sz);
                if (file_name != null) {
                    response.addHeader("Content-Disposition",
                            //"attachment; filename=" + toUtf8String(file_name));
                            "attachment; filename=\"" + toUtf8String(file_name) + "\"");
                }
                os = response.getOutputStream();
                is = content.getBinaryStream();
                rbc = Channels.newChannel(is);
                wbc = Channels.newChannel(os);
                // System.out.println("buffer size:"+Buffer_size);
                ByteBuffer buf = ByteBuffer.allocate(Buffer_size);
                int size = -1;
                while ((size = rbc.read(buf)) > 0) {
                    buf.position(0);
                    wbc.write(buf);
                    buf.clear();
                    os.flush();
                }
            }

        } finally {
            try {
                DBUtil.closeConnection(conn);
                DBUtil.closeResultSet(rs);
                DBUtil.closeStatement(pst);
                if (rbc != null)
                    rbc.close();
                if (pst != null)
                    pst.close();
                if (is != null)
                    is.close();
                if (os != null)
                    os.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return EventModel.HANDLE_NO_SAME_SEQUENCE;
    }

}
