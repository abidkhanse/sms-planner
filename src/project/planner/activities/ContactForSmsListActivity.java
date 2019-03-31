package project.planner.activities;

import java.util.ArrayList;
import java.util.List;

import project.planner.adapters.ContactsAdapter;
import project.planner.models.ContactInfo;
import project.planner.models.ParcelData;
import project.planner.sms.R;

import android.app.ListActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;




/**
 * An activity that displays a list of contacts from phone by using array  
 * and exposes event handlers when the user selects an item.
 * <p>
 * User can select or de-select the item from the list. Selected list will do back to  
 * the caller activity.
 * <p>
 */
public class ContactForSmsListActivity extends ListActivity {

	ListView contactsListView;
	List<ContactInfo> contactlist = new ArrayList<ContactInfo>();
	List<ContactInfo> contactlistforselection = new ArrayList<ContactInfo>();
	ArrayList<ParcelData> dataList = new ArrayList<ParcelData>();
	Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 
		LoadContactListFromPhone();
		ContactsAdapter contactadAdapter = new ContactsAdapter(this, contactlist);
		setListAdapter(contactadAdapter);
		context = getApplicationContext();
	}
	
	
	
	
	/**
     * This method will be called when an item in the list is selected.
     * Data associated with the selected item is set active.
     */
	@Override
	public void onListItemClick(ListView parent, View v, int position, long id) {
		CheckedTextView ct = (CheckedTextView) v.findViewById(R.id.firstname);
		{
			ct.toggle();
			if (ct.isChecked()) {
				contactlistforselection.get(position).selected = 1;
				ct.setTextColor(Color.GREEN);
			} else {
				contactlistforselection.get(position).selected = 0;
				ct.setTextColor(Color.BLACK);
			}
		}
	}

	/**
     * This method parses the contact list and fetch all contacts 
     * which are selected for caller Activity   
     **/
	
	public void GetFinalList() {
		dataList.clear();
		ParcelData p;
		int i = 0;
		while (contactlistforselection.size() > i) {
			if (contactlistforselection.get(i).selected == 1) {
				p = new ParcelData();
				p.SetFirstName(contactlistforselection.get(i).firstname);
				p.SetPhoneNumber(contactlistforselection.get(i).phonenumber);
				dataList.add(p);
			}
			i++;
		}
	}
	
	/**
     * This method uses the ContentResolver and fetches contact information from mobile
     * including name and phone number and returns List of ContactInfo 
     **/
	List<ContactInfo> LoadContactListFromPhone() {

		Cursor cursor = getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null,
				"UPPER(" + ContactsContract.Contacts.DISPLAY_NAME + ") ASC");

		if (cursor.moveToFirst()) {
			while (cursor.moveToNext()) {
				String contactId = cursor.getString(cursor
						.getColumnIndex(ContactsContract.Contacts._ID));
				
				String hasPhone = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
				
				int hasph = Integer.parseInt(hasPhone);

				if (hasph > -1) {
					Cursor phones = getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = " + contactId, null, null);
					
					if (phones.getCount() > 0) {
						while (phones.moveToNext()) {
							String phoneNumber = phones
									.getString(phones
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							String phonename = phones
									.getString(phones
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
							contactlist.add(new ContactInfo(phonename, 0, phoneNumber,
									0));
						}
					}
					phones.close();
				}
			}
			contactlistforselection = contactlist;
		} else {
			Toast.makeText(this, "No contact found", Toast.LENGTH_SHORT).show();
		}
		cursor.close();
		return contactlistforselection;
	}


	private boolean MenuChoice(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			GetFinalList();
			Intent parcelIntent = new Intent(this, SendSMSActivity.class);
			parcelIntent.putParcelableArrayListExtra("custom_data_list",
					dataList);
			startActivity(parcelIntent);
			return true;
		}
		return false;
	}
	
	
	private void CreateMenu(Menu menu) {
		menu.add(0, 0, 0,context.getResources().getString(R.string.add_in_sms_activity));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		CreateMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return MenuChoice(item);
	}

	final class ContactHolder {
		CheckedTextView txtviewfirstname;
		TextView txtviewphone;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
}
