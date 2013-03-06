package com.cattong.weibo.entity;

import com.cattong.entity.GeoLocation;

/**
 * 搜索数据类
 *
 * @version
 * @author cattong.com
 * @time 2010-7-24 上午09:54:47
 */
public final class Query implements java.io.Serializable {
	private String query = null;
	private String lang = null;
	private String locale = null;
	private long maxId = -1L;
	private int rpp = -1;
	private int page = -1;
	private String since = null;
	private long sinceId = -1;
	private String geocode = null;
	private String until = null;
	private static final long serialVersionUID = -8108425822233599808L;

	public Query() {
	}

	public Query(String query) {
		this.query = query;
	}

	/**
	 * Returns the specified query
	 *
	 * @return query
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * Sets the query string
	 *
	 * @param query
	 *            the query string
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 * Sets the query string
	 *
	 * @param query
	 *            the query string
	 * @return the instance
	 */
	public Query query(String query) {
		setQuery(query);
		return this;
	}

	/**
	 * Returns the lang
	 *
	 * @return lang
	 */
	public String getLang() {
		return lang;
	}

	/**
	 * restricts tweets to the given language, given by an <a href="http://en.wikipedia.org/wiki/ISO_639-1">ISO 639-1 code</a>
	 *
	 * @param lang
	 *            an <a href="http://en.wikipedia.org/wiki/ISO_639-1">ISO 639-1 code</a>
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}

	/**
	 * restricts tweets to the given language, given by an <a href="http://en.wikipedia.org/wiki/ISO_639-1">ISO 639-1 code</a>
	 *
	 * @param lang
	 *            an <a href="http://en.wikipedia.org/wiki/ISO_639-1">ISO 639-1 code</a>
	 * @return the instance
	 */
	public Query lang(String lang) {
		setLang(lang);
		return this;
	}

	/**
	 * Returns the language of the query you are sending (only ja is currently
	 * effective). This is intended for language-specific clients and the
	 * default should work in the majority of cases.
	 *
	 * @return locale
	 */
	public String getLocale() {
		return locale;
	}

	/**
	 * Specify the language of the query you are sending (only ja is currently
	 * effective). This is intended for language-specific clients and the
	 * default should work in the majority of cases.
	 *
	 * @param locale
	 *            the locale
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}

	/**
	 * Specify the language of the query you are sending (only ja is currently
	 * effective). This is intended for language-specific clients and the
	 * default should work in the majority of cases.
	 *
	 * @param locale
	 *            the locale
	 * @return the instance
	 */
	public Query locale(String locale) {
		setLocale(locale);
		return this;
	}

	/**
	 * Returns tweets with status ids less than the given id.
	 *
	 * @return maxId
	 */
	public long getMaxId() {
		return maxId;
	}

	/**
	 * If specified, returns tweets with status ids less than the given id.
	 *
	 * @param maxId
	 *            maxId
	 */
	public void setMaxId(long maxId) {
		this.maxId = maxId;
	}

	/**
	 * If specified, returns tweets with status ids less than the given id.
	 *
	 * @param maxId
	 *            maxId
	 * @return this instance
	 */
	public Query maxId(long maxId) {
		setMaxId(maxId);
		return this;
	}

	/**
	 * Returns the number of tweets to return per page, up to a max of 100
	 *
	 * @return rpp
	 */
	public int getRpp() {
		return rpp;
	}

	/**
	 * sets the number of tweets to return per page, up to a max of 100
	 *
	 * @param rpp
	 *            the number of tweets to return per page
	 */
	public void setRpp(int rpp) {
		this.rpp = rpp;
	}

	/**
	 * sets the number of tweets to return per page, up to a max of 100
	 *
	 * @param rpp
	 *            the number of tweets to return per page
	 * @return the instance
	 */
	public Query rpp(int rpp) {
		setRpp(rpp);
		return this;
	}

	/**
	 * Returns the page number (starting at 1) to return, up to a max of roughly
	 * 1500 results
	 *
	 * @return the page number (starting at 1) to return
	 */
	public int getPage() {
		return page;
	}

	/**
	 * sets the page number (starting at 1) to return, up to a max of roughly
	 * 1500 results
	 *
	 * @param page
	 *            the page number (starting at 1) to return
	 */
	public void setPage(int page) {
		this.page = page;
	}

	/**
	 * sets the page number (starting at 1) to return, up to a max of roughly
	 * 1500 results
	 *
	 * @param page
	 *            the page number (starting at 1) to return
	 * @return the instance
	 */
	public Query page(int page) {
		setPage(page);
		return this;
	}

	/**
	 * Returns tweets with since the given date. Date should be formatted as
	 * YYYY-MM-DD
	 *
	 * @return since
	 */
	public String getSince() {
		return since;
	}

