package net.dev123.sns.api;

import java.io.File;
import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.exception.LibException;
import net.dev123.sns.entity.Album;
import net.dev123.sns.entity.Photo;
import net.dev123.sns.entity.Privacy;

public interface MediaMethods {

	boolean uploadPhoto(File photo, String caption) throws LibException;

	Photo showPhoto(String photoId, String ownerId) throws LibException;

	boolean destroyPhoto(String photoId) throws LibException;

	boolean createAlbum(String name, String description, String ownerId,
			Privacy privacy) throws LibException;

	boolean uploadPhoto(File photo, String albumId, String caption)
			throws LibException;

	Album showAlbum(String albumId, String ownerId) throws LibException;

	boolean destroyAlbum(String albumId) throws LibException;

	List<Album> getAlbums(String ownerId, Paging<Album> paging)
			throws LibException;

	List<Photo> getAlbumPhotos(String albumId, String ownerId,
			Paging<Photo> paging) throws LibException;

}
