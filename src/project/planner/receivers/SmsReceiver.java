package project.planner.receivers;

import java.util.ArrayList;

import project.planner.activities.NotificationViewActivity;
import project.planner.db.DBAdapter;
import project.planner.db.DataBaseOperations;
import project.planner.db.SmsRecordsTable;
import project.planner.sms.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * 
 * SmsReceiver class is important class in this application. 
 * This class is responsible to deliver sms which use had scheduled. 
 * 
 * @author KHAN
 * 
 */
public class SmsReceiver extends BroadcastReceiver {
	Context _context;
	String message = "";
	String contacts = "";
	String title = "";
	public NotificationManager myNotificationManager;
	boolean success = true;
	int sentfailed = 0;

	String[] temp;

	private String cleanString(String s) {
		if (s.contains("<"))
			s = s.substring(s.indexOf("<") + 1);

		if (s.contains(">"))
			s = s.substring(0, s.indexOf(">"));

		if (s.contains(" "))
			s = s.replace(" ", "");

		return s;
	}

	/**
	 * Fetch record from smsrecordinfo table.
	 * make sure that message is valid and not delivered yet.
	 * Activate all scheduled messages and inform user with notification.
	 */
	
	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {
		_context = context;

		String uniqueid = intent.getStringExtra("uniqueid");
		DataBaseOperations operations = DataBaseOperations.getInstance(context);
		Cursor c = operations.GetContact(SmsRecordsTable.SMSRECORD_ID + " = "
				+ uniqueid, SmsRecordsTable.TABLE_NAME, null, null);
		if (c.getCount() > 0 && c.moveToFirst()) {

			int RECORD_MESSAGE = 2;
			int RECORD_CONTACTS = 3;

			message = c.getString(RECORD_MESSAGE);
			contacts = c.getString(RECORD_CONTACTS);
			title = c.getString(1);
			String str = contacts;
			String delimiter = ";";
			temp = str.split(delimiter);

			for (int i = 0; i < temp.length; i++) {
				temp[i] = cleanString(temp[i]);

				if (temp[i].length() > 0 && message.length() > 0)
					SendSMS(temp[i], message);
				if (!success)
					sentfailed++;
			}

			operations.UpdateStatusInfo(uniqueid, 1);

			myNotificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			String NotificationTicket = "Message Delivered";
			if (sentfailed > 0)
				NotificationTicket += " "+  sentfailed;

			if (temp.length == sentfailed)
				NotificationTicket = context.getResources().getString(R.string.cant_send_msg) +
						context.getResources().getString(R.string.chk_network_con);

			String NotificationTitle = contacts;
			String NotificationContent = title;

			Notification notification = new Notification(
					R.drawable.ic_launcher, NotificationTicket, 0);

			Intent _intent = new Intent(context, NotificationViewActivity.class);
			_intent.putExtra("message", message);
			_intent.putExtra("contacts", contacts);

			PendingIntent contentIntent = PendingIntent.getActivity(context,
					Integer.parseInt(uniqueid), _intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			notification.setLatestEventInfo(context, NotificationTitle,
					NotificationContent, contentIntent);
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			myNotificationManager.notify(Integer.parseInt(uniqueid),
					notification);
		} else {
			Toast.makeText(_context, "no record found " + uniqueid,
					Toast.LENGTH_SHORT).show();
		}

	}

	private void SendSMS(String phoneNo, String sms) {
		try {
			SmsManager smsManager = SmsManager.getDefault();
			ArrayList<String> parts = smsManager.divideMessage(sms);
			smsManager.sendMultipartTextMessage(phoneNo, null, parts, null,null);

			success = true;
		} catch (Exception e) {
			Toast.makeText(_context, phoneNo + " " + sms, Toast.LENGTH_SHORT).show();
			success = false;
		}
	}
}
