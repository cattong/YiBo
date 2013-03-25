package com.shejiaomao.weibo.widget;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.locks.ReentrantLock;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.util.Log;

import com.cattong.commons.Logger;
import com.shejiaomao.common.MemoryManager;

public class GifDecoder extends Thread {
    private static final String TAG = "GifDecoder";

    /**解析状态*/
	public static final int STATUS_PARSING       = 0;    //状态：正在解码中
	public static final int STATUS_FORMAT_ERROR  = 1;    //状态：图片格式错误
	public static final int STATUS_OPEN_ERROR    = 2;    //状态：打开失败
	public static final int STATUS_INTERRUPT     = 3;    //状态: 解析中断
	public static final int STATUS_FINISH        = -1;   //状态：解码成功

	private static final long MAX_HEAP_SIZE;
	private static final int MaxStackSize   = 4096;      // max decoder pixel stack size
    static {
    	MAX_HEAP_SIZE =	Runtime.getRuntime().maxMemory() / 2; //16M用8M
    }

	/**gif global attribute*/
	private int bgColor;              // gif background color
	private int frameCount;           // gif frame count
	private GifFrame gifFrame;        // gif head frame
	private int[] framePixels;        // gif pixels
	//private int[] lastFramePixels;    // last gif pixels;
	public static ReentrantLock lock = new ReentrantLock();
	private boolean isFree = false;   //free resource;

	/**Logical Screen Descriptor*/
	public int width;               // full image width
	public int height;              // full image height
	private boolean lsdGctFlag;     // global color table used
	private int lsdCr;              // Color ResoluTion
	private int lsdSf;              // Sort Flag;
	private int lsdGctSize;         // size of global color table
	private int lsdBgColorIndex;    // background color index
	private int lsdPar;             // pixel aspect radio
	private int loopCount = 1;      // iterations; 0 = repeat forever

	/**color table**/
	private int[] gct;    // global color table

	private GifImageDescriptor currentImgDescriptor;
	private GifGraphicControlExt currentGCExt;

	private byte[] block = new byte[256]; // current data block
	private int blockSize = 0;            // block size

	// LZW decoder working arrays
	private short[] prefix;
	private byte[] suffix;
	private byte[] pixelStack;


	private InputStream gifStream;
	private int status;

	private GifAction action = null;
	private boolean isShow = false;

	private Bitmap image;        // current frame
	private GifFrame currentFrame;
	private GifFrame lastFrame;
	public GifDecoder(byte[] gifDatabyte, GifAction action) {
		this.gifStream = new ByteArrayInputStream(gifDatabyte);
		this.action = action;
	}

	public GifDecoder(InputStream gifStream, GifAction action) {
		this.gifStream = gifStream;
		this.action = action;
	}

	@Override
	public void run() {
		if (gifStream != null) {
			decodeGif();
		}
	}

	/**
	 * 释放资源
	 */
	public void free() {
		lock.lock();
		if (status == STATUS_PARSING) {
			status = STATUS_INTERRUPT;
		}

		GifFrame fg = gifFrame;
		GifFrame temp;
		while (fg != null) {
			fg.image = null;
			temp = fg.nextFrame;
			fg.nextFrame = null;
			fg.pixels = null;
			fg = temp;
		}
		if (gifStream != null) {
			try {
				gifStream.close();
			} catch (Exception ex) {}
			gifStream = null;
		}

		if (image != null && !image.isRecycled()) {
			image.recycle();
		}
		image = null;

		framePixels = null;

		isFree = true;
		lock.unlock();
	}

	/**
	 * 当前状态
	 * @return
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * 解码是否成功，成功返回true
	 * @return 成功返回true，否则返回false
	 */
	public boolean parseOk() {
		return status == STATUS_FINISH;
	}

