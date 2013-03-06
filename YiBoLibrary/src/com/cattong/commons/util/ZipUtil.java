package com.cattong.commons.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.cattong.commons.Logger;

public class ZipUtil {

	//private static final String CHINESE_CHARSET = "GBK";
	
	private static final int CACHE_SIZE = 1024;

	 public static void zip(String sourceDir, String zipFilePath) {
		 if (StringUtil.isEmpty(sourceDir) || StringUtil.isEmpty(zipFilePath)) {
			 return;
		 }
		 
		 File sourceFile = new File(sourceDir);
	     if (!sourceFile.exists()) {
	    	return;
	     }
	    	
		 ZipOutputStream zos = null;
		 
		 try {
			 File zipFile = new File(zipFilePath);
			 if (!zipFile.exists()) {
				 zipFile.createNewFile();
			 }
			 
			 FileOutputStream fos = new FileOutputStream(zipFilePath);
			 BufferedOutputStream bos = new BufferedOutputStream(fos);
		     zos = new ZipOutputStream(bos);
		     
		    // 解决中文文件名乱码
		     //zos.setEncoding(CHINESE_CHARSET);
		     String basePath = null;
		     if (sourceFile.isDirectory()) {
		         basePath = sourceFile.getPath();
		     } else {
		         basePath = sourceFile.getParent();
		     }
		     
		     zipFile(sourceFile, basePath, zos);
		 } catch (FileNotFoundException e) {
			 e.printStackTrace();
		 } catch (IOException e) {
			 e.printStackTrace();
		 } finally {
			 try {
				 if (zos != null) {
					 zos.closeEntry();
					 zos.close();
				 }
			 } catch (Exception e) {}
		 }
	}
	
	private static void zipFile(File sourceFile, String basePath, ZipOutputStream zos) {
	    File[] files = new File[0];
	    if (sourceFile.isDirectory()) {
	        files = sourceFile.listFiles();
	    } else {
	        files = new File[1];
	        files[0] = sourceFile;
	    }
	    
	    String pathName = null;
	    BufferedInputStream bis = null;
	    byte[] cache = new byte[CACHE_SIZE];
	    
	    for (File file : files) {
	    	bis = null;
	    	
	    	pathName = file.getPath().substring(basePath.length() + 1);
	    	try {
		        if (file.isDirectory()) {
		            pathName = pathName + "/";
		            zos.putNextEntry(new ZipEntry(pathName));
		            zipFile(file, basePath, zos);
		        } else {		            
		            InputStream is = new FileInputStream(file);
		            bis = new BufferedInputStream(is);
		            zos.putNextEntry(new ZipEntry(pathName));
		            
		            int nRead = 0;
		            while ((nRead = bis.read(cache, 0, CACHE_SIZE)) != -1) {
		                zos.write(cache, 0, nRead);
		            }
		        }
		    } catch (IOException e) {
		    	e.printStackTrace();
		    } finally {
		    	try {
		    		if (bis != null) {
		    			bis.close();
		    		}
		    	} catch (Exception e) {}
		    }
	    }
	    
	}
	
    public static void unzip(String zipFilePath, String unzipDir) {
    	if (StringUtil.isEmpty(zipFilePath) || StringUtil.isEmpty(unzipDir)) {
    		return;
    	}
    	
    	File zipTempFile = new File(zipFilePath);
    	if (!zipTempFile.exists()) {
    		return;
    	}
    	if (!unzipDir.endsWith("/")) {
    		unzipDir += "/";
    	}
    	
        ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(zipFilePath);
			Enumeration<?> entries = zipFile.entries();			 

	        ZipEntry entry;
	        while (entries.hasMoreElements()) {
	            entry = (ZipEntry) entries.nextElement();
	            
	            unzip(entry, unzipDir, zipFile);
	        }
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (zipFile != null) {
					zipFile.close();
				}
			} catch (Exception e) {}
		} 

    }
    
    public static void unzip(String zipFilePath, String entryName, String unzipDir) {
    	if (StringUtil.isEmpty(zipFilePath) || StringUtil.isEmpty(unzipDir)) {
    		return;
    	}
    	
    	File zipTempFile = new File(zipFilePath);
    	if (!zipTempFile.exists()) {
    		return;
    	}
    	if (!unzipDir.endsWith("/")) {
    		unzipDir += "/";
    	}
    	
        ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(zipFilePath);
			Enumeration<?> entries = zipFile.entries();			 

	        ZipEntry entry;
	        while (entries.hasMoreElements()) {
	            entry = (ZipEntry) entries.nextElement();
	            if (entry.getName().startsWith(entryName)) {
	                unzip(entry, unzipDir, zipFile);
	            }
	        }
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (zipFile != null) {
					zipFile.close();
				}
			} catch (Exception e) {}
		} 

    }
    
    private static void unzip(ZipEntry entry, String unzipDir, ZipFile zipFile) {
        if (entry == null || StringUtil.isEmpty(unzipDir)) {
        	return;
        }
        
        if (entry.isDirectory()) {
            new File(unzipDir + entry.getName()).mkdirs();
            return;
        }
        
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        
        byte[] cache = new byte[CACHE_SIZE];
        
        try {			
	        File file = new File(unzipDir + entry.getName());
	        File parentFile = file.getParentFile();
	        if (parentFile != null && (!parentFile.exists())) {
	            parentFile.mkdirs();
	        }
	        Logger.info("unzip:" + file.getPath());
	        
	        bis = new BufferedInputStream(zipFile.getInputStream(entry));
	        
	        FileOutputStream fos = new FileOutputStream(file);
	        bos = new BufferedOutputStream(fos, CACHE_SIZE);
	        
	        int nRead = 0;
	        while ((nRead = bis.read(cache, 0, CACHE_SIZE)) != -1) {
	            fos.write(cache, 0, nRead);
	        }
	        bos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
				if (bos != null) {
					bos.close();
				}
			} catch (Exception e) {}
		}

    }
    
}
