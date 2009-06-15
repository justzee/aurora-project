/** CopyFile
 *  Created on 2009-5-7
 */
package org.lwap.siebelplugin;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.lwap.controller.ControllerProcedures;
import org.lwap.controller.FormController;
import org.lwap.controller.IController;
import org.lwap.controller.MainService;
import org.lwap.database.DBUtil;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.core.ConfigurationError;
import uncertain.event.Configuration;
import uncertain.event.EventModel;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.proc.IFeature;
import uncertain.proc.ProcedureRunner;

public class CopyFile implements IFeature, IController {

	// public String Srcfilepath;
	public String Destdir;

	public String Return_flag;

	MainService service;

	public int Buffer_size = 500 * 1024;

	// public String InputFilePath;

	// Logger mLogger;
	ILogger mLogger;
	ILogger mErrorLogger;

	public CopyFile() {

	}

	public String toString() {
		CompositeMap invoke = new CompositeMap("siebel",
				"org.lwap.siebelplugin", "attachment");
		invoke.put("destdir", Destdir);

		return invoke.toXML();
	}

	public void onPostDone(ProcedureRunner runner) throws Exception {
		CompositeMap context = runner.getContext();
		initLogger(context);
		mLogger.log(Level.FINE, context.toXML());
		mLogger.log(Level.INFO, "copy-file");
		mLogger.log(Level.INFO, "===================================");
		mLogger.log(Level.CONFIG, toString());
		if (service == null)
			service = MainService.getServiceInstance(runner.getContext());

		CompositeMap target = null;
		CompositeMap model = null;

		Return_flag = "N";

		if (service != null)
			model = service.getModel();
		else
			model = context.getRoot().getChild("model");
		if (model == null)
			model = context.getRoot().createChild("model");
		CompositeMap params = service.getParameters();
		if (Destdir == null)
			throw new ConfigurationError(
					"Must set 'destdir' attribute for invoke_method");

		if (Return_flag != null) {
			String t = TextParser.parse(Return_flag, context);
			target = (CompositeMap) model.getObject(t);
			if (target == null)
				target = model.createChildByTag(t);
		}

		String file_name = context.getObject("/parameter/@FILE_NAME")
				.toString();
		int dotIndex = file_name.indexOf(".");

		String fileName = file_name.substring(0, dotIndex);
		String subfix = file_name.substring(dotIndex);
		String siebelFile = fileName
		+ "_"+System.currentTimeMillis() + subfix;
		String uploadName = Destdir + File.separator + fileName
				+ "_"+System.currentTimeMillis() + subfix;

		SmbFile rmifile;

		try {
			rmifile = new SmbFile(uploadName);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Destfilepath '" + uploadName
					+ "' not exist");
		}
		try {
			if (!rmifile.exists())
				rmifile.createNewFile();
		} catch (SmbException e) {
			throw new IllegalArgumentException("Create File '"
					+ rmifile.getPath() + "' fail ");
		}

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		OutputStream os = null;
		InputStream is = null;

		try {
			conn = service.getConnection();
			String attachment_id = context.getObject(
					"/model/FILEUPLOAD/@RESULT").toString();
			// attachment_id ="1443";
			mLogger.log(Level.CONFIG, "attachment_id:" + attachment_id);
			pst = conn
					.prepareStatement("select m.content from fnd_atm_attachment m where m.attachment_id="
							+ attachment_id);
			rs = pst.executeQuery();
			if (!rs.next())
				throw new IllegalArgumentException("attachment_id '"
						+ attachment_id + "' not found ");
			Blob content = rs.getBlob(1);
			mLogger.log(Level.INFO, "blob size :" + content.length());

			if (content != null) {
				os = new BufferedOutputStream(new SmbFileOutputStream(rmifile));
				is = content.getBinaryStream();

				// os = new FileOutputStream("c:/1.doc");

				int c;
				int size = 0;

				// byte数组接受文件的数据
				byte[] buffer = new byte[50 * 1024];//
				// while ((c = is.read()) != -1)
				// os.write(c);
				Date date = new Date();

				while ((c = is.read(buffer)) != -1) {
					size += c;
					os.write(buffer, 0, c); // 读入流,保存在BYTe数组中

				}
				// 流的关闭:
				os.close();
				is.close();
				params.put("UPLOAD_FILE", siebelFile);
				Date end = new Date();
				int time = (int) ((end.getTime() - date.getTime()) / 1000);
				if (time > 0)
					mLogger.log(Level.INFO, "time:" + time + "second "
							+ "speed:" + size / time / 1024 + "kb/s");

			}

		} catch (IOException e) {
			os.close();
			is.close();
			DBUtil.closeConnection(conn);
			DBUtil.closeResultSet(rs);
			DBUtil.closeStatement(pst);
			mErrorLogger.severe(e.getMessage());
		}

		DBUtil.closeConnection(conn);
		DBUtil.closeResultSet(rs);
		DBUtil.closeStatement(pst);
		mLogger.log(Level.INFO, "copy File finished");

	}

	public void initLogger(CompositeMap context) {
		CompositeMap m = context.getRoot();
		mLogger = LoggingContext.getLogger(m, SiebelInstance.LOGGING_TOPIC);
		// mLogger = Logger.getLogger(SiebelInstance.LOGGING_TOPIC);
		mErrorLogger = LoggingContext.getErrorLogger(m);
	}

	public int attachTo(CompositeMap arg0, Configuration arg1) {
		return IFeature.NORMAL;
	}

	public int detectAction(HttpServletRequest arg0, CompositeMap arg1) {
		return IController.ACTION_DETECTED;
	}

	public String getProcedureName() {
		return ControllerProcedures.FORM_POST;
	}

	public void setServiceInstance(MainService service_inst) {
		service = service_inst;
	}

}
