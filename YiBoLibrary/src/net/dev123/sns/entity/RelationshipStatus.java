package net.dev123.sns.entity;

public enum RelationshipStatus {
	SINGLE("Single"), // 单身
	IN_A_RELATIONSHIP("In a relationship"), // 恋爱中……
	ENGAGED("Engaged"), // 已订婚
	MARRIED("Married"), // 已婚
	COMPLICATED("It's complicated"), // 关系很难解释
	IN_AN_OPEN_RELATIONSHIP("In an open relationship"), // 开放式的交往关系
	WIDOWED("Widowed"), // 丧偶
	SEPARATED("Separated"), // 分居
	DIVORCED("Divorced"), // 离婚
	IN_A_CIVIL_UNION("In a civil union"), // 民事结合，特指同性婚姻
	IN_A_DOMESTIC_PARTNERSHIP("In a domestic partnership"); // 同居伴侣关系

	private String description;

	private RelationshipStatus(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
