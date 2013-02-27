package net.dev123.yibome.entity;

import java.io.Serializable;

public class PointLevel implements Serializable {
	private static final long serialVersionUID = 6934234389762792595L;

	private int points;
	
	private String pointLevel;
	
	private String title;

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public String getPointLevel() {
		return pointLevel;
	}

	public void setPointLevel(String pointLevel) {
		this.pointLevel = pointLevel;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "PointLevel [points=" + points + ", pointLevel=" + pointLevel
		    + ", title=" + title + "]";
	}
	
}
