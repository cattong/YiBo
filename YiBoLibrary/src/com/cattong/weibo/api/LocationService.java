package com.cattong.weibo.api;

import com.cattong.commons.LibException;
import com.cattong.entity.Location;

/**
 * @author cattong.com
 * @version 创建时间：2011-8-19 下午5:44:20
 **/
public interface LocationService {
	/**
	 * 根据坐标返回地址信息。
	 * @param latitude 经度
	 * @param longitude 纬度
	 * @return 地址信息
	 * @throws LibException
	 */
	public Location getLocationByCoordinate(double latitude, double longitude)
			throws LibException;

//	/**
//	 * 返回指定位置附近或者符合搜索关键字的地点列表。
//	 * @param latitude 经度
//	 * @param longitude 纬度
//	 * @param paging 分页
//	 * @return 地点列表
//	 * @throws LibException
//	 */
//	public List<GeoLocation> findGeoLocationsByCoordinate(double latitude,
//			double longitude, Paging paging) throws LibException;
}
