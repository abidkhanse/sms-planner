package project.planner.activities;

import java.util.Calendar;

import project.planner.db.DataBaseOperations;
import project.planner.db.SmsRecordsTable;
import project.planner.models.ContactInfo;
import project.planner.models.LoadContacts;
import project.planner.models.globalVariables;
import project.planner.sms.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * <h3>Overview</h3>
 * Main screen of this application. This activity contains multi-threads. one
 * thread keeps time string up-to-date and second thread loads all contacts
 * information from mobile. This activity also holds all important views,
 * images of main screen.
 * <p>
 * Visit google play store for download
 * See <a href="https://play.google.com/store/apps/details?id=project.planner.sms">https://play.google.com/store/apps/details?id=project.planner.sms</a>
 */
public class MainScreenActivity extends Activity implements OnClickListener {

	ImageView btncreategroup;
	ImageView btnopengrouplist;
	ImageView bthopencontactlist;
	ImageView bthsendsms;
	ImageView bthshowsms;
	ImageView btnshowhistory;
	ImageView btnextrafeatures;
	ImageView btnactivehistory;
	ImageView help;

	RelativeLayout relativelayoutactivemessages;

	TextView daymonthyeartext;
	TextView hoursminutestext;
	TextView textviewheading;
	TextView activemessagecount;
    Context context;

	String[] monthsOfYear;

	final int SMSTITLE 		= 1;
	final int SMSMESSAGE 	= 2;
	final int CONTACTS 		= 3;
	final int MESSAGEID 	= 4;
	final int INTENTID 		= 5;
	final int ISVALID 		= 6;
	final int TIMECOUNT 	= 7;
	final int ISDELIVERED 	= 8;

	String helptext; 
	
