package net.dev123.commons;

import java.io.File;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import net.dev123.commons.http.HttpRequestHelper;
import net.dev123.commons.util.FileUtil;
import net.dev123.exception.LibException;

public class ImageInfo {
	/**
	 * Image formats
	 */
	public enum Format {
		PNG, JPEG, GIF, TIFF, BMP, ICO, WEBP
	}

	private static final int EOI_MARKER = 0xd9;

	private static final int RST_MARKER_START = 0xd0;

	private static final int RST_MARKER_END = 0xd7;

	private static final int TEM_MARKER = 0x01;

	private static final int HUFFMAN_TABLE_MARKER = 0xc4;

	private static final int ARITHMETIC_CODING_CONDITIONING_MARKER = 0xcc;

	private URL url;
	private Format format;
	private byte[] imageData;
	private int width;
	private int height;
	private long size;

	public ImageInfo(URL url) {
		this.url = url;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public long getSize() {
		return size;
	}

	public String getDisplaySize() {
		return FileUtil.byteCountToDisplaySize(size);
	}

	public void setSize(long size) {
		this.size = size;
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	public URL getUrl() {
		return url;
	}

	/*
	public void setImageData(byte[] imageData) {
		if (imageData == null) {
			throw new IllegalArgumentException("imageData must not be null");
		}
		if (imageData.length == 0) {
			throw new IllegalArgumentException("imageData must not be empty");
		}
		this.imageData = imageData.clone();
		this.width = -1;
		this.height = -1;
		this.format = null;
	}
	*/

	/**
	 * Updates the dimension fields of the image.
	 *
	 */
	private void updateDimensions() {
		if (imageData == null) {
			throw new UnsupportedOperationException(
					"No image data is available.");
		}
		if (imageData.length < 8) {
			throw new IllegalArgumentException(
					"imageData must be a valid image");
		}
		if (imageData[0] == 'G' && imageData[1] == 'I' && imageData[2] == 'F') {
			updateGifDimensions();
			format = Format.GIF;
		} else if (imageData[0] == (byte) 0x89 && imageData[1] == 'P'
				&& imageData[2] == 'N' && imageData[3] == 'G'
				&& imageData[4] == 0x0d && imageData[5] == 0x0a
				&& imageData[6] == 0x1a && imageData[7] == 0x0a) {
			updatePngDimensions();
			format = Format.PNG;
		} else if (imageData[0] == (byte) 0xff && imageData[1] == (byte) 0xd8) {
			updateJpegDimensions();
			format = Format.JPEG;
		} else if ((imageData[0] == 'I' && imageData[1] == 'I'
				&& imageData[2] == 0x2a && imageData[3] == 0x00)
				|| (imageData[0] == 'M' && imageData[1] == 'M'
						&& imageData[2] == 0x00 && imageData[3] == 0x2a)) {
			updateTiffDimensions();
			format = Format.TIFF;
		} else if (imageData[0] == 'B' && imageData[1] == 'M') {
			updateBmpDimensions();
			format = Format.BMP;
		} else if (imageData[0] == 0x00 && imageData[1] == 0x00
				&& imageData[2] == 0x01 && imageData[3] == 0x00) {
			updateIcoDimensions();
			format = Format.ICO;
		} else if (imageData.length > 16 && imageData[0] == 'R'
				&& imageData[1] == 'I' && imageData[2] == 'F'
				&& imageData[3] == 'F' && imageData[8] == 'W'
				&& imageData[9] == 'E' && imageData[10] == 'B'
				&& imageData[11] == 'P' && imageData[12] == 'V'
				&& imageData[13] == 'P' && imageData[14] == '8') {
			updateWebpDimensions();
			format = Format.WEBP;
		} else {
			throw new IllegalArgumentException(
					"imageData must be a valid image");
		}
	}

	/**
	 * Updates the dimension fields of the GIF image. Based on
	 * http://www.w3.org/Graphics/GIF/spec-gif89a.txt.
	 *
	 */
	private void updateGifDimensions() {
		if (imageData.length < 10) {
			throw new IllegalArgumentException("corrupt GIF format");
		}
		ByteBuffer buffer = ByteBuffer.wrap(imageData);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		width = buffer.getChar(6) & 0xffff;
		height = buffer.getChar(8) & 0xffff;
	}

	/**
	 * Updates the dimension fields of the PNG image. Based on
	 * http://www.w3.org/TR/2003/REC-PNG-20031110/ sections 5 and 11.2.2.
	 *
	 */
	private void updatePngDimensions() {
		if (imageData.length < 24) {
			throw new IllegalArgumentException("corrupt PNG format");
		}
		ByteBuffer buffer = ByteBuffer.wrap(imageData);
		buffer.order(ByteOrder.BIG_ENDIAN);
		width = buffer.getInt(16);
		height = buffer.getInt(20);
	}

	/**
	 * Updates the dimension fields of the JPEG image. Based on
	 * http://www.w3.org/Graphics/JPEG/itu-t81.pdf.
	 *
	 */
	private void updateJpegDimensions() {

		ByteBuffer buffer = ByteBuffer.wrap(imageData);
		buffer.order(ByteOrder.BIG_ENDIAN);

		if (extend(buffer.get()) != 0xff || extend(buffer.get()) != 0xd8) {
			throw new IllegalArgumentException(
					"corrupt JPEG format: Expected SOI marker");
		}

		int code;

		try {
			while (true) {
				do {
					code = extend(buffer.get());
				} while (code != 0xff);

				while (code == 0xff) {
					code = extend(buffer.get());
				}

				if (isFrameMarker(code)) {
					buffer.position(buffer.position() + 3);
					height = extend(buffer.getShort());
					width = extend(buffer.getShort());
					return;
				}

				if (code == EOI_MARKER) {
					throw new IllegalArgumentException(
							"corrupt JPEG format: No frame sgements found.");
				}

				if (code >= RST_MARKER_START && code <= RST_MARKER_END) {
					continue;
				}

				if (code == TEM_MARKER) {
					continue;
				}

				int length = extend(buffer.getShort(buffer.position()));
				buffer.position(buffer.position() + length);
			}
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("corrupt JPEG format");
		} catch (java.nio.BufferUnderflowException ex) {
			throw new IllegalArgumentException("corrupt JPEG format");
		}
	}

	private static boolean isFrameMarker(int code) {
		return ((code & 0xf0) == 0xc0) && code != HUFFMAN_TABLE_MARKER
				&& code != ARITHMETIC_CODING_CONDITIONING_MARKER;
	}

	private static int extend(byte b) {
		return b & 0xFF;
	}

	private static int extend(short s) {
		return s & 0xFFFF;
	}

	/**
	 * Updates the dimension fields of the TIFF image. Based on
	 * http://partners.adobe.com/public/developer/en/tiff/TIFF6.pdf sections 2
	 * and 3.
	 *
	 */
	private void updateTiffDimensions() {
		ByteBuffer buffer = ByteBuffer.wrap(imageData);
		if (imageData[0] == 'I') {
			buffer.order(ByteOrder.LITTLE_ENDIAN);
		}
		int offset = buffer.getInt(4);

		int ifdSize = buffer.getChar(offset) & 0xffff;
		offset += 2;
		for (int i = 0; i < ifdSize && offset + 12 <= imageData.length; i++) {
			int tag = buffer.getChar(offset) & 0xffff;
			if (tag == 0x100 || tag == 0x101) {
				int type = buffer.getChar(offset + 2) & 0xffff;
				int result;
				if (type == 3) {
					result = buffer.getChar(offset + 8) & 0xffff;
				} else if (type == 4) {
					result = buffer.getInt(offset + 8);
				} else {
					result = imageData[offset + 8];
				}
				if (tag == 0x100) {
					width = result;
					if (height != -1) {
						return;
					}
				} else {
					height = result;
					if (width != -1) {
						return;
					}
				}
			}
			offset += 12;
		}
		if (width == -1 || height == -1) {
			throw new IllegalArgumentException("corrupt tiff format");
		}
	}

	/**
	 * Updates the dimension fields of the BMP image. Based on
	 * http://msdn.microsoft.com/en-us/library/ms532290(VS.85).aspx
	 * http://msdn.microsoft.com/en-us/library/ms532300(VS.85).aspx
	 * http://msdn.microsoft.com/en-us/library/ms532331(VS.85).aspx for windows
	 * versions.
	 *
	 */
	private void updateBmpDimensions() {
		if (imageData.length < 18) {
			throw new IllegalArgumentException("corrupt BMP format");
		}
		ByteBuffer buffer = ByteBuffer.wrap(imageData);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		width = buffer.get(6) & 0xff;
		height = buffer.get(7) & 0xff;
		int headerLength = buffer.getInt(14);
		if (headerLength == 12 && imageData.length >= 22) {
			width = buffer.getChar(18) & 0xffff;
			height = buffer.getChar(20) & 0xffff;
		} else if ((headerLength == 40 || headerLength == 108
				|| headerLength == 124 || headerLength == 64)
				&& imageData.length >= 26) {
			width = buffer.getInt(18);
			height = buffer.getInt(22);
		} else {
			throw new IllegalArgumentException("corrupt BMP format");
		}
	}

	/**
	 * Updates the dimension fields of the ICO image.
	 *
	 */
	private void updateIcoDimensions() {
		if (imageData.length < 8) {
			throw new IllegalArgumentException("corrupt ICO format");
		}
		ByteBuffer buffer = ByteBuffer.wrap(imageData);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		width = buffer.get(6) & 0xff;
		height = buffer.get(7) & 0xff;

		if (width == 0) {
			width = 256;
		}
		if (height == 0) {
			height = 256;
		}
	}

	private void updateWebpDimensions() {
		if (imageData.length < 30) {
			throw new IllegalArgumentException("corrupt WEBP format");
		}

		ByteBuffer buffer = ByteBuffer.wrap(imageData);
		buffer.order(ByteOrder.LITTLE_ENDIAN);

		int bits = buffer.get(20) | buffer.get(21) << 8 | buffer.get(22) << 16;

		boolean keyFrame = (bits & 1) == 0;

		if (!keyFrame) {
			throw new IllegalArgumentException(
					"corrupt WEBP format: not a key frame");
		}

		int profile = (bits >> 1) & 7;
		int showFrame = (bits >> 4) & 1;

		if (profile > 3) {
			throw new IllegalArgumentException(
					"corrupt WEBP format: invalid profile");
		}
		if (showFrame == 0) {
			throw new IllegalArgumentException(
					"corrupt WEBP format: frame is not visible");
		}

		width = extend(buffer.getShort(26));
		height = extend(buffer.getShort(28));

	}

	public static ImageInfo getImageInfo(URL url) throws LibException {
		if (url == null) {
			return null;
		}

		ImageInfo info = null;
		String scheme = url.getProtocol();
		if ("file".equalsIgnoreCase(scheme)) {
			File imageFile = FileUtil.toFile(url);
			info = new ImageInfo(url);
			info.imageData = FileUtil.readFileToByteArray(imageFile);
			info.setSize(FileUtil.sizeOf(imageFile)); // 设置文件大小
			info.updateDimensions(); // 更新图片尺寸、格式信息
		} else if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) {
			info = HttpRequestHelper.getImageInfo(url.toString());
		}
		return info;
	}

	public static ImageInfo getImageInfo(File file) throws LibException {
		if (file == null || !file.exists() || !file.isFile()) {
			return null;
		}

		ImageInfo info = null;
		info = new ImageInfo(null);
		info.imageData = FileUtil.readFileToByteArray(file);
		info.setSize(FileUtil.sizeOf(file)); // 设置文件大小
		info.updateDimensions(); // 更新图片尺寸、格式信息

		return info;
	}
	
	@Override
	public String toString() {
		return "ImageInfo [url=" + url + ", format=" + format + ", width="
				+ width + ", height=" + height + ", size=" + getDisplaySize() + "]";
	}

}
