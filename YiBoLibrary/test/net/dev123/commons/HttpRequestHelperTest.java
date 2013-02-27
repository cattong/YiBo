package net.dev123.commons;

import net.dev123.commons.http.HttpRequestHelper;
import net.dev123.exception.LibException;

import org.junit.Test;

public class HttpRequestHelperTest {

	@Test
	public void testGetRedirectUrl() throws LibException, InterruptedException {
		String[] tinyUrls = {
				"http://t.cn/SPS3OY",
				"http://t.cn/SPoMrj",
				"http://t.cn/SPKkgD",
				"http://t.cn/SvBeWz",
				"http://url.cn/0461YX",
				"http://url.cn/3GWIU5",
				"http://url.cn/021cSo"
		};

		for (int i = 0; i < tinyUrls.length; i++ ) {
			//Thread.sleep(500);
			System.out.println(i + " " + HttpRequestHelper.getRedirectUrl(tinyUrls[i]));
		}
	}

}
