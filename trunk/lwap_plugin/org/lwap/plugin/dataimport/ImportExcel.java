package org.lwap.plugin.dataimport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilderFactory;

import jxl.Cell;
import jxl.CellView;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.commons.io.FileUtils;
import org.lwap.controller.ControllerProcedures;
import org.lwap.controller.IController;
import org.lwap.controller.MainService;
import org.lwap.database.DBUtil;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.proc.ProcedureRunner;

/**
 * 
 * @version $Id$
 * @author <a href="mailto:njq.niu@hand-china.com">znjq</a>
 */
public class ImportExcel implements IController {

	private static final String DEFAULT_TARGET = "/parameter/import";

	private static final String DEFAULT_FAILED_RECORD = "/parameter/importFailed";

	private static final String DEFAULT_SUCCESS_FLAG = "/parameter/@ImportSuccess";

	private static final String DEFAULT_FILE_PATH = "/model/file/@path";

	private static final String HEADER_NAME = "_DataHeader";

	private static final String MAP_CHILD_NAME = "item";

	private static final String FILE_NAME = "fileName";

	private static final String DEFAULT_ERROR_FIELD = "_exception";

	private DiskFileItemFactory diskFileItemFactory = null;

	private ServletFileUpload upload = null;

	private Map fileMap = null;

	private String target = DEFAULT_TARGET;

	private String errorField = DEFAULT_ERROR_FIELD;

	private String success_flag = DEFAULT_SUCCESS_FLAG;

	private String failed_record = DEFAULT_FAILED_RECORD;

	private String file_path = DEFAULT_FILE_PATH;

	private String store_flag;
	
	private String session_id;

	private ImportSettings settings;

	private MainService service;

	public ImportExcel(ImportSettings settings) {
		this.settings = settings;
		// System.out.println("ImportExcel created");
	}

