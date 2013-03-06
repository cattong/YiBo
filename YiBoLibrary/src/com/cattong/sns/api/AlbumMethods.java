package com.cattong.sns.api;

import java.io.File;
import java.util.List;

import com.cattong.commons.LibException;
import com.cattong.commons.Paging;
import com.cattong.sns.entity.Album;
import com.cattong.sns.entity.Photo;


public interface AlbumMethods {

	boolean uploadPhoto(File photo, String caption) throws LibException;

	Photo showPhoto(String photoId, String ownerId) throws LibException;

	boolean destroyPhoto(String photoId) throws LibException;

	boolean createAlbum(String name, String description, String ownerId) throws LibException;

	boolean uploadPhoto(File photo, String albumId, String caption)
			throws LibException;

	Album showAlbum(String albumId, String ownerId) throws LibException;

	boolean destroyAlbum(String albumId) throws LibException;

	List<Album> getAlbums(String ownerId, Paging<Album> paging)
			throws LibException;

	List<Photo> getAlbumPhotos(String albumId, String ownerId,
			Paging<Photo> paging) throws LibException;

}
