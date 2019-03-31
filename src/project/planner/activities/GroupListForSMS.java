package project.planner.activities;

import java.util.ArrayList;
import java.util.List;

import project.planner.adapters.GroupAdapter;
import project.planner.db.DBAdapter;
import project.planner.db.DataBaseOperations;
import project.planner.models.GroupInfo;
import project.planner.models.ParcelData;
import project.planner.sms.R;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


/**
 * An activity that helps to get contacts information in groups which is created by user. 
 */
public class GroupListForSMS extends ListActivity implements OnClickListener {

	List<GroupInfo> list = new ArrayList<GroupInfo>();
	public static final String GROUP_GROUPTABLENAME = "groupinfo";
	public static final String CONTACT_CONTACTTABLENAME = "contactinfo";
	GroupInfo selectedGroupInfo = null;
	String defaultName = "";
	String messagefromactivity = "";
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getListView().setBackgroundColor(Color.WHITE);
		LoadGroupList();
		ArrayAdapter<GroupInfo> adapter = new GroupAdapter(this, list);
		setListAdapter(adapter);
		messagefromactivity = getIntent().getStringExtra("sampleData");
		context = getApplicationContext();
	}

	@Override
	public void onClick(View v) {
	}

	private GroupInfo GetGroupInfo(int position) {
		return list.get(position);
	}

	@Override
	public void onListItemClick(ListView parent, View v, int position, long id) {
		GroupInfo info = GetGroupInfo(position);
		Intent intent = new Intent();
		Bundle b = new Bundle();
		b.putString("contacts", info.getID());
		intent.putExtras(b);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void onBackPressed() {
		Intent returnIntent = new Intent();
		returnIntent.putExtra("backpressed", "");
		setResult(RESULT_CANCELED, returnIntent);
		finish();
	}
	
	

	/**
	 * 
	 * Create list of contact information from group
	 *  
	 */
	public ArrayList<ParcelData> OnReceiveList() {
		Bundle b = getIntent().getExtras();
		ArrayList<ParcelData> data = b.getParcelableArrayList("custom_data_list");
		int i = 0;
		if (data.size() > 0) {
			while (data.size() > i) {

				data.get(i).SetPhoneNumber(RefineString(data.get(i).GetPhoneNumber()));
				i++;
			}
		} else {
			Toast.makeText(this, "No contact found", Toast.LENGTH_SHORT)
			.show();
		}
		return data;
	}
	
	/**
	 * 
	 * Remove any invalid character from phone number. 
	 *  
	 */

	private String RefineString(String str) {
		return str.replaceAll("-", "");
	}

	/**
	 * 
	 * Load group list from table 
	 *  
	 */
	private void LoadGroupList() {
		String[] array = { "ID", "groupname" };
		DataBaseOperations operations = DataBaseOperations.getInstance(getApplicationContext());
		Cursor c = operations.GetAllRecords(GROUP_GROUPTABLENAME, array, null);
		if (c.moveToFirst()) {
			do {
				String wherclause = "groupid = " + c.getString(0);
				Cursor cc = operations.GetSelectedRows(
						CONTACT_CONTACTTABLENAME, wherclause, null);
				list.add(new GroupInfo(c.getString(0), c.getString(1), cc
						.getCount() + ""));
			} while (c.moveToNext());
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
}
