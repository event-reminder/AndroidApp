package com.yuriylisovskiy.er.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkHelpers {

	public static final int TYPE_WIFI = 1;
	public static final int TYPE_MOBILE = 2;
	public static final int TYPE_NOT_CONNECTED = 0;
	public static final int NETWORK_STATUS_NOT_CONNECTED = 0;
	public static final int NETWORK_STATUS_WIFI = 1;
	public static final int NETWORK_STATUS_MOBILE = 2;

	public static int getConnectivityStatus(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (null != activeNetwork) {
			if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
				return TYPE_WIFI;
			}

			if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
				return TYPE_MOBILE;
			}
		}
		return TYPE_NOT_CONNECTED;
	}

	public static int getConnectivityStatusString(Context context) {
		int conn = NetworkHelpers.getConnectivityStatus(context);
		int status = NETWORK_STATUS_NOT_CONNECTED;
		if (conn == NetworkHelpers.TYPE_WIFI) {
			status = NETWORK_STATUS_WIFI;
		} else if (conn == NetworkHelpers.TYPE_MOBILE) {
			status = NETWORK_STATUS_MOBILE;
		}
		return status;
	}
}
