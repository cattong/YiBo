package net.dev123.mblog.entity;

public class Trend implements java.io.Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = -6530810942885022211L;

	private String id;
	private String name;
	private String url = null;
	private String query = null;
	private long num;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getNum() {
		return num;
	}
	public void setNum(long num) {
		this.num = num;
	}
	@Override
	public String toString() {
		return "Trend [id=" + id + ", name=" + name + ", query=" 
				+ query + ", url=" + url + ", num=" + num + "]";
	}
}
