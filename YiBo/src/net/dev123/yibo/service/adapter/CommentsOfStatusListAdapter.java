package net.dev123.yibo.service.adapter;

import java.util.ArrayList;
import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.commons.util.ListUtil;
import net.dev123.commons.util.TimeSpanUtil;
import net.dev123.mblog.entity.Comment;
import net.dev123.mblog.entity.Status;
import net.dev123.yibo.MicroBlogActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.EmotionLoader;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.service.task.QueryCommentsOfStatusTask;
import android.content.Context;
import android.text.Spannable;
import android.view.View;
import android.view.ViewGroup;

public class CommentsOfStatusListAdapter extends CacheAdapter<Comment> {
    private Status status;
	private List<Comment> listComment;
	QueryCommentsOfStatusTask task = null;

	public CommentsOfStatusListAdapter(Context context, LocalAccount account, Status status) {
    	super(context, account);
    	this.status = status;

    	listComment = new ArrayList<Comment>(Constants.PAGING_DEFAULT_COUNT);

    	paging = new Paging<Comment>();
    	paging.setPageSize(Constants.PAGING_DEFAULT_COUNT / 2);
    	if (GlobalVars.IS_AUTO_LOAD_COMMENTS) {
        	task = new QueryCommentsOfStatusTask(this);
        	task.execute();
    	} else {
    		if (context instanceof MicroBlogActivity) {
    			((MicroBlogActivity) context).showLoadCommentsFooter();
    		}
    	}
    }

	@Override
	public int getCount() {
		return listComment.size();
	}

	@Override
	public Object getItem(int position) {
		return listComment.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Object obj = getItem(position);
		if (convertView == null) {
		    convertView = inflater.inflate(R.layout.list_item_comments_of_status, null);
		    CommentOfStatusHolder holder = new CommentOfStatusHolder(convertView);
		    convertView.setTag(holder);
		}

		Comment comment = (Comment)obj;
		fillInView(convertView, comment);

		return convertView;
	}

	private View fillInView(View convertView, Comment comment) {
		if (convertView == null || comment == null) {
			return null;
		}
		CommentOfStatusHolder holder = (CommentOfStatusHolder) convertView.getTag();
        holder.reset();

		holder.tvScreenName.setText(comment.getUser().getScreenName());
		holder.tvCreatedAt.setText(TimeSpanUtil.toTimeSpanString(comment.getCreatedAt()));

		Spannable textSpan = EmotionLoader.getEmotionSpannable(
			comment.getServiceProvider(), comment.getText());
		holder.tvText.setText(textSpan);
        holder.replyClickListener.setComment(comment);

		return convertView;
	}

	@Override
	public boolean addCacheToFirst(List<Comment> list) {
		if (list == null || list.size() == 0) {
			return false;
		}

		listComment.addAll(0, list);

		this.notifyDataSetChanged();

		return true;
	}

	@Override
	public boolean addCacheToDivider(Comment value, List<Comment> list) {
		if (value == null || ListUtil.isEmpty(list)) {
			return false;
		}

		listComment.addAll(list);

		this.notifyDataSetChanged();

		return true;
	}

	@Override
	public boolean addCacheToLast(List<Comment> list) {
		if (ListUtil.isEmpty(list)) {
			return false;
		}

		listComment.addAll(listComment.size(), list);

		this.notifyDataSetChanged();

		return true;
	}

	@Override
	public void clear() {
		listComment.clear();
	}

	@Override
	public Comment getMax() {
		Comment max = null;
		if (listComment != null && listComment.size() > 0) {
			max = listComment.get(0);
		}

		return max;
	}

	@Override
	public Comment getMin() {
		Comment min = null;
		if (listComment != null && listComment.size() > 0) {
			min = listComment.get(listComment.size() - 1);
		}

		return min;
	}

	@Override
	public boolean remove(int position) {
		if (position < 0 || position >= getCount()) {
			return false;
		}
		listComment.remove(position);
		this.notifyDataSetChanged();

		return true;
	}

	@Override
	public boolean remove(Comment comment) {
		if (comment == null) {
			return false;
		}
		listComment.remove(comment);
		this.notifyDataSetChanged();
		return true;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
}
