package com.cattong.weibo;

import java.io.File;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.PagingSupport;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.entity.Location;
import com.cattong.weibo.api.AccountService;
import com.cattong.weibo.api.BlockService;
import com.cattong.weibo.api.CommentService;
import com.cattong.weibo.api.CountService;
import com.cattong.weibo.api.DirectMessageService;
import com.cattong.weibo.api.FavoriteService;
import com.cattong.weibo.api.FriendshipService;
import com.cattong.weibo.api.GroupMembersService;
import com.cattong.weibo.api.GroupService;
import com.cattong.weibo.api.LocationService;
import com.cattong.weibo.api.StatusService;
import com.cattong.weibo.api.TimelineService;
import com.cattong.weibo.api.UserService;
import com.cattong.weibo.conf.ApiConfig;
import com.cattong.weibo.conf.ApiConfigFactory;


public abstract class Weibo extends PagingSupport implements java.io.Serializable,
        TimelineService, StatusService,	UserService, DirectMessageService,
        FriendshipService, AccountService, FavoriteService,
		CommentService, CountService, BlockService, GroupService,
		GroupMembersService, LocationService {
	private static final long serialVersionUID = 2899023676683266230L;
	
	protected final ApiConfig conf;
	protected Authorization auth;

	public Weibo(Authorization auth) {
		this.auth = auth;
		this.conf = ApiConfigFactory.getApiConfig(auth);
	}

	public abstract String getScreenName() throws LibException;

	public abstract String getUserId() throws LibException;

	/**
	 * tests if the instance is authenticated by Basic
	 *
	 * @return returns true if the instance is authenticated by Basic
	 */
	public final boolean isBasicAuthEnabled() {
		return auth.getAuthVersion() == Authorization.AUTH_VERSION_BASIC;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (this == o) {
			return true;
		}
		if (!(o instanceof Weibo))
			return false;

		Weibo that = (Weibo) o;

		if (!auth.equals(that.auth))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return auth != null ? auth.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "MicroBlog{" + "auth=" + auth + '}';
	}

	public void setAuthorization(Authorization auth) {
		this.auth = auth;
	}

	public Authorization getAuthorization() {
		return auth;
	}

	/**
	 * 判断文件合法性
	 *
	 * @param image
	 *            将被上传的图片文件
	 * @throws LibException
	 * @author cattong.com
	 * @version 创建时间: 2011-7-28 15:54:36
	 */
	public void checkFileValidity(File image) throws LibException {
		if (image == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (!image.exists()) {
			throw new LibException(LibResultCode.FILE_NOT_FOUND);
		}
		if (!image.isFile()) {
			throw new LibException(LibResultCode.FILE_TYPE_INVALID);
		}
	}

	/**
	 * 获取坐标。
	 */
	@Override
	public Location getLocationByCoordinate(double latitude, double longitude)
			throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

}