	/**
	 * 取某帧的延时时间
	 * @param n 第几帧
	 * @return 延时时间，毫秒
	 */
	public int getDelay(int n) {
		int delay = -1;
		if ((n >= 0) && (n < frameCount)) {
			// delay = ((GifFrame) frames.elementAt(n)).delay;
			GifFrame f = getFrame(n);
			if (f != null)
				delay = f.delay;
		}
		return delay;
	}

	/**
	 * 取所有帧的延时时间
	 * @return
	 */
	public int[] getDelays() {
		GifFrame f = gifFrame;
		int[] d = new int[frameCount];
		int i = 0;
		while (f != null && i < frameCount) {
			d[i] = f.delay;
			f = f.nextFrame;
			i++;
		}
		return d;
	}


	/**
	 * 取总帧 数
	 * @return 图片的总帧数
	 */
	public int getFrameCount() {
		return frameCount;
	}

	/**
	 * 取第一帧图片
	 * @return
	 */
	public Bitmap getImage() {
		return getFrameImage(0);
	}

	public int getLoopCount() {
		return loopCount;
	}


	/**
	 * 取第几帧的图片
	 * @param n 帧数
	 * @return 可画的图片，如果没有此帧或者出错，返回null
	 */
	public Bitmap getFrameImage(int n) {
		GifFrame frame = getFrame(n);
		if (frame == null)
			return null;
		else
			return frame.image;
	}

	/**
	 * 取当前帧图片
	 * @return 当前帧可画的图片
	 */
	public GifFrame getCurrentFrame() {
		return currentFrame;
	}

	/**
	 * 取第几帧，每帧包含了可画的图片和延时时间
	 * @param n 帧数
	 * @return
	 */
	public GifFrame getFrame(int n) {
		GifFrame frame = gifFrame;
		int i = 0;
		while (frame != null) {
			if (i == n) {
				return frame;
			} else {
				frame = frame.nextFrame;
			}
			i++;
		}
		return null;
	}

	/**
	 * 重置，进行本操作后，会直接到第一帧
	 */
	public void reset() {
		currentFrame = gifFrame;
	}

	/**
	 * 下一帧，进行本操作后，通过getCurrentFrame得到的是下一帧
	 * @return 返回下一帧
	 */
	public GifFrame next() {
		GifFrame frame = null;
		if (status != STATUS_FINISH && status != STATUS_PARSING) {
			return frame;
		}

		boolean isMove = false;
		if (isShow == false || currentFrame == null) {
			isShow = true;
			frame = gifFrame;
			if (frame != null) {
				isMove = true;
			}
		} else {
			//预数据缓冲;
			//prepareFrames();
			frame = currentFrame.nextFrame;

			if (status == STATUS_PARSING) {
				if (frame != null) {
					isMove = true;
				}
				//currentFrame = gifFrame;
			} else {
				if (frame == null) {
					frame = gifFrame;
				}
				isMove = true;
			}

			if (Logger.isDebug()) {
				int frameIndex = currentFrame.frameIndex;
				int delay = currentFrame.delay;
				Log.d(TAG, "display currentFrame index->" + frameIndex + " delay->" + delay);
			}
		}

		if (isMove && frame != null) {
			currentFrame = frame;

			fillFramePixels(frame);
			image = createBitmap();
			if (!(image == null || image.isRecycled() || framePixels == null)) {
			    image.setPixels(framePixels, 0, width, 0, 0, width, height);
			    frame.image = image;
			}
		}

		return currentFrame;
	}

	private int decodeGif() {
		init();
		if (gifStream == null) {
			status = STATUS_OPEN_ERROR;
			action.parseOk(false, -1);
			return status;
		}

		readHeader();
		if (!err()) {
			readContents();
			if (frameCount <= 0) {
				status = STATUS_FORMAT_ERROR;
				action.parseOk(false, -1);
			} else {
				status = STATUS_FINISH;
				action.parseOk(true, -1);
			}
		}

		try {
			if (gifStream != null) {
			    gifStream.close();
			}
		} catch (Exception e) {
		    if (Logger.isDebug()) Log.d(TAG, TAG, e);
		}

		return status;
	}