	/**
	 * If specified, returns tweets with since the given date. Date should be
	 * formatted as YYYY-MM-DD
	 *
	 * @param since
	 *            since
	 */
	public void setSince(String since) {
		this.since = since;
	}

	/**
	 * If specified, returns tweets with since the given date. Date should be
	 * formatted as YYYY-MM-DD
	 *
	 * @param since
	 *            since
	 * @return since
	 */
	public Query since(String since) {
		setSince(since);
		return this;
	}

	/**
	 * returns sinceId
	 *
	 * @return sinceId
	 */
	public long getSinceId() {
		return sinceId;
	}

	/**
	 * returns tweets with status ids greater than the given id.
	 *
	 * @param sinceId
	 *            returns tweets with status ids greater than the given id
	 */
	public void setSinceId(long sinceId) {
		this.sinceId = sinceId;
	}

	/**
	 * returns tweets with status ids greater than the given id.
	 *
	 * @param sinceId
	 *            returns tweets with status ids greater than the given id
	 * @return the instance
	 */
	public Query sinceId(long sinceId) {
		setSinceId(sinceId);
		return this;
	}

	/**
	 * Returns the specified geocode
	 *
	 * @return geocode
	 */
	public String getGeocode() {
		return geocode;
	}

	public static final String MILES = "mi";
	public static final String KILOMETERS = "km";

	/**
	 * returns tweets by users located within a given radius of the given
	 * latitude/longitude, where the user's location is taken from their MicroBlog
	 * profile
	 *
	 * @param location
	 *            geo location
	 * @param radius
	 *            radius
	 * @param unit
	 *            Query.MILES or Query.KILOMETERS
	 */
	public void setGeoCode(GeoLocation location, double radius, String unit) {
		this.geocode = location.getLatitude() + "," + location.getLongitude() + "," + radius + unit;
	}

	/**
	 * returns tweets by users located within a given radius of the given
	 * latitude/longitude, where the user's location is taken from their MicroBlog
	 * profile
	 *
	 * @param location
	 *            geo location
	 * @param radius
	 *            radius
	 * @param unit
	 *            Query.MILES or Query.KILOMETERS
	 * @return the instance
	 */
	public Query geoCode(GeoLocation location, double radius, String unit) {
		setGeoCode(location, radius, unit);
		return this;
	}

	/**
	 * Returns until
	 *
	 * @return until
	 */
	public String getUntil() {
		return until;
	}

	/**
	 * If specified, returns tweets with generated before the given date. Date
	 * should be formatted as YYYY-MM-DD
	 *
	 * @param until
	 *            until
	 */
	public void setUntil(String until) {
		this.until = until;
	}

	/**
	 * If specified, returns tweets with generated before the given date. Date
	 * should be formatted as YYYY-MM-DD
	 *
	 * @param until
	 *            until
	 * @return until
	 */
	public Query until(String until) {
		setUntil(until);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Query query1 = (Query) o;

		if (maxId != query1.maxId)
			return false;
		if (page != query1.page)
			return false;
		if (rpp != query1.rpp)
			return false;
		if (sinceId != query1.sinceId)
			return false;
		if (geocode != null ? !geocode.equals(query1.geocode) : query1.geocode != null)
			return false;
		if (lang != null ? !lang.equals(query1.lang) : query1.lang != null)
			return false;
		if (locale != null ? !locale.equals(query1.locale) : query1.locale != null)
			return false;
		if (query != null ? !query.equals(query1.query) : query1.query != null)
			return false;
		if (since != null ? !since.equals(query1.since) : query1.since != null)
			return false;
		if (until != null ? !until.equals(query1.until) : query1.until != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = query != null ? query.hashCode() : 0;
		result = 31 * result + (lang != null ? lang.hashCode() : 0);
		result = 31 * result + (locale != null ? locale.hashCode() : 0);
		result = 31 * result + (int) (maxId ^ (maxId >>> 32));
		result = 31 * result + rpp;
		result = 31 * result + page;
		result = 31 * result + (since != null ? since.hashCode() : 0);
		result = 31 * result + (int) (sinceId ^ (sinceId >>> 32));
		result = 31 * result + (geocode != null ? geocode.hashCode() : 0);
		result = 31 * result + (until != null ? until.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Query{"
				+ "query='" + query + '\''
				+ ", lang='" + lang + '\''
				+ ", locale='" + locale + '\''
				+ ", maxId=" + maxId
				+ ", rpp=" + rpp
				+ ", page=" + page
				+ ", since='" + since + '\''
				+ ", sinceId=" + sinceId
				+ ", geocode='" + geocode + '\''
				+ ", until='" + until + '\''
				+ '}';
	}
}
