package com.cattong.commons.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;

class GzipDecompressingEntity extends HttpEntityWrapper {

	public GzipDecompressingEntity(final HttpEntity entity) {
		super(entity);
	}

	@Override
	public InputStream getContent() throws IOException, IllegalStateException {
		// the wrapped entity's getContent() decides about repeatability
		InputStream wrappedin = wrappedEntity.getContent();
		return new GZIPInputStream(wrappedin);
	}

	@Override
	public long getContentLength() {
		// length of ungzipped content is not known
		return -1;
	}

}
