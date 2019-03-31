package project.planner.activities;

import java.util.ArrayList;
import java.util.List;

import project.planner.adapters.GroupAdapter;
import project.planner.db.DBAdapter;
import project.planner.db.DataBaseOperations;
import project.planner.db.GroupContactTable;
import project.planner.db.GroupInfoTable;
import project.planner.models.ContactInfo;
import project.planner.models.GroupInfo;
import project.planner.models.ParcelData;
import project.planner.sms.R;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;


/**
 * 
 *	List Activity display list of groups, created by user. 
 * 
 */
public class GroupListActivity extends ListActivity implements OnClickListener,
		OnItemLongClickListener {

	List<GroupInfo> list = new ArrayList<GroupInfo>();

	GroupInfo selectedGroupInfo = null;
	List<ContactInfo> finalList = new ArrayList<ContactInfo>();
	ArrayList<ParcelData> dataList = new ArrayList<ParcelData>();
	String defaultName = "";
	int rowposition;
	ArrayAdapter<GroupInfo> adapter;
	Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LoadGroupList();
		getListView().setOnItemLongClickListener(this);
		getListView().setOnCreateContextMenuListener(this);
		getListView().setBackgroundColor(Color.WHITE);
		adapter = new GroupAdapter(this, list);
		setListAdapter(adapter);
		context = getApplicationContext();
	}

	public void OnReceiveList() {
		Bundle b = getIntent().getExtras();
		ArrayList<ParcelData> data = b
				.getParcelableArrayList("custom_data_list");
		if (data.size() > 0) {
			dataList = data;
			GetFinalList();
		} else {
			Toast.makeText(this, "No selection found", Toast.LENGTH_SHORT)
			.show();
		}
	}

	private GroupInfo GetGroupInfo(int position) {
		return list.get(position);
	}

	public void GetFinalList() {
		String str = "";
		int i = 0;
		while (dataList.size() > i) {
			str = str + dataList.get(i).GetPhoneNumber();
			i++;
		}
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 
	 * Load Group list from GroupInfoTable.
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
				String wherclause = GroupContactTable.GROUPCONTACT_GROUPID
						+ " = " + c.getString(0);
				Cursor cc = operations.GetSelectedRows(
						GroupContactTable.TABLE_NAME, wherclause, null);
				list.add(new GroupInfo(c.getString(0), c.getString(1), cc
						.getCount() + ""));
			} while (c.moveToNext());
		}

	}

	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			int position, long arg3) {
		selectedGroupInfo = GetGroupInfo(position);
		rowposition = position;
		return false;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		CreatMenu(menu);
	}

	
	private void CreatMenu(ContextMenu menu) {
		menu.add(0, 0, 0,context.getResources().getString(R.string.editgroup));
		menu.add(0, 1, 1, context.getResources().getString(R.string.deletegroup));
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		return MenuChoice(item);
	}

	/**
	 * 
	 * 
	 * @param item Menu Item to select
	 * 
	 */
	private boolean MenuChoice(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			Intent i = new Intent(this, EditGroupActivity.class);
			i.putExtra("position", selectedGroupInfo.getName());
			i.putExtra("name", selectedGroupInfo.getName());
			i.putExtra("count", selectedGroupInfo.getTimeStamp());
			startActivityForResult(i, 2);
			return true;

		case 1:
			Intent ii = new Intent(this, DeleteGroupActivity.class);
			ii.putExtra("position", selectedGroupInfo.getID());
			ii.putExtra("name", selectedGroupInfo.getName());
			startActivityForResult(ii, 1);
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * Click on group list item and open relevant contacts list. 
	 * 
	 */

	@Override
	public void onListItemClick(ListView parent, View v, int position, long id) {
		rowposition = position;
		GroupInfo info = GetGroupInfo(position);
		String where = GroupInfoTable.GROUP_ID + " = " + info.getID();
		DataBaseOperations operations = DataBaseOperations
				.getInstance(getApplicationContext());
		int count = operations.IsRecordExists(where,
				GroupInfoTable.GROUP_GROUPNAME);
		if (count > 0) {

			Intent i = new Intent(this, SmsPlannerActivity.class);
			i.putExtra("activityvalue", "groupListActivity");
			i.putExtra("groupid", info.getID().toString());
			i.putExtra("name", info.getName());
			startActivityForResult(i, 3);
		} else {
			Toast.makeText(this, "No contact found", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Display result if group name edited or deleted.  
	 * 
	 * @param requestCode token code.
	 * @param resultCode  received code.
	 * @param data result from activity.
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {

				Bundle b = data.getExtras();
				if (b.getBoolean("result")) {
					list.remove(rowposition);
				} else {
					GroupInfo g = new GroupInfo(b.getString("id"),
							b.getString("name"), "0");
					list.set(rowposition, g);
				}
				adapter.notifyDataSetChanged();
			}
			if (resultCode == RESULT_CANCELED) {
			}
		}

		if (requestCode == 2) {
			if (resultCode == RESULT_OK) {
				String result = data.getStringExtra("result");
				if (result.equals("edited")) {
					Bundle b = data.getExtras();
					GroupInfo g = new GroupInfo(b.getString("id"),
							b.getString("name"), b.getString("count"));
					list.set(rowposition, g);
					adapter.notifyDataSetChanged();
				}
			}
		}

		if (requestCode == 3) {
			if (resultCode == RESULT_OK) {
				String result = data.getStringExtra("result");
				if (result.equals("contactsdeleted")) {
					Bundle b = data.getExtras();
					String where = GroupInfoTable.GROUP_ID + " = "
							+ b.getString("id");
					DataBaseOperations operations = DataBaseOperations
							.getInstance(getApplicationContext());
					int count = operations.IsRecordExists(where,
							GroupInfoTable.TABLE_NAME);

					GroupInfo g = new GroupInfo(b.getString("id"),
							b.getString("name"), count + "");
					list.set(rowposition, g);
					adapter.notifyDataSetChanged();
				}
			}
		}
	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();
		finish();
	}

	@Override
	protected void onResume() {

		super.onResume();
		if (!IsGroupAvailable())
			finish();
	}

	/**
	 * Check if Group exist  
	 * 
	 * @return true if found false otherwise. 
	 */
	
	public boolean IsGroupAvailable() {
		DataBaseOperations operations = DataBaseOperations
				.getInstance(getApplicationContext());
		Cursor c = operations.GetAllRecords(GroupInfoTable.TABLE_NAME, null,
				null);
		boolean recordfound = false;
		recordfound = c.moveToFirst();
		return recordfound;
	}

	@Override
	public void onClick(View v) {
	}
}
