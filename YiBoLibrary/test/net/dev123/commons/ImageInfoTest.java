package net.dev123.commons;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import net.dev123.commons.http.HttpMethod;
import net.dev123.commons.http.HttpRequestHelper;
import net.dev123.commons.http.HttpRequestMessage;
import net.dev123.commons.http.auth.NullAuthorization;
import net.dev123.commons.util.FileUtil;
import net.dev123.commons.util.MimeTypeUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.exception.LibRuntimeException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.junit.Test;

public class ImageInfoTest {
	private static final String localImage = "H:\\棋局.jpg";
	private static final String remoteImage = "http://ww1.sinaimg.cn/bmiddle/807a1f1cgw1dkp6g3rc0ng.gif";
	//private static final String remoteImage = "http://ww4.sinaimg.cn/large/6e0c3771jw1dl72kc7gu4j.jpg";
	//private static final String remoteImage = "http://ww3.sinaimg.cn/large/6f5f4c16gw1dkythk1x08j.jpg";

	@Test
	public void testLocalImage() {
		try {

			System.out.println(ImageInfo.getImageInfo(new File(localImage).toURI().toURL()));
		} catch (LibException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testRemoteImage() {
		try {
			System.out.println(ImageInfo.getImageInfo(new URL(remoteImage)));
		} catch (LibException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testRemoteImage2() {
		try {
			long time = System.currentTimeMillis();
			System.out.println(HttpRequestHelper.getImageInfo(remoteImage));
			time = System.currentTimeMillis() - time;
			System.out.println(time);
		} catch (LibException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
