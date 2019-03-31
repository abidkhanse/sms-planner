package project.planner.activities;

import java.util.ArrayList;

import java.util.Calendar;
import java.util.List;

import project.planner.adapters.SMSHistoryAdapter;
import project.planner.db.DBAdapter;
import project.planner.db.DataBaseOperations;
import project.planner.db.SmsRecordsTable;
import project.planner.models.GroupInfo;
import project.planner.sms.R;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;

/**
 * 
 * This activity shows history of all scheduled sms.
 * 
 * @author KHAN
 * 
 */

public class ShowHistoryActivity extends ListActivity implements
		OnClickListener, OnItemLongClickListener {
	final int SMSRECORD_ID = 0;
	final int SMSTITLE = 1;
	final int SMSMESSAGE = 2;
	final int CONTACTS = 3;
	final int MESSAGEID = 4;
	final int INTENTID = 5;
	final int ISVALID = 6;
	final int TIMECOUNT = 7;
	final int ISDELIVERED = 8;
	final int TIMESTAMP = 9;


	List<GroupInfo> list = new ArrayList<GroupInfo>();
	
	Context context;

	String id = "";
	int pos;
	ArrayAdapter<GroupInfo> adapter;
	String d;
	String nd;
	String nyd;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadSmsHistory();
		getListView().setOnItemLongClickListener(this);
		getListView().setOnCreateContextMenuListener(this);
		getListView().setBackgroundColor(Color.WHITE);
		adapter = new SMSHistoryAdapter(this, list);
		setListAdapter(adapter);
		context = getApplicationContext();
		
		nyd = context.getResources().getString(R.string.not_yet_delivered);
		
		nd = context.getResources().getString(R.string.not_delivered);
	
		d =  context.getResources().getString(R.string.delivered);
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
	 * Delete selected record from list. 
	 * @param id
	 */
	public void deleteRecords(String id) {

		String where = "ID = " + id;

		DataBaseOperations operations = DataBaseOperations
				.getInstance(getApplicationContext());
		Cursor c = operations.GetSelectedRows(SmsRecordsTable.TABLE_NAME,
				where, null);

		if (c.getString(ISVALID).equals("1")
				&& convertmillisecondsindays(c.getString(TIMECOUNT)) > 0) {
			Toast.makeText(this, "could not delete, message is active", Toast.LENGTH_SHORT).show();

		} else if (operations
				.DeleteRecords(SmsRecordsTable.TABLE_NAME, where) > 0) {
			list.remove(pos);
			adapter.notifyDataSetChanged();
			if (list.size() < 1)
				finish();
		}

	}

	private long convertmillisecondsindays(String milli) {

		long timefromstring = Long.parseLong(milli);
		Calendar today = Calendar.getInstance();
		long systemtimeinmilli = DateToCalendar(today);

		return (timefromstring - systemtimeinmilli);
	}

	/**
	 * Get data from 
	 */
	private void loadSmsHistory() {
		DataBaseOperations operations = DataBaseOperations
				.getInstance(getApplicationContext());
		Cursor c = operations.GetAllRecords(SmsRecordsTable.TABLE_NAME, null,
				SmsRecordsTable.SMSRECORD_DELIVERED);
		if (c.moveToFirst()) {
			do {
				String delivered = "";
				if (c.getInt(ISDELIVERED) == 0)
					if (convertmillisecondsindays(c.getString(TIMECOUNT)) > 0)
						delivered = nyd;
					else
						delivered = "nd";
				else
					delivered = "d";

				list.add(new GroupInfo(c.getString(SMSRECORD_ID), c
						.getString(SMSTITLE), c.getString(TIMESTAMP) +
						">"	+ 
						delivered));
			} while (c.moveToNext());
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			int position, long arg3) {
		id = list.get(position).getID();
		pos = position;
		return false;
	}

	private void CreatMenu(ContextMenu menu) {
		menu.add(0, 0, 0, context.getResources().getString(R.string.activate_msg));
		menu.add(0, 1, 1,context.getResources().getString(R.string.delete_msg));
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		CreatMenu(menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		return MenuChoice(item);
	}

	private boolean MenuChoice(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			Intent i = new Intent(this, SendSMSActivity.class);
			i.putExtra("id", id);
			startActivityForResult(i, -1);
			return true;
		case 1:
			deleteRecords(id);
			return true;
		}
		return false;
	}



	@Override
	public void onClick(View arg0) {

	}

}
