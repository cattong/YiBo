package com.cattong.sns;

import java.io.File;
import java.util.List;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import com.cattong.commons.LibException;
import com.cattong.commons.Paging;
import com.cattong.sns.entity.Album;
import com.cattong.sns.entity.Photo;


public class AlbumMethods {
	private static Sns sns = null;
	private static final String IMAGE_PATH = "H:\\renren.png";

	@BeforeClass
	public static void beforClass() throws LibException {
		sns = TokenConfig.getSns(TokenConfig.currentProvider);
	}

	@Test
	public void uploadPhoto() throws LibException {
		boolean result = sns.uploadPhoto(new File(IMAGE_PATH), "图片上传测试");
		Assert.assertTrue(result);
	}

	@Test
	public void showPhoto() throws LibException {
		Paging<Album> paging = new Paging<Album>();
		paging.moveToFirst();
		List<Album> albums = sns.getAlbums(sns.getUserId(), paging);
		if (albums != null && albums.size() > 0) {
			String albumId = albums.get(0).getId();
			sns.uploadPhoto(new File(IMAGE_PATH), albumId, "图片上传测试");
			Paging<Photo> photoPaging = new Paging<Photo>();
			photoPaging.moveToFirst();
			List<Photo> photos = sns.getAlbumPhotos(albumId, sns.getUserId(), photoPaging);
			if (photos != null && photos.size() > 0) {
				Photo photo = sns.showPhoto(photos.get(0).getId(), null);
				Assert.assertTrue(photo != null);
			} else {
				Assert.assertTrue(false);
			}
		} else {
			Assert.assertTrue(false);
		}
	}

	@Test
	public void destroyPhoto() throws LibException {

	}

	@Test
	public void createAlbum() throws LibException {
		boolean result = sns.createAlbum("测试相册" + System.currentTimeMillis(),
				"测试相册" + System.currentTimeMillis(), null);
		Assert.assertTrue(result);
	}

	@Test
	public void destroyAlbum() throws LibException {

	}

	@Test
	public void getAlbums() throws LibException {
		Paging<Album> paging = new Paging<Album>();
		paging.moveToFirst();
		List<Album> albums = sns.getAlbums(sns.getUserId(), paging);
		Assert.assertTrue(albums != null);
	}

	@Test
	public void getAlbumPhotos() throws LibException {
		Paging<Album> paging = new Paging<Album>();
		paging.moveToFirst();
		List<Album> albums = sns.getAlbums(sns.getUserId(), paging);
		if (albums != null && albums.size() > 0) {
			String albumId = albums.get(0).getId();
			sns.uploadPhoto(new File(IMAGE_PATH), albumId, "图片上传测试");
			Paging<Photo> photoPaging = new Paging<Photo>();
			photoPaging.moveToFirst();
			List<Photo> photos = sns.getAlbumPhotos(albumId, sns.getUserId(), photoPaging);
			Assert.assertTrue(photos != null);
		} else {
			Assert.assertTrue(false);
		}
	}

}