	private void init() {
		status = STATUS_PARSING;
		frameCount = 0;
		gifFrame = null;
		gct = null;
		//lct = null;
	}

	private void readHeader() {
		StringBuilder id = new StringBuilder("");
		for (int i = 0; i < 6; i++) {
			id.append((char) readByte());
		}
		if (!id.toString().startsWith("GIF")) {
			status = STATUS_FORMAT_ERROR;
			return;
		}
		readLSD();
		if (lsdGctFlag && !err()) {
			gct = readColorTable(lsdGctSize);
			bgColor = gct[lsdBgColorIndex];
		}
	}

	private void readLSD() {
		// logical screen size
		width = readShort();
		height = readShort();

		// packed fields
		int packed = readByte();
		lsdGctFlag = (packed & 0x80) != 0; // 1 : global color table flag
		lsdCr = (packed & 0x70);           // 2-4 : color resolution
		lsdSf = (packed & 0x08);           // 5 : gct sort flag
		//TODO: 文档上说gct size = 2(n+1)，其中n是包装域（packed）的后三位
		lsdGctSize = 2 << (packed & 0x07); // 6-8 : gct size
                                           // 实际数量是2 << (packed & 0x07),由于数组从0开始
		lsdBgColorIndex = readByte(); // background color index
		lsdPar = readByte();         // pixel aspect radio
	}

	private void readContents() {
		// read GIF file content blocks
		boolean isDone = false;
		while (!(isDone || err())) {
			int code = readByte();
			switch (code) {
			case 0x2C: // image separator
				readImage();
				break;
			case 0x21: // extension
				code = readByte();
				switch (code) {
				case 0xf9: // graphics control extension
					currentGCExt = readGraphicControlExt();
					break;
				case 0xff: // application extension
					readBlock();
					StringBuilder app = new StringBuilder("");
					for (int i = 0; i < 11; i++) {
						app.append((char) block[i]);
					}
					if ("NETSCAPE2.0".equals(app.toString())) {
						readNetscapeExt();
					} else {
						skip(); // don't care
					}
					break;
				default: // uninteresting extension
					skip();
				}
				break;
			case 0x3b: // terminator
				isDone = true;
				break;
			case 0x00: // bad byte, but keep going and see what happens
				break;
			default:
				status = STATUS_FORMAT_ERROR;
			}
		}
	}

	private void readImage() {
		if (currentGCExt == null) {
			//status = STATUS_FORMAT_ERROR;
			currentGCExt = new GifGraphicControlExt();
		}
		if (err()) {
			return;
		}

		currentImgDescriptor = readImageDescriptor();
		if (currentImgDescriptor == null) {
			status = STATUS_FORMAT_ERROR;
			return;
		}

		int[] colorTable;
		if (currentImgDescriptor.lctFlag) {
			colorTable = readColorTable(currentImgDescriptor.lctSize); // read table
		} else {
			colorTable = gct; // use make global table
		}

		int tempColor = 0;
		if (currentGCExt.transparentColorFlag) {
			tempColor = colorTable[currentGCExt.transparentColorIndex];
			colorTable[currentGCExt.transparentColorIndex] = 0;    // set transparent color if specified
		}
		if (colorTable == null) {
			status = STATUS_FORMAT_ERROR; // no color table defined
		}
		if (err()) {
			return;
		}

		GifImageData imgData = readImageData(currentImgDescriptor); // decode pixel data
		if (imgData != null) {
			imgData.colorTable = colorTable;
		}

		skip();
		if (err()) {
			return;
		}

		frameCount++;
		if (currentGCExt.delay < 10) {
			currentGCExt.delay = 80;
		}

		GifFrame newFrame = createFrame(imgData, frameCount, currentGCExt.delay);
		if (newFrame == null) {
			return;
		}
		newFrame.gcExt = currentGCExt;
		newFrame.imgData = imgData;
		newFrame.imgDescriptor = currentImgDescriptor;

		if (gifFrame == null) {
			gifFrame = newFrame;
		} else {
			GifFrame f = gifFrame;
			while(f.nextFrame != null) {
				f = f.nextFrame;
			}
			f.nextFrame = newFrame;
		}

		if (currentGCExt.transparentColorFlag) {
			colorTable[currentGCExt.transparentColorIndex] = tempColor;
		}
		resetFrame();
		action.parseOk(true, frameCount);
	}

