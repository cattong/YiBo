package com.shejiaomao.weibo.common;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.common.theme.Theme;
import com.shejiaomao.weibo.common.theme.ThemeUtil;

public class GlobalResource {

    /**常用图片**/
	private static Drawable defaultMinHeader;
	private static Drawable defaultHeader;
	private static Drawable defaultThumbnail;
    private static Drawable bgRetweetFrame;
    private static Drawable iconVerification;
    private static Drawable iconLocation;
    private static Drawable iconFavorite;
    private static Drawable iconAttachment;
    
    /**字符串**/
    private static String statusSourceFormat;
    private static String statusResponseFormat;
    private static String commentReplyFormat;
    private static String commentFormat;

    /**颜色值**/
    private static ColorStateList statusTimelineReadColor;
    private static ColorStateList statusTimelineUnreadColor;

	public static Drawable getDefaultMinHeader(Context context) {
		if (defaultMinHeader == null && context != null) {
			Theme theme = ThemeUtil.createTheme(context);
			defaultMinHeader = theme.getDrawable("icon_header_default_min");
		}
		return defaultMinHeader;
	}

	public static Drawable getDefaultNormalHeader(Context context) {
		if (defaultHeader == null && context != null) {
			Theme theme = ThemeUtil.createTheme(context);
			defaultHeader = theme.getDrawable("icon_header_default");
		}
		return defaultHeader;
	}
	
	public static Drawable getDefaultThumbnail(Context context) {
		if (defaultThumbnail == null && context != null) {
			defaultThumbnail = context.getResources().getDrawable(R.drawable.icon_thumbnail_default);
		}
		return defaultThumbnail;
	}

	public static Drawable getBgRetweetFrame(Context context) {
		//if (bgRetweetFrame == null && context != null) {
		    Theme theme = ThemeUtil.createTheme(context);
			bgRetweetFrame = theme.getDrawable("bg_retweet_frame");
		//}
		return bgRetweetFrame;
	}

	public static Drawable getIconVerification(Context context) {
		if (iconVerification == null && context != null) {
			Theme theme = ThemeUtil.createTheme(context);
			iconVerification = theme.getDrawable("icon_verification");
		}
		return iconVerification;
	}
	
	public static Drawable getIconLocation(Context context) {
		if (iconLocation == null && context != null) {
			Theme theme = ThemeUtil.createTheme(context);
			iconLocation = theme.getDrawable("icon_location");
		}
		return iconLocation;
	}

	public static Drawable getIconFavorite(Context context) {
		if (iconFavorite == null && context != null) {
			Theme theme = ThemeUtil.createTheme(context);
			iconFavorite = theme.getDrawable("icon_favorite");
		}
		return iconFavorite;
	}

	public static Drawable getIconAttachment(Context context) {
		if (iconAttachment == null && context != null) {
			Theme theme = ThemeUtil.createTheme(context);
			iconAttachment = theme.getDrawable("icon_attachment");
		}
		return iconAttachment;
	}
	
	public static String getStatusSourceFormat(Context context) {
		if (statusSourceFormat == null && context != null) {
			statusSourceFormat = context.getString(R.string.label_status_source);
		}
		return statusSourceFormat;
	}

	public static String getStatusResponseFormat(Context context) {
		if (statusResponseFormat == null && context != null) {
			statusResponseFormat = context.getString(R.string.label_blog_response_count);
		}
		return statusResponseFormat;
	}

	public static String getCommentReplyFormat(Context context) {
		if (commentReplyFormat == null && context != null) {
			commentReplyFormat = context.getString(R.string.label_comments_reply_comment);
		}
		return commentReplyFormat;
	}

	public static String getCommentFormat(Context context) {
		if (commentFormat == null && context != null) {
			commentFormat = context.getString(R.string.label_comments_reply_status);
		}
		return commentFormat;
	}

	public static ColorStateList getStatusTimelineReadColor(Context context) {
		if (statusTimelineReadColor == null && context != null) {
			Theme theme = ThemeUtil.createTheme(context);
			statusTimelineReadColor = theme.getColorStateList("list_status_time_readed");
		}
		return statusTimelineReadColor;
	}

	public static ColorStateList getStatusTimelineUnreadColor(Context context) {
		if (statusTimelineUnreadColor == null && context != null) {
			Theme theme = ThemeUtil.createTheme(context);
			statusTimelineUnreadColor = theme.getColorStateList("list_status_time_unreaded");
		}
		return statusTimelineUnreadColor;
	}

	public static String getVersionName(Context context) {
		String versionName = null;
		if (context != null) {
			versionName = context.getResources().getString(R.string.defaultVersion);
			try {
				String packageName = context.getPackageName();
				versionName = context.getPackageManager().getPackageInfo(
					packageName, 0).versionName;
			} catch (NameNotFoundException e) {
				// 什么都不做
			}
		}
		
		return versionName;
	}
	
	public static void clearResource() {
	    statusSourceFormat = null;
	    statusResponseFormat = null;
	    commentReplyFormat = null;
	    commentFormat = null;
	}
}
