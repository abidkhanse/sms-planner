package project.planner.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import project.planner.db.ContactsTable;
import project.planner.db.DBAdapter;
import project.planner.db.DataBaseOperations;
import project.planner.db.GroupContactTable;
import project.planner.db.SmsRecordsTable;
import project.planner.models.globalVariables;
import project.planner.receivers.SmsReceiver;
import project.planner.sms.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * 
 * Send SMS Activity which is responsible to
 * <p>
 * create message, save message,
 * <p>
 * set time for sms, load contact,
 * <p>
 * get contacts from group.
 * 
 * @author KHAN
 * 
 */
public class SendSMSActivity extends Activity implements OnClickListener {

	Button buttonSendlater;
	ImageView btnperson;
	ImageView btnGroup;
	ImageView btnTimeAndDate;
	ImageView imgSmsText;

	EditText textPhoneNo;
	EditText textSMS;
	EditText textTitle;
	TextView txtdatetime;

	String title = "";
	String smsmessage = "";
	String contacts = "";
	String msg = "";

	long smstimeinmilliseconds = -1;

	private int myear = -1;
	private int mmonth = -1;
	private int mday = -1;
	private int mhour = -1, mminute = -1;
	public int year, month, day, hour, minute;

	private final int SMSLENGTH = 140;

	int recordcount = -1;
	Date selecteddateobj;
	Date systemdateobj;
	Date selectedtimeobj;
	Date systemtimeobj;

	static final int DATE_DIALOG_ID = 1;
	static final int TIME_DIALOG_ID = 0;

	List<String> list = new ArrayList<String>();

	ArrayList<String> contactsList = new ArrayList<String>();

	final int SMSTITLE = 1;
	final int SMSMESSAGE = 2;
	final int CONTACTS = 3;
	final int MESSAGEID = 4;
	final int INTENTID = 5;
	final int ISVALID = 6;
	final int TIMECOUNT = 7;
	final int ISDELIVERED = 8;
	final int TIMESTAMP = 9;