	private GifImageDescriptor readImageDescriptor() {
		GifImageDescriptor imgDescriptor = new GifImageDescriptor();
		imgDescriptor.offX = readShort(); // (sub)image position & size
		imgDescriptor.offY = readShort();
		imgDescriptor.width = readShort();
		imgDescriptor.height = readShort();

		int packed = readByte();
		imgDescriptor.lctFlag = (packed & 0x80) != 0;        // 1 - local color table flag
		imgDescriptor.interlaceFlag = (packed & 0x40) != 0;  // 2 - interlace flag
		//TODO: 没有设置Sort Flag(设置 彩色表排序标志)和Reserved(保留)？
		// 3 - sort flag
		// 4-5 - reserved
		imgDescriptor.lctSize = 2 << (packed & 0x07); // 6-8 - local color table size

		return imgDescriptor;
	}

	private GifImageData readImageData(GifImageDescriptor imgDescriptor) {
		int NullCode = -1;
		int npix = imgDescriptor.width * imgDescriptor.height;
		int available, clear, code_mask, code_size, end_of_information, in_code, old_code, bits, code, count, i, datum, data_size, first, top, bi, pi;

		GifImageData imageData = new GifImageData();

		//if ((pixels == null) || (pixels.length < npix)) {
		byte[] pixels = new byte[npix]; // allocate new pixel array
		//}
		if (prefix == null) {
			prefix = new short[MaxStackSize];
		}
		if (suffix == null) {
			suffix = new byte[MaxStackSize];
		}
		if (pixelStack == null) {
			pixelStack = new byte[MaxStackSize + 1];
		}

		// Initialize GIF data stream decoder.
		data_size = readByte();
		clear = 1 << data_size;
		end_of_information = clear + 1;
		available = clear + 2;
		old_code = NullCode;
		code_size = data_size + 1;
		code_mask = (1 << code_size) - 1;
		for (code = 0; code < clear; code++) {
			prefix[code] = 0;
			suffix[code] = (byte) code;
		}

		// Decode GIF pixel stream.
		datum = bits = count = first = top = pi = bi = 0;
		for (i = 0; i < npix;) {
			if (top == 0) {
				if (bits < code_size) {
					// Load bytes until there are enough bits for a code.
					if (count == 0) {
						// Read a new data block.
						count = readBlock();
						if (count <= 0) {
							break;
						}
						bi = 0;
					}
					datum += (((int) block[bi]) & 0xff) << bits;
					bits += 8;
					bi++;
					count--;
					continue;
				}
				// Get the next code.
				code = datum & code_mask;
				datum >>= code_size;
				bits -= code_size;

				// Interpret the code
				if ((code > available) || (code == end_of_information)) {
					break;
				}
				if (code == clear) {
					// Reset decoder.
					code_size = data_size + 1;
					code_mask = (1 << code_size) - 1;
					available = clear + 2;
					old_code = NullCode;
					continue;
				}
				if (old_code == NullCode) {
					pixelStack[top++] = suffix[code];
					old_code = code;
					first = code;
					continue;
				}
				in_code = code;
				if (code == available) {
					pixelStack[top++] = (byte) first;
					code = old_code;
				}
				while (code > clear) {
					pixelStack[top++] = suffix[code];
					code = prefix[code];
				}
				first = ((int) suffix[code]) & 0xff;
				// Add a new string to the string table,
				if (available >= MaxStackSize) {
					break;
				}
				pixelStack[top++] = (byte) first;
				prefix[available] = (short) old_code;
				suffix[available] = (byte) first;
				available++;
				if ((available & code_mask) == 0
					&& available < MaxStackSize) {
					code_size++;
					code_mask += available;
				}
				old_code = in_code;
			}

			// Pop a pixel off the pixel stack.
			top--;
			pixels[pi++] = pixelStack[top];
			i++;
		}
		for (i = pi; i < npix; i++) {
			pixels[i] = 0; // clear missing pixels
		}

		imageData.pixels = pixels;
		return imageData;
	}

