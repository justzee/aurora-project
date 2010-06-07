package org.lwap.plugin.dataimport;

import uncertain.core.IGlobalInstance;

/**
 * ImportSettings
 * 
 * @version $Id$
 * @author <a href="mailto:njq.niu@hand-china.com">znjq</a>
 */
public class ImportSettings implements IGlobalInstance {

	private static final int DEFAULT_UPLOAD_MAX_SIZE = 10485760;

	private static final int DEFAULT_UPLOAD_THRESHOLD_SIZE = 4096;
	
	private static final int DEFAULT_FILE_TIME_OUT = 3;

	private static final String DEFAULT_UPLOAD_HEADER_ENCODING = "UTF-8";

	private static final String DEFAULT_DEST_PATH = ".";

	private static final String DEFAULT_TEMP_PATH = ".";

	private int maxSize = DEFAULT_UPLOAD_MAX_SIZE;

	private int thresholdSize = DEFAULT_UPLOAD_THRESHOLD_SIZE;

	private String encoding = DEFAULT_UPLOAD_HEADER_ENCODING;

	private String tempPath = DEFAULT_TEMP_PATH;

	private String destPath = DEFAULT_DEST_PATH;
	
	private String downLoadPath = "/";
	
	private int fileTimeOut = DEFAULT_FILE_TIME_OUT;
	
	public ImportSettings() {
	}

	public String getDestPath() {
		return destPath;
	}

	public void setDestPath(String destPath) {
		this.destPath = destPath;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public String getTempPath() {
		return tempPath;
	}

	public void setTempPath(String tempPath) {
		this.tempPath = tempPath;
	}

	public int getThresholdSize() {
		return thresholdSize;
	}

	public void setThresholdSize(int thresholdSize) {
		this.thresholdSize = thresholdSize;
	}

	public String getDownLoadPath() {
		return downLoadPath;
	}

	public void setDownLoadPath(String downLoadPath) {
		this.downLoadPath = downLoadPath;
	}

	public int getFileTimeOut() {
		return fileTimeOut;
	}

	public void setFileTimeOut(int fileTimeOut) {
		this.fileTimeOut = fileTimeOut;
	}

}
