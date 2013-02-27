package net.dev123.yibo.service.adapter;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.util.ListUtil;
import net.dev123.commons.util.StringUtil;
import net.dev123.commons.util.TimeSpanUtil;
import net.dev123.mblog.FeaturePatternUtils;
import net.dev123.mblog.entity.Status;
import net.dev123.mblog.entity.User;
import net.dev123.yibo.EditMicroBlogActivity;
import net.dev123.yibo.EditRetweetActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.EmotionLoader;
import net.dev123.yibo.common.GlobalResource;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.ImageQuality;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.db.LocalStatus;
import net.dev123.yibo.service.cache.wrap.StatusWrap;
import net.dev123.yibo.service.listener.ImageClickListener;
import net.dev123.yibo.service.task.ImageLoad4HeadTask;
import net.dev123.yibo.service.task.ImageLoad4ThumbnailTask;
import net.dev123.yibo.service.task.QueryResponseCountTask;
import net.dev123.yibo.service.task.TwitterRetweetTask;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class StatusUtil {
	public static View initConvertView(Context context, View convertView, ServiceProvider serviceProvider) {
		if (convertView != null
			&& convertView.getId() == R.id.llStatus) {
			return convertView;
		}

		LayoutInflater inflater = LayoutInflater.from(context);
		convertView = inflater.inflate(R.layout.list_item_status, null);
		StatusHolder holder = new StatusHolder(convertView, serviceProvider);
		convertView.setTag(holder);

		return convertView;
	}

	public static View fillConvertView(View convertView, Status status) {
		if (convertView == null
			|| status == null) {
			return null;
		}

		Context context = convertView.getContext();
        StatusHolder holder = (StatusHolder)convertView.getTag();
        if (holder == null) {
        	return null;
        }
        holder.reset();

        boolean isNetEase = status.getServiceProvider() == ServiceProvider.NetEase;
        User user = status.getUser();
        if (user == null) {
        	return convertView;
        }
		if (GlobalVars.IS_SHOW_HEAD) {
			holder.ivProfilePicture.setVisibility(View.VISIBLE);
			holder.headClickListener.setUser(user);
		    String profileUrl = user.getProfileImageUrl();
		    if (StringUtil.isNotEmpty(profileUrl)) {
			    ImageLoad4HeadTask headTask = new ImageLoad4HeadTask(holder.ivProfilePicture, profileUrl, true);
			    holder.headTask = headTask;
			    headTask.execute();
		    }
		} else {
			holder.ivProfilePicture.setVisibility(View.GONE);
		}

		holder.tvScreenName.setText(user.getScreenName());
        if (status.getUser().isVerified()) {
        	holder.ivVerify.setVisibility(View.VISIBLE);
        }
        if (status.getGeoLocation() != null) {
        	holder.ivLocation.setVisibility(View.VISIBLE);
        }
        if (status.isFavorited()) {
        	holder.ivFavorite.setVisibility(View.VISIBLE);
        }

		holder.tvCreatedAt.setText(TimeSpanUtil.toTimeSpanString(status.getCreatedAt()));

		Spannable textSpan = EmotionLoader.getEmotionSpannable(status.getServiceProvider(), status.getText());
		holder.tvText.setText(textSpan);

		Status retweet = status.getRetweetedStatus();
		if (retweet != null) {
			holder.llRetweet.setVisibility(View.VISIBLE);
			holder.tvRetweetText.setVisibility(View.VISIBLE);
			String retweetText = "";
            if (retweet.getUser() != null) {
            	retweetText = retweet.getUser().getMentionTitleName() +
			    ": " + retweet.getText();
            }

            Spannable retweetTextSpan = EmotionLoader.getEmotionSpannable(
            	status.getServiceProvider(), retweetText);
            holder.tvRetweetText.setText(retweetTextSpan);
		}

		String thumbnailUrl = status.getThumbnailPicture();
		ImageView ivTempThumbnail = holder.ivThumbnail;
		if (retweet != null) {
			thumbnailUrl = retweet.getThumbnailPicture();
			ivTempThumbnail = holder.ivRetweetThumbnail;
		}
		if (StringUtil.isNotEmpty(thumbnailUrl)) {
			holder.ivAttachment.setVisibility(View.VISIBLE);
			if (GlobalVars.IS_SHOW_THUMBNAIL && !isNetEase) {
				ivTempThumbnail.setVisibility(View.VISIBLE);
			    ImageLoad4ThumbnailTask thumbnailTask =
				    new ImageLoad4ThumbnailTask(ivTempThumbnail, thumbnailUrl);
			    holder.thumbnailTask = thumbnailTask;
			    thumbnailTask.execute(status);
			    ivTempThumbnail.setOnClickListener(new ImageClickListener(status));
			}
		}

		String source = String.format(GlobalResource.getStatusSourceFormat(context), status.getSource());
		holder.tvSource.setText(Html.fromHtml(source).toString());

		String responseFormat = GlobalResource.getStatusResponseFormat(context);
		String responseText = String.format(responseFormat, 0, 0);
		if (status.getRetweetCount() == null) {
			holder.tvResponse.setText(responseText);
			QueryResponseCountTask countTask = new QueryResponseCountTask(context, status, holder.tvResponse);
			holder.responseCountTask = countTask;
			countTask.execute();
		} else {
			responseText = String.format(responseFormat, status.getRetweetCount(), status.getCommentCount());
			holder.tvResponse.setText(responseText);
		}

		return convertView;
	}

	public static View fillConvertView(View convertView, StatusWrap statusWrap) {
		if (convertView == null
			|| statusWrap == null
			|| statusWrap.getWrap() == null) {
			return null;
		}

        Status status = statusWrap.getWrap();
        fillConvertView(convertView, status);
        StatusHolder holder = (StatusHolder)convertView.getTag();
        if (holder == null) {
        	return null;
        }

        statusWrap.hit();
		if (!statusWrap.isReaded()) {
			holder.tvCreatedAt.setTextColor(GlobalResource.getStatusTimelineUnreadColor(convertView.getContext()));
			if (statusWrap.getHit() > 2) {
				statusWrap.setReaded(true);
			}
		}

		return convertView;
	}

    public static String extraRichStatus(Context context, Status status) {
		String statusText = status.getUser().getMentionName() + ": " + status.getText();
		String middleUrl = status.getMiddlePicture();
	    Status retweet = status.getRetweetedStatus();
	    if (retweet != null) {
		    String retweetText = retweet.getUser().getMentionName() + ": " + retweet.getText();
		    statusText = context.getString(R.string.msg_extra_rich_text, statusText, retweetText);
		    middleUrl = retweet.getMiddlePicture();
	    }
	    if (middleUrl != null) {
	    	statusText += context.getString(R.string.msg_extra_image, middleUrl);
	    }

	    return statusText;
    }

    public static String extraSimpleStatus(Context context, Status status) {
		String statusText = status.getUser().getMentionName() + ": " + status.getText();
	    Status retweet = status.getRetweetedStatus();
	    if (retweet != null) {
		    String retweetText = retweet.getUser().getMentionName() + ": " + retweet.getText();
		    statusText = context.getString(R.string.msg_extra_simple_text, statusText, retweetText);
	    }

	    return statusText;
    }

    public static Set<String> extraStatusMentions(final Status status, boolean excludeSelf) {
    	Pattern mentionPattern = FeaturePatternUtils.getMentionPattern(status.getServiceProvider());
    	Matcher matcher = mentionPattern.matcher(status.getText());
    	Set<String> mentions = new LinkedHashSet<String>();
    	while (matcher.find()) {
    		mentions.add(matcher.group());
    	}
    	if (excludeSelf) {
    		mentions.remove(status.getUser().getMentionName());
    	}
    	return mentions;
    }

	public static LocalStatus createDividerStatus(List<Status> statusList, LocalAccount account) {
		if (ListUtil.isEmpty(statusList) || account == null) {
			return null;
		}

		Status status = statusList.get(statusList.size() - 1);
		StringBuffer newId = new StringBuffer(status.getId());
		char c = newId.charAt(newId.length() - 1);
		byte b = (byte)((int)c - 1);
		newId.setCharAt(newId.length() - 1, (char)b);

		LocalStatus dividerStatus = new LocalStatus();
		dividerStatus.setId(newId.toString());
		dividerStatus.setAccountId(account.getAccountId());
		dividerStatus.setServiceProvider(account.getServiceProvider());
		Date createdAt = new Date(status.getCreatedAt().getTime() -1);
		dividerStatus.setCreatedAt(createdAt);
		dividerStatus.setDivider(true);
		dividerStatus.setText("divider");

		return dividerStatus;
	}

	public static void retweet(final Context context, final Status status) {
		if (context == null
			|| status == null
			|| StringUtil.isEmpty(status.getId())) {
			return;
		}

		if (status.getServiceProvider() == ServiceProvider.Twitter) {
			new AlertDialog.Builder(context)
				.setTitle(R.string.title_dialog_retweet)
	    		.setMessage(R.string.msg_dialog_retweet)
	    		.setPositiveButton(R.string.btn_dialog_retweet, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,	int which) {
						dialog.dismiss();
						//官方RT，RT的是原始微博，不是转发后形成的新微博
						new TwitterRetweetTask(context, status).execute();
					}
	    		})
				.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.setNeutralButton(R.string.btn_dialog_quote, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						//民间RT支持，会将微薄内容拷贝出来，作为引用内容，发布微薄
						quoteTweet(context, status);
					}
				})
				.create()
				.show();
		} else {
			officialRetweet(context, status);
		}
	}

	private static void quoteTweet(Context context, Status status){
		String appendText = String.format(
			FeaturePatternUtils.getRetweetFormat(status.getServiceProvider()),
			FeaturePatternUtils.getRetweetSeparator(status.getServiceProvider()),
			status.getUser().getMentionName(),
			status.getText()
		);

		Status retweet = status.getRetweetedStatus();
		if (retweet != null) {
			//官方RT形成的微博结构，引用推文时不再插入RT，因为上面的status.getText()就是RT
			appendText += String.format(
				FeaturePatternUtils.getRetweetFormat(status.getServiceProvider()),
				"",
				retweet.getUser().getMentionName(),
				retweet.getText());
		}

		Intent intent = new Intent();
		intent.putExtra("TYPE", Constants.EDIT_TYPE_RETWEET);
		intent.putExtra("APPEND_TEXT", appendText);

		intent.setClass(context, EditMicroBlogActivity.class);
		((Activity)context).startActivity(intent);
	}

	private static void officialRetweet(Context context, Status status){
		Intent intent = new Intent();
		intent.putExtra("TYPE", Constants.EDIT_TYPE_RETWEET);
		intent.putExtra("STATUS", status);

		intent.setClass(context, EditRetweetActivity.class);
		((Activity)context).startActivity(intent);
	}
	
	public static String getBigImageUrl(Status status) {
		String url = null;
		if (status == null) {
			return url;
		}
		if (status.getRetweetedStatus() != null) {
			status = status.getRetweetedStatus();
		}
		
		if (GlobalVars.IMAGE_DOWNLOAD_QUALITY == ImageQuality.Low 
			|| GlobalVars.IMAGE_DOWNLOAD_QUALITY == ImageQuality.Middle) {
			url = status.getMiddlePicture();
		} else if (GlobalVars.IMAGE_DOWNLOAD_QUALITY == ImageQuality.High){
			url = status.getOriginalPicture();
		} else if (GlobalVars.IMAGE_DOWNLOAD_QUALITY == ImageQuality.Adaptive_Net) {
			switch (GlobalVars.NET_TYPE) {
			case WIFI:
				url = status.getOriginalPicture();
				break;
			case NONE:
			case UNKNOW:
			case MOBILE_GPRS:
			case MOBILE_EDGE:
			case MOBILE_3G:
				url = status.getMiddlePicture();
				break;
			}
		}
		
		return url;
	}
}
