package net.dev123.commons.http;

import java.io.IOException;
import java.io.InputStream;

import net.dev123.commons.Constants;
import net.dev123.commons.ServiceProvider;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibRuntimeException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.ByteArrayBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ByteArrayResponseHandler implements ResponseHandler<byte[]> {
	private static final Logger logger = LoggerFactory.getLogger(ByteArrayResponseHandler.class.getSimpleName());

	@Override
	public byte[] handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
		return toByteArray(response.getEntity());
	}

	/**
	 * HttpEntity到 byte数组的转换
	 *
	 * @param entity HttpEntity对象
	 * @return byte[] byte数组对象
	 * @throws IOException
	 */
	private byte[] toByteArray(final HttpEntity entity) throws IOException {
        if (entity == null) {
        	throw new LibRuntimeException(ExceptionCode.PARAMETER_ERROR, "", "Null HttpEntity", ServiceProvider.None);
        }
        InputStream instream = entity.getContent();
        if (instream == null) {
            return new byte[] {};
        }
        if (entity.getContentLength() > Integer.MAX_VALUE) {
        	throw new LibRuntimeException(ExceptionCode.PARAMETER_ERROR, "", "HTTP entity too large to be buffered in memory", ServiceProvider.None);
        }
        if (Constants.DEBUG) {
			logger.debug("{}", entity.getContentType().toString());
		}
        int i = (int) entity.getContentLength();
        if (i < 0) {
            i = 4096;
        }
        ByteArrayBuffer buffer = new ByteArrayBuffer(i);
        try {
            byte[] tmp = new byte[4096];
            int l;
            while ((l = instream.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
            }
        } finally {
            instream.close();
        }
        return buffer.buffer();
    }
}
