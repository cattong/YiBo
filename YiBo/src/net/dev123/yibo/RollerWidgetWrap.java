package net.dev123.yibo;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.commons.util.StringUtil;
import net.dev123.commons.util.TimeSpanUtil;
import net.dev123.mblog.entity.Status;
import net.dev123.mblog.entity.User;
import net.dev123.yibo.common.CacheManager;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.StatusCatalog;
import net.dev123.yibo.db.LocalStatus;
import net.dev123.yibo.db.StatusDao;
import net.dev123.yibo.service.cache.ImageCache;
import net.dev123.yibo.service.cache.wrap.CachedImage;
import net.dev123.yibo.service.cache.wrap.CachedImageKey;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class RollerWidgetWrap {
	private static final String TAG = "RollerWidgetReceiver";
    public static final String ALART_ACTION = "net.dev123.action.update_widget";
    private static final String MOVE_NEXT = "net.dev123.yibo.move_next";
    private static final String IS_AUTO = "net.dev123.yibo.is_auto";

    private static final int PAGE_COLOR = 0xFF297acc;
    private Bitmap DEFAULT_HEADER_BITMAP;

    private ComponentName thisWidget;
    private AppWidgetManager manager;;

    private List<Status> listStatus;
    private int index;
    private int count;
    private boolean isLastAuto = true;

    private ForegroundColorSpan fcSpan;

	public void onReceive(Context context, Intent intent) {
		boolean isMoveNext = intent.getBooleanExtra(MOVE_NEXT, true);
		boolean isAuto = intent.getBooleanExtra(IS_AUTO, true);
		//如果手动移动的话,则取消掉自动一次
		if (!isLastAuto && isAuto) {
			isLastAuto = true;
			return;
		}
        if (!isAuto) {
        	isLastAuto = false;
		}

		Status status = null;
		if (listStatus == null) {
			listStatus = getFreshStatus(context);
			index = 0;
			//清除使用数据View
			//remoteViewsHasData = new RemoteViews(context.getPackageName(), R.layout.widget_roller);
		}
		if (listStatus != null) {
			count = listStatus.size();
			if (!isMoveNext) {
				index = (listStatus.size() + index - 2) % listStatus.size();
			}
		    status = listStatus.get(index++);
			if (index >= listStatus.size()) {
				index = 0;
				listStatus = null;
			}
		}

		RemoteViews remoteViews = buildRemoteViews(context, status);

		thisWidget = new ComponentName(context, RollerWidget.class);
        manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(thisWidget, remoteViews);

        if (Constants.DEBUG) Log.d(TAG, "onReceive" + ": " + this.hashCode());
	}

	private RemoteViews buildRemoteViews(Context context, Status status) {
		if (status == null
			|| (status instanceof LocalStatus
				&& ((LocalStatus)status).isDivider())) {
			return buildRemoteViewsNoData(context);
		}
		User user = status.getUser();

		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_roller);

		if (DEFAULT_HEADER_BITMAP == null) {
			Resources res = context.getResources();
			DEFAULT_HEADER_BITMAP = BitmapFactory.decodeResource(res, R.drawable.icon_header_default_min);
		}
		remoteViews.setImageViewBitmap(R.id.ivProfilePicture, DEFAULT_HEADER_BITMAP);
		String profileUrl = user.getProfileImageUrl();
		if (StringUtil.isNotBlank(profileUrl)) {
			ImageCache imageCache = (ImageCache)CacheManager.getInstance().getCache(ImageCache.class.getName());
			CachedImageKey imageInfo = new CachedImageKey(profileUrl, CachedImageKey.IMAGE_HEAD_MINI);
		    CachedImage wrap = imageCache.get(imageInfo);
			if (wrap != null && wrap.getWrap() != null) {
				remoteViews.setImageViewBitmap(R.id.ivProfilePicture, wrap.getWrap());
			}
		}
		remoteViews.setTextViewText(R.id.tvScreenName, user.getScreenName());
		if (user.isVerified()) {
			remoteViews.setViewVisibility(R.id.ivVerify, View.VISIBLE);
		} else {
			remoteViews.setViewVisibility(R.id.ivVerify, View.GONE);
		}
		if (status.isFavorited()) {
			remoteViews.setViewVisibility(R.id.ivFavorite, View.VISIBLE);
		} else {
			remoteViews.setViewVisibility(R.id.ivFavorite, View.GONE);
		}
		String thumbnailUrl = status.getThumbnailPicture();
		Status retweet = status.getRetweetedStatus();
		if (retweet != null) {
			thumbnailUrl = retweet.getThumbnailPicture();
		}
		if (StringUtil.isNotEmpty(thumbnailUrl)) {
			remoteViews.setViewVisibility(R.id.ivAttachment, View.VISIBLE);
		} else {
			remoteViews.setViewVisibility(R.id.ivAttachment, View.GONE);
		}

		remoteViews.setTextViewText(R.id.tvCreateAt, TimeSpanUtil.toTimeSpanString(status.getCreatedAt()));
		remoteViews.setTextViewText(R.id.tvText, Html.fromHtml(status.getText()));
		if (retweet != null) {
			remoteViews.setViewVisibility(R.id.llRetweet, View.VISIBLE);
			String retweetText = "@" + retweet.getUser().getScreenName() +
			    ": " + retweet.getText();
			remoteViews.setTextViewText(R.id.tvRetweetText, Html.fromHtml(retweetText));
		} else {
			remoteViews.setViewVisibility(R.id.llRetweet, View.GONE);
		}
		int pageIndex = (index == 0) ? count : index;
		String page = pageIndex + "/" + count;
		SpannableString pageSpan = new SpannableString(page);
		if (fcSpan == null) {
			fcSpan = new ForegroundColorSpan(PAGE_COLOR);
		}
		pageSpan.setSpan(
			fcSpan, 0,
			String.valueOf(pageIndex).length(),
			Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
		);
		remoteViews.setTextViewText(R.id.tvPage, pageSpan);

		
	    Intent appIntent = new Intent(context, HomePageActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.ibtnWidgetLogo, appPendingIntent);

        Intent editMicroBlogIntent = new Intent(context, EditMicroBlogActivity.class);
        PendingIntent editStatusPendingIntent = PendingIntent.getActivity(context, 0, editMicroBlogIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.ibtnWidgetStatus, editStatusPendingIntent);

        Intent viewStatusIntent = new Intent(context, MicroBlogActivity.class);
        viewStatusIntent.putExtra("STATUS", status);
        viewStatusIntent.putExtra("SOURCE", Constants.SOURCE_WIDGET);
        PendingIntent viewStatusPendingIntent = PendingIntent.getActivity(context, (int)System.currentTimeMillis(), viewStatusIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.llStatus, viewStatusPendingIntent);

        Intent cameraIntent = new Intent(context, EditMicroBlogActivity.class);
        cameraIntent.putExtra("TYPE", Constants.EDIT_TYPE_TWEET);
        cameraIntent.putExtra("SOURCE", Constants.SOURCE_WIDGET_CAMERA);
        PendingIntent cameraPendingIntent = PendingIntent.getActivity(context, 1, cameraIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.ibtnWidgetCamera, cameraPendingIntent);

        Intent previousIntent = new Intent(ALART_ACTION);
        previousIntent.putExtra(MOVE_NEXT, false);
        previousIntent.putExtra(IS_AUTO, false);
        PendingIntent previousPendingIntent = PendingIntent.getBroadcast(context, 1, previousIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.ibtnWidgetPrevious, previousPendingIntent);

        Intent nextIntent = new Intent(ALART_ACTION);
        previousIntent.putExtra(MOVE_NEXT, true);
        nextIntent.putExtra(IS_AUTO, false);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 2, nextIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.ibtnWidgetNext, nextPendingIntent);

        return remoteViews;
	}

	private RemoteViews buildRemoteViewsNoData(Context context) {
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_roller_no_data);

        Intent appIntent = new Intent(context, HomePageActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.ibtnWidgetLogo, appPendingIntent);

        Intent editStatusIntent = new Intent(context, EditMicroBlogActivity.class);
        PendingIntent editStatusPendingIntent = PendingIntent.getActivity(context, 0, editStatusIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.ibtnWidgetStatus, editStatusPendingIntent);

        Intent cameraIntent = new Intent(context, EditMicroBlogActivity.class);
        cameraIntent.putExtra("TYPE", Constants.EDIT_TYPE_TWEET);
        cameraIntent.putExtra("SOURCE", Constants.SOURCE_WIDGET_CAMERA);
        PendingIntent cameraPendingIntent = PendingIntent.getActivity(context, (int)System.currentTimeMillis(), cameraIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.ibtnWidgetCamera, cameraPendingIntent);

        return remoteViews;
	}

	private List<Status> getFreshStatus(Context context) {
	    Paging<Status> page = new Paging<Status>();
	    page.moveToNext();
		StatusDao dao = new StatusDao(context);

		StringBuffer sql = new StringBuffer();
		sql.append(
			"select " +
			"    * " +
			"from " +
			"    Status " +
			"where " +
			"    Catalog = " + StatusCatalog.Home.getCatalogId() + " " +
			"order by " +
			"    Created_At desc " +
			"limit " +
			"    " + page.getPageSize()
		);

		List<Status> listStatus = dao.find(sql.toString());

		return listStatus;
	}

}
