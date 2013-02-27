package net.dev123.sns;

import static org.junit.Assert.assertTrue;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.exception.LibException;
import net.dev123.sns.entity.Status;

import org.junit.BeforeClass;
import org.junit.Test;

public class StatusMethods {
	private static Sns sns = null;

	@BeforeClass
	public static void beforClass() throws LibException {
		sns = TokenConfig.getSns(TokenConfig.currentProvider);
	}

	@Test
	public void createStatus() throws LibException{
		boolean result = sns.createStatus("测试发状态信息……" + System.currentTimeMillis());
		assertTrue(result);
	}

	@Test
	public void getStatuses() throws LibException {
		String userId = sns.getUserId();
		Paging<Status> paging = new Paging<Status>();
		paging.moveToFirst();
		List<Status> statuses = sns.getStatuses(userId, paging);
		assertTrue(statuses != null);
	}

	@Test
	public void showStatus() throws LibException {
		String userId = sns.getUserId();
		Paging<Status> paging = new Paging<Status>();
		paging.moveToFirst();
		List<Status> statuses = sns.getStatuses(userId, paging);
		Status status = null;
		if (statuses != null && statuses.size() > 0) {
			Status tmp = statuses.get(0);
			status = sns.showStatus(tmp.getId(), tmp.getFrom().getProfileId());
		}
		assertTrue(status != null);
	}

	@Test
	public void destroyStatus() throws LibException {
		String userId = sns.getUserId();
		Paging<Status> paging = new Paging<Status>();
		paging.moveToFirst();
		List<Status> statuses = sns.getStatuses(userId, paging);
		boolean result = false;
		if (statuses != null && statuses.size() > 0) {
			Status tmp = statuses.get(0);
			result = sns.destroyStatus(tmp.getId());
		}
		assertTrue(result);
	}

}
