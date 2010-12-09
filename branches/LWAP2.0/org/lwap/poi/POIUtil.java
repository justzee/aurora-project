package org.lwap.poi;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class POIUtil {
public static final int seed =1000000;
	public static final String filetype =".xls";
	public static String getFileName(String path) {
		String filename = getRandomFileName();
		File file = new File(path, filename);
		if (file.exists())
			return getFileName(path);
		else
			return filename;

	}

	public static String getRandomFileName() {
		Random random = new Random();
		SimpleDateFormat dateformat1 = new SimpleDateFormat("yyyyMMdd");
		String a1 = dateformat1.format(new Date());
		return a1 + "x" + random.nextInt(seed)+filetype;
	}
}
