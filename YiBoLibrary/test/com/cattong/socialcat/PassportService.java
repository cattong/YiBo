package com.cattong.socialcat;

import org.junit.BeforeClass;
import org.junit.Test;

import com.cattong.commons.LibException;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.socialcat.impl.socialcat.SocialCat;

public class PassportService {
	private static SocialCat socialCat;

	@BeforeClass
	public static void beforClass() {
		Authorization auth = new Authorization(ServiceProvider.SocialCat);
		auth.setAccessToken("863020017969605_yM0UKa9OBqQzvxvYkdQmB1EX7JBbDxFw"); 
	    
	    socialCat = new SocialCat(auth);
	}
	
	@Test
	public void testWithdraw() throws LibException {
		
	}
}
