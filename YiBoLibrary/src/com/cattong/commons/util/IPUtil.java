package com.cattong.commons.util;

public class IPUtil {

	private static final String IP_PATTERN = "([1-9]|[1-9]//d|1//d{2}|2[0-4]//d|25[0-5])(//.(//d|[1-9]//d|1//d{2}|2[0-4]//d|25[0-5])){3}"; 
	 
	public static long ip2Num(String ip) {
		long num = -1l;
		if (StringUtil.isEmpty(ip)) {
			return num;
		}
		if (ip.matches(IP_PATTERN)) {
			return num;
		}
		String[] tokens = ip.split("\\.");
		
		num = 0l;
		num += Long.parseLong(tokens[0]) * 16777216;
		num += Long.parseLong(tokens[1]) * 65536;
		num += Long.parseLong(tokens[2]) * 256;
		num += Long.parseLong(tokens[3]);
		return num;
	}
	
	public static String num2Ip(long num) {  
	    return (num >> 24 & 0xFF) + "." 
	       + ((num >> 16) & 0xFF) + "." 
	       + ((num >> 8) & 0xFF) + "." 
	       + (num & 0xFF);  
	}
}
