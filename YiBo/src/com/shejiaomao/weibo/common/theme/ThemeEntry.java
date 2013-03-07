package com.shejiaomao.weibo.common.theme;

import com.cattong.commons.util.StringUtil;


public class ThemeEntry {
	public static final int STATE_UNINSTALLED = 0;
	public static final int STATE_INSTALLED = 1;
	public static final int STATE_USING = 2;

	private String name;
	
	private String packageName;
	
	private int fileSize;
	
	private String thumbnailUrl;
	
	private String fileUrl;
    
    private boolean isInstalled;
    
    private String version;
    
    private String description;

    private int state;
    
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public boolean isInstalled() {
		return isInstalled;
	}

	public void setInstalled(boolean isInstalled) {
		this.isInstalled = isInstalled;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
	public static int getThemeEntryState(ThemeEntry entry) {
		int state = ThemeEntry.STATE_UNINSTALLED;
		if (entry == null) {
			return state;
		}
		
        if (entry.isInstalled()) {
        	if (StringUtil.isEquals(Theme.currentPackageName, entry.getPackageName())) {
        		state = ThemeEntry.STATE_USING;
        	} else {
        		state = ThemeEntry.STATE_INSTALLED;
        	}
        } else {
        	state = ThemeEntry.STATE_UNINSTALLED;
        }
        
        return state;
	}
}