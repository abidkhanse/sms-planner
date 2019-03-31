package project.planner.activities;

import java.util.ArrayList;
import java.util.List;

import project.planner.adapters.ContactsAdapter;
import project.planner.db.DataBaseOperations;
import project.planner.db.GroupContactTable;
import project.planner.models.ContactInfo;
import project.planner.sms.R;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;


/**
 * An activity that displays a list of contacts from GroupContactTable by using array  
 * and exposes event handlers when the user selects an item.
 * <p>
 * User can select or de-select the item from the list. Selected list will do back to  
 * the caller activity.
 * <p>
 */

public class ContactListActivity extends ListActivity {

	List<ContactInfo> m_contactlist = new ArrayList<ContactInfo>();
	String m_groupid = "";
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GetDataFromCaller();
		LoadContactListFromPhone();
		ContactsAdapter contactadAdapter = new ContactsAdapter(this, m_contactlist);
		setListAdapter(contactadAdapter);
		context = getApplicationContext();
	}
	
	
	/**
	 * 
	 * Get data from previous Activity
	 */
	private void GetDataFromCaller() {
		Intent i = getIntent();
		m_groupid = i.getStringExtra("groupid");
	}

	/**
	 * Get contact information including name and phone number from GroupContactTable
	 */
	private void LoadContactListFromPhone() {
		m_contactlist.clear();
		String where = GroupContactTable.GROUPCONTACT_GROUPID + " = " + m_groupid;
		DataBaseOperations operations = DataBaseOperations
				.getInstance(getApplicationContext());
		Cursor c = operations.GetContact(where, GroupContactTable.TABLE_NAME,
				null, null);
		if (c.moveToFirst()) {
			do {
				m_contactlist.add(new ContactInfo(c.getString(2), 0, c.getString(3), 0));
			} while (c.moveToNext());
		} else {
			Toast.makeText(this, "No contact found", Toast.LENGTH_SHORT)
			.show();
		}
	}

	@Override
	public void onListItemClick(ListView parent, View v, int position, long id) {
		CheckedTextView ct = (CheckedTextView) v.findViewById(R.id.firstname);
		{
			ct.toggle();
			if (ct.isChecked()) {
				ct.setTextColor(Color.GREEN);
			} else {
				ct.setTextColor(Color.BLACK);
			}
		}
	}
}
