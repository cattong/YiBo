package com.cattong.commons.http;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.ByteArrayBuffer;

import com.cattong.commons.LibResultCode;
import com.cattong.commons.LibRuntimeException;
import com.cattong.commons.Logger;
import com.cattong.commons.ServiceProvider;

class ByteArrayResponseHandler implements ResponseHandler<byte[]> {

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
        	throw new LibRuntimeException(LibResultCode.E_PARAM_ERROR, "", "Null HttpEntity", ServiceProvider.None);
        }
        InputStream instream = entity.getContent();
        if (instream == null) {
            return new byte[] {};
        }
        if (entity.getContentLength() > Integer.MAX_VALUE) {
        	throw new LibRuntimeException(LibResultCode.E_PARAM_ERROR, "", "HTTP entity too large to be buffered in memory", ServiceProvider.None);
        }
        
		Logger.verbose("ByteArrayResponseHandler: content-type{}", entity.getContentType());

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
