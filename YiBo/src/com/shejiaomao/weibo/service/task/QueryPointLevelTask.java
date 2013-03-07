package com.shejiaomao.weibo.service.task;

import android.os.AsyncTask;

import com.cattong.entity.PointsLevel;
import com.shejiaomao.weibo.activity.AccountsActivity;

public class QueryPointLevelTask extends AsyncTask<Void, Void, PointsLevel> {

	private AccountsActivity context;
	public QueryPointLevelTask(AccountsActivity context) {
		this.context = context;
	}

	@Override
	protected PointsLevel doInBackground(Void... params) {
		PointsLevel pointsLevel = null;
//		SocialCat socialCat = Util.getSocialCat(context);
//		if (socialCat == null) {
//			return pointsLevel;
//		}
//		
//		try {
//			Passport passport = socialCat.getPoints();
//			pointsLevel = passport.getPointsLevel();
//			if (pointsLevel != null) {
//				ConfigSystemDao dao = new ConfigSystemDao(context);
//				dao.put(Constants.PASSPORT_POINTS, pointsLevel.getPoints(), "通行证积分");
//				dao.put(Constants.PASSPORT_TITLE, pointsLevel.getTitle(), "通行证头衔");
//			}
//		} catch (LibException e) {
//			if (Constants.DEBUG) e.printStackTrace();
//		}
		
		return pointsLevel;
	}

	@Override
	protected void onPostExecute(PointsLevel result) {
		super.onPostExecute(result);
		if (result != null) {
			context.showPassportPoints(result);
		}
	}

}
