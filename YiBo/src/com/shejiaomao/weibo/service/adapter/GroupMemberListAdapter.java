package com.shejiaomao.weibo.service.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Filter;
import android.widget.Filterable;

import com.cattong.commons.util.ListUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.BaseUser;
import com.cattong.entity.User;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.activity.GroupMemberActivity;
import com.shejiaomao.weibo.common.SelectMode;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.SocialGraphDao;
import com.shejiaomao.weibo.service.task.ImageLoad4HeadTask;

public class GroupMemberListAdapter extends CacheAdapter<BaseUser> implements Filterable {
	private GroupMemberListAdapter self;
    private SelectMode mode;

    private List<BaseUser> listUser = null;
    private List<BaseUser> listSelectedUser = null;

    private List<BaseUser> listOriginalUser = null;
    private Filter filter = null;
    public GroupMemberListAdapter(Context context, LocalAccount account, SelectMode mode) {
    	super(context, account);
    	this.self = this;
    	this.mode = mode;

    	listUser = new ArrayList<BaseUser>();
    	listSelectedUser = new ArrayList<BaseUser>();
    }

	@Override
	public int getCount() {
		return listUser.size();
	}

	@Override
	public Object getItem(int position) {
		if (position < 0 || position >= listUser.size()) {
			return null;
		}

		return listUser.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Object obj = getItem(position);

        if (convertView == null || !isSocialGraphView(convertView)) {
		    convertView = inflater.inflate(R.layout.list_item_user_selector, null);
		    UserSelectorHolder holder = new UserSelectorHolder(convertView);
		    convertView.setTag(holder);
        }

		User user = (User) obj;
		fillInView(convertView, user);

		return convertView;
	}

	private View fillInView(View convertView, final User user) {
		if (convertView == null) {
			return null;
		}

		final UserSelectorHolder holder = (UserSelectorHolder)convertView.getTag();
        if (holder == null || user == null) {
        	return convertView;
        }
        holder.reset();

        String profileUrl = user.getProfileImageUrl();
        if (StringUtil.isNotEmpty(profileUrl)) {
            ImageLoad4HeadTask headTask = new ImageLoad4HeadTask(holder.ivProfilePicture, profileUrl, true);
            holder.headTask = headTask;
            headTask.execute();
        }

		holder.tvScreenName.setText(user.getScreenName());
		if(StringUtil.isNotEmpty(user.getName())){
			holder.tvScreenName.setText(holder.tvScreenName.getText() + "@" + user.getName());
		}
		if (user.isVerified()) {
			holder.ivVerify.setVisibility(View.VISIBLE);
		}

		String impress = "";
		if (user.getGender() != null) {
			impress += ResourceBook.getGenderValue(user.getGender(), context);
		}
		if(StringUtil.isNotEmpty(user.getLocation())){
			impress += (", " +user.getLocation());
		}
		holder.tvImpress.setText(impress);

		holder.cbUser.setChecked(listSelectedUser.contains(user));
		holder.cbUser.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (holder.cbUser.isChecked()) {
					if(mode == SelectMode.Single){
						listSelectedUser.clear();
					}
					listSelectedUser.add(user);
				} else {
					listSelectedUser.remove(user);
				}

				self.notifyDataSetChanged();
				((GroupMemberActivity)context).updateButtonState();
			}
		});


		return convertView;
	}



	@Override
	public boolean addCacheToFirst(List<BaseUser> list) {
		if (ListUtil.isEmpty(list)) {
			return false;
		}

		if (listOriginalUser != null) {
			synchronized (mLock) {
				listOriginalUser.addAll(list);
				if (filter != null) {
				    filter.filter(filterText);
				}
			}
		} else {
			listUser.addAll(list);
		}
		this.notifyDataSetChanged();

		return true;
	}

	@Override
	public boolean addCacheToDivider(BaseUser value, List<BaseUser> list) {
		if (ListUtil.isEmpty(list)) {
			return false;
		}

		if (listOriginalUser != null) {
			synchronized (mLock) {
				listOriginalUser.addAll(list);
				if (filter != null) {
				    filter.filter(filterText);
				}
			}
		} else {
			listUser.addAll(list);
		}
		this.notifyDataSetChanged();

		return true;
	}

	@Override
	public boolean addCacheToLast(List<BaseUser> list) {
		if (ListUtil.isEmpty(list)) {
			return false;
		}

		if (listOriginalUser != null) {
			synchronized (mLock) {
				listOriginalUser.addAll(list);
				if (filter != null) {
				    filter.filter(filterText);
				}
			}
		} else {
			listUser.addAll(list);
		}
		this.notifyDataSetChanged();

		return true;
	}

	@Override
	public boolean remove(BaseUser user) {
		if (user == null) {
			return false;
		}

		boolean isSuccess = false;
		if (listOriginalUser != null) {
			synchronized (mLock) {
			    listOriginalUser.remove(user);
			}
		}
		if (listUser != null) {
		    listUser.remove(user);
		}
		if (listSelectedUser != null) {
		    listSelectedUser.remove(user);
		}
		this.notifyDataSetChanged();
		isSuccess = true;
		return isSuccess;
	}

	@Override
	public User getMax() {
		return null;
	}

	@Override
	public User getMin() {
		return null;
	}

	@Override
	public void clear() {
		listUser.clear();
		listOriginalUser.clear();
	}

	private boolean isSocialGraphView(View convertView) {
		boolean isSocialGraphView = false;
		try {
			View view = convertView.findViewById(R.id.ivProfilePicture);
			if (view != null) {
				isSocialGraphView = true;
			}
		} catch (Exception e) {
		}

		return isSocialGraphView;
	}

	public List<BaseUser> getListSelectedUser() {
		return listSelectedUser;
	}

	@Override
	public Filter getFilter() {
		if (filter == null) {
			filter = new UserFilter();
		}
		return filter;
	}

	private Object mLock = new Object();
	private String filterText;
	private class UserFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
            if (listOriginalUser == null) {
                synchronized (mLock) {
                	listOriginalUser = new ArrayList<BaseUser>(listUser);
                }
            }

            String filterStr = null;
            if (constraint != null) {
            	filterStr = constraint.toString().toLowerCase();
            }
            filterText = filterStr;
            if (StringUtil.isEmpty(filterStr)) {
                synchronized (mLock) {
                    results.values = listOriginalUser;
                    results.count = listOriginalUser.size();
                }
            } else {
                final List<BaseUser> values = listOriginalUser;
                final int count = values.size();

                final List<BaseUser> newValues = new ArrayList<BaseUser>(count);

                for (int i = 0; i < count; i++) {
                    final BaseUser user = values.get(i);
                    final String screenName = user.getScreenName().toLowerCase();
                    final String name = user.getName().toLowerCase();

                    if (screenName.contains(filterStr) || name.contains(filterStr)) {
                        newValues.add(user);
                    }
                }

                searchFromSocialGraph(filterStr, newValues);

                results.values = newValues;
                results.count = newValues.size();
            }

			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			listUser = (List<BaseUser>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }

		}

		private void searchFromSocialGraph(String filterName, List<BaseUser> listResult) {
			if (StringUtil.isEmpty(filterName) || listResult == null) {
				return;
			}
			SocialGraphDao dao = new SocialGraphDao(context);
			List<BaseUser> listUser = dao.findUsers(account.getUser(), filterName, null);
			if (listUser == null || listUser.size() == 0) {
				return;
			}
			for (BaseUser user : listUser) {
				if (!listResult.contains(user)) {
					listResult.add(user);
				}
			}
		}
	}
}
