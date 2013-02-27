package net.dev123.mblog.entity;

import java.util.Arrays;
import java.util.Date;

public class Trends implements Comparable<Trends>, java.io.Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 7730832019791859091L;

	private Date asOf;
	private Date trendAt;
	private Trend[] trends;

	@Override
	public int compareTo(Trends that) {
		return this.trendAt.compareTo(that.getTrendAt());
	}

	public Date getAsOf() {
		return asOf;
	}

	public void setAsOf(Date asOf) {
		this.asOf = asOf;
	}

	public Date getTrendAt() {
		return trendAt;
	}

	public void setTrendAt(Date trendAt) {
		this.trendAt = trendAt;
	}

	public Trend[] getTrends() {
		return trends;
	}

	public void setTrends(Trend[] trends) {
		this.trends = trends;
	}

	@Override
	public String toString() {
		return "Trends [asOf=" + asOf + ", trendAt=" + trendAt + ", trends=" + Arrays.toString(trends) + "]";
	}



}
