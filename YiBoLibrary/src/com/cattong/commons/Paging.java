package com.cattong.commons;

import java.util.HashMap;
import java.util.Map;

/**
 * <tt>Paging</tt> 分页工具类<br />
 * <p>
 * <strong>使用说明：</strong><br />
 * <tt>Paging</tt>类，用于封装微博接口分页相关参数；Since和Max值分全局和单页两种，
 * 单页的Since和Max参数值优先级高于全局Since和Max值， 即如果对某页设置了页Since或者Max，
 * 那么设置的全局Since或者Max就会失效；在当前页数据获取完成后，可通过方法 {@link #setNextPageMax(Object)}、
 * {@link #setNextPageMax(Object)}、 {@link #setNextPageCursor(long)}、
 * {@link #setNextPageCursor(String)} 设置下一页所需的Since、Max和Cursor值。
 * </p>
 * <p>
 * 如果需要设置自定义分页参数，可使用 {@link #setAttribution(String, Object)}方法。
 * </p>
 * <p>
 * <strong>分页控制参数Since、Max约定组合如下：</strong><br />
 * <table border="1" borderColor="#000000" cellSpacing="0" cellPadding="1">
 * <tr>
 *   <th>组合方式</th>
 *   <th>功能说明</th>
 * </tr>
 * <tr>
 *   <td><code>Since = null, Max = null</code></td>
 *   <td>获取接口中最新N条数据，N的数量由<code>PageSize</code>参数确定</td>
 * </tr>
 * <tr>
 *   <td><code>Since != null, Max = null</code></td>
 *   <td>获取接口中自<code>Since</code>之后的N条数据(不包含<code>Since</code>)，N的数量由<code>PageSize</code>参数确定</td>
 * </tr>
 * <tr>
 *   <td><code>Since = null, Max != null</code></td>
 *   <td>获取接口中<code>Max</code>之前的N条数据(不包含<code>Max</code>)，N的数量由<code>PageSize</code>参数确定</td>
 * </tr>
 * <tr>
 *   <td><code>Since != null, Max != null</code></td>
 *   <td>获取接口中<code>Since</code>之后，<code>Max</code>之前的N条数据(不包含<code>Since, Max</code>)，N的数量由<code>PageSize</code>参数确定</td>
 * </tr>
 * </table>
 * </p>
 *
 * @version 1.0
 * @author 
 * @time 2010-9-26 下午04:59:03
 */
public class Paging<T> implements java.io.Serializable {

	private static final long serialVersionUID = -3285857427993796670L;

	/** 页Since值Key前缀 */
	private static final String PAGE_SINCE_PREFIX = "SINCE_";
	/** 页Max值Key前缀 */
	private static final String PAGE_MAX_PREFIX = "MAX_";
	/** 页Cursor值Key前缀 */
	private static final String PAGE_CURSOR_PREFIX = "CURSOR_";

	/** 游标分页初始游标值 */
	public static final long CURSOR_START = -1;
	/** 游标分页结束游标值 */
	public static final long CURSOR_END = 0;

	/** 默认每页记录条数 */
	public static final int DEFAULT_PAGE_SIZE = 20;
	/** 最大每页记录条数 */
	public static final int MAX_PAGE_SIZE = 200;

	/** 当前页码 */
	private int pageIndex; // 页索引
	/** 页内所含记录条数 */
	private int pageSize; // 页大小
	/** 当前分页的全局Since，优先级低于单页的Since */
	private T globalSince;
	/** 当前分页的全局Max，优先级低于单页的Max */
	private T globalMax;
	/** 存储分页数据的Map */
	private Map<String, Object> pageSession = new HashMap<String, Object>();

	/** 是否是游标分页 */
	private boolean isCursorPaging;
	/** 是否是最后一页 */
	private boolean isLastPage;

	/**
	 * 无参构造函数，初始化后当前页为第0页
	 */
	public Paging() {
		this.pageIndex = 0;
		this.isLastPage = false;
		this.globalSince = null;
		this.globalMax = null;
	}

	/**
	 * 使用全局Since和Max的构造函数，初始化后当前页为第0页
	 *
	 * @param globalSince
	 *            全局Since
	 * @param globalMax
	 *            全局Max
	 */
	public Paging(T globalSince, T globalMax) {
		this.pageIndex = 0;
		this.isLastPage = false;
		this.globalSince = globalSince;
		this.globalMax = globalMax;
	}

	public void setGlobalSince(T globalSince) {
		this.globalSince = globalSince;
		pageSession.clear();
	}

	public void setGlobalMax(T globalMax) {
		this.globalMax = globalMax;
		pageSession.clear();
	}

	/**
	 * @return 当前分页是否是Cursor分页
	 */
	public boolean isCursorPaging() {
		return isCursorPaging;
	}

