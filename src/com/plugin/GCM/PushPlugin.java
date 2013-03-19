package com.plugin.gcm;

import java.io.IOException;

import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.plugin.utils.Constants;
import com.plugin.utils.PropertyReader;

public class PushPlugin extends CordovaPlugin {
	private static final String TAG = PushPlugin.class.getName();
	private static PropertyReader propreader = new PropertyReader();

	public PluginResult execute(String action, JSONArray args, String callbackId) {
		try {
			if (Constants.REGPUSH_METHOD.equals(action)) {
				return registerPush(args);
			} else if (Constants.UNREGPUSH_METHOD.getName().equals(action)) {
				return unregisterPush(args);
			} else if (Constants.SENDID_METHOD.getName().equals(action)) {
				return sendDeviceId(args);
		    } else {
				Log.e(TAG, action);
				return new PluginResult(PluginResult.Status.INVALID_ACTION);
			}
		} catch (JSONException ex) {
			ex.printStackTrace();
			return new PluginResult(PluginResult.Status.JSON_EXCEPTION);
		}
	}

	private PluginResult sendDeviceId(JSONArray args) throws JSONException {
		String deviceIdStr = args.getString(0);
		String serverUrl = PushPlugin.propreader.getProperty(Constants.SERVER_URL_PROPERTY.getName());
		HttpPost httpMethod = new HttpPost(serverUrl.replace("#id#", deviceIdStr));
        httpMethod.addHeader("Accept", "text/html");
        httpMethod.addHeader("Content-Type", "application/xml");
        AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        try {
            HttpResponse response = client.execute(httpMethod);
            return new PluginResult(PluginResult.Status.OK, response.toString());
        } catch (IOException e) {
            return new PluginResult(PluginResult.Status.ERROR, e.getMessage());
        }
	}
	
	private PluginResult unregisterPush(JSONArray args) {
		try {
			GCMRegistrar.unregister(cordova.getActivity()
					.getApplicationContext());
			Log.d(TAG, "PUSH notifications.");
			return new PluginResult(PluginResult.Status.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new PluginResult(PluginResult.Status.ERROR, ex.getMessage());
		}
	}

	private PluginResult registerPush(JSONArray args) throws JSONException {
		String projectIdStr = args.getString(0);
		if (isBlank(projectIdStr)) {
			return new PluginResult(PluginResult.Status.ERROR,
					"project id must be set in parameters");
		} else {
			try {
				long projectId = Long.parseLong(projectIdStr);
				String deviceId = registerPush(projectId);
				return new PluginResult(PluginResult.Status.OK, deviceId);
			} catch (NumberFormatException ex) {
				ex.printStackTrace();
				return new PluginResult(PluginResult.Status.ERROR,
						"project id must be wellformed number");
			} catch (IOException ex) {
				ex.printStackTrace();
				return new PluginResult(PluginResult.Status.ERROR,
						ex.getMessage());
			}
		}
	}

	private String registerPush(long projectId) throws IOException {
		inetIsOk();
		GCMRegistrar.checkDevice(cordova.getActivity().getApplicationContext());
		GCMRegistrar.checkManifest(cordova.getActivity()
				.getApplicationContext());
		String regId = GCMRegistrar.getRegistrationId(cordova.getActivity()
				.getApplicationContext());
		if (regId.equals("")) {
			GCMRegistrar.register(
					cordova.getActivity().getApplicationContext(), projectId
							+ "");
			Log.i(TAG,
					"just now registered: "
							+ GCMRegistrar.getRegistrationId(cordova
									.getActivity().getApplicationContext()));
			regId = GCMRegistrar.getRegistrationId(cordova.getActivity()
					.getApplicationContext());
			return regId;
		} else {
			Log.i(TAG, "Already registered: " + regId);
			return regId;
		}
	}

	public void inetIsOk() throws IOException {
		ConnectivityManager connMgr = (ConnectivityManager) cordova
				.getActivity().getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return;
		} else {
			throw new IOException("error:inetIsOk method-->error connection");
		}
	}

	public static boolean isBlank(String s) {
		if (s == null || "".equals(s.trim()))
			return true;
		else
			return false;
	}
}