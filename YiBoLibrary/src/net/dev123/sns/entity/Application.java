package net.dev123.sns.entity;

import net.dev123.entity.BaseEntity;

public class Application extends BaseEntity {

	private static final long serialVersionUID = 2771673743244496707L;
	/** 应用Id */
	private String id;
	/** 应用名 */
	private String name;
	/** 应用简介或描述 */
	private String desctiption;
	/** 开发公司 */
	private String company;
	/** 应用分类 */
	private String category;
	/** 应用子分类 */
	private String subCategory;
	/** 应用链接地址 */
	private String link;
	/** 应用icon地址 */
	private String iconUrl;
	/** 应用logo地址 */
	private String logoUrl;
	/** 应用命名空间 */
	private String namespace;
	/** 日活跃用户数 */
	private long dailyActiveUsersCount;
	/** 周活跃用户数 */
	private long weeklyActiveUsersCount;
	/** 月活跃用户数 */
	private long monthlyActiveUsersCount;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesctiption() {
		return desctiption;
	}

	public void setDesctiption(String desctiption) {
		this.desctiption = desctiption;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public long getDailyActiveUsersCount() {
		return dailyActiveUsersCount;
	}

	public void setDailyActiveUsersCount(long dailyActiveUsersCount) {
		this.dailyActiveUsersCount = dailyActiveUsersCount;
	}

	public long getWeeklyActiveUsersCount() {
		return weeklyActiveUsersCount;
	}

	public void setWeeklyActiveUsersCount(long weeklyActiveUsersCount) {
		this.weeklyActiveUsersCount = weeklyActiveUsersCount;
	}

	public long getMonthlyActiveUsersCount() {
		return monthlyActiveUsersCount;
	}

	public void setMonthlyActiveUsersCount(long monthlyActiveUsersCount) {
		this.monthlyActiveUsersCount = monthlyActiveUsersCount;
	}

}
