package com.google.android.gcm;

import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.luxoft.dms.App;
import com.luxoft.dms.R;

public class GCMIntentService extends GCMBaseIntentService {

	
	protected GCMIntentService(String senderId) {
		super(senderId);
	}
	private final static String senderID = "304113618910";
	
	public GCMIntentService(){
	    super(senderID);
	    Log.i("GCMIntentService", "GCM passed");
	}
	
	@Override
	protected void onError(Context arg0, String arg1) {
		Log.d("GCM onError", arg1);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		Log.d("GCM onRecoverableError", errorId);
		return false;
	}

	@Override
	protected void onMessage(Context ctx, Intent intt) {
		Log.d("onMessage", String.valueOf(intt));
		for (String s : intt.getExtras().keySet()) {
			Log.d(s, intt.getExtras().getString(s));
		}

		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		
		int icon = R.drawable.ic_launcher;
		CharSequence tickerText = intt.getStringExtra("message");
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		notification.flags = Notification.DEFAULT_LIGHTS
				| Notification.FLAG_AUTO_CANCEL;

		CharSequence contentTitle = intt.getStringExtra("title");
		Random r = new Random();
		int notificationId = r.nextInt();
		Intent notificationIntent = new Intent(this, App.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this,
				notificationId, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(ctx, contentTitle, tickerText,
				contentIntent);

		mNotificationManager.notify(notificationId, notification);
	}

	@Override
	protected void onRegistered(Context context, String arg1) {
		Log.d("onRegistered", arg1);
		Log.d("onRegistered",
				"turn on PUSH");
	}

	@Override
	protected void onUnregistered(Context context, String arg1) {
		Log.d("onUnregistered", arg1);
		Log.d("onUnregistered",
				"turn off PUSH");
	}

}
