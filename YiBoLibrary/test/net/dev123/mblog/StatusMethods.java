package net.dev123.mblog;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.commons.util.ListUtil;
import net.dev123.entity.GeoLocation;
import net.dev123.entity.StatusUpdate;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.Status;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

//已经完成基本的测试用例
//@Ignore
public class StatusMethods {
	private static MicroBlog mBlog = null;
    private static String imagePath = "H:\\棋局.jpg";

	@BeforeClass
	public static void beforClass() {
        mBlog = Config.getMicroBlog(Config.currentProvider);
	}

	@AfterClass
	public static void afterClass() {
	}


	@Test
	public void showStatus() {
		Status showStatus = null;

		try {
			String text = "测试接口：showStatus，莫怕，" + System.currentTimeMillis();
			StatusUpdate sUpdate = new StatusUpdate(text);
			Status status = mBlog.updateStatus(sUpdate);
			assertTrue(status != null);

			showStatus = mBlog.showStatus(status.getId());
		} catch (LibException e) {
			e.printStackTrace();
		}

		assertTrue(showStatus != null);
	}

    @Test
	public void updateStatus() {
		Status status = null;

		try {
			String text = "测试接口:updateStatus，莫慌，" + System.currentTimeMillis();
			StatusUpdate sUpdate = new StatusUpdate(text);
			//地理位置
			sUpdate.setLocation(new GeoLocation(39.9594049, 116.298419));

			status = mBlog.updateStatus(sUpdate);
			assertTrue(status != null);
		} catch (LibException e) {
			e.printStackTrace();
		}
	}

    @Test
	public void uploadStatusWithPic() {
		Status status = null;

		try {
			String text = "测试发微博带图:updateStatus，莫慌，" + System.currentTimeMillis();
			File imageFile = new File(imagePath);
			StatusUpdate sUpdate = new StatusUpdate(text);
			//地理位置
			//sUpdate.setLocation(new GeoLocation(39.9594049, 116.298419));
			sUpdate.setImage(imageFile);

		    status = mBlog.updateStatus(sUpdate);
		    assertTrue(status != null);
		} catch (LibException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

    @Test
	public void destroyStatus() {
		Status destroyedStatus = null;
		try {
			String text = "测试删除微博:destroyStatus，看到这条证明失败，" + System.currentTimeMillis();
			StatusUpdate sUpdate = new StatusUpdate(text);
			Status status = mBlog.updateStatus(sUpdate);
			assertTrue(status != null);

			TestUtil.sleep();
			destroyedStatus = mBlog.destroyStatus(status.getId());
		} catch (LibException e) {
			e.printStackTrace();
		}

		assertTrue(destroyedStatus != null);
	}

	@Test
	public void retweetStatus() {
		Status retweetStatus = null;

		try {
			String text = "转发原微博：updateStatus，" + System.currentTimeMillis();
			StatusUpdate sUpdate = new StatusUpdate(text);
			Status status = mBlog.updateStatus(sUpdate);
			assertTrue(status != null);

			TestUtil.sleep();
			String retweetText = "测试转发：retweetStatus，" + System.currentTimeMillis();
			boolean isComment = false;

			retweetStatus = mBlog.retweetStatus(status.getId(), retweetText, isComment);
		} catch (LibException e) {
			e.printStackTrace();
		}

		assertTrue(retweetStatus != null);
	}

	//翻页：单向(SigleStyle)
    @Test
	public void getRetweetsOfStatus() {
		List<Status> listStatus = null;
		Paging<Status> paging = new Paging<Status>();

		try {
			String text = "测试获取转发列表:getRetweetsOfStatus，造原数据，" + System.currentTimeMillis();
			StatusUpdate sUpdate = new StatusUpdate(text);
			Status status = mBlog.updateStatus(sUpdate);
			assertTrue(status != null);

			TestUtil.sleep();
			String retweetText = "测试转发列表：getRetweetsOfStatus，转发微博，" + System.currentTimeMillis();
			boolean isComment = false;
			Status retweet = mBlog.retweetStatus(status.getId(), retweetText, isComment);
			assertTrue(retweet != null);

			TestUtil.sleep();
			listStatus = mBlog.getRetweetsOfStatus(status.getId(), paging);
			assertTrue(ListUtil.isNotEmpty(listStatus));
		} catch (LibException e) {
			if (e.getExceptionCode() == ExceptionCode.UNSUPPORTED_API) {
				assertTrue(true);
			} else {
				e.printStackTrace();
				assertTrue(false);
			}
		}

	}

    @Test
	public void getRetweetsOfStatus_param() {
		List<Status> listStatus = null;
		Paging<Status> paging = new Paging<Status>();

		try {
			listStatus = mBlog.getRetweetsOfStatus(null, paging);
			assertTrue(false);
		} catch (LibException e) {
			assertTrue(true);
		}

		try {
			listStatus = mBlog.getRetweetsOfStatus("1235", null);
			assertTrue(false);
		} catch (LibException e) {
			assertTrue(true);
		}

		assertTrue(ListUtil.isEmpty(listStatus));
	}

  //翻页：单向(SigleStyle)
    @Test
	public void getRetweetsOfStatus_paging() {
		List<Status> listStatus = null;
		Paging<Status> paging = new Paging<Status>();

		try {
			String text = "测试获取转发列表:getRetweetsOfStatus_paging，造原数据，" + System.currentTimeMillis();
			StatusUpdate sUpdate = new StatusUpdate(text);
			Status status = mBlog.updateStatus(sUpdate);
			assertTrue(status != null);

			TestUtil.sleep();
			for (int i = 0; i < 6; i++) {
			    String retweetText = "测试转发列表：getRetweetsOfStatus，转发微博，" + System.currentTimeMillis();
			    boolean isComment = true;
			    Status retweet = mBlog.retweetStatus(status.getId(), retweetText, isComment);
			    assertTrue(status != retweet);
			    TestUtil.sleep();
			}

			listStatus = mBlog.getRetweetsOfStatus(status.getId(), paging);
			assertTrue(ListUtil.isNotEmpty(listStatus));
		} catch (LibException e) {
			if (e.getExceptionCode() == ExceptionCode.UNSUPPORTED_API) {
				assertTrue(true);
			} else {
				e.printStackTrace();
				assertTrue(false);
			}
		}
	}

    //翻页：单向(SigleStyle)
    @Test
	public void searchStatus() {
		List<Status> listStatus = null;
		Paging<Status> paging = new Paging<Status>();

		try {
			listStatus = mBlog.searchStatuses("#话题", paging);
		} catch (LibException e) {
			e.printStackTrace();
		}

		assertTrue(ListUtil.isNotEmpty(listStatus));
	}

    @Test
	public void searchStatus_param() {
		List<Status> listStatus = null;
		Paging<Status> paging = new Paging<Status>();

		try {
			listStatus = mBlog.searchStatuses("", paging);
			assertTrue(false);
		} catch (LibException e) {
			assertTrue(true);
		}

		try {
			listStatus = mBlog.searchStatuses("Google", null);
			assertTrue(false);
		} catch (LibException e) {
			assertTrue(true);
		}

		assertTrue(ListUtil.isEmpty(listStatus));
	}

    //翻页：单向(SigleStyle)
    @Test
	public void searchStatus_paging() {
		List<Status> listStatus = null;
		Paging<Status> paging = new Paging<Status>();

		try {
			listStatus = mBlog.searchStatuses("Google", paging);
			assertTrue(ListUtil.isNotEmpty(listStatus));
			if (paging.moveToNext()) {
				listStatus = mBlog.searchStatuses("Google", paging);
				assertTrue(ListUtil.isNotEmpty(listStatus));
			} else {
			    assertTrue(false);
			}
		} catch (LibException e) {
			assertTrue(false);
		}

		assertTrue(ListUtil.isNotEmpty(listStatus));
	}

    @Test
    public void getDailyHotRetweets() {
    	try {
			mBlog.getDailyHotRetweets(new Paging<Status>());
		} catch (LibException e) {
			e.printStackTrace();
			assertTrue(false);
		}
    }
    @Test
    public void getWeeklyHotRetweets() {
    	try {
			mBlog.getWeeklyHotRetweets(new Paging<Status>());
		} catch (LibException e) {
			e.printStackTrace();
			assertTrue(false);
		}
    }
    @Test
    public void getDailyHotComments() {
    	try {
			mBlog.getDailyHotComments(new Paging<Status>());
		} catch (LibException e) {
			e.printStackTrace();
			assertTrue(false);
		}
    }
    @Test
    public void getWeeklyHotComments() {
    	try {
			mBlog.getWeeklyHotComments(new Paging<Status>());
		} catch (LibException e) {
			e.printStackTrace();
			assertTrue(false);
		}
    }
}
