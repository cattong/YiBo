package net.dev123.mblog.api;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.Status;
import net.dev123.mblog.entity.Trend;
import net.dev123.mblog.entity.Trends;

public interface TrendsMethods {

	/**
	 * 获取最近一小时内的热门话题
	 *
	 * @return 最近一小时内的热门话题
	 * @throws LibException
	 */
	Trends getCurrentTrends() throws LibException;

	/**
	 * 获取最近一天内的热门话题
	 *
	 * @return 最近一天内的热门话题
	 * @throws LibException
	 */
	List<Trends> getDailyTrends() throws LibException;

	/**
	 * 获取最近一周内的热门话题
	 *
	 * @return 最近一周内的热门话题
	 * @throws LibException
	 */
	List<Trends> getWeeklyTrends() throws LibException;
	
	/**
	 * 获取某用户的话题。
	 * @param userId 用户id
	 * @param paging
	 * @return 用户的话题
	 * @throws LibException
	 */
	List<Trend> getUserTrends(String userId, Paging<Trend> paging) throws LibException;
	
	/**
	 * 获取某话题下的微博消息。
	 * @param trendName 话题名称
	 * @return 微博消息
	 * @throws LibException
	 */
	List<Status> getUserTrendsStatus(String trendName, Paging<Status> paging) throws LibException;

}
