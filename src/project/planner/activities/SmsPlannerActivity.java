package project.planner.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import project.planner.adapters.ContactsAdapter;
import project.planner.db.ContactsTable;
import project.planner.db.DBAdapter;
import project.planner.db.DataBaseOperations;
import project.planner.models.ContactInfo;
import project.planner.models.LoadContacts;
import project.planner.models.ParcelData;
import project.planner.sms.R;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * This activity helps to load data on request of different activities and
 * returns data set according to activity requirement. Activity contains search
 * able edit field.
 * 
 * @author KHAN
 * 
 */
public class SmsPlannerActivity extends ListActivity {

	ListView contactsListView;

	public static final String GROUP_GROUPTABLENAME = "groupinfo";
	CheckBox check;
	int count;
	List<ContactInfo> myList = new ArrayList<ContactInfo>();
	ArrayList<String> contactsList = new ArrayList<String>();
	ArrayList<Integer> groupcontact = new ArrayList<Integer>();
	ArrayList<ParcelData> dataList = new ArrayList<ParcelData>();

	ImageView btnSelect;
	ListView lv;
	String activityvalue = "";
	String groupname = "";
	EditText inputSearch;

	String groupid = "";
	Bundle extras;
	ContactsAdapter contactadAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.smsplanneractivity);

		btnSelect = (ImageView) findViewById(R.id.SelectButton);
		extras = getIntent().getExtras();
		activityvalue = extras.getString("activityvalue");

		LoadContactListFromPhone();
		inputSearch = (EditText) findViewById(R.id.inputSearch);

		inputSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {

				String text = inputSearch.getText().toString()
						.toLowerCase(Locale.getDefault());
				contactadAdapter.filter(text);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
			}
		});

	}

	public void onClick(View v) {
		if (GetFinalList()) {

			/***
			 * return data set of request come from mainActivity
			 */
			if (activityvalue.compareTo("mainActivity") == 0) {
				if (!dataList.isEmpty()) {
					Intent parcelIntent = new Intent(this,
							AddContactsListActivity.class);
					parcelIntent.putParcelableArrayListExtra("custom_data_list", dataList);
					startActivity(parcelIntent);
				}
				/***
				 * return data set of request come from sendSMSActivity
				 */
			} else if (activityvalue.compareTo("sendSMSActivity") == 0) {
				if (contactsList.size() > 0) {
					Intent intent = new Intent();
					Bundle b = new Bundle();
					b.putStringArrayList("contacts", contactsList);
					intent.putExtras(b);
					setResult(RESULT_OK, intent);
					contactadAdapter.filter("");
					finish();
				}
				/***
				 * return data set of request come from groupListActivity
				 */
			} else if (activityvalue.compareTo("groupListActivity") == 0) {
				int i = 0;

				DataBaseOperations operations = DataBaseOperations
						.getInstance(getApplicationContext());
				while (groupcontact.size() > i) {
					String wherclause = ContactsTable.CONTACT_ID + " = "
							+ groupcontact.get(i);
					operations.DeleteRecords(
							ContactsTable.TABLE_NAME, wherclause);
					i++;
				}

				Toast.makeText(this,
						groupcontact.size() + " contact(s) deleted",
						Toast.LENGTH_SHORT).show();
				Intent returnIntent = new Intent();
				returnIntent.putExtra("result", "contactsdeleted");
				Bundle b = new Bundle();
				b.putString("name", groupname);
				b.putString("id", groupid);
				returnIntent.putExtras(b);
				setResult(RESULT_OK, returnIntent);
				finish();
			}
		} else {
			Toast.makeText(this, "Select atleast one contact",
					Toast.LENGTH_SHORT).show();
		}
	}

	List<ContactInfo> LoadContactListFromPhone() {

		if (activityvalue.compareTo("mainActivity") == 0) {
			btnSelect.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.selector_addin));
			contactadAdapter = new ContactsAdapter(this, LoadContacts.list);
			myList = LoadContacts.list;
		} else if (activityvalue.compareTo("sendSMSActivity") == 0) {
			btnSelect.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.selector_addin));
			contactadAdapter = new ContactsAdapter(this, LoadContacts.list);
			myList = LoadContacts.list;
		} else if (activityvalue.compareTo("groupListActivity") == 0) {
			btnSelect.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.selector_deletecontacts));
			groupid = extras.getString("groupid");
			groupname = extras.getString("name");
			myList.clear();
			String where = ContactsTable.CONTACT_GROUPID + " = " + groupid;

			DataBaseOperations operations = DataBaseOperations
					.getInstance(getApplicationContext());
			Cursor c = operations.GetContact(where, ContactsTable.TABLE_NAME,
					null, null);
			if (c.moveToFirst()) {
				do {
					myList.add(new ContactInfo(c.getString(2), 0, c
							.getString(3), c.getInt(0)));

				} while (c.moveToNext());
				contactadAdapter = new ContactsAdapter(this, myList);
			} else {
				Toast.makeText(this, "No Contact Found", Toast.LENGTH_SHORT).show();
			}

		}

		setListAdapter(contactadAdapter);

		return myList;
	}

	@Override
	protected void onPause() {
		super.onPause();
		contactadAdapter.filter("");
		finish();
	}

	private boolean GetFinalList() {
		dataList.clear();
		contactsList.clear();
		boolean found = false;
		ParcelData p;
		int i = 0;

		while (myList.size() > i) {
			if (myList.get(i).selected == 1) {
				p = new ParcelData();
				p.SetFirstName(myList.get(i).firstname);
				p.SetPhoneNumber(myList.get(i).phonenumber);
				contactsList.add(myList.get(i).firstname + "<"
						+ myList.get(i).phonenumber + ">");
				groupcontact.add(myList.get(i).isvalid);
				dataList.add(p);
				myList.get(i).selected = 0;
				found = true;
			}
			i++;
		}
		return found;
	}

	@Override
	public void onBackPressed() {
		Intent returnIntent = new Intent();
		returnIntent.putExtra("backpressed", "");
		setResult(RESULT_CANCELED, returnIntent);
		contactadAdapter.filter("");
		finish();
	}

	@Override
	public void onListItemClick(ListView parent, View v, int position, long id) {

		TextView ct = (TextView) v.findViewById(R.id.firstname);
		if (myList.get(position).selected == 1) {
			myList.get(position).selected = 0;
			ct.setTextColor(Color.BLACK);
			ct.setTypeface(null, Typeface.NORMAL);
		} else {
			myList.get(position).selected = 1;
			ct.setTextColor(Color.parseColor("#03A7E0"));
			ct.setTypeface(null, Typeface.BOLD);
		}
	}
}
