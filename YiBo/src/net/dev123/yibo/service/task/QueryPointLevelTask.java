package net.dev123.yibo.service.task;

import net.dev123.exception.LibException;
import net.dev123.yibo.AccountsActivity;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.YiBoMeUtil;
import net.dev123.yibo.db.ConfigSystemDao;
import net.dev123.yibome.YiBoMe;
import net.dev123.yibome.entity.PointLevel;
import android.os.AsyncTask;

/**
 * @author Weiping Ye
 * @version 创建时间：2011-10-21 下午2:20:26
 **/
public class QueryPointLevelTask extends AsyncTask<Void, Void, PointLevel> {

	private AccountsActivity accountsActivity;
	public QueryPointLevelTask(AccountsActivity accountsActivity) {
		this.accountsActivity = accountsActivity;
	}

	@Override
	protected PointLevel doInBackground(Void... params) {
		PointLevel pointLevel = null;
		YiBoMe yiboMe = YiBoMeUtil.getYiBoMeOAuth(accountsActivity);
		if (yiboMe == null) {
			return pointLevel;
		}
		
		try {
			pointLevel = yiboMe.getPoints();
			if (pointLevel != null) {
				ConfigSystemDao dao = new ConfigSystemDao(accountsActivity);
				dao.put(Constants.PASSPORT_POINTS, pointLevel.getPoints(), "通行证积分");
				dao.put(Constants.PASSPORT_TITLE, pointLevel.getTitle(), "通行证头衔");
			}
		} catch (LibException e) {
			if (Constants.DEBUG) e.printStackTrace();
		}
		
		return pointLevel;
	}

	@Override
	protected void onPostExecute(PointLevel result) {
		super.onPostExecute(result);
		if (result != null) {
			accountsActivity.showPassportPoints(result);
		}
	}

}