	public void onPrepareService(ProcedureRunner runner) throws Exception {
		 System.out.println(getProcedureName());
		try {
			CompositeMap context = runner.getContext();			
			HttpServletRequest request = service.getRequest();			
			session_id=TextParser.parse(session_id, context);
			// RequestContext requestContext = new
			// ServletRequestContext(request);
			// boolean isMultipart =
			// ServletFileUpload.isMultipartContent(requestContext);
			// if (isMultipart) {
			init();
			// System.out.println("init()");
			processUpload(request);
			// System.out.println("processUpload()");
			parseExcelFile(context);
			// System.out.println("parseExcelFile()");
			/*
			 * }else System.out.println("Upload content Not multipart");
			 */
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	private void processUpload(HttpServletRequest request) throws Exception {
		// Parse the request
		List items = upload.parseRequest(request);
		// Process the uploaded items
		Iterator iter = items.iterator();
		while (iter.hasNext()) {
			FileItem item = (FileItem) iter.next();
			if (!item.isFormField()) {
				String radomFileName = String.valueOf(System
						.currentTimeMillis());
				String returnString = formatPath(item.getName());
				String fileName = radomFileName.concat(returnString
						.substring(returnString.lastIndexOf(".")));
				// String fileType = item.getContentType();
				// System.out.println("processing "+radomFileName+" as "+formatPath(item.getName()));
				// System.out.println("file type:"+fileType);
				// if ("application/vnd.ms-excel".equals(fileType)) {
				File uploadedFile = new File(settings.getDestPath(), fileName);
				item.write(uploadedFile);
				fileMap.put(fileName, uploadedFile);
				// System.out.println("saving "+uploadedFile);
				// }
			}
		}
	}

	private void parseExcelFile(CompositeMap context) throws Exception {
		String suffix;
		Iterator it = fileMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String fileName = (String) entry.getKey();
			suffix = fileName.substring(fileName.lastIndexOf("."));
			File file = (File) entry.getValue();
			if (".xls".equalsIgnoreCase(suffix.toLowerCase())) {
				parseExcel2003(file);
			} 
		}
	}

	private void parseExcel2003(File file)throws Exception {
		InputStream is = null;
		FileInputStream fn = null;
		CompositeMap params = service.getParameters();
		boolean is_store = "Y".equals(store_flag)?true:false;
		try {	
			fn = new FileInputStream(file);
			is = fn;
			Workbook rb = Workbook.getWorkbook(is);
			Sheet s = rb.getSheet(0);
			int rs = s.getRows();			
			if (is_store) {
				Connection conn = service.getConnection();
				Statement st = conn.createStatement();				
				String sqlpre;
				String sql;
				int max_column=0;
				try {					
					st.executeUpdate("delete from fnd_import_temp where creation_date<sysdate-1 or session_id="+session_id);
					st.executeUpdate("delete from fnd_import_error_msg where creation_date<sysdate-1 or session_id="+session_id);
					st.executeUpdate("delete from fnd_import_status where creation_date<sysdate-1 or session_id="+session_id);
					for (int j = 0; j < rs; j++) {
						Cell[] c = s.getRow(j);
						int m = c.length;
						if(max_column<m)max_column=m;
						sqlpre = "insert into fnd_import_temp(creation_date,session_id,row_num";
						sql = ")values(sysdate,'"+session_id+"',"+j;
						for (int k = 0; k < m; k++) {							
							sqlpre += ",c" + (k+1);
							sql += ",'" + c[k].getContents()+"'";							
						}
						st.executeUpdate(sqlpre + sql + ")");
					}
					st.executeUpdate("insert into fnd_import_status(creation_date,session_id,max_row,max_col)values(sysdate,'"+session_id+"',"+rs+","+ max_column+")");
					st.execute("commit");
					conn.commit();
				} finally {
					try {
						DBUtil.closeConnection(conn);
						DBUtil.closeStatement(st);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			} else {	
				Cell[] headerCells = s.getRow(0);
				List headers = new ArrayList();
				for (int k = 0; k < headerCells.length; k++)
					headers.add(headerCells[k].getContents());
				params.put(HEADER_NAME, headers);
				CompositeMap dataMap = new CompositeMap();
				dataMap.put(FILE_NAME, file.getName());				
				for (int j = 1; j < rs; j++) {
					CompositeMap item = new CompositeMap(MAP_CHILD_NAME);
					Cell[] c = s.getRow(j);
					for (int x = 0; x < c.length; x++) {
						item.put(headerCells[x].getContents(), c[x]
								.getContents() == null ? "" : c[x]
								.getContents());
					}
					dataMap.addChild(item);
				}
				params.putObject(getTarget(), dataMap, true);				
			}
		} finally {
			try {
				fn.close();
				is.close();
			} catch (Exception e) {
				throw e;
			}
		}
	}

	private void init() throws IOException {
		fileMap = new HashMap();
		File repository = new File(settings.getTempPath());
		File destFile = new File(settings.getDestPath());

		FileUtils.forceMkdir(repository);
		FileUtils.forceMkdir(destFile);

		// Create a factory for disk-based file items
		diskFileItemFactory = new DiskFileItemFactory();
		diskFileItemFactory.setSizeThreshold(settings.getThresholdSize());
		diskFileItemFactory.setRepository(repository);

		// Create a new file upload handler
		upload = new ServletFileUpload(diskFileItemFactory);

		// Set overall request size constraint
		upload.setSizeMax(settings.getMaxSize());

		// Set upload header encoding
		upload.setHeaderEncoding(settings.getEncoding());
	}

	public void postDoAction(ProcedureRunner runner) throws Exception {

		CompositeMap context = runner.getContext();
		CompositeMap parameter = service.getParameters();
		Boolean isSuccess = (Boolean) context.getObject(getSuccess_flag());
		if (isSuccess != null && !isSuccess.booleanValue()) {
			List headers = (List) parameter.get(HEADER_NAME);
			CompositeMap sourceData = (CompositeMap) parameter
					.getObject(getTarget());
			CompositeMap errorData = (CompositeMap) parameter
					.getObject(getFailed_record());

			CompositeMap errorMap = new CompositeMap();
			String errorFileName = "error_" + sourceData.getString(FILE_NAME);
			errorMap.put(FILE_NAME, errorFileName);

			Iterator it = errorData.getChildIterator();
			while (it.hasNext()) {
				CompositeMap item = (CompositeMap) it.next();
				String error = item.getString(getErrorField());
				if (error != null && !"".equals(error.trim())) {
					errorMap.addChild(item);
				}
			}

			parameter.putObject(getTarget(), errorMap, true);
			generateExcel(headers, errorMap);

			String downPath = settings.getDownLoadPath();
			StringBuffer sb = new StringBuffer();
			if (!downPath.startsWith("/"))
				sb.append("/");
			sb.append(settings.getDownLoadPath());
			if (!downPath.endsWith("/"))
				sb.append("/");
			sb.append(errorFileName);
			context.putObject(getFile_path(), sb.toString(), true);
		}
	}

	/**
	 * Each file which was created 3 days ago will be deleted.
	 * 
	 * @param settings
	 */
	public void clearFile(ImportSettings settings) {
		Calendar deleteDay = Calendar.getInstance();
		deleteDay.set(Calendar.DAY_OF_YEAR, deleteDay.get(Calendar.DAY_OF_YEAR)
				- settings.getFileTimeOut());

		String destPath = settings.getDestPath();
		File dest = new File(destPath);
		if (dest.exists() && dest.isDirectory()) {
			File[] files = dest.listFiles();
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				String fileName = file.getName();
				String suffix = fileName.substring(
						fileName.lastIndexOf(".") + 1, fileName.length());
				if ("xls".equalsIgnoreCase(suffix)) {
					Date lastModifyDate = new Date(file.lastModified());
					if (lastModifyDate.before(deleteDay.getTime())) {
						file.delete();
					}
				}
			}
		}
	}

	private void generateExcel(List headers, CompositeMap dataMap)
			throws Exception {
		WritableWorkbook workbook = null;
		File file = null;
		try {
			String temp_dir = settings.getDestPath();

			file = new File(temp_dir, dataMap.getString(FILE_NAME));

			WorkbookSettings wbs = new WorkbookSettings();
			workbook = Workbook.createWorkbook(file, wbs);
			WritableSheet ws = workbook.createSheet("ErrorResult", 0);

			WritableFont headerWF = new WritableFont(WritableFont.ARIAL, 8,
					WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,
					jxl.format.Colour.BLACK);
			WritableCellFormat headerWCF = new WritableCellFormat(headerWF);
			headerWCF.setBackground(Colour.GRAY_25);
			headerWCF.setAlignment(Alignment.CENTRE);
			headerWCF.setBorder(Border.ALL, BorderLineStyle.THIN);
			Iterator headerIt = headers.iterator();
			int comlun = 0;
			while (headerIt.hasNext()) {
				String header = (String) headerIt.next();
				ws.addCell(new Label(comlun, 0, header, headerWCF));
				comlun++;
			}

			int column, row = 1;
			Iterator itemIt = dataMap.getChildIterator();
			while (itemIt.hasNext()) {
				column = 0;
				CompositeMap item = (CompositeMap) itemIt.next();
				Iterator headerKeyIt = headers.iterator();
				while (headerKeyIt.hasNext()) {
					String headerKey = (String) headerKeyIt.next();
					WritableFont itemWF = new WritableFont(WritableFont.ARIAL,
							8, WritableFont.NO_BOLD, false,
							UnderlineStyle.NO_UNDERLINE,
							jxl.format.Colour.BLACK);
					WritableCellFormat itemWCF = new WritableCellFormat(itemWF);
					itemWCF.setAlignment(Alignment.CENTRE);
					itemWCF.setWrap(false);
					itemWCF.setBorder(Border.ALL, BorderLineStyle.THIN);

					Label l = new Label(column, row, item.getString(headerKey),
							itemWCF);
					if (column == 0) {
						WritableCellFeatures cellFeatures = new WritableCellFeatures();
						cellFeatures
								.setComment(item.getString(getErrorField()));
						l.setCellFeatures(cellFeatures);
					}
					ws.addCell(l);
					column++;
				}
				row++;
			}

			CellView cf = new CellView();
			cf.setAutosize(true);
			int columns = ws.getColumns();
			for (int n = 0; n < columns; n++) {
				ws.setColumnView(n, cf);
			}

			workbook.write();
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				workbook.close();
			} catch (Exception e) {
				throw e;
			}
		}
	}

	private String formatPath(String filepath) {

		String returnstr = filepath;
		int length = filepath.trim().length();
		filepath = filepath.replace('\\', '/');
		if (length > 0) {
			int i = filepath.lastIndexOf("/");
			if (i >= 0) {
				returnstr = filepath.substring(i + 1);
			}
		}
		return returnstr;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getErrorField() {
		return errorField;
	}

	public void setErrorField(String errorField) {
		this.errorField = errorField;
	}

	public String getProcedureName() {
		return ControllerProcedures.PARAMETER_INPUT;
	}

	public int detectAction(HttpServletRequest request, CompositeMap context) {
		return IController.ACTION_DETECTED;
	}

	public void setServiceInstance(MainService service_inst) {
		this.service = service_inst;
	}

	public String getSuccess_flag() {
		return success_flag;
	}

	public void setSuccess_flag(String success_flag) {
		this.success_flag = success_flag;
	}

	public String getFailed_record() {
		return failed_record;
	}

	public void setFailed_record(String failed_record) {
		this.failed_record = failed_record;
	}

	public String getFile_path() {
		return file_path;
	}

	public void setFile_path(String file_path) {
		this.file_path = file_path;
	}

	public String getStore_flag() {
		return store_flag;
	}

	public void setStore_flag(String storeFlag) {		
		store_flag = storeFlag;
	}
	public String getSession_id() {
		return session_id;
	}

	public void setSession_id(String sessionId) {		
		session_id = sessionId;
	}
}
