package net.dev123.yibo.service.task;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.commons.util.StringUtil;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.User;
import net.dev123.yibo.GroupActivity;
import net.dev123.yibo.SocialGraphActivity;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.service.adapter.CacheAdapter;
import net.dev123.yibo.service.adapter.SocialGraphListAdapter;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class SocialGraphTask extends AsyncTask<Void, Void, List<User>> {
	private static final String TAG = "SocialGraphTask";
	public static final int TYPE_FOLLOWERS = 0x001;
	public static final int TYPE_FRIENDS = 0x002;
	public static final int TYPE_BLOCKS = 0x003;

	private MicroBlog microBlog = null;
	private Context context;
	private CacheAdapter<User> adapter = null;
	private Paging<User> paging;

	private int socialGraphType = TYPE_FOLLOWERS;
	private User user;
	private String message;

	private LocalAccount account;

	public SocialGraphTask(CacheAdapter<User> adapter, User user) {
		this.adapter = adapter;
		this.context = adapter.getContext();
		this.user = user;
		this.account = adapter.getAccount();
		this.microBlog = GlobalVars.getMicroBlog(account);
        this.paging = adapter.getPaging();
		if (adapter instanceof SocialGraphListAdapter) {
		    this.socialGraphType = ((SocialGraphListAdapter)adapter).getSocialGraphType();
		}
	}

	@Override
	protected void onPreExecute() {
		if (adapter instanceof SocialGraphListAdapter) {
			if (context instanceof SocialGraphActivity) {
		       ((SocialGraphActivity)context).showLoadingFooter();
			} else if (context instanceof GroupActivity) {
				((GroupActivity)context).showLoadingFooter();
			}
		}
	}

	@Override
	protected List<User> doInBackground(Void... params) {
		if (adapter == null 
			|| microBlog == null 
			|| user == null) {
			return null;
		}
		
		String myUserId = null;
		if (account.getUser() != null) {
			myUserId = account.getUser().getId();
		}
		boolean isSelf = StringUtil.isEquals(myUserId, user.getId());
		List<User> listUserInfo = null;
		try {
			if (paging.moveToNext()) {
				if (socialGraphType == TYPE_FOLLOWERS) {
					listUserInfo = microBlog.getUserFollowers(user.getId(), paging);
				} else if (socialGraphType == TYPE_FRIENDS) {
					listUserInfo = microBlog.getUserFriends(user.getId(), paging);
					if (isSelf) {
						for (User u : listUserInfo) {
							u.setFollowing(true);
						}
					}
				} else if (socialGraphType == TYPE_BLOCKS) {
					listUserInfo = microBlog.getBlockingUsers(paging);
					if (isSelf) {
						for (User u : listUserInfo) {
							u.setBlocking(true);
						}
					}
				}
			}
		} catch (LibException e) {
			if (Constants.DEBUG) Log.e(TAG, "Task", e);
			message = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
			paging.moveToPrevious();
		}

		return listUserInfo;
	}

	@Override
	protected void onPostExecute(List<User> result) {
		if (result != null && result.size() > 0) {
			adapter.addCacheToDivider(null, result);
		} else {
			adapter.notifyDataSetChanged();

			if (message != null) {
				Toast.makeText(adapter.getContext(), message, Toast.LENGTH_LONG).show();
			}
		}

		if (adapter instanceof SocialGraphListAdapter) {
			if (paging.hasNext()) {
				if (context instanceof SocialGraphActivity) {
				    ((SocialGraphActivity)context).showMoreFooter();
				} else if (context instanceof GroupActivity) {
					((GroupActivity)context).showMoreFooter();
				}
			} else {
				if (context instanceof SocialGraphActivity) {
				    ((SocialGraphActivity)context).showNoMoreFooter();
				} else if (context instanceof GroupActivity) {
					((GroupActivity)context).showNoMoreFooter();
				}
			}
		}

	}

	public int getSocialGraphType() {
		return socialGraphType;
	}

	public void setSocialGraphType(int socialGraphType) {
		this.socialGraphType = socialGraphType;
	}

}
