package com.cattong.entity;

public class Education {

	public enum SchoolType {
		GRADUATE_SCHOOL, // 研究生院
		COLLEGE,         // 大学
		JUNIOR_COLLEGE,  // 大专
		HIGH_SCHOOL,	 // 高中
		JUNIOR_HIGH_SCHOOL,   // 初中
		PRIMARY_SCHOOL   // 小学
	}

	/** 学校 */
	private String school;
	/** 院系 */
	private String department;
	/** 毕业年份 */
	private String year;
	/** 学校类型 */
	private SchoolType schoolType;
	/** 专业 */
	private String major;
	/** 学位 */
	private String degree;

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public SchoolType getSchoolType() {
		return schoolType;
	}

	public void setSchoolType(SchoolType schoolType) {
		this.schoolType = schoolType;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

}
