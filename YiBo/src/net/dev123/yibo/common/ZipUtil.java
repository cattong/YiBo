package net.dev123.yibo.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.util.Log;

/**
 * @author Weiping Ye
 * @version 创建时间：2011-9-14 下午12:09:41
 **/
public class ZipUtil {
	private static final String TAG = ZipUtil.class.getSimpleName();

	protected ZipFile zippy;

	protected byte[] b;

	private String unzipFileTargetLocation;

	public ZipUtil() {
		b = new byte[2048];
	}

	/**
	 * For a given Zip file, process each entry.
	 * 
	 * @param fileName
	 *            unzip file name
	 * @param unZipTarget
	 *            location for unzipped file
	 */
	public void unZip(String fileName, String unZipTarget) {
		this.unzipFileTargetLocation = unZipTarget;
		try {
			zippy = new ZipFile(fileName);
			Enumeration all = zippy.entries();
			while (all.hasMoreElements()) {
				saveFile((ZipEntry) all.nextElement());
			}
		} catch (IOException err) {
			if (Constants.DEBUG) Log.e(TAG, "IO Error: ", err);
			return;
		}
	}

	protected void saveFile(ZipEntry e) throws IOException {
		String zipName = e.getName();
		if (zipName.endsWith("/")) {
			return;
		}
		int ix = zipName.lastIndexOf('/');
		if (ix > 0) {
			String dirName = zipName.substring(0, ix);
			String fileName=zipName.substring(ix+1,zipName.length());
			zipName=fileName;
			
		}
		if (Constants.DEBUG) Log.e(TAG, "Creating " + zipName);
		String targetFile = this.unzipFileTargetLocation;
		File file=new File(targetFile);
		if(!file.exists()) {
			file.mkdir();
		}
		FileOutputStream os = new FileOutputStream(this.unzipFileTargetLocation + zipName);
		InputStream is = zippy.getInputStream(e);
		int n = 0;
		while ((n = is.read(b)) > 0) {
			os.write(b, 0, n);
		}
		is.close();
		os.close();
	}
}
