package com.shejiaomao.weibo.service.cache.wrap;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import android.graphics.Bitmap;

public class CachedImage extends Wrap<Bitmap> {
	private static final int REFERENCE_DIVIDER_SIZE = 120 * 100;
    private Reference<Bitmap> ref;
	public CachedImage(Bitmap bitmap) {
        setWrap(bitmap);
		setHit(1);
	}

	@Override
	public Bitmap getWrap() {
		return ref.get();
	}

	@Override
	public void setWrap(Bitmap bitmap) {
		int size = 0;
		if (bitmap != null) {
			size = bitmap.getWidth() * bitmap.getHeight();
		}
		if (size > REFERENCE_DIVIDER_SIZE) {
			this.ref = new WeakReference<Bitmap>(bitmap);
		} else {
			this.ref = new WeakReference<Bitmap>(bitmap);
		}
	}

}
