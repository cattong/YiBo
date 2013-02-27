package net.dev123.commons.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.dev123.commons.Constants;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibRuntimeException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileResponseHandler implements ResponseHandler<File> {
	private static final Logger logger = LoggerFactory.getLogger(ByteArrayResponseHandler.class);

    private File file;

	public FileResponseHandler(File file) {
	    this.file = file;
	}

	@Override
	public File handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
		if (file == null) {
			throw new LibRuntimeException(ExceptionCode.PARAMETER_NULL, "file is null!");
		}
        return writeToFile(response.getEntity());
	}

	public File writeToFile(final HttpEntity entity) throws ClientProtocolException, IOException {
        if (entity == null) {
        	throw new LibRuntimeException(ExceptionCode.PARAMETER_ERROR);
        }
        InputStream instream = entity.getContent();
        if (instream == null) {
            return null;
        }
        if (entity.getContentLength() > Integer.MAX_VALUE) {
        	throw new LibRuntimeException(
        		ExceptionCode.PARAMETER_ERROR, "HTTP entity is too large!"
        	);
        }
        if (Constants.DEBUG) {
			logger.debug("File Content Type : {}", entity.getContentType().toString());
		}

        if (!file.exists()) {
        	file.createNewFile();
        }

        FileOutputStream fos = null;
        try {
        	fos = new FileOutputStream(file);
        	byte[] tmp = new byte[4096];
            int l;
            while ((l = instream.read(tmp)) != -1) {
                fos.write(tmp, 0, l);
            }
            fos.flush();
        } catch (FileNotFoundException e) {
        	logger.error("File Not Found", e.getMessage(), e);
        } finally {
            instream.close();
            fos.close();
        }

        return file;
	}
}