	private static final Uri URI = ContactsContract.Contacts.CONTENT_URI;
	String messagefromactivity = "";
	long futuretimeinmilli;
	String timestamp;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sendsmsactivityactivity);

		initializeViews();
		Calendar today = Calendar.getInstance();
		myear = today.get(Calendar.YEAR);
		mmonth = today.get(Calendar.MONTH);
		mday = today.get(Calendar.DAY_OF_MONTH);
		mhour = today.get(Calendar.HOUR_OF_DAY);
		mminute = today.get(Calendar.MINUTE);

		if (getIntent().hasExtra("id")) {
			String where = SmsRecordsTable.SMSRECORD_ID + " = "
					+ getIntent().getStringExtra("id");
			DataBaseOperations operations = DataBaseOperations
					.getInstance(getApplicationContext());
			Cursor c = operations.GetContact(where, SmsRecordsTable.TABLE_NAME,
					null, null);
			if (c.moveToFirst()) {
				textTitle.setText(c.getString(SMSTITLE));
				textSMS.setText(c.getString(SMSMESSAGE));
				msg = c.getString(CONTACTS).toString();
				textPhoneNo.setText(msg);
			} else {
				Toast.makeText(this, "No Contact Exists", Toast.LENGTH_SHORT)
						.show();
			}
		}

		if (savedInstanceState != null) {
			msg = savedInstanceState.getString("PHONE");
		}
	}

	/**
	 * initialize views
	 */
	private void initializeViews() {
		btnperson = (ImageView) findViewById(R.id.person);
		btnGroup = (ImageView) findViewById(R.id.group);
		btnTimeAndDate = (ImageView) findViewById(R.id.timeanddate);
		imgSmsText = (ImageView) findViewById(R.id.smstext);

		buttonSendlater = (Button) findViewById(R.id.sendlater);
		textPhoneNo = (EditText) findViewById(R.id.EditviewPhoneNo);
		textSMS = (EditText) findViewById(R.id.editTextSMS);
		textTitle = (EditText) findViewById(R.id.editTextTitle);
		txtdatetime = (TextView) findViewById(R.id.timeanddatetext);
	}

	@Override
	protected void onPause() {
		super.onPause();
		msg = textPhoneNo.getText().toString();
	}

	@Override
	protected void onResume() {
		super.onResume();
		textPhoneNo.setText(msg);
	}

	/**
	 * 
	 * get the contact list and refine it from invalid characters.
	 * 
	 */

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED) {
		} else if (requestCode == 1) {
			LoadContactListFromPhone(data.getStringExtra("contacts"));
			textPhoneNo.setText(msg);
		} else if (requestCode == 2) {
			contactsList = (ArrayList<String>) data
					.getStringArrayListExtra("contacts");
			int i = 0;
			while (contactsList.size() > i) {
				list.add(contactsList.get(i));
				if (msg.compareTo("") == 0)
					msg = msg + contactsList.get(i).replaceAll("-", "");
				else
					msg = msg + ";" + contactsList.get(i).replaceAll("-", "");
				i++;
			}
			textPhoneNo.setText(msg);
		} else if (requestCode == 3) {
			textTitle.setText(data.getStringExtra("smsheading"));
			textSMS.setText(data.getStringExtra("smstext"));

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		/*
		 * Get groups
		 * */
		case R.id.group: {
			DataBaseOperations operations = DataBaseOperations
					.getInstance(getApplicationContext());
			Cursor c = operations.GetAllRecords(GroupContactTable.TABLE_NAME,
					null, null);
			if (c.moveToFirst()) {
				Intent intent = new Intent(this, GroupListForSMS.class);
				intent.putExtra("sampleData", "smsinfo");
				startActivityForResult(intent, 1);
			} else {
				Toast.makeText(this, "No Group found", Toast.LENGTH_SHORT)
						.show();
			}

		}
			break;

			/*
			 * Get contacts
			 * */
		case R.id.person:
			ContentResolver contentResolver = getContentResolver();
			Cursor cursor = contentResolver.query(URI, null, null, null, null);
			if (cursor.getCount() > 0) {
				if (!globalVariables.loadingcontactsinprogress) {
					Intent intent = new Intent(this, SmsPlannerActivity.class);
					intent.putExtra("activityvalue", "sendSMSActivity");
					startActivityForResult(intent, 2);
				} else {
					int count = globalVariables.contactscount;
					Toast.makeText(this,
							"loading " + count + " contacts... wait a sec",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(this, "No Contact found", Toast.LENGTH_SHORT)
						.show();
			}
			break;
			/*
			 * verify sms with all requirements and set time in future
			 * */
		case R.id.sendlater: {
			if (GetContactsList()) {
				if (GetSMSTitle()) {
					if (GetSMSText()) {
						if (Validate()) {
							if (smsmessage.length() > SMSLENGTH) {
								int length = smsmessage.length();
								int numberofsms = length / SMSLENGTH;
								int extra = length % SMSLENGTH;
								if (extra > 0)
									numberofsms += 1;

								AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
										this);
								myAlertDialog
										.setTitle("Warning... \n1 SMS Length "
												+ SMSLENGTH);
								myAlertDialog.setMessage(numberofsms
										+ " Messages" + "\n" + "SMS length is "
										+ length + " characters");
								myAlertDialog.setPositiveButton("Continue",
										new DialogInterface.OnClickListener() {

											public void onClick(
													DialogInterface arg0,
													int arg1) {

												TimeDifference();
											}
										});
								myAlertDialog.setNegativeButton("Cancel",
										new DialogInterface.OnClickListener() {

											public void onClick(
													DialogInterface arg0,
													int arg1) {

												// do something when the Cancel
												// button is clicked
												//
											}
										});
								myAlertDialog.show();

							} else {
								TimeDifference();
							}
						}
					}
				}
			} else {
				Toast.makeText(this, "No Contact Selected", Toast.LENGTH_SHORT)
						.show();
			}
		}
			break;

		case R.id.smstext: {
			Intent intent = new Intent(this, ExpandableSmsActivity.class);
			intent.putExtra("sampleData", "smsinfo");
			startActivityForResult(intent, 3);
 
		}

			break;
 

		case R.id.timeanddate: {
			showDialog(TIME_DIALOG_ID);
			showDialog(DATE_DIALOG_ID);
		}
		default:
			break;
		}
	}


	private boolean TimeDifference() {

		Calendar future = Calendar.getInstance();
		future.set(Calendar.YEAR, year);
		future.set(Calendar.MONTH, month);
		future.set(Calendar.DATE, day);
		future.set(Calendar.HOUR_OF_DAY, hour);
		future.set(Calendar.MINUTE, minute);
		future.set(Calendar.SECOND, 0);

		Calendar today = Calendar.getInstance();
		timestamp = "" + year + "/" + month + "/" + day + " " + hour + ":"
				+ minute;

		long systemtimeinmilli = DateToCalendar(today);
		futuretimeinmilli = DateToCalendar(future);

		smstimeinmilliseconds = futuretimeinmilli - systemtimeinmilli;
		long i = smstimeinmilliseconds / (1000 * 60);

		if (smstimeinmilliseconds / (1000 * 60) > 5) {
			SendSMS(futuretimeinmilli, timestamp);
			return true;
		} else {
			Toast.makeText(this, "Time must be 5 minutes from now",
					Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	/*
	 * select date
	 */
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int yearSelected,
				int monthOfYear, int dayOfMonth) {
			year = yearSelected;
			month = monthOfYear;
			day = dayOfMonth;
		}
	};

	/*
	 * select time
	 */
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int min) {
			hour = hourOfDay;
			minute = min;
			String minutesinstring = min + "";
			String hourinstring = hourOfDay + "";
			int m = month + 1;
			String monthinstring = m + "";

			if (m < 10)
				monthinstring = "0" + monthinstring;
			if (hour < 10)
				hourinstring = "0" + hourinstring;

			if (min < 10)
				minutesinstring = "0" + minutesinstring;

			txtdatetime.setText(year + "/" + monthinstring + "/" + day + "  "
					+ hourinstring + ":" + minutesinstring);
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, myear, mmonth,
					mday);

		case TIME_DIALOG_ID:
			return new TimePickerDialog(this, mTimeSetListener, mhour, mminute,
					false);

		}
		return null;
	}

	/*
	 * set time from now.
	 */
	private void SendSMS(long futuretimeinmilliseconds, String timestamp) {
		String sms = textSMS.getText().toString();
		String title = textTitle.getText().toString().trim();
		Intent intent = new Intent(this, SmsReceiver.class);

		DataBaseOperations operations = DataBaseOperations
				.getInstance(getApplicationContext());
		long rowid = operations.InsertMessageInfo(title, sms, contacts, 1, 1,
				1, futuretimeinmilliseconds, 0, timestamp);
		if (rowid > -1) {
			intent.putExtra("uniqueid", Long.toString(rowid));
			int a = operations.UpdateMessageInfo(rowid, rowid, rowid, 1);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
					(int) rowid, intent, 0);
			AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
			alarmManager.set(AlarmManager.RTC_WAKEUP,
					System.currentTimeMillis() + smstimeinmilliseconds,
					pendingIntent);

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
			Toast.makeText(
					this,

					"sms will be delivered in"
							+ " " + dayshoursminutes, Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(this, "cannot deliver. database error", Toast.LENGTH_SHORT).show();
		}

		finish();
	}

	/*
	 * activate this method if restriction required. (for paid app)
	 */
	boolean Validate() {
		boolean vaild = true;

		// String str = contacts;
		// String[] contact;
		// String delimiter = ";";
		// contact = str.split(delimiter);
		//
		// if(contact.length > 2)
		// {
		// String message;
		// String heading = "Contacts Restriction";
		// message =
		// "You can send future sms to only two contacts, want to get Full Version ? ";
		// MessageAlert(message,heading);
		// vaild = false;
		//
		// }
		//
		// if(LoadGroupList() > 1)
		// {
		// String message;
		// String heading = "Future SMS Restriction";
		// message =
		// "You have already set two sms in future, want to get Full version ? ";
		// MessageAlert(message, heading);
		// vaild = false;
		//
		// }

		return vaild;
	}

	private long convertmillisecondsindays(String milli) {

		long timefromstring = Long.parseLong(milli);
		Calendar today = Calendar.getInstance();
		long systemtimeinmilli = DateToCalendar(today);

		return (timefromstring - systemtimeinmilli);
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

	void MessageAlert(String msg, String heading) {
		{
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);
			alertDialogBuilder.setTitle(heading);
			alertDialogBuilder
					.setMessage(msg)
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {

									dialog.cancel();
								}
							});

			AlertDialog alertDialog = alertDialogBuilder.create();

			alertDialog.show();
		}
	}

	/**
	 * Check if contact inserted
	 * 
	 * @return true if found, otherwise false
	 */
	private boolean GetContactsList() {
		msg = textPhoneNo.getText().toString().trim();
		if (msg.equals("")) {
			Toast.makeText(
					this,
					"no contact selected", Toast.LENGTH_SHORT)
					.show();
			return false;
		} else {
			contacts = msg;
			return true;
		}
	}

	/**
	 * Check if sms text inserted
	 * 
	 * @return true if found, otherwise false
	 */
	private boolean GetSMSText() {
		smsmessage = textSMS.getText().toString().trim();

		if (smsmessage.equals("")) {
			Toast.makeText(this,
					"insert message", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			contacts = msg;
			return true;
		}
	}

	/**
	 * Check if title inserted
	 * 
	 * @return true if found, otherwise false
	 */
	private boolean GetSMSTitle() {
		title = textTitle.getText().toString().trim();
		if (title.equals("")) {
			Toast.makeText(this,
					"enter title", Toast.LENGTH_SHORT).show();
					 
			return false;
		} else if (title.length() > 30) {
			Toast.makeText(this,
					"title length 20 characters", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private void LoadContactListFromPhone(String groupid) {
		String where = ContactsTable.CONTACT_GROUPID + " = " + groupid;
		DataBaseOperations operations = DataBaseOperations
				.getInstance(getApplicationContext());
		Cursor c = operations.GetContact(where, ContactsTable.TABLE_NAME, null,
				null);
		if (c.moveToFirst()) {
			do {
				list.add(c.getString(3));
				if (msg.compareTo("") == 0) {
					msg = msg + c.getString(3).replace("-", "");
				} else {
					msg = msg + ";" + c.getString(3).replace("-", "");
				}
			} while (c.moveToNext());
		} else {
			Toast.makeText(this,
					"no contact found", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 
	 * Check if contact inserted
	 * 
	 */
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
	 * Send message now
	 */

	private void SendSMSNow() {
		String sms = textSMS.getText().toString();

		String str = contacts;
		String[] contact;
		String delimiter = ";";
		contact = str.split(delimiter);

		boolean success = true;
		for (int i = 0; i < contact.length; i++) {
			contact[i] = cleanString(contact[i]);
			if (contact[i].length() > 0 && sms.length() > 0) {
				try {
					SmsManager smsManager = SmsManager.getDefault();
					ArrayList<String> parts = smsManager.divideMessage(sms);
					smsManager.sendMultipartTextMessage(contact[i], null,
							parts, null, null);
				} catch (Exception e) {
					Toast.makeText(this,
							"Could't send message to " + contact[i],
							Toast.LENGTH_SHORT).show();
					success = false;
				}
			}
		}
		if (success)
			Toast.makeText(this,
					context.getResources().getString(R.string.sms_delivered),
					Toast.LENGTH_SHORT).show();

		finish();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("PHONE", textPhoneNo.getText().toString());
	}

}
