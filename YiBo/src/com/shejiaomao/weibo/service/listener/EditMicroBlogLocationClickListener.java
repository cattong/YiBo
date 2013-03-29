package com.shejiaomao.weibo.service.listener;

import com.shejiaomao.maobo.R;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.cattong.commons.Logger;
import com.cattong.entity.GeoLocation;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.activity.EditMicroBlogActivity;
import com.shejiaomao.weibo.common.theme.Theme;

public class EditMicroBlogLocationClickListener implements OnClickListener {
    private EditMicroBlogActivity context;
	private LocationManager locationManager;
    private GeoLocation geoLocation;
    private boolean isAutoLocate = false;
    private boolean isFineLocation = false;
    private boolean isCoarseLocation = false;

    private Button btnLocation;
    public EditMicroBlogLocationClickListener(EditMicroBlogActivity context) {
    	this.context = context;
    	SheJiaoMaoApplication sheJiaoMao = (SheJiaoMaoApplication)context.getApplicationContext();

    	locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
    	if (sheJiaoMao.isAutoLocate()) {
    		registerListener();
    		isAutoLocate = true;
    	}

    	btnLocation = (Button)context.findViewById(R.id.btnLocation);
    }

	@Override
	public void onClick(View v) {
		isAutoLocate = false;
		if (isCoarseLocation || isFineLocation) {
			context.setGeoLocation(null);
			Theme theme = context.getSkinTheme();
			btnLocation.setBackgroundDrawable(theme.getDrawable("selector_btn_location"));
			removeListener();
			isCoarseLocation = false;
			isFineLocation = false;
		} else {
			registerListener();
		}

	}

	public void registerListener() {
		String provider;
		if (isFineLocation) {
			Toast.makeText(context, R.string.msg_fine_location, Toast.LENGTH_SHORT).show();
			return;
		}
		locationManager.removeUpdates(listener);
		if (isCoarseLocation) {
			provider = LocationManager.GPS_PROVIDER;
		} else {
			provider = LocationManager.NETWORK_PROVIDER;
		}

        try {
			locationManager.requestLocationUpdates(provider, 2000, 0, listener);
	        Location location = locationManager.getLastKnownLocation(provider);
	        if (location != null) {
	        	geoLocation = new GeoLocation(location.getLatitude(), location.getLongitude());
	        	context.setGeoLocation(geoLocation);
	            if (Logger.isDebug()) {
	            	Toast.makeText(context, location.getProvider() + " last known-->" + location.getLatitude() + ":" + location.getLongitude(), Toast.LENGTH_LONG).show();
	            }
	        }
        } catch(Exception e) {
        	if (provider.equalsIgnoreCase(LocationManager.NETWORK_PROVIDER)) {
				isCoarseLocation = true;
				registerListener();
			}
		}
	}

	public void removeListener() {
		locationManager.removeUpdates(listener);
	}

	private LocationListener listener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			if (location == null) {
				return;
			}

			geoLocation = new GeoLocation(location.getLatitude(), location.getLongitude());
			context.setGeoLocation(geoLocation);

			if (location.getProvider().equalsIgnoreCase(LocationManager.GPS_PROVIDER)) {
			    isFineLocation = true;
			    Toast.makeText(context, R.string.msg_fine_location, Toast.LENGTH_SHORT).show();
			    removeListener();
			} else {
			    isCoarseLocation = true;
			    Toast.makeText(context, R.string.msg_coarse_location, Toast.LENGTH_LONG).show();
			    removeListener();
			    registerListener();
			}
			if (Logger.isDebug()) {
			    Toast.makeText(context, location.getProvider() + "-->" + location.getLatitude() + ":" + location.getLongitude(), Toast.LENGTH_LONG).show();
			    System.out.println("location: " + location.getProvider() + location);
		    }

			if (isCoarseLocation || isFineLocation) {
				Theme theme = context.getSkinTheme();
				btnLocation.setBackgroundDrawable(theme.getDrawable("selector_btn_location_success"));
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			if (provider.equalsIgnoreCase(LocationManager.GPS_PROVIDER)) {
				Toast.makeText(context, R.string.msg_gps_not_turn_on, Toast.LENGTH_SHORT).show();
			} else if (provider.equalsIgnoreCase(LocationManager.NETWORK_PROVIDER)) {
				//无线网络定位未开启
				Toast.makeText(context, R.string.msg_network_not_turn_on, Toast.LENGTH_SHORT).show();
				isCoarseLocation = true;
				registerListener();
			}
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

	};
	public boolean isAutoLocate() {
		return isAutoLocate;
	}

	public void setAutoLocate(boolean isAutoLocate) {
		this.isAutoLocate = isAutoLocate;
	}
}
