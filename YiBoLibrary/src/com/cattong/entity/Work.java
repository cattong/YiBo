package com.cattong.entity;

import java.io.Serializable;
import java.util.Date;

public class Work implements Serializable {
	private static final long serialVersionUID = 7705760300854495827L;
	/** 雇主 */
	private String employer;
	/** 工作所在地 */
	private String location;
	/** 职位 */
	private String position;
	/** 开始时间 */
	private Date startDate;
	/** 结束时间 */
	private Date endDate;
	/** 项目 */
	private Project[] projects;

	public String getEmployer() {
		return employer;
	}

	public void setEmployer(String employer) {
		this.employer = employer;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Project[] getProjects() {
		return projects;
	}

	public void setProjects(Project... projects) {
		this.projects = projects;
	}

	public static class Project extends BaseSocialEntity {
		private static final long serialVersionUID = 78935326657221633L;
		/** 项目名 */
		private String name;
		/** 描述 */
		private String description;
		/** 开始时间 */
		private Date startDate;
		/** 结束时间 */
		private Date endDate;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public Date getStartDate() {
			return startDate;
		}

		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}

		public Date getEndDate() {
			return endDate;
		}

		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}

	}
}
