package com.shejiaomao.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.cattong.commons.http.HttpRequestHelper;
import com.cattong.commons.util.FileUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.commons.LibException;
import com.cattong.commons.Logger;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class ImageUtil {
	private ImageUtil() { }

	public static final String TAG = ImageUtil.class.getSimpleName();

	public static final long MAX_BITMAP_SIZE   = 1024 * 1024 * 4; //允许的图片最大值;
	public static final long SAFE_DISTANCE     = 1024 * 100;      //至少要保留多大预留空间;

	public static final long SCALE_BOUND_SIZE  = 1024;          //图片宽高最小值大于这个时，选择缩小图片而不是画质.

	public static final int ROTATE_MAX_SIZE = 512; //旋转图片时，图片缩放的最大尺寸

	public static final int UNCONSTRAINED = -1;

	/*
	 * 进行图片缩放
	 */
	public static Bitmap scaleBitmapTo(Bitmap bitmap, int targetSize) {
		if (bitmap == null) {
			return null;
		}

		int srcWidth = bitmap.getWidth();
		int srcHeight = bitmap.getHeight();
		int dstWidth = targetSize;
		int dstHeight = targetSize;
		if (srcWidth > srcHeight) {
			dstHeight = ((targetSize * srcHeight) / srcWidth);
        } else {
        	dstWidth = ((targetSize * srcWidth) / srcHeight);
        }

		Bitmap dstBitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, true);
		return dstBitmap;
	}

	/*
	 * 进行图片缩放
	 */
	public static Bitmap scaleImageFile(File imageFile, int size) {
		if (imageFile == null
			|| !imageFile.isFile()
			|| !imageFile.exists()
			|| size  <= 0) {
			return null;
		}

		BitmapFactory.Options options = new BitmapFactory.Options();

		//先指定原始大小
		options.inSampleSize = 1;
		//只进行大小判断
		options.inJustDecodeBounds = true;
		//调用此方法得到options得到图片的大小
		BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
		//获得缩放比例
		options.inSampleSize = getScaleSampleSize(options, size);
		//OK,我们得到了缩放的比例，现在开始正式读入BitMap数据
		options.inJustDecodeBounds = false;
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;

		//根据options参数，减少所需要的内存
		Bitmap sourceBitmap = null;
		sourceBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

		return sourceBitmap;
	}

	/*
	 * 进行图片缩放
	 */
	public static boolean scaleImageFile(File source, File dest, int size) {
		boolean isSuccess = false;
		if (source == null
			|| !source.isFile()
			|| !source.exists()
			|| dest == null
			|| size  <= 0) {
			return isSuccess;
		}

        Bitmap bitmap = scaleImageFile(source, size);
        if (bitmap == null) {
        	return isSuccess;
        }

        FileOutputStream fos = null;
        try {
        	if (!dest.exists()) {
        	   dest.createNewFile();
        	}
			fos = new FileOutputStream(dest);
            bitmap.compress(getCompressFormat(dest), 100, fos);
            isSuccess = true;
        } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
			    try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		bitmap.recycle();
		return isSuccess;
	}

	/*
	 * 进行图片缩放
	 * @param: resolver
	 * @param: url
	 * @param: size 目标缩放大小
	 */
	public static Bitmap scaleImageUriTo(ContentResolver resolver, Uri uri, int size) {
		if (resolver == null
			|| uri   == null
			|| size  <= 0) {
			return null;
		}

		ParcelFileDescriptor pfd;
		try {
		     pfd = resolver.openFileDescriptor(uri, "r");
		} catch (IOException e) {
			Logger.debug(e.getMessage(), e);
		    return null;
		}
		
		java.io.FileDescriptor fd = pfd.getFileDescriptor();
		BitmapFactory.Options options = new BitmapFactory.Options();

		//先指定原始大小
		options.inSampleSize = 1;
		//只进行大小判断
		options.inJustDecodeBounds = true;
		//调用此方法得到options得到图片的大小
		BitmapFactory.decodeFileDescriptor(fd, null, options);
		//获得缩放比例
		options.inSampleSize = getScaleSampleSize(options, size);
		//OK,我们得到了缩放的比例，现在开始正式读入BitMap数据
		options.inJustDecodeBounds = false;
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;

		//根据options参数，减少所需要的内存
		Bitmap sourceBitmap = null;
		sourceBitmap = BitmapFactory.decodeFileDescriptor(fd, null, options);

		return sourceBitmap;
	}

	/*
	 * 这个函数会对图片的大小进行判断，并得到合适的缩放比例，比如2即1/2,3即1/3
	 * @target: 要缩放成的宽或高
	 *
	 * @return: 缩放比例
	 */
	public static int getScaleSampleSize(BitmapFactory.Options options, int target) {
		if (options == null || target <= 0) {
			return 1;
		}

	    int w = options.outWidth;
	    int h = options.outHeight;
	    int candidateW = w / target;
	    int candidateH = h / target;
        int candidate = Math.max(candidateW, candidateH);
	    if (candidate == 0)
            return 1;
	    if (candidate > 1) {
	        if ((w > target) && (w / candidate) < target) {
	            candidate -= 1;
	        }
	    }
	    if (candidate > 1) {
	        if ((h > target) && (h / candidate) < target) {
	            candidate -= 1;
	        }
	    }

	    return candidate;
	}

    /*
     * 图片处理成圆角
     */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}

	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
	        bitmap.getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);

	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	    final RectF rectF = new RectF(rect);
	    final int minBound = Math.min(bitmap.getWidth(), bitmap.getHeight());
	    final float roundPx = minBound * 4/50;

	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);

	    return output;
	}

	/*
	 * 获取网络上的图片，用于取代BitmapFactory.decodeStream(is);
	 * 防止由于设备或网络不好，无法取到图片;
	 */
	public static Bitmap getBitmapByUrl(String imgUrl) throws LibException {
		Bitmap bitmap = null;
		byte[] imgBytes = getByteArrayByUrl(imgUrl);
		if (imgBytes == null) {
			return bitmap;
		}

		bitmap = decodeByteArray(imgBytes);
		return bitmap;
	}

	public static byte[] getByteArrayByUrl(String imgUrl) throws LibException {
		if (StringUtil.isEmpty(imgUrl)) {
			return null;
		}

		byte[] imgBytes = HttpRequestHelper.getContentBytes(imgUrl);
		return imgBytes;
	}

	public static File getFileByUrl(String imgUrl, File destFile) throws LibException {
		if (StringUtil.isEmpty(imgUrl)
			|| destFile == null) {
			return null;
		}

		File saveFile = HttpRequestHelper.getBitmapFile(imgUrl, destFile);
		return saveFile;
	}

	public static Bitmap decodeByteArray(byte[] imgBytes) {
		Bitmap bitmap = null;
		if (imgBytes == null || imgBytes.length == 0) {
			return bitmap;
		}

	    BitmapFactory.Options options = new BitmapFactory.Options();
		//先指定原始大小
		options.inSampleSize = 1;
		//只进行大小判断
		options.inJustDecodeBounds = true;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888; //Bitmap.Config.ARGB_8888 像素深度为4，导致占用内存加倍
	    BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length, options);

	    long bitmapSize = ImageUtil.calculateBitmapSize(options);
	    if (bitmapSize - ImageUtil.SAFE_DISTANCE >= MemoryManager.getAvailableNativeMemorySize()
	    	|| bitmapSize - ImageUtil.SAFE_DISTANCE >= ImageUtil.MAX_BITMAP_SIZE) {
	    	Log.d(TAG, "Image size(" + bitmapSize + ") is beyond allowed Size, we will first reclaim memory!");

	    	Runtime.getRuntime().gc();

	    	if (Math.min(options.outWidth, options.outHeight) > ImageUtil.SCALE_BOUND_SIZE) {
	    		options.inSampleSize = 2;
	    	} else {
	    	    options.inPreferredConfig = Bitmap.Config.ARGB_4444; //降低清晰度;
	    	}

	    	BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length, options);
	    	bitmapSize = ImageUtil.calculateBitmapSize(options);
	    }
	    if (bitmapSize >= MemoryManager.getAvailableNativeMemorySize()
	    	|| bitmapSize >= ImageUtil.MAX_BITMAP_SIZE) {
	    	Log.d(TAG, "Image size(" + bitmapSize + ") is beyond allowed Size, but will scale it!");

	    	long targetSize = Math.min(MemoryManager.getAvailableNativeMemorySize(), ImageUtil.MAX_BITMAP_SIZE);
	    	int size = (options.inSampleSize > 0 ? options.inSampleSize : 1);
	    	options.inJustDecodeBounds = true;
	    	while (bitmapSize > targetSize) {
	    		size++;
	    		options.inSampleSize = size;
	    		BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length, options);
	    		bitmapSize = ImageUtil.calculateBitmapSize(options);
	    	}

			Log.d(TAG, "after scaled, Image size is (" + bitmapSize + ")");
	    }

		options.inJustDecodeBounds = false;
		options.inDither = false;

		if (Logger.level <= Logger.DEBUG) MemoryManager.trace();

		bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length, options);
		return bitmap;
	}

	/*
	 * 计算图片在内存中，占的大小
	 * 2 是像素的深度，一般占用两个字节
	 */
	public static long calculateBitmapSize(BitmapFactory.Options options) {
		int depth = 2;
		int sampleSize = 1;
		if (options.inPreferredConfig == null) {
			depth = 2;
		} else {
		    switch (options.inPreferredConfig) {
		    case ARGB_4444: depth = 2; break;
		    case ARGB_8888: depth = 4; break;
		    default: depth = 2; break;
		    }
		}

		if (options.inSampleSize > 1) {
			sampleSize = options.inSampleSize;
		}

		Logger.debug("image widht:{}, hight:{}, inSampleSize:", 
			options.outWidth, options.outHeight, sampleSize);

		//options.outWidth * options.outHeight * depth / sampleSize
		return options.outWidth * options.outHeight * depth; 
	}

	/**
	 * 旋转图片
	 *
	 * @param bitmap 图片
	 * @param degrees 角度
	 * @return 旋转后的图片Bitmap对象
	 */
	public static Bitmap rotate(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix matrix = new Matrix();
            matrix.setRotate(degrees,
            		(float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
            try {
                Bitmap rotated = Bitmap.createBitmap(
                		bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                if (bitmap != rotated) {
                	bitmap.recycle();
                	bitmap = rotated;
                }
            } catch (OutOfMemoryError ex) {
                // We have no memory to rotate. Return the original bitmap.
            }
        }
        return bitmap;
	}

	/**
	 * 旋转图片文件
	 *
	 * @param imageFile 图片文件
	 * @param degrees 角度
	 * @return 缩放并旋转后的图片文件
	 * @throws IOException
	 */
	public static boolean rotateImageFile(File imageFile, File dest,  int degrees) {
		boolean isSuccess = false;
		if (imageFile == null
			|| !imageFile.isFile()
			|| !imageFile.exists()
			|| dest == null) {
			return isSuccess;
		}

		Bitmap bitmap = scaleImageFile(imageFile, ROTATE_MAX_SIZE);
        Bitmap rotated = rotate(bitmap, degrees); //翻转图片

        FileOutputStream fileOutputStream = null;
        try {
        	if (!dest.exists()) {
        		dest.createNewFile();
        	}

        	fileOutputStream = new FileOutputStream(dest);

        	rotated.compress(getCompressFormat(dest), 100, fileOutputStream);

        	isSuccess = true;
        } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileOutputStream != null) {
			    try {
			    	fileOutputStream.flush();
			    	fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		bitmap.recycle();
		rotated.recycle();

		return isSuccess;
	}

	private static Bitmap.CompressFormat getCompressFormat(File file) {
		Bitmap.CompressFormat format = null;
		try {
			String fileExtension = FileUtil.getFileExtensionFromName(file.getName());
			format = Bitmap.CompressFormat.valueOf(fileExtension);
		} catch (Exception e) {
			format = Bitmap.CompressFormat.JPEG;
		}
		return format;
	}

	public static Uri getImageUri(String path) {
		return Uri.fromFile(new File(path));
	}

	public static Bitmap getBitmap(String path) {
		try {
			InputStream in = new FileInputStream(path);
			return BitmapFactory.decodeStream(in);
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	public static BitmapFactory.Options getBitmapOptions(String path) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		if (StringUtil.isEmpty(path)) {
			return options;
		} 
		
		File imageFile = new File(path);
		if (imageFile == null
			|| !imageFile.isFile()
			|| !imageFile.exists()) {
			return options;
		}

		options = new BitmapFactory.Options();

		//先指定原始大小
		options.inSampleSize = 1;
		//只进行大小判断
		options.inJustDecodeBounds = true;
		//调用此方法得到options得到图片的大小
		BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

		return options;
	}
	
    public static final int OPTIONS_NONE = 0x0;
    public static final int OPTIONS_SCALE_UP = 0x1;
    /**
     * Constant used to indicate we should recycle the input in
     * {@link #extractThumbnail(Bitmap, int, int, int)} unless the output is the input.
     */
    public static final int OPTIONS_RECYCLE_INPUT = 0x2;

    /**
     * Creates a centered bitmap of the desired size.
     *
     * @param source original bitmap source
     * @param width targeted width
     * @param height targeted height
     */
    public static Bitmap extractThumbnail(
            Bitmap source, int width, int height) {
        return extractThumbnail(source, width, height, OPTIONS_NONE);
    }

    /**
     * Creates a centered bitmap of the desired size.
     *
     * @param source original bitmap source
     * @param width targeted width
     * @param height targeted height
     * @param options options used during thumbnail extraction
     */
    public static Bitmap extractThumbnail(
            Bitmap source, int width, int height, int options) {
        if (source == null) {
            return null;
        }

        float scale;
        if (source.getWidth() < source.getHeight()) {
            scale = width / (float) source.getWidth();
        } else {
            scale = height / (float) source.getHeight();
        }
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        Bitmap thumbnail = transform(matrix, source, width, height,
                OPTIONS_SCALE_UP | options);
        return thumbnail;
    }

    /**
     * Transform source Bitmap to targeted width and height.
     */
    public static Bitmap transform(Matrix scaler,
            Bitmap source,
            int targetWidth,
            int targetHeight,
            int options) {
        boolean scaleUp = (options & OPTIONS_SCALE_UP) != 0;
        boolean recycle = (options & OPTIONS_RECYCLE_INPUT) != 0;

        int deltaX = source.getWidth() - targetWidth;
        int deltaY = source.getHeight() - targetHeight;
        if (!scaleUp && (deltaX < 0 || deltaY < 0)) {
            /*
            * In this case the bitmap is smaller, at least in one dimension,
            * than the target.  Transform it by placing as much of the image
            * as possible into the target and leaving the top/bottom or
            * left/right (or both) black.
            */
            Bitmap b2 = Bitmap.createBitmap(targetWidth, targetHeight,
                Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b2);

            int deltaXHalf = Math.max(0, deltaX / 2);
            int deltaYHalf = Math.max(0, deltaY / 2);
            Rect src = new Rect(
            deltaXHalf,
            deltaYHalf,
            deltaXHalf + Math.min(targetWidth, source.getWidth()),
            deltaYHalf + Math.min(targetHeight, source.getHeight()));
            int dstX = (targetWidth  - src.width())  / 2;
            int dstY = (targetHeight - src.height()) / 2;
            Rect dst = new Rect(
                    dstX,
                    dstY,
                    targetWidth - dstX,
                    targetHeight - dstY);
            c.drawBitmap(source, src, dst, null);
            if (recycle) {
                source.recycle();
            }
            return b2;
        }
        float bitmapWidthF = source.getWidth();
        float bitmapHeightF = source.getHeight();

        float bitmapAspect = bitmapWidthF / bitmapHeightF;
        float viewAspect   = (float) targetWidth / targetHeight;

        if (bitmapAspect > viewAspect) {
            float scale = targetHeight / bitmapHeightF;
            if (scale < .9F || scale > 1F) {
                scaler.setScale(scale, scale);
            } else {
                scaler = null;
            }
        } else {
            float scale = targetWidth / bitmapWidthF;
            if (scale < .9F || scale > 1F) {
                scaler.setScale(scale, scale);
            } else {
                scaler = null;
            }
        }

        Bitmap b1;
        if (scaler != null) {
            // this is used for minithumb and crop, so we want to filter here.
            b1 = Bitmap.createBitmap(source, 0, 0,
            source.getWidth(), source.getHeight(), scaler, true);
        } else {
            b1 = source;
        }

        if (recycle && b1 != source) {
            source.recycle();
        }

        int dx1 = Math.max(0, b1.getWidth() - targetWidth);
        int dy1 = Math.max(0, b1.getHeight() - targetHeight);

        Bitmap b2 = Bitmap.createBitmap(
                b1,
                dx1 / 2,
                dy1 / 2,
                targetWidth,
                targetHeight);

        if (b2 != b1) {
            if (recycle || b1 != source) {
                b1.recycle();
            }
        }

        return b2;
    }

    /*
     * Compute the sample size as a function of minSideLength and maxNumOfPixels.
     * minSideLength is used to specify that minimal width or height of a bitmap.
     * maxNumOfPixels is used to specify the maximal size in pixels that is
     * tolerable in terms of memory usage.
     *
     * The function returns a sample size based on the constraints.
     * Both size and minSideLength can be passed in as UNCONSTRAINED,
     * which indicates no care of the corresponding constraint.
     * The functions prefers returning a sample size that
     * generates a smaller bitmap, unless minSideLength = UNCONSTRAINED.
     *
     * Also, the function rounds up the sample size to a power of 2 or multiple
     * of 8 because BitmapFactory only honors sample size this way.
     * For example, BitmapFactory downsamples an image by 2 even though the
     * request is 3. So we round up the sample size to avoid OOM.
     */
    public static int computeSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8 ) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    public static int computeInitialSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == UNCONSTRAINED) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == UNCONSTRAINED) &&
                (minSideLength == UNCONSTRAINED)) {
            return 1;
        } else if (minSideLength == UNCONSTRAINED) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    private static int computeSampleSize(InputStream stream, int maxResolutionX,
        int maxResolutionY) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(stream, null, options);
        int maxNumOfPixels = maxResolutionX * maxResolutionY;
        int minSideLength = Math.min(maxResolutionX, maxResolutionY) / 2;
        return computeSampleSize(options, minSideLength, maxNumOfPixels);
    }

    public static final Bitmap createBitmapFromUri(Context context, String uri,
    		int maxResolutionX, int maxResolutionY) throws FileNotFoundException {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inDither = true;
        Bitmap bitmap = null;

        // Get the input stream for computing the sample size.
        BufferedInputStream bufferedInput = null;
        if (uri.startsWith(ContentResolver.SCHEME_CONTENT)
        	|| uri.startsWith(ContentResolver.SCHEME_FILE)) {
            // Get the stream from a local file.
            bufferedInput = new BufferedInputStream(context.getContentResolver()
                    .openInputStream(Uri.parse(uri)), 16348); // 16 * 1024
        }

        // Compute the sample size, i.e., not decoding real pixels.
        if (bufferedInput != null) {
            options.inSampleSize = computeSampleSize(bufferedInput, maxResolutionX, maxResolutionY);
        } else {
            return null;
        }

        // Get the input stream again for decoding it to a bitmap.
        bufferedInput = null;
        if (uri.startsWith(ContentResolver.SCHEME_CONTENT) ||
                uri.startsWith(ContentResolver.SCHEME_FILE)) {
            // Get the stream from a local file.
            bufferedInput = new BufferedInputStream(context.getContentResolver()
                    .openInputStream(Uri.parse(uri)), 16384);
        }

        // Decode bufferedInput to a bitmap.
        if (bufferedInput != null) {
            options.inDither = false;
            options.inJustDecodeBounds = false;
            Thread timeoutThread = new Thread("BitmapTimeoutThread") {
                public void run() {
                    try {
                        Thread.sleep(6000);
                        options.requestCancelDecode();
                    } catch (InterruptedException e) {
                    }
                }
            };
            timeoutThread.start();

            bitmap = BitmapFactory.decodeStream(new FlushedInputStream(bufferedInput), null, options);
        }
        return bitmap;
    }

    public static final Bitmap resizeBitmap(Bitmap bitmap, int maxSize) {
        int srcWidth = bitmap.getWidth();
        int srcHeight = bitmap.getHeight();
        int width = maxSize;
        int height = maxSize;
        boolean needsResize = false;
        if (srcWidth > srcHeight) {
            if (srcWidth > maxSize) {
                needsResize = true;
                height = ((maxSize * srcHeight) / srcWidth);
            }
        } else {
            if (srcHeight > maxSize) {
                needsResize = true;
                width = ((maxSize * srcWidth) / srcHeight);
            }
        }
        if (needsResize) {
            Bitmap retVal = Bitmap.createScaledBitmap(bitmap, width, height, true);
            return retVal;
        } else {
            return bitmap;
        }
    }


    /**
     * @see <a href='http://code.google.com/p/android/issues/detail?id=6066'>
     * 			<code>BitmapFactory.decodeStream()</code> fails
     * 			if <code>InputStream.skip()</code> does not skip fully
     *		</a>
     */
    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                      if (read() < 0) {
                          break;  // we reached EOF
                      } else {
                          bytesSkipped = 1; // we read one byte
                      }
               }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }

}
