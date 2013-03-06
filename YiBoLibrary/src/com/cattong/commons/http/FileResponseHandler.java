package com.cattong.commons.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;

import com.cattong.commons.LibResultCode;
import com.cattong.commons.LibRuntimeException;
import com.cattong.commons.Logger;

class FileResponseHandler implements ResponseHandler<File> {
    private File file;

	public FileResponseHandler(File file) {
	    this.file = file;
	}

	@Override
	public File handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
		if (file == null) {
			throw new LibRuntimeException(LibResultCode.E_PARAM_NULL);
		}
        return writeToFile(response.getEntity());
	}

	public File writeToFile(final HttpEntity entity) throws ClientProtocolException, IOException {
        if (entity == null) {
        	throw new LibRuntimeException(LibResultCode.E_PARAM_ERROR);
        }
        InputStream instream = entity.getContent();
        if (instream == null) {
            return null;
        }
        if (entity.getContentLength() > Integer.MAX_VALUE) {
        	throw new LibRuntimeException(
        		LibResultCode.E_PARAM_ERROR, "HTTP entity is too large!"
        	);
        }
        
		Logger.verbose("FileResponseHandler Content Type : {}", entity.getContentType());

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
        	Logger.debug(e.getMessage(), e);
        } finally {
            instream.close();
            fos.close();
        }

        return file;
	}
}
