package com.shejiaomao.weibo.common;

public enum StatusCatalog {
	Home(1), Mentions(2), Favorites(3), Others(-1);

	private StatusCatalog(int catalogId) {
		this.catalogId = catalogId;
	}

	private int catalogId;

	public int getCatalogId() {
		return catalogId;
	}

}
