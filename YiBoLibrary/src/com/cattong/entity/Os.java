package com.cattong.entity;

public enum Os {

	Unknow(0), 
	Android(1), iPhone(2), WindowPhone(3),
	Window(11), Mac(12), Linux(13);
	private int osNo;
	
	private Os(int osNo) {
		this.osNo = osNo;
	}

	public int getOsNo() {
		return osNo;
	}

	public void setOsNo(int osNo) {
		this.osNo = osNo;
	}
	
	public static Os getOs(int osNo) {
		Os os = Os.Unknow;
		switch (osNo) {
		case 1:
			os = Os.Android;
			break;
		case 2:
			os = Os.iPhone;
			break;
		case 3:
			os = Os.WindowPhone;
			break;
		}
		
		return os;
	}
}
