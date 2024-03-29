package com.reminderService.activity;


/**
 * @author Sumit Kumar Maji
 */
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.reminderService.util.CommonUtilities;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";
	int count=0;
	private String messageNew;

	public GCMIntentService() {
		super(CommonUtilities.SENDER_ID);
	}

	/**
	 * Method called on device registered
	 **/
	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(TAG, "Device registered: regId = " + registrationId);
		CommonUtilities.displayMessage(context, "Your device registred with GCM");
		// Log.d("NAME", MainActivity.name);
		//  ServerUtilities.register(context, MainActivity.name, MainActivity.email, registrationId);
	}

	/**
	 * Method called on device un registred
	 * */
	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
		CommonUtilities.displayMessage(context, getString(R.string.gcm_unregistered));
		// ServerUtilities.unregister(context, registrationId);
	}

	/**
	 * Method called on Receiving a new message
	 * */
	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(TAG, "Received message");
		String message = intent.getExtras().getString("msg");

		CommonUtilities.displayMessage(context, message);
		// notifies user
		generateNotification(context, message);
	}

	/**
	 * Method called on receiving a deleted message
	 * */
	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
		String message = getString(R.string.gcm_deleted, total);
		CommonUtilities.displayMessage(context, message);
		// notifies user
		generateNotification(context, message);
	}

	/**
	 * Method called on Error
	 * */
	@Override
	public void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
		CommonUtilities.displayMessage(context, getString(R.string.gcm_error, errorId));
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		Log.i(TAG, "Received recoverable error: " + errorId);
		CommonUtilities.displayMessage(context, getString(R.string.gcm_recoverable_error,
				errorId));
		return super.onRecoverableError(context, errorId);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	private  void generateNotification(final Context context, String message) {
		getPreferance();
		//int count=0;
		int icon = R.drawable.icon;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager)
				context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);

		String title = context.getString(R.string.app_name);
		int counter = ++count;
		notification.number = counter;
		SavePreferences(counter);
		Intent notificationIntent = new Intent(context, CancelTicketActivity.class);
		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);//Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP
		PendingIntent intent =
				PendingIntent.getActivity(context, 0, notificationIntent, 0);

		if(message.equalsIgnoreCase("call successful"))
		{
			messageNew="Its your turn !!!";
		}
		else if(message.equalsIgnoreCase("ticket cancel"))
		{
			messageNew="Your ticket is cancelled. Please contact to admin";
		}
		notification.setLatestEventInfo(context, title, messageNew, intent);

		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// Play default notification sound
		//notification.defaults |= Notification.DEFAULT_SOUND;

		//notification.sound = Uri.parse("android.resource://" +getApplicationContext().getPackageName() +"/"+R.raw.call);




		// Vibrate if vibrate is enabled
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notificationManager.notify(45892354, notification);      

		//		new AlertDialog.Builder(context)
		//		.setMessage(messageNew)			
		//		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		//
		//			@Override
		//			public void onClick(DialogInterface dialog, int which) {
		//				
		//				calListActivity();
		//			}
		//
		//			private void calListActivity() {
		//				Intent intent = new Intent(context,ListingActivity.class);
		//				startActivity(intent);
		//				
		//			}
		//		}).show();
	}


	////////////////////////////////////////////////////////////////////////////

	public void SavePreferences(int count){
		SharedPreferences sharedPreferences = getSharedPreferences("Prerference",MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt("Mem1", count);
		editor.commit();
	}


	// This use for save in share preference and get from share preference  

	public void getPreferance() 
	{

		SharedPreferences settings=getSharedPreferences("Prerference",0);
		count=settings.getInt("Mem1",0);

	}

}
