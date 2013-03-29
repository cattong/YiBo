package com.shejiaomao.weibo.service.task;

import com.shejiaomao.maobo.R;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.entity.Location;
import com.cattong.weibo.Weibo;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.db.LocalAccount;

public class QueryLocationTask extends AsyncTask<Void, Void, Location> {
    private Location location;
    private TextView tvLocation;
    private Weibo microBlog;
	public QueryLocationTask(Location location, TextView tvLocation, LocalAccount account) {
		this.location = location;
		this.tvLocation = tvLocation;
		this.microBlog = GlobalVars.getMicroBlog(account);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		tvLocation.setText(R.string.label_blog_geo_2_address);
	}

	@Override
	protected Location doInBackground(Void... params) {
		Location newLocation = null;
		if (location == null || microBlog == null) {
			return newLocation;
		}

		try {
			newLocation = microBlog.getLocationByCoordinate(
				location.getLatitude(), location.getLongitude());
		} catch (LibException e) {
			Logger.debug(e.getMessage(), e);
		}

		return newLocation;
	}

	@Override
	protected void onPostExecute(Location result) {
		super.onPostExecute(result);
		if (result == null) {
			tvLocation.setText(R.string.label_blog_geo_unknow);
			tvLocation.setVisibility(View.GONE);
			return;
		} else {
//			location.setCountry(result.getCountry());
//			location.setProvince(result.getProvince());
//			location.setCity(result.getCity());
//			location.setDistrict(result.getDistrict());
//			location.setStreet(result.getStreet());
			if (tvLocation != null) {
				tvLocation.setText(result.getFormatedAddress());
			}
		}


	}
}