	private GifFrame createFrame(GifImageData imgData, int frameCount, int delay) {
		GifFrame newFrame = null;

		long frameHeapSize = width * height * 1; //只存储像素索引
		if (Logger.isDebug()) MemoryManager.trace();
		if (frameHeapSize * (frameCount + 1) > MAX_HEAP_SIZE) {
			return newFrame;
		}

		newFrame = new GifFrame(frameCount, delay);

		return newFrame;
	}

	private void fillFramePixels(GifFrame frame) {
		lock.lock();
		if (isFree || frame == null) {
			lock.unlock();
			return;
		}

		if (framePixels == null) {
			framePixels = new int[width * height];
		}
		if (frame.frameIndex == 0 || frame.frameIndex == 1) {
			initPixels(framePixels, bgColor);
		}

		GifGraphicControlExt gcExt = frame.gcExt;
		GifImageDescriptor imgDescriptor = frame.imgDescriptor;
		GifImageData imgData = frame.imgData;
		if (gcExt == null || imgDescriptor == null || imgData == null) {
			status = STATUS_FORMAT_ERROR;
			return;
		}
		// fill in starting image contents based on last image's dispose code
		switch(gcExt.disposal) {
		case 0: break;
		case 1: break;
		case 2:
			GifImageDescriptor lastImgDescriptor = null;
			if (lastFrame != null) {
				lastImgDescriptor = lastFrame.imgDescriptor;
			}
			if (lastImgDescriptor == null) {
				break;
			}
			// fill last image rect area with background color
			int color = 0;
			if (!gcExt.transparentColorFlag) {
				color = bgColor;
			}
			int lastHeight = lastImgDescriptor.height;
			for (int i = 0; i < lastHeight; i++) {
				int n1 = (lastImgDescriptor.offY + i) * width + lastImgDescriptor.offX;
				int n2 = n1 + lastImgDescriptor.width;
				for (int k = n1; k < n2; k++) {
					framePixels[k] = color;
				}
			}
			break;
		case 3:
			//System.arraycopy(lastFramePixels, 0, framePixels, 0, width * height);
			break;
		default: break;
		}

        if (Logger.isDebug()) Log.d(TAG, "graphic control disposal:" + gcExt.disposal);
		int tempColor = 0;
		if (gcExt.transparentColorFlag) {
			tempColor = imgData.colorTable[gcExt.transparentColorIndex];
			imgData.colorTable[gcExt.transparentColorIndex] = 0;    // set transparent color if specified
		}
		// copy each source line to the appropriate place in the destination
		int pass = 1;
		int inc = 8;
		int iline = 0;
		for (int i = 0; i < imgDescriptor.height; i++) {
			int line = i;
			if (imgDescriptor.interlaceFlag) {
				if (iline >= imgDescriptor.height) {
					pass++;
					switch (pass) {
					case 2:
						iline = 4;
						break;
					case 3:
						iline = 2;
						inc = 4;
						break;
					case 4:
						iline = 1;
						inc = 2;
					}
				}
				line = iline;
				iline += inc;
			}

			line += imgDescriptor.offY;
			if (line < height) {
				int k = line * width;
				int dx = k + imgDescriptor.offX; // start of line in dest
				int dlim = dx + imgDescriptor.width; // end of dest line
				if ((k + width) < dlim) {
					dlim = k + width; // past dest edge
				}
				int sx = i * imgDescriptor.width; // start of line in source
				while (dx < dlim) {
					// map color and insert in destination
					int index = ((int) imgData.pixels[sx++]) & 0xff;
					int c = imgData.colorTable[index];
					if (c != 0) {
						framePixels[dx] = c;
					}
					dx++;
				}
			}
		}

		if (gcExt.transparentColorFlag) {
			imgData.colorTable[gcExt.transparentColorIndex] = tempColor;
		}

		lastFrame = frame;
		lock.unlock();
	}

