package com.cattong.commons;

import java.util.List;


public abstract class PagingSupport {

	/**
	 * 更新Paging对象
	 * <p>
	 * 如果是Cursor分页，则设置下一页Cursor值；如果是Page分页， 则根据列表是否为空设置是否是最后一页。
	 * </p>
	 *
	 * @param list
	 *            请求结果列表
	 * @param paging
	 *            paging对象
	 */
	protected void updatePaging(List<?> list, Paging<?> paging) {
		if (list == null || paging == null) {
			return;
		}
		if (paging.isCursorPaging() && list instanceof PagableList<?>) {
			long nextCursor = Paging.CURSOR_START;
			if (list.size() <= paging.getPageSize() / 2) {
				nextCursor = Paging.CURSOR_END;
			} else {
				nextCursor = ((PagableList<?>) list).getNextCursor();
			}
			setNextPageCursor(paging, nextCursor);
			if (nextCursor == Paging.CURSOR_END) {
				paging.setLastPage(true);
			}
		} else {
			if (list.size() <= paging.getPageSize() / 2) {
			    paging.setLastPage(true);
			}
		}
	}

	/**
	 * 检查分页控制参数是否为Cursor分页
	 *
	 * @param paging
	 *            分页控制参数
	 * @throws LibException
	 */
	protected void initCursorPaging(Paging<?> paging) throws LibException {
		if (null == paging) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		if (paging.isCursorPaging()) {
			return;
		}

		paging.initCursorPaging(); // 若传入的不是Cursor分页，则初始化Cursor分页参数
		if (paging.getPageIndex() == 0) {
			paging.moveToFirst();
		}
	}

	/**
	 * 检查分页控制参数是否是page分页
	 *
	 * @param paging
	 * @throws LibException
	 */
	protected void initPagePaging(Paging<?> paging) throws LibException {
		if (null == paging) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		if (!paging.isCursorPaging()) {
			return;
		}

		paging.initPagePaging();
		if (paging.getPageIndex() == 0) {
			paging.moveToFirst();
		}
	}

	protected <T> void setNextPageSince(Paging<T> paging, T since) {
		if (paging == null) {
			return;
		}

		paging.setNextPageSince(since);
	}

	protected <T> void setNextPageMax(Paging<T> paging, T max) {
		if (paging == null) {
			return;
		}

		paging.setNextPageMax(max);
	}

	protected void setNextPageCursor(Paging<?> paging, long cursor) {
		if (paging == null) {
			return;
		}

		paging.setNextPageCursor(cursor);
	}

	protected void setNextPageCursor(Paging<?> paging, String cursor) {
		if (paging == null) {
			return;
		}

		paging.setNextPageCursor(cursor);
	}

	protected void setLastPage(Paging<?> paging, boolean isLastPage) {
		if (paging == null) {
			return;
		}

		paging.setLastPage(isLastPage);
	}


	public static <T> Paging<T> getPagingInstance(){
		return new Paging<T>();
	}

	public static <T> Paging<T> getPagingInstance(T globalSince, T globalMax){
		return new Paging<T>(globalSince, globalMax);
	}

}
