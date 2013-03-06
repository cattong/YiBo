package com.cattong.entity;

import java.io.Serializable;

public class PointsLevel implements Serializable {
	private static final long serialVersionUID = 6934234389762792595L;

	private int points;
	
	private String militaryRank;//军衔级别,军阶
	
	private String title; //军衔名称

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public String getMilitaryRank() {
		return militaryRank;
	}

	public void setMilitaryRank(String militaryRank) {
		this.militaryRank = militaryRank;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "PointLevel [points=" + points + ", militaryRank=" + militaryRank
		    + ", title=" + title + "]";
	}
	
}
