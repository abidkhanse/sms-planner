package project.planner.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import project.planner.adapters.ActiveSMSAdapter;
import project.planner.db.DBAdapter;
import project.planner.db.DataBaseOperations;
import project.planner.db.SmsRecordsTable;
import project.planner.models.ContactInfo;
import project.planner.receivers.SmsReceiver;
import project.planner.sms.R;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SmsHistoryActivity extends ListActivity {

	ListView contactsListView;
	List<ContactInfo> myList = new ArrayList<ContactInfo>();
	List<ContactInfo> list = new ArrayList<ContactInfo>();

	
	final int SMSTITLE = 1;
	final int SMSMESSAGE = 2;
	final int CONTACTS = 3;
	final int MESSAGEID = 4;
	final int INTENTID = 5;
	final int ISVALID = 6;
	final int TIMECOUNT = 7;
	final int ISDELIVERED = 8;

	long smstimeinmilliseconds;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActiveSMSAdapter activeSMSAdapter = new ActiveSMSAdapter(this, myList);
		getListView().setBackgroundColor(Color.WHITE);
		LoadContactListFromPhone();
		setListAdapter(activeSMSAdapter);
        context = getApplicationContext();
	}

	@Override
	public void onListItemClick(ListView parent, View v, int position, long id) {

		TextView tv = (TextView) v.findViewById(R.id.phone);
		CheckedTextView ct = (CheckedTextView) v.findViewById(R.id.firstname);
		{
			ContactInfo info = (ContactInfo) myList.get(position);
			String time = convertmillisecondsindays(myList.get(position)
					.getTimeinstring());
			if (smstimeinmilliseconds > 5000) {
				ct.toggle();
				if (ct.isChecked()) {

					ct.setText(myList.get(position).getSmsTitle());
					tv.setText(time);
					ct.setTextColor(Color
							.parseColor(getString(R.color.theme_blue)));
					myList.get(position).setIsvalid(1);
					activateSms(info.getIntentid(), info.getIntentid(),
							info.GetMessage(), info.GetContas(),
							info.getTimeinstring());
				} else {
					myList.get(position).setIsvalid(0);
					cancelSms(myList.get(position).getIntentid());
					ct.setTextColor(Color.BLACK);
				}
			} else {
				tv.setText("Selected time is in past");
			}
		}
	}

	@Override
	public void onBackPressed() {
		
		finish();
	}

	/**
	 * cancel or deactivate sms on user request
	 * 
	 * @param requestcode
	 */
	public void cancelSms(int requestcode) {

		Intent alarmIntent = new Intent(this, SmsReceiver.class);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		PendingIntent displayIntent = PendingIntent.getBroadcast(
			context, requestcode, alarmIntent, 0);
		alarmManager.cancel(displayIntent);
		DataBaseOperations operations = DataBaseOperations
				.getInstance(context);
		operations.UpdateIsValidStatus(requestcode, 0);

		Toast.makeText(this, "SMS Canceled", Toast.LENGTH_SHORT).show();

	}

	/**
	 * 
	 * Activate sms if already deactivated
	 * 
	 * @param requestcode
	 * @param rowid
	 * @param sms
	 * @param contacts
	 * @param milliseconds
	 */
	public void activateSms(int requestcode, int rowid, String sms,
			String contacts, String milliseconds) {
		Intent intent = new Intent(this, SmsReceiver.class);

		if (rowid > -1) {
			intent.putExtra("message", sms);
			intent.putExtra("contacts", contacts);
			intent.putExtra("uniqueid", Long.toString(rowid));
			DataBaseOperations operations = DataBaseOperations
					.getInstance(context);
			int a = operations.UpdateMessageInfo(rowid, rowid, requestcode, 1);

			PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
					(int) requestcode, intent, 0);
			AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
			alarmManager.set(AlarmManager.RTC_WAKEUP,
					System.currentTimeMillis() + smstimeinmilliseconds,
					pendingIntent);

			Toast.makeText(this, "SMS Activated", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "Could not save in DB", Toast.LENGTH_SHORT)
					.show();
		}

	}

	public static long DateToCalendar(Calendar difference) {
		Calendar start = Calendar.getInstance();

		start.set(Calendar.YEAR, 2012);
		start.set(Calendar.MONTH, 0);
		start.set(Calendar.DATE, 1);
		start.set(Calendar.HOUR_OF_DAY, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);

		return difference.getTimeInMillis() - start.getTimeInMillis();
	}
	
	/**
	 * convert milliseconds into number of days.
	 * 
	 * @param milli
	 * @return number of days
	 */

	String convertmillisecondsindays(String milli) {

		long timefromstring = Long.parseLong(milli);
		Calendar today = Calendar.getInstance();
		long systemtimeinmilli = DateToCalendar(today);

		smstimeinmilliseconds = timefromstring - systemtimeinmilli;
		long diffinseconds = smstimeinmilliseconds / 1000;
		long minutes = diffinseconds / 60;
		long hours = minutes / 60;
		long minute = minutes % 60;
		long days = hours / 24;
		hours = hours % 24;

		String dayshoursminutes = "";
		if (days > 0)
			dayshoursminutes = days + " days ";
		if (hours > 0)
			dayshoursminutes += hours + " hours ";

		dayshoursminutes += minute + " minute";
		if (days == 0 && hours == 0 && minute == 0) {
			dayshoursminutes = "with in a minute";
		}
		return dayshoursminutes;
	}

	List<ContactInfo> LoadContactListFromPhone() {
		DataBaseOperations operations = DataBaseOperations
				.getInstance(context);
		Cursor c = operations.GetAllRecords(SmsRecordsTable.TABLE_NAME, null,null);
		if (c.moveToFirst()) {
			do {
				ContactInfo info = new ContactInfo(c.getString(SMSTITLE),
						convertmillisecondsindays(c.getString(TIMECOUNT)),
						c.getString(TIMECOUNT), c.getInt(ISVALID),
						c.getInt(INTENTID), c.getInt(ISDELIVERED),
						c.getString(SMSMESSAGE), c.getString(CONTACTS));
						convertmillisecondsindays(info.getTimeinstring());
				if (smstimeinmilliseconds > 0 && info.isdelivered == 0)
					myList.add(info);
			} while (c.moveToNext());
		} else {
			Toast.makeText(this, "no sms record found", Toast.LENGTH_SHORT).show();
		}
		return myList;
	}

}
