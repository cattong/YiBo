package com.cattong.commons.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {
	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

	private FileUtil(){}

	/**
	 * The number of bytes in a kilobyte.
	 */
	public static final long ONE_KB = 1024;

	/**
	 * The number of bytes in a megabyte.
	 */
	public static final long ONE_MB = ONE_KB * ONE_KB;

	/**
	 * The number of bytes in a gigabyte.
	 */
	public static final long ONE_GB = ONE_KB * ONE_MB;

    /**
     * The UTF-8 character set, used to decode octets in URLs.
     */
    private static final Charset UTF8 = Charset.forName("UTF-8");

    public static String getFileName(String filePath) {
    	if (StringUtil.isEmpty(filePath)) {
    		return null;
    	}
    	
    	int pos = filePath.lastIndexOf(File.separator);
    	if (pos == -1) {
    		return null;
    	}
    	return filePath.substring(pos + 1);
    }
    
    /**
	 * 根据文件名获取扩展名
	 *
	 * @param filename
	 *            文件名
	 * @return 文件扩展名
	 */
	public static String getFileExtensionFromName(String filename) {
		if (filename == null) {
			return null;
		}
		int index = indexOfExtension(filename);
		if (index == -1) {
			return "";
		} else {
			return filename.substring(index + 1);
		}
	}

	private static int indexOfExtension(String filename) {
		if (filename == null) {
			return -1;
		}
		int extensionPos = filename.lastIndexOf(".");
		int lastSeparator = filename.lastIndexOf("/");
		return (lastSeparator > extensionPos ? -1 : extensionPos);
	}

	/**
	 * Returns the file extension or an empty string if there is no extension.
	 * This method is a convenience method for obtaining the extension of a url
	 * and has undefined results for other Strings.
	 *
	 * @param url
	 * @return The file extension of the given url.
	 */
	public static String getFileExtensionFromUrl(String url) {
		if (url != null && url.length() > 0) {
			int query = url.lastIndexOf('?');
			if (query > 0) {
				url = url.substring(0, query);
			}
			int filenamePos = url.lastIndexOf('/');
			String filename = 0 <= filenamePos ? url.substring(filenamePos + 1)
					: url;

			// if the filename contains special characters, we don't
			// consider it valid for our matching purposes:
			if (filename.length() > 0
					&& Pattern.matches("[a-zA-Z_0-9\\.\\-\\(\\)]+", filename)) {
				int dotPos = filename.lastIndexOf('.');
				if (0 <= dotPos) {
					return filename.substring(dotPos + 1);
				}
			}
		}

		return "";
	}

	public static String getFileExtensionFromSource(byte[] picHeader) {
		String picExtendName = null;

		if (picHeader.length >= 2 && (picHeader[0] == 66) && (picHeader[1] == 77)) {
			//header bytes contains BM?
			picExtendName = "BMP";
		} else if (picHeader.length >= 4 && (picHeader[1] == 80)
				&& (picHeader[2] == 78) && (picHeader[3] == 71)) {
			//header bytes contains PNG?
			picExtendName = "PNG";
		} else if (picHeader.length >= 6 && (picHeader[0] == 71) && (picHeader[1] == 73)
				&& (picHeader[2] == 70) && (picHeader[3] == 56)
				&& ((picHeader[4] == 55)||(picHeader[4] == 57)) && (picHeader[5] == 97)) {
			//header bytes contains GIF87a or GIF89a?
			picExtendName = "GIF";
		} else if (picHeader.length >= 10 && (picHeader[6] == 74) && (picHeader[7] == 70)
				&& (picHeader[8] == 73) && (picHeader[9] == 70)) {
			//header bytes contains JFIF?
			picExtendName = "JPG";
		}

		return picExtendName;
	}

	public static boolean isGif(String filePath) {
		boolean isGif = false;
		if (StringUtil.isEmpty(filePath)) {
			return isGif;
		}

		File file = new File(filePath);
		if (file.exists() && file.isFile()) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				StringBuilder head = new StringBuilder("");
				for (int i = 0; i < 6; i++) {
					head.append((char) fis.read());
				}
				if (head.indexOf("GIF") == 0) {
					isGif = true;
				}
			} catch (FileNotFoundException e) {
				logger.error("isGif", e);
			} catch (IOException e) {
				logger.error("isGif", e);
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
					}
				}
			}
		}

		return isGif;
	}

	public static byte[] readFileToByteArray(String filePath) {
		if (StringUtil.isEmpty(filePath)) {
			return null;
		}
		File file = new File(filePath);

		return readFileToByteArray(file);
	}

	public static byte[] readFileToByteArray(File file) {
		byte[] fileBytes = new byte[(int) file.length()];

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			int offset = 0;
			int count = (int) file.length();
			int temp = 0;

			while ((temp = fis.read(fileBytes, offset, count)) > 0) {
				offset += temp;
				count -= temp;
			}
		} catch (FileNotFoundException e) {
			logger.error("readFile", e);
		} catch (IOException e) {
			logger.error("readFile", e);
		} catch (Exception e) {
			logger.error("readFile", e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					logger.error("readFile", e);
				}
			}
		}

		return fileBytes;
	}

	public static String byteCountToDisplaySize(long size) {
		String displaySize;

		if (size / ONE_GB > 0) {
			displaySize = String.format("%1$.1f GB", (float)size / ONE_GB);
		} else if (size / ONE_MB > 0) {
			displaySize = String.format("%1$.1f MB", (float)size / ONE_MB);
		} else if (size / ONE_KB > 0) {
			displaySize = String.format("%1$.1f KB",(float)size / ONE_KB);
		} else {
			displaySize = String.valueOf(size) + " Bytes";
		}
		return displaySize;
	}

	public static long sizeOf(File file) {

		if (!file.exists()) {
			throw new IllegalArgumentException(file + " does not exist");
		}

		if (file.isDirectory()) {
			return sizeOfDirectory(file);
		} else {
			return file.length();
		}

	}

	public static long sizeOfDirectory(File directory) {
		if (!directory.exists()) {
			String message = directory + " does not exist";
			throw new IllegalArgumentException(message);
		}

		if (!directory.isDirectory()) {
			String message = directory + " is not a directory";
			throw new IllegalArgumentException(message);
		}

		long size = 0;

		File[] files = directory.listFiles();
		if (files == null) { // null if security restricted
			return 0L;
		}
		for (File file : files) {
			size += sizeOf(file);
		}

		return size;
	}

    public static File toFile(URL url) {
        if (url == null || !"file".equalsIgnoreCase(url.getProtocol())) {
            return null;
        } else {
            String filename = url.getFile().replace('/', File.separatorChar);
            filename = decodeUrl(filename);
            return new File(filename);
        }
    }

    /**
     * Decodes the specified URL as per RFC 3986, i.e. transforms
     * percent-encoded octets to characters by decoding with the UTF-8 character
     * set. This function is primarily intended for usage with
     * {@link java.net.URL} which unfortunately does not enforce proper URLs. As
     * such, this method will leniently accept invalid characters or malformed
     * percent-encoded octets and simply pass them literally through to the
     * result string. Except for rare edge cases, this will make unencoded URLs
     * pass through unaltered.
     *
     * @param url  The URL to decode, may be <code>null</code>.
     * @return The decoded URL or <code>null</code> if the input was
     *         <code>null</code>.
     */
    static String decodeUrl(String url) {
        String decoded = url;
        if (url != null && url.indexOf('%') >= 0) {
            int n = url.length();
            StringBuffer buffer = new StringBuffer();
            ByteBuffer bytes = ByteBuffer.allocate(n);
            for (int i = 0; i < n;) {
                if (url.charAt(i) == '%') {
                    try {
                        do {
                            byte octet = (byte) Integer.parseInt(url.substring(i + 1, i + 3), 16);
                            bytes.put(octet);
                            i += 3;
                        } while (i < n && url.charAt(i) == '%');
                        continue;
                    } catch (RuntimeException e) {
                        // malformed percent-encoded octet, fall through and
                        // append characters literally
                    } finally {
                        if (bytes.position() > 0) {
                            bytes.flip();
                            buffer.append(UTF8.decode(bytes).toString());
                            bytes.clear();
                        }
                    }
                }
                buffer.append(url.charAt(i++));
            }
            decoded = buffer.toString();
        }
        return decoded;
    }

    public static void deleteFile(String filePath) {
    	if (StringUtil.isEmpty(filePath)) {
    		return;
    	}
    	
    	File file = new File(filePath);
    	if (!file.exists()) {
    		return;
    	}
    	
    	if (file.isDirectory()) {
    		for (File tempFile : file.listFiles()) {
    			deleteFile(tempFile.getPath());
    		}
    	}
    	
    	file.delete();
    }
    
    public static void clearDir(String dirPath) {
    	if (StringUtil.isEmpty(dirPath)) {
    		return;
    	}
    	
    	File file = new File(dirPath);
    	if (!file.exists() || file.isFile()) {
    		return;
    	}
    	
    	File[] files = file.listFiles();
    	if (ArrayUtil.isEmpty(files)) {
    		return;
    	}
    	for (File tempFile : files) {
    		deleteFile(tempFile.getPath());
    	}
    }
}
