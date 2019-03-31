package project.planner.receivers;

import java.util.Calendar;

import project.planner.activities.SmsHistoryActivity;
import project.planner.db.DBAdapter;
import project.planner.db.DataBaseOperations;
import project.planner.db.SmsRecordsTable;
import project.planner.models.ContactInfo;
import project.planner.sms.R;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;


/**
 * 
 * LoadOnBoot class is very critical and important class in this application. When 
 * user restart mobile phone, this class makes sure that all valid and not delivered
 * messages are ready to deliver.
 *  
 * @author KHAN
 *
 */
public class LoadOnBoot extends BroadcastReceiver {

	final int SMSTITLE 		= 1;
	final int SMSMESSAGE	= 2;
	final int CONTACTS 		= 3;
	final int MESSAGEID 	= 4;
	final int INTENTID 		= 5;
	final int ISVALID 		= 6;
	final int TIMECOUNT 	= 7;
	final int ISDELIVERED 	= 8;
	final int TIMESTAMP 	= 9;
	
	int FALSE 	= 0;
	int TRUE 	= 1; 

	public NotificationManager myNotificationManager;
	long smstimeinmilliseconds;

	/**
	 * Fetch record from smsrecordinfo table.
	 * make sure that message is valid and not delivered yet.
	 * send message and update user by notification
	 */
	@Override
	public void onReceive(Context context, Intent intent) {

		Intent _intent = new Intent(context, SmsReceiver.class);
		boolean found = false;
		int i = 0;

		DataBaseOperations dbb = DataBaseOperations.getInstance(context);
		Cursor c = dbb.GetAllRecords(SmsRecordsTable.TABLE_NAME, null, null);
		if (c.moveToFirst()) {
			do {
				ContactInfo info = new ContactInfo(c.getString(SMSTITLE),
						timeDifference(c.getString(TIMECOUNT)),
						c.getString(TIMECOUNT), c.getInt(ISVALID),
						c.getInt(INTENTID), c.getInt(ISDELIVERED),
						c.getString(SMSMESSAGE), c.getString(CONTACTS));

				if (smstimeinmilliseconds > 0 && info.isdelivered == FALSE  && info.isvalid == TRUE) {
					_intent.putExtra("message", c.getString(SMSMESSAGE));
					_intent.putExtra("contacts", c.getString(CONTACTS));
					_intent.putExtra("uniqueid",
							Long.toString(info.getIntentid()));

					found = true;
					i++;
					PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) info.getIntentid(), _intent, 0);
					AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
					alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + smstimeinmilliseconds,pendingIntent);
				}
			} while (c.moveToNext());
		}

		if (found) {
			myNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			String NotificationTicket = "You have set " + i + " future Message(s)";

			Notification notification = new Notification(R.drawable.ic_launcher, NotificationTicket, 0);
			Intent __intent = new Intent(context, SmsHistoryActivity.class);

			PendingIntent contentIntent = PendingIntent.getActivity(context, 1,
					__intent, PendingIntent.FLAG_UPDATE_CURRENT);
			notification.setLatestEventInfo(context, "Future SMS",
					NotificationTicket, contentIntent);
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			myNotificationManager.notify(1, notification);
		}

	}

	public static long dateToCalendar(Calendar difference) {
		Calendar start = Calendar.getInstance();

		start.set(Calendar.YEAR, 2012);
		start.set(Calendar.MONTH, 0);
		start.set(Calendar.DATE, 1);
		start.set(Calendar.HOUR_OF_DAY, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);

		return difference.getTimeInMillis() - start.getTimeInMillis();
	}
	
	
	private String timeDifference(String milli) {

		long timefromsdatabase = Long.parseLong(milli);
		Calendar today = Calendar.getInstance();
		long systemtimeinmilli = dateToCalendar(today);
		smstimeinmilliseconds = timefromsdatabase - systemtimeinmilli;

		return null;

	}

}
