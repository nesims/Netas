package com.netas;

import java.util.List;
import com.google.android.gcm.GCMBaseIntentService;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.netas.utilities.CommonUtilities;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

	@SuppressWarnings("hiding")
	private static final String TAG = "GCMIntentService";

	public GCMIntentService() {
		super(CommonUtilities.SENDER_ID);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(TAG, "Device registered: regId = " + registrationId);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(TAG, "Received message");
		String message = getString(R.string.gcm_message);

		String mes = intent.getExtras().getString("message");
		String param = intent.getExtras().getString("param");
		String time = intent.getExtras().getString("time");
		String from = intent.getExtras().getString("from");

		message = "";
		message = mes;

		try {
			if (isRunning(context)) {
				if (isFrontActivity(context)) {
					CommonUtilities.displayMessage(context, message, param);
				} else {
					generateNotification(context, intent.getExtras());
				}
			} else {
				generateNotification(context, intent.getExtras());
			}
		} catch (Exception e) {
		}
	}

	public boolean isRunning(Context ctx) {
		ActivityManager activityManager = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = activityManager
				.getRunningTasks(Integer.MAX_VALUE);
		for (RunningTaskInfo task : tasks) {

			if (ctx.getPackageName().equalsIgnoreCase(
					task.baseActivity.getPackageName()))
				return true;
		}

		return false;
	}

	private boolean isFrontActivity(Context ctx) {
		ActivityManager activityManager = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = activityManager
				.getRunningTasks(Integer.MAX_VALUE);
		if (ctx.getPackageName().equalsIgnoreCase(
				tasks.get(0).baseActivity.getPackageName())) {
			return true;
		}
		return false;
	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
		String message = getString(R.string.gcm_deleted, total);
		Bundle bundle = new Bundle();
		bundle.putString(CommonUtilities.EXTRA_MESSAGE, message);
		generateNotification(context, bundle);
	}

	@Override
	public void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		Log.i(TAG, "Received recoverable error: " + errorId);
		return super.onRecoverableError(context, errorId);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	private static void generateNotification(Context context, Bundle bundle) {
		int icon = R.drawable.icon;
		long when = System.currentTimeMillis();
		String message = bundle.getString("message");
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);
		String title = context.getString(R.string.app_name);
		Intent notificationIntent = new Intent(context, NetasDemoActivity.class);

		notificationIntent.putExtras(bundle);

		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.defaults |= Notification.DEFAULT_SOUND
				| notification.DEFAULT_LIGHTS | notification.DEFAULT_VIBRATE;
		// long[] vibrate = {0,100,200,300};
		// notification.vibrate = vibrate;
		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL
				| Notification.FLAG_SHOW_LIGHTS;
		notification.ledOnMS = 1000; // light on in milliseconds
		notification.ledOffMS = 4000; // light off in milliseconds
		notificationManager.notify(0, notification);
	}
}