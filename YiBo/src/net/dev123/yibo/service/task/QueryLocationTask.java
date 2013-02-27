package net.dev123.yibo.service.task;

import net.dev123.commons.Constants;
import net.dev123.entity.GeoLocation;
import net.dev123.entity.Location;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.yibo.R;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.db.LocalAccount;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

public class QueryLocationTask extends AsyncTask<Void, Void, Location> {
    private GeoLocation location;
    private TextView tvLocation;
    private MicroBlog microBlog;
	public QueryLocationTask(GeoLocation location, TextView tvLocation, LocalAccount account) {
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
			if (Constants.DEBUG) e.printStackTrace();
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
			location.setCountry(result.getCountry());
			location.setProvince(result.getProvince());
			location.setCity(result.getCity());
			location.setDistrict(result.getDistrict());
			location.setStreet(result.getStreet());
			if (tvLocation != null) {
				tvLocation.setText(result.getFormatedAddress());
			}
		}


	}
}
