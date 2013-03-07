package com.shejiaomao.weibo.common;

public class NotificationEntity implements java.io.Serializable {

	private static final long serialVersionUID = -3904148991018492187L;
	
	private String tickerText;
	private String contentTitle;
	private String contentText;
	private int contentType;
	
	public String getTickerText() {
		return tickerText;
	}
	public void setTickerText(String tickerText) {
		this.tickerText = tickerText;
	}
	public String getContentTitle() {
		return contentTitle;
	}
	public void setContentTitle(String contentTitle) {
		this.contentTitle = contentTitle;
	}
	public String getContentText() {
		return contentText;
	}
	public void setContentText(String contentText) {
		this.contentText = contentText;
	}
	public int getContentType() {
		return contentType;
	}
	public void setContentType(int contentType) {
		this.contentType = contentType;
	}
	
	@Override
	public String toString() {
		return "tickerText=" + tickerText + ";contentType=" + contentType +
		    ";contentTitle=" + contentTitle + ";contentText=" + contentText;
	}
}
