package project.planner.activities;

import java.util.ArrayList;
import java.util.List;

import project.planner.adapters.GroupAdapter;
import project.planner.db.DataBaseOperations;
import project.planner.db.GroupContactTable;
import project.planner.db.GroupInfoTable;
import project.planner.models.ContactInfo;
import project.planner.models.GroupInfo;
import project.planner.models.ParcelData;
import project.planner.sms.R;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * A List activity that helps user to select contacts information from mobile
 * and insert selected contacts into existing group..
 */

public class AddContactsListActivity extends ListActivity implements
		OnClickListener {

	List<GroupInfo> list = new ArrayList<GroupInfo>();
	GroupInfo selectedGroupInfo = null;
	List<ContactInfo> finalList = new ArrayList<ContactInfo>();
	ArrayList<ParcelData> dataList = new ArrayList<ParcelData>();
	String defaultName = "";
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LoadGroupList();
		ArrayAdapter<GroupInfo> adapter = new GroupAdapter(this, list);
		setListAdapter(adapter);
		getWindow().getDecorView().setBackgroundColor(Color.WHITE);
		context = getApplicationContext();

	}

	@Override
	public void onClick(View v) {

	}

	private GroupInfo GetGroupInfo(int position) {
		return list.get(position);
	}

	/**
	 * 
	 * Insert selected contact in group.
	 * 
	 */
	@Override
	public void onListItemClick(ListView parent, View v, int position, long id) {
		boolean success = true;

		GroupInfo info = (GroupInfo) GetGroupInfo(position);
		int groupid = Integer.parseInt(info.getID());
		ArrayList<ParcelData> data = OnReceiveList();
		int j = 0;
		if (data.size() > 0) {
			DataBaseOperations operations = DataBaseOperations
					.getInstance(getApplicationContext());
			while (data.size() > j) {
				if (operations.InsertContactInfo(groupid, data.get(j)
						.GetFirstName(), data.get(j).GetPhoneNumber()) == -1) {
					Toast.makeText(this, "cannot add contact", Toast.LENGTH_SHORT)
					.show();
					success = false;
					break;
				}
				j++;
			}

			if (success)
				Toast.makeText(
						this,
						data.size()
								+ " "
								+ context.getResources().getString(
										R.string.cannot_add_contact_error)
								+ " "
								+ context.getResources().getString(
										R.string.contact_added_msg) + " "
								+ info.getName(), Toast.LENGTH_SHORT).show();

			finish();
		}
	}

	public ArrayList<ParcelData> OnReceiveList() {
		Bundle b = getIntent().getExtras();
		ArrayList<ParcelData> data = b
				.getParcelableArrayList("custom_data_list");
		int i = 0;
		if (data.size() > 0) {
			while (data.size() > i) {
				data.get(i).SetPhoneNumber(
						RefineString(data.get(i).GetPhoneNumber()));
				i++;
			}
		} else {
			Toast.makeText(this, "No Group found", Toast.LENGTH_SHORT)
			.show();
		}
		return data;
	}

	private String RefineString(String str) {
		return str.replaceAll("-", "");
	}

	/**
	 * 
	 * Load group list to insert contacts in.
	 * 
	 */
	private void LoadGroupList() {

		String[] array = { "ID", "groupname" };
		DataBaseOperations operations = DataBaseOperations
				.getInstance(getApplicationContext());
		Cursor c = operations.GetAllRecords(GroupInfoTable.TABLE_NAME, array,
				null);

		if (c.moveToFirst()) {
			do {
				String wherclause = "groupid = " + c.getString(0);
				Cursor cc = operations.GetSelectedRows(
						GroupContactTable.TABLE_NAME, wherclause, null);
				list.add(new GroupInfo(c.getString(0), c.getString(1), cc
						.getCount() + ""));
			} while (c.moveToNext());
		} else {
			Toast.makeText(this,
					context.getResources().getString(R.string.no_group_found),
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
}
