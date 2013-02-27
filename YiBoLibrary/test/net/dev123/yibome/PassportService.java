package net.dev123.yibome;

import net.dev123.exception.LibException;

import org.junit.Test;

public class PassportService {

	@Test
	public void testRegister() throws LibException{
//		String name = String.valueOf(System.currentTimeMillis());
		String name = "raise007";
		YiBoMe.register(name, "24097410", "24097410", "raise-0@163.com");
	}

	@Test
	public void testLogin() throws LibException{
		String name = String.valueOf(System.currentTimeMillis());
		YiBoMe.register(name, "24097410", "24097410", "raise-0@163.com");
		YiBoMe.login(name, "24097410");
	}

	@Test
	public void testTimeNow() throws LibException{
		System.out.println(YiBoMe.getTimeNow());
	}

}
