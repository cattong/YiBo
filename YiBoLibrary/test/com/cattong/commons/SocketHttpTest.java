package com.cattong.commons;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class SocketHttpTest {

	private static final byte CR = '\r';
	private static final byte LF = '\n';
	private static final byte[] CRLF = { CR, LF };

	public static void main(String[] args) throws UnknownHostException,
			IOException {
		new SocketHttpTest().testHttp();
	}

	public void testHttp() throws UnknownHostException, IOException {
		URL url = new URL("http://ww2.sinaimg.cn/large/4726cd59tw1dl64abc8lqj.jpg");
		Socket socket = new Socket(url.getHost(), url.getPort() == -1 ? 80 : url.getPort());

		OutputStream out = socket.getOutputStream();
		InputStream in = socket.getInputStream();

		writeRequest(out, url.getHost());
		readResponse(in);
		System.out.println("\n\n\n");
	}

	private void writeRequest(OutputStream out, String host) throws IOException {
		// 请求行
		out.write("GET /large/4726cd59tw1dl64abc8lqj.jpg HTTP/1.1".getBytes());
		out.write(CRLF); // 请求头的每一行都是以CRLF结尾的

		// 请求头
		out.write(("Host: " + host).getBytes()); // 此请求头必须
		out.write(CRLF);

		out.write(CRLF); // 单独的一行CRLF表示请求头的结束

		// 可选的请求体，GET方法没有请求体

		out.flush();
	}

	private void readResponse(InputStream in) throws IOException {
		// 读取状态行
		String statusLine = readStatusLine(in);
		System.out.println("statusLine :" + statusLine);

		// 消息报头
		Map<String, String> headers = readHeaders(in);

		int contentLength = 200;// Integer.valueOf(headers.get("Content-Length"));

		// 可选的响应正文
		byte[] body = readResponseBody(in, contentLength);
		in.close();

		String charset = headers.get("Content-Type");
		if (charset.matches(".+;charset=.+")) {
			charset = charset.split(";")[1].split("=")[1];
		} else {
			charset = "ISO-8859-1"; // 默认编码
		}

		System.out.println("content:\n" + new String(body, charset));
	}

	private byte[] readResponseBody(InputStream in, int contentLength)
			throws IOException {

		ByteArrayOutputStream buff = new ByteArrayOutputStream(contentLength);

		int b;
		int count = 0;
		while (count++ < contentLength) {
			b = in.read();
			buff.write(b);
		}

		return buff.toByteArray();
	}

	private Map<String, String> readHeaders(InputStream in) throws IOException {
		Map<String, String> headers = new HashMap<String, String>();

		String line;

		while (!("".equals(line = readLine(in)))) {
			System.out.println(line);
			String[] nv = line.split(": "); // 头部字段的名值都是以(冒号+空格)分隔的
			headers.put(nv[0], nv[1]);
		}

		return headers;
	}

	private String readStatusLine(InputStream in) throws IOException {
		return readLine(in);
	}

	/**
	 * 读取以CRLF分隔的一行，返回结果不包含CRLF
	 */
	private String readLine(InputStream in) throws IOException {
		int b;

		ByteArrayOutputStream buff = new ByteArrayOutputStream();
		while ((b = in.read()) != CR) {
			buff.write(b);
		}

		in.read(); // 读取 LF

		String line = buff.toString();

		return line;
	}

}
