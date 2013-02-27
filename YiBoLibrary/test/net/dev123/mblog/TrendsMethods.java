package net.dev123.mblog;

import java.util.List;

import junit.framework.Assert;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.Trends;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

//@Ignore
public class TrendsMethods {

	private static MicroBlog mBlog = null;

	@BeforeClass
	public static void beforClass() {
        mBlog = Config.getMicroBlog(Config.currentProvider);
	}

	@AfterClass
	public static void afterClass() {

	}

	@Test
	public void getCurrentTrends(){
		Trends trends = null;
		try {
			trends = mBlog.getCurrentTrends();
		} catch (LibException e) {
			e.printStackTrace();
		}
		Assert.assertTrue(trends != null && trends.getTrends().length > 0);
	}

	@Test
	public void getDailyTrends(){
		List<Trends> trends = null;
		try {
			trends = mBlog.getDailyTrends();
		} catch (LibException e) {
			e.printStackTrace();
		}
		Assert.assertTrue(trends != null && trends.size() > 0);
	}

	@Test
	public void getWeeklyTrends(){
		List<Trends> trends = null;
		try {
			trends = mBlog.getWeeklyTrends();
			Assert.assertTrue(trends.size() > 0);
		} catch (LibException e) {
			e.printStackTrace();
		}
		Assert.assertTrue(trends != null && trends.size() > 0);
	}

}
