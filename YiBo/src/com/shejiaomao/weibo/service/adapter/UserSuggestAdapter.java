package com.shejiaomao.weibo.service.adapter;

import java.util.ArrayList;
import java.util.List;

import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.ListUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.BaseUser;
import com.cattong.entity.User;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.UserDao;

public class UserSuggestAdapter extends CacheAdapter<User> implements Filterable {
	private List<BaseUser> listUser = null;

	private List<BaseUser> listOriginalUser = null;
	private Filter filter = null;
	boolean isUseName = false;
	public UserSuggestAdapter(Activity context) {
		super(context, null);
        SheJiaoMaoApplication sheJiaoMao = (SheJiaoMaoApplication)context.getApplication();
        LocalAccount account = sheJiaoMao.getCurrentAccount();
        this.account = account;

		listUser = new ArrayList<BaseUser>(Constants.PAGING_DEFAULT_COUNT);
		isUseName = account != null &&
			(account.getServiceProvider() == ServiceProvider.Tencent ||
			account.getServiceProvider() == ServiceProvider.Twitter);
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

        if (convertView == null) {
		    convertView = inflater.inflate(android.R.layout.simple_dropdown_item_1line, null);
        }

		User user = (User) obj;
		TextView tvName = (TextView)convertView;
		if (isUseName) {
			tvName.setText(user.getScreenName() + "@" + user.getName());
		} else {
		    tvName.setText(user.getScreenName());
		}

		return convertView;
	}

	@Override
	public boolean addCacheToFirst(List<User> list) {
		return false;
	}

	@Override
	public boolean addCacheToDivider(User value, List<User> list) {
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
	public boolean addCacheToLast(List<User> list) {
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

                searchFromUser(filterStr, newValues);

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

		@Override
	    public CharSequence convertResultToString(Object resultValue) {
			if (resultValue == null ||
				!(resultValue instanceof User)
			) {
				return "";
			}

			String name = "";
			User user = (User)resultValue;
			if (isUseName) {
				name = user.getName();
			} else {
				name = user.getScreenName();
			}
	        return name;
	    }

		private void searchFromUser(String filterName, List<BaseUser> listResult) {
			if (StringUtil.isEmpty(filterName) || listResult == null) {
				return;
			}
			UserDao dao = new UserDao(context);
			ServiceProvider sp = account != null ? account.getServiceProvider() : ServiceProvider.Sina;
			List<BaseUser> listUser = dao.findUsers(sp, filterName);
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
