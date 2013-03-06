package com.cattong.commons.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class IPUtilTest {

	@Test
	public void ipToNum() {
		String ip = "192.188.134.23";
		long num = IPUtil.ip2Num(ip);
		System.out.println(num);
		
		String ipResult = IPUtil.num2Ip(num);
		System.out.println(ipResult);
		assertTrue(ipResult.equals(ip));
	}
}
