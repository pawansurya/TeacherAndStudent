package tandc.ramana.com.teacherandstudent;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";

	private Controller aController = null;
	int notification_no = 0;
	SharedPreferences shared_notification_no;
	private static Editor editor_notification_id;
	// private static GPSTracker gpsLocation;

	private int defValue;

	private static double dist;

	private static String lat_message;

	private static String long_message;

	private static String id_message;

	public GCMIntentService() {
		super(CommonUtilities.SENDER_ID);
	}

	/**
	 * Method called on device registered
	 **/
	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(TAG, "Device registered: regId = " + registrationId);
		CommonUtilities.displayMessage(context,
				"Your device registred with GCM");
		// Log.d("NAME", MainActivity.name);
		// ServerUtilities.register(context, MainActivity.name,
		// MainActivity.email, registrationId);
	}

	/**
	 * Method called on device un registred
	 * */
	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
		CommonUtilities.displayMessage(context,
				getString(R.string.gcm_unregistered));
		// ServerUtilities.unregister(context, registrationId);
	}

	/**
	 * Method called on Receiving a new message
	 * */
	/*
	 * @Override protected void onMessage(Context context, Intent intent) {
	 * Log.i(TAG, "Received message"); String message =
	 * intent.getExtras().getString("test");
	 * System.out.println("++++++++++++++++" + message);
	 * 
	 * // displayMessage(context, message); // notifies user
	 * generateNotification(context, message); }
	 */
	/**
	 * Method called on receiving a deleted message
	 * */
	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
		String message = getString(R.string.gcm_deleted, total);
		// displayMessage(context, message);
		// notifies user
		generateNotification(context, message);
	}

	/**
	 * Method called on Error
	 * */
	@Override
	public void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
		CommonUtilities.displayMessage(context,
				getString(R.string.gcm_error, errorId));
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		Log.i(TAG, "Received recoverable error: " + errorId);
		CommonUtilities.displayMessage(context,
				getString(R.string.gcm_recoverable_error, errorId));
		return super.onRecoverableError(context, errorId);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	private static void generateNotification(Context context, String message) {

		Log.e("ramanaResponceNotificat",""+message);
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		@SuppressWarnings("deprecation")
		Notification notification = new Notification(icon, message, when);

		String title = context.getString(R.string.app_name);

		Intent notificationIntent = new Intent(context, Home.class);
		notificationIntent.putExtra("msg",message);
		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		 //notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// Play default notification sound
		notification.defaults |= Notification.DEFAULT_SOUND;

		// notification.sound = Uri.parse("android.resource://" +
		// context.getPackageName() + "your_sound_file_name.mp3");

		// Vibrate if vibrate is enabled
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notificationManager.notify(0, notification);

	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		// gpsLocation = new GPSTracker(this);
		Log.e("ramanaaaaNotification","");
		shared_notification_no = getApplicationContext().getSharedPreferences(
				"MyPref", 0);
		editor_notification_id = shared_notification_no.edit();


	/*	if (aController == null)
			aController = (Controller) getApplicationContext();*/


		
		lat_message = intent.getExtras().getString("test");

		System.out.println("++++++++++++++++++++++++++++Meaasge" +lat_message);

		String message = lat_message;
		

		/*
		 * double theta = gpsLocation.getLongitude() -
		 * Double.valueOf(long_message); dist =
		 * Math.sin(deg2rad(Double.valueOf(gpsLocation.getLatitude()))) *
		 * Math.sin(deg2rad(Double.valueOf(lat_message))) +
		 * Math.cos(deg2rad(Double.valueOf(gpsLocation.getLatitude()))) *
		 * Math.cos(deg2rad(Double.valueOf(lat_message))) *
		 * Math.cos(deg2rad(theta)); dist = Math.acos(dist); dist =
		 * rad2deg(dist); dist = dist * 60 * 1.1515 * 1.6093; Log.i("",
		 * "Dist is : "+dist +"   Km");
		 *
		 * Log.i(TAG, "XXX Received message"+message);
		 */
		//displayMessageOnScreen(context, message);
		// notifies user
		notification_no = shared_notification_no.getInt("nitifcation_ID", 0) + 1;
		generateNotification(context, message, notification_no, null);
	}

	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	private double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}

	@SuppressWarnings("deprecation")
	private static void generateNotification(Context context, String message,
			int notification_Id, String message_2) {

		Log.e("ramanaaaa notification","++++");
		
		String strtitle ="Alert";
		// Set Notification Text
		String strtext = ""+message;
 
		// Open NotificationView Class on Notification Click
		Intent intent = new Intent(context, Home.class);
		// Send data to NotificationView Class
		intent.putExtra("title", strtitle);
		intent.putExtra("msg",message);

		// Open NotificationView.java Activity
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
 
		//Create Notification using NotificationCompat.Builder 
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
				// Set Icon
				.setSmallIcon(R.drawable.ic_launcher)
				// Set Ticker Message
				.setTicker("Teacher")
				// Set Title
				.setContentTitle(""+lat_message)
				// Set Text
				.setContentText(""+message)
				// Add an Action Button below Notification
				//.addAction(R.drawable., "Action Button", pIntent)
				// Set PendingIntent into Notification
				.setContentIntent(pIntent)
				// Dismiss Notification
				.setAutoCancel(true);
 
		// Create Notification Manager
		NotificationManager notificationmanager = (NotificationManager)context. getSystemService(NOTIFICATION_SERVICE);
		// Build Notification with Notification Manager
		notificationmanager.notify(0, builder.build());
	}
	
	  void displayMessageOnScreen(Context context, String message) {
	    	 
	        Intent intent = new Intent(Config.DISPLAY_MESSAGE_ACTION);
	        intent.putExtra(Config.EXTRA_MESSAGE, message);
	        
	        // Send Broadcast to Broadcast receiver with message
	        context.sendBroadcast(intent);
	        
	    }


}
