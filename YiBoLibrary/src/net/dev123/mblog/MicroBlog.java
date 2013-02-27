package net.dev123.mblog;

import java.io.File;
import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.commons.PagingSupport;
import net.dev123.commons.http.auth.Authorization;
import net.dev123.commons.http.auth.BasicAuthorization;
import net.dev123.commons.http.auth.OAuthAuthorization;
import net.dev123.entity.Location;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.api.AccountMethods;
import net.dev123.mblog.api.BlockMethods;
import net.dev123.mblog.api.CommentMethods;
import net.dev123.mblog.api.CountMethods;
import net.dev123.mblog.api.DirectMessageMethods;
import net.dev123.mblog.api.FavoriteMethods;
import net.dev123.mblog.api.FriendshipMethods;
import net.dev123.mblog.api.GroupMembersMethods;
import net.dev123.mblog.api.GroupMethods;
import net.dev123.mblog.api.GroupSubscribersMethods;
import net.dev123.mblog.api.LocationMethods;
import net.dev123.mblog.api.SocialGraphMethods;
import net.dev123.mblog.api.StatusMethods;
import net.dev123.mblog.api.TimelineMethods;
import net.dev123.mblog.api.TrendsMethods;
import net.dev123.mblog.api.UserMethods;
import net.dev123.mblog.conf.ApiConfiguration;
import net.dev123.mblog.conf.ApiConfigurationFactory;
import net.dev123.mblog.entity.Status;
import net.dev123.mblog.entity.Trend;

/**
 * MicroBlogBase
 *
 * @version
 * @author 马庆升
 * @time 2010-8-1 上午10:49:29
 */
public abstract class MicroBlog extends PagingSupport implements java.io.Serializable,
        TimelineMethods, StatusMethods,	UserMethods, DirectMessageMethods,
        FriendshipMethods, SocialGraphMethods, AccountMethods, FavoriteMethods,
		CommentMethods, CountMethods, TrendsMethods, BlockMethods, GroupMethods,
		GroupMembersMethods, GroupSubscribersMethods, LocationMethods {
	private static final long serialVersionUID = -3812176145960812140L;

	protected final ApiConfiguration conf;
	protected Authorization auth;

	public MicroBlog(Authorization auth) {
		this.auth = auth;
		this.conf = ApiConfigurationFactory.getApiConfiguration(auth.getServiceProvider());
	}

	public abstract String getScreenName() throws LibException;

	public abstract String getUserId() throws LibException;

	/**
	 * tests if the instance is authenticated by Basic
	 *
	 * @return returns true if the instance is authenticated by Basic
	 */
	public final boolean isBasicAuthEnabled() {
		return auth instanceof BasicAuthorization;
	}

	/**
	 * tests if the instance is authenticated by OAuth
	 *
	 * @return returns true if the instance is authenticated by OAuth
	 */
	public final boolean isOAuthEnabled() {
		return auth instanceof OAuthAuthorization;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (this == o) {
			return true;
		}
		if (!(o instanceof MicroBlog))
			return false;

		MicroBlog that = (MicroBlog) o;

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
	 * @author Weiping Ye
	 * @version 创建时间: 2011-7-28 15:54:36
	 */
	public void checkFileValidity(File image) throws LibException {
		if (image == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (!image.exists()) {
			throw new LibException(ExceptionCode.FILE_NOT_FOUND, image.getName() + " do't exist!");
		}
		if (!image.isFile()) {
			throw new LibException(ExceptionCode.NOT_A_FILE, image.getName() + " is not a file!");
		}
	}

	/**
	 * 默认用新浪的接口。
	 */
	@Override
	public Location getLocationByCoordinate(double latitude, double longitude)
			throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public List<Trend> getUserTrends(String userId, Paging<Trend> paging)
			throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public List<Status> getUserTrendsStatus(String trendName, Paging<Status> paging)
			throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

}