	boolean file = false;
	private static final Uri URI = ContactsContract.Contacts.CONTENT_URI;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.mainscreenactivity);
		getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.ic_launcher);
        context = getApplicationContext();
        monthsOfYear = new String [12];
		setObjectsForOnClickListner();
		startProgress();
		setTimeandDate();
		loadGroupList();
	}
	
	/**
	 * 	initialize views
	 */
	private void setObjectsForOnClickListner() {
		btncreategroup = (ImageView) findViewById(R.id.cgbutton);
		bthsendsms = (ImageView) findViewById(R.id.smsbutton);
		btnshowhistory = (ImageView) findViewById(R.id.shbutton);
		btnactivehistory = (ImageView) findViewById(R.id.ambutton);
		btnextrafeatures = (ImageView) findViewById(R.id.morebutton);
		relativelayoutactivemessages = (RelativeLayout) findViewById(R.id.relativelayoutactivemessages);
		hoursminutestext = (TextView) findViewById(R.id.hoursminutes);
		daymonthyeartext = (TextView) findViewById(R.id.daymonthyear);
		textviewheading = (TextView) findViewById(R.id.textviewheading);
		activemessagecount = (TextView) findViewById(R.id.activemessagecount);
		help = (ImageView) findViewById(R.id.help);
        setStringText();
	}
    
	
	/**
	 * 
	 * Initialize array with months of year and text to display help
	 * 
	 */
    private void setStringText(){
        
        monthsOfYear [0] = 
        context.getResources().getString(R.string.january);
        monthsOfYear [1] =
        context.getResources().getString(R.string.february);
        monthsOfYear [2] =
        context.getResources().getString(R.string.march);
        monthsOfYear [3] =
        context.getResources().getString(R.string.april);
        monthsOfYear [4] =
        context.getResources().getString(R.string.may);
        monthsOfYear [5] =
        context.getResources().getString(R.string.june);
        monthsOfYear [6] =
        context.getResources().getString(R.string.july);
        monthsOfYear [7] =
        context.getResources().getString(R.string.august);
        monthsOfYear [8] =
        context.getResources().getString(R.string.september);
        monthsOfYear [9] =
        context.getResources().getString(R.string.october);
        monthsOfYear [10] =
        context.getResources().getString(R.string.november);
        monthsOfYear [11] =
        context.getResources().getString(R.string.december);
        
        
       helptext =  context.getResources().getString(R.string.send_sms_text) + 
       "\n" + 
       context.getResources().getString(R.string.send_sms_text_detail) + 
       "\n\n" +
       context.getResources().getString(R.string.create_group_text) + 
       "\n" +
       context.getResources().getString(R.string.create_group_text_detail) + 
       "\n\n" +
       context.getResources().getString(R.string.active_message_text) + 
       "\n"+
       context.getResources().getString(R.string.active_message_text_detail) +
       "\n\n" + 
       context.getResources().getString(R.string.show_history_text) + 
       "\n" + 
        context.getResources().getString(R.string.show_history_text_detail) + 
       "\n\n" + 
       context.getResources().getString(R.string.extra_features_text) +
       "\n" +
       context.getResources().getString(R.string.extra_features_text_detail);  
    }


	/**
	 * 	Load contact list from phone  
	 */
	public void LoadContactListFromPhone() {
		if (LoadContacts.list.isEmpty()) {
			Cursor cursor = getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI, null, null, null,
					ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
			if (cursor.moveToFirst()) {
				globalVariables.loadingcontactsinprogress = true;
				globalVariables.contactscount = cursor.getCount();

				while (cursor.moveToNext()) {
					String contactId = cursor.getString(cursor
							.getColumnIndex(ContactsContract.Contacts._ID));
					String hasPhone = cursor
							.getString(cursor
									.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
					int hasph = Integer.parseInt(hasPhone);

					if (hasph > -1) {
						Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
										ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = " + contactId, null, null);
						if (phones.getCount() > 0) {
							while (phones.moveToNext()) {
								String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
								String phonename = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
								LoadContacts.list.add(new ContactInfo(phonename, 0, phoneNumber, 0));
							}
						}
						phones.close();
					}
				}
				globalVariables.loadingcontactsinprogress = false;
			}

			cursor.close();
		}
	}



	@Override
	public void onBackPressed() {
		finish();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		/**
		 * 	Help 
		 */
		case R.id.help:

			AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
			alertbox.setMessage(helptext);
			alertbox.setTitle(
                 context.getResources().getString(R.string.help)  
                );
			alertbox.setNeutralButton("OK",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface arg0, int arg1) {

						}
					});

			alertbox.show();

			break;

		/**
		 * 	Create groups
		 */
		case R.id.cgbutton: // Create Group
			startActivity(new Intent(this, ManageGroupActivity.class));
			break;

		/**
		 * 	Plan SMS
		 */
		case R.id.smsbutton: 
			startActivity(new Intent(this, SendSMSActivity.class));
			break;
	
		/**
		 * 	Active Message
		 */
		case R.id.ambutton: // Active Message

			boolean found = false;
			DataBaseOperations ops = DataBaseOperations
					.getInstance(getApplicationContext());
			Cursor c = ops
					.GetAllRecords(SmsRecordsTable.TABLE_NAME, null, null);
			if (c.moveToFirst()) {
				do {
					if (convertmillisecondsindays(c.getString(TIMECOUNT)) > 0
							&& c.getInt(ISDELIVERED) == 0) {
						found = true;
						break;
					}
				} while (c.moveToNext());
			}
			if (found) {
				Intent intent = new Intent(this, SmsHistoryActivity.class);
				startActivity(intent);
			} else
				Toast.makeText(this, "No active sms found", Toast.LENGTH_SHORT)
				.show();
			break;

			/**
			 * Extra Features 
			 */
		case R.id.morebutton: 
			startActivity(new Intent(this, ExtraFeaturesActivity.class));
			break;

			
			/**
			 * Show sms history 
			 */
		case R.id.shbutton: 
		{
			DataBaseOperations operations = DataBaseOperations.getInstance(getApplicationContext());
			Cursor cc = operations.GetAllRecords(SmsRecordsTable.TABLE_NAME,null, SmsRecordsTable.SMSRECORD_TIMESTAMP);
			boolean f = cc.moveToFirst();
			if (f) {
				Intent i = new Intent(this, ShowHistoryActivity.class);
				startActivity(i);
			} else {
				Toast.makeText(this,"no history found",Toast.LENGTH_SHORT).show();
			}
		}
			break;

		default:
			break;
		}
	}

	/**
	 * 	Thread for display current time.  
	 */
	private void setTimeandDate() {

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				while (!Thread.currentThread().isInterrupted()) {
					try {
						createTimeStamp();
						Thread.sleep(1000); 
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					} catch (Exception e) {
					}
				}
			}
		};
		new Thread(runnable).start();
	}
	
	/**
	 * 	Thread for loading contacts.  
	 */
	public void startProgress() {

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				LoadContactListFromPhone();
			}
		};
		new Thread(runnable).start();

	}

	
	/**
	 * 
	 * 	Create Time Stamp and display on screen
	 *   
	 */
	public void createTimeStamp() {
		runOnUiThread(new Runnable() {
			public void run() {
				try {

					Calendar ci = Calendar.getInstance();
					int hours = ci.get(Calendar.HOUR);
					int min = ci.get(Calendar.MINUTE);

					String h = ci.get(Calendar.HOUR) + "";
					String m = ci.get(Calendar.MINUTE) + "";

					if (hours < 10)
						h = "0" + h;
					if (min < 10)
						m = "0" + m;

					if (hours == 0)
						h = "12";

					String hoursminutes = h + ":" + m;
					String daymonthyear = ci.get(Calendar.DAY_OF_MONTH) + " "+ 
										monthsOfYear[ci.get(Calendar.MONTH)] + 
										" "+ ci.get(Calendar.YEAR);

				
					Typeface font = Typeface.createFromAsset(getAssets(),"CopperplateGothicLight.ttf");
					textviewheading.setTypeface(font);

					hoursminutestext.setText(hoursminutes);
					daymonthyeartext.setText(daymonthyear);

				} catch (Exception e) {
				}
			}
		});
	}

	
	/**
	 * 
	 * Load contact information from mobile and insert into global contact list.
	 *  
	 */
	private int loadGroupList() {
		int count = 0;
		DataBaseOperations operations = DataBaseOperations
				.getInstance(MainScreenActivity.this);
		Cursor c = operations.GetAllRecords(SmsRecordsTable.TABLE_NAME, null,
				SmsRecordsTable.SMSRECORD_DELIVERED);
		if (c.moveToFirst()) {
			do {

				if (c.getInt(ISDELIVERED) == 0)
					if (convertmillisecondsindays(c.getString(TIMECOUNT)) > 0)
						count++;
			} while (c.moveToNext());
		}
		return count;
	}
	
	/**
	 * 
	 * 
	 * Get remaining time of message to be delivered.
	 * 
	 * @param milli value in milliseconds.
	 * @return long value in millisecinds
	 * 
	 */
	long convertmillisecondsindays(String milli) {
		long timefromstring = Long.parseLong(milli);
		Calendar today = Calendar.getInstance();
		long systemtimeinmilli = DateToCalendar(today);
		return (timefromstring - systemtimeinmilli);
	}

	@Override
	protected void onResume() {
		super.onResume();

		int count = loadGroupList();
		if (count > 0) {
			relativelayoutactivemessages.setVisibility(View.VISIBLE);
			activemessagecount.setText(Integer.toString(count));
		} else {
			relativelayoutactivemessages.setVisibility(View.GONE);
		}
	}

	/**
	 * 
	 * 
	 * Get Time difference in two dates.
	 * 
	 * @param difference value in Calendar.
	 * @return long value in milliseconds
	 * 
	 */
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
}