	public boolean isPagePaging() {
		return !isCursorPaging;
	}

	/**
	 * @param isLastPage
	 *            是否最后一页
	 */
	public void setLastPage(boolean isLastPage) {
		this.isLastPage = isLastPage;
	}

	/**
	 * @param cursor
	 *            设置下一页Cursor值，仅在Cursor分页时有效
	 */
	protected void setNextPageCursor(String cursor) {
		if (!isCursorPaging) {
			return;
		}
		pageSession.put(PAGE_CURSOR_PREFIX + getNextPageIndex(), cursor);
	}

	/**
	 * @param cursor
	 *            设置下一页Cursor值，仅在Cursor分页时有效
	 */
	protected void setNextPageCursor(long cursor) {
		if (!isCursorPaging) {
			return;
		}
		pageSession.put(PAGE_CURSOR_PREFIX + getNextPageIndex(), String.valueOf(cursor));
	}

	/**
	 * @param entity
	 *            设置下一页的Since值，页Since值优先级高于全局Since值
	 */
	protected void setNextPageSince(T since) {
		pageSession.put(PAGE_SINCE_PREFIX + getNextPageIndex(), since);
	}

	/**
	 * @param entity
	 *            设置下一页Max值，页Max值优先级高于全局Max值
	 */
	protected void setNextPageMax(T max) {
		pageSession.put(PAGE_MAX_PREFIX + getNextPageIndex(), max);
	}

	/**
	 * @return 获取当前页的Max值，若当前页有设置页Max值，则优先返回
	 */
	@SuppressWarnings("unchecked")
	public T getMax() {
		if (pageSession.get(PAGE_MAX_PREFIX + getPageIndex()) != null) {
			return (T) pageSession.get(PAGE_MAX_PREFIX + getPageIndex());
		}

		return this.globalMax;
	}

	/**
	 * @return 获取当前页的Since值，若当前页有设置页Since值，则优先返回
	 */
	@SuppressWarnings("unchecked")
	public T getSince() {
		if (pageSession.get(PAGE_SINCE_PREFIX + getPageIndex()) != null) {
			return (T) pageSession.get(PAGE_SINCE_PREFIX + getPageIndex());
		}

		return this.globalSince;
	}

	/**
	 * @return 获取当前页Cursor值
	 */
	public String getCursor() {
		if (pageSession.get(PAGE_CURSOR_PREFIX + getPageIndex()) != null) {
			return String.valueOf(pageSession.get(PAGE_CURSOR_PREFIX + getPageIndex()));
		}

		return null;
	}

	protected int getNextPageIndex() {
		return pageIndex + 1;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageSize() {
		if (pageSize > 0) {
			return pageSize;
		}

		return DEFAULT_PAGE_SIZE;
	}

	/**
	 * 设置页内条目数量，调用此方法将重置当前页为第0页
	 *
	 * @param pageSize
	 *            每页记录条数
	 */
	public void setPageSize(int pageSize) {
		if (pageSize <= 0) {
			pageSize = DEFAULT_PAGE_SIZE;
		} else if (pageSize > MAX_PAGE_SIZE) {
			pageSize = MAX_PAGE_SIZE;
		}

		this.pageSize = pageSize;
		//this.reset();
	}

	public boolean isLastPage() {
		return isLastPage;
	}

	public boolean hasNext() {
		return !isLastPage;
	}

	public boolean hasPrevious() {
		return pageIndex > 1;
	}

	/**
	 * 初始化Cursor分页相关属性，初始化之后，当前为第0页
	 */
	public void initCursorPaging() {
		this.reset();
		this.isCursorPaging = true;
		this.setNextPageCursor(CURSOR_START);
	}

	public void initPagePaging() {
		this.reset();
	}

	public void reset() {
		this.pageIndex = 0;
		this.isCursorPaging = false;
		this.isLastPage = false;
		this.pageSession.clear();
	}

	/**
	 * 移动到下一页
	 */
	public boolean moveToNext() {
		if (hasNext()) {
			pageIndex++;
			return true;
		}

		return false;
	}

	/**
	 * 移动到前一页
	 */
	public boolean moveToPrevious() {
		if (hasPrevious()) {
			pageIndex--;
		} else {
			pageIndex = 0;
			isLastPage = false;
		}

		return true;
	}

	/**
	 * 移动到第一页
	 */
	public boolean moveToFirst() {
		this.pageIndex = 1;
		return true;
	}

	/**
	 * 设置自定义的分页属性值
	 *
	 * @param key
	 *            属性Key
	 * @param value
	 *            属性Value
	 */
	public void setAttribute(String key, Object value) {
		pageSession.put(key, value);
	}

	/**
	 * 获取所设置的自定义分页属性值
	 *
	 * @param key
	 * @return
	 */
	public Object getAttribute(String key) {
		return pageSession.get(key);
	}
}