	private void initPixels(int[] pixels, int color) {
	    if (pixels == null) {
	    	return;
	    }
	    for (int i = 0; i < pixels.length; i++) {
	    	pixels[i] = color;
	    }
	}

	private boolean err() {
		return status != STATUS_PARSING;
	}

	private int readByte() {
		int curByte = 0;
		try {
			// read 8-bit value
			curByte = gifStream.read();
		} catch (Exception e) {
			status = STATUS_FORMAT_ERROR;
		}
		return curByte;
	}

	private int readBlock() {
		blockSize = readByte();
		int n = 0;
		if (blockSize > 0) {
			try {
				int count = 0;
				while (n < blockSize) {
					count = gifStream.read(block, n, blockSize - n);
					if (count == -1) {
						break;
					}
					n += count;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (n < blockSize) {
				status = STATUS_FORMAT_ERROR;
			}
		}
		return n;
	}

	private int[] readColorTable(int ncolors) {
		int nbytes = 3 * ncolors;
		int[] tab = null;
		byte[] c = new byte[nbytes];
		int n = 0;
		try {
			n = gifStream.read(c);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (n < nbytes) {
			status = STATUS_FORMAT_ERROR;
		} else {
			tab = new int[256]; // max size to avoid bounds checks
			int i = 0;
			int j = 0;
			while (i < ncolors) {
				//TODO: 这里是否需要和0xff做“与”操作？
				int r = ((int) c[j++]) & 0xff;
				int g = ((int) c[j++]) & 0xff;
				int b = ((int) c[j++]) & 0xff;
				tab[i++] = 0xff000000 | (r << 16) | (g << 8) | b;
			}
		}
		return tab;
	}



	private GifGraphicControlExt readGraphicControlExt() {
		GifGraphicControlExt ext = new GifGraphicControlExt();
		readByte(); // block size
		int packed = readByte(); // packed fields
		ext.disposal = (packed & 0x1c) >> 2; // disposal method
		if (ext.disposal == 0) {
			ext.disposal = 1; // elect to keep old image if discretionary
		}
		ext.transparentColorFlag = (packed & 1) != 0;
		ext.delay = readShort() * 10;           // delay in milliseconds
		ext.transparentColorIndex = readByte(); // transparent color index
		readByte(); // block terminator

		return ext;
	}

	private void readNetscapeExt() {
		do {
			readBlock();
			if (block[0] == 1) {
				// loop count sub-block
				int b1 = ((int) block[1]) & 0xff;
				int b2 = ((int) block[2]) & 0xff;
				loopCount = (b2 << 8) | b1;
			}
		} while ((blockSize > 0) && !err());
	}

	private int readShort() {
		// read 16-bit value, LSB first
		return readByte() | (readByte() << 8);
	}

	private void resetFrame() {
		currentImgDescriptor = null;
		currentGCExt = null;
	}

	/**
	 * Skips variable length blocks up to and including next zero length block.
	 */
	private void skip() {
		do {
			readBlock();
		} while ((blockSize > 0) && !err());
	}

	private Bitmap createBitmap() {
		if (image == null || image.isRecycled()) {
			image = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		}
		return image;
	}
}


