package com.shejiaomao.weibo.widget;

import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.cattong.commons.Logger;
import com.shejiaomao.widget.ImageViewTouchBase;

/**
 * GifView<br>
 * 本类可以显示一个gif动画，其使用方法和android的其它view（如imageview)一样。<br>
 * 如果要显示的gif太大，会出现OOM的问题。
 */
public class GifView implements GifAction {

	/**gif解码器*/
	private GifDecoder gifDecoder = null;
	/**当前要画的帧的图*/
	private Bitmap currentImage = null;

	private boolean isRun = true;

	private boolean pause = false;

	private DrawThread drawThread = null;

	private GifImageType animationType = GifImageType.SYNC_DECODER;

	/**
	 * 解码过程中，Gif动画显示的方式<br>
	 * 如果图片较大，那么解码过程会比较长，这个解码过程中，gif如何显示
	 * @author liao
	 *
	 */
	public enum GifImageType {
		/**
		 * 在解码过程中，不显示图片，直到解码全部成功后，再显示
		 */
		WAIT_FINISH (0),
		/**
		 * 和解码过程同步，解码进行到哪里，图片显示到哪里
		 */
		SYNC_DECODER (1),
		/**
		 * 在解码过程中，只显示第一帧图片
		 */
		COVER (2);

		GifImageType(int i) {
			nativeInt = i;
		}
		final int nativeInt;
	}

    private Context context;
	private ImageViewTouchBase displayView;
	public GifView(Context context, ImageViewTouchBase displayView) {
		this.context = context;
		this.displayView = displayView;
	}

    /**
     * 设置图片，并开始解码
     * @param gif 要设置的图片
     */
    public void setGifImage(byte[] gifDatabyte) {
    	if (gifDecoder != null) {
    		gifDecoder.free();
    		gifDecoder = null;
    	}
    	gifDecoder = new GifDecoder(gifDatabyte, this);
    	gifDecoder.start();
    }

    /**
     * 设置图片，开始解码
     * @param is 要设置的图片
     */
    public void setGifImage(InputStream is) {
    	if (gifDecoder != null) {
    		gifDecoder.free();
    		gifDecoder = null;
    	}
    	gifDecoder = new GifDecoder(is, this);
    	gifDecoder.start();
    }

    /**
     * 以资源形式设置gif图片
     * @param resId gif图片的资源ID
     */
    public void setGifImage(int resId) {
    	Resources r = context.getResources();
    	InputStream is = r.openRawResource(resId);
    	setGifImage(is);
    }

    /**
     * 只显示第一帧图片<br>
     * 调用本方法后，gif不会显示动画，只会显示gif的第一帧图
     */
    public void showCover() {
    	if (gifDecoder == null) {
    		return;
    	}
    	pause = true;
    	currentImage = gifDecoder.getImage();
    	reDraw();
    }

    /**
     * 继续显示动画<br>
     * 本方法在调用showCover后，会让动画继续显示，如果没有调用showCover方法，则没有任何效果
     */
    public void showAnimation() {
    	if (pause) {
    		pause = false;
    	}
    }

    /**
     * 设置gif在解码过程中的显示方式<br>
     * <strong>本方法只能在setGifImage方法之前设置，否则设置无效</strong>
     * @param type 显示方式
     */
    public void setGifImageType(GifImageType type) {
    	if (gifDecoder == null) {
    		animationType = type;
    	}
    }

    public void parseOk(boolean parseStatus, int frameIndex) {
    	isRun = true;
    	if (!parseStatus || gifDecoder == null) {
    		if (Logger.isDebug()) Log.e("gif","parse error");
    		return;
    	}

		switch(animationType) {
		case WAIT_FINISH:
			if (frameIndex == -1) {
				if (gifDecoder.getFrameCount() >= 1) {     //当帧数大于1时，启动动画线程
    				DrawThread dt = new DrawThread();
    	    		dt.start();
    			} else {
    				reDraw();
    			}
			}
			break;
		case COVER:
			if (frameIndex == 1) {
				GifFrame frame = gifDecoder.next();
				currentImage = frame.image;
				reDraw();
			} else if(frameIndex == -1) {
				if (gifDecoder.getFrameCount() >= 1) {
					if (drawThread == null) {
						drawThread = new DrawThread();
						drawThread.start();
					}
				} else {
					reDraw();
				}
			}
			break;
		case SYNC_DECODER:
			if (frameIndex == 1) {
				GifFrame frame = gifDecoder.next();
				currentImage =frame.image;
				reDraw();
			} else if(frameIndex == -1) {
				reDraw();
			} else {
				if (drawThread == null) {
					drawThread = new DrawThread();
					drawThread.start();
				}
			}
			break;
		}


    }

    private void reDraw() {
    	if (redrawHandler != null) {
			Message msg = redrawHandler.obtainMessage();
			redrawHandler.sendMessage(msg);
    	}
    }

    private Handler redrawHandler = new RedrawHandler();

    public void destroy() {
    	isRun = false;
    	if (gifDecoder != null) {
    		gifDecoder.free();
    		gifDecoder = null;
    	}
    	drawThread = null;

    	if (currentImage != null && !currentImage.isRecycled()) {
    		currentImage.recycle();
    	}
    	currentImage = null;
    	pause = false;
    }

    /**
     * 动画线程
     */
    private class DrawThread extends Thread {
    	public void run() {
    		if (gifDecoder == null) {
    			return;
    		}
    		while (isRun) {
    			if (pause == false) {
    				if (gifDecoder == null) {
    					SystemClock.sleep(10);
    					continue;
    				}
    				GifFrame frame = gifDecoder.next();
    				if (frame == null) {
    					SystemClock.sleep(10);
    					continue;
    				}
    				currentImage = frame.image;
    				long sp = frame.delay;
    				if (redrawHandler != null) {
    					Message msg = redrawHandler.obtainMessage();
    					redrawHandler.sendMessage(msg);
    					int status = (gifDecoder != null ? gifDecoder.getStatus()
    						: GifDecoder.STATUS_PARSING);
    					if (status == GifDecoder.STATUS_PARSING) {
    						//sp = sp/4;
    					}
    					SystemClock.sleep(sp);
    				} else {
    					break;
    				}
    			} else {
    				SystemClock.sleep(10);
    			}
    		}
    	}
    }

	private class RedrawHandler extends Handler {
    	@Override
    	public void handleMessage(Message msg) {
    		if (displayView == null
    			|| currentImage == null
    			|| currentImage.isRecycled() == true) {
    			return;
    		}
    		displayView.setImageBitmap(currentImage);
    	}
    }
}
