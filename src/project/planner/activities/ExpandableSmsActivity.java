package project.planner.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import project.planner.adapters.ExpandableListAdapter;
import project.planner.sms.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;


/**
 * An activity that creates the best wishes templates      
 */
public class ExpandableSmsActivity extends Activity {

	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;
	Context c;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.expandablelistview);
		c = getApplicationContext();

		bestWishesList();
		expListView = (ExpandableListView) findViewById(R.id.lvExp);
		listAdapter = new ExpandableListAdapter(this, listDataHeader,listDataChild);
		expListView.setAdapter(listAdapter);
		expListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {

				return false;
			}
		});

	 
		expListView.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
 
			}
		});

 
		expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
 

			}
		});

 
		expListView.setOnChildClickListener(new OnChildClickListener() {

			/**
			 * Expand the list item and see the wishes items.
			 */
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {

				Intent intent = new Intent();
				Bundle b = new Bundle();
				b.putString("smsheading", listDataHeader.get(groupPosition));
				b.putString("smstext",listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition));
				intent.putExtras(b);
				setResult(RESULT_OK, intent);
				finish();
				return false;
			}
		});
	}

	@Override
	public void onBackPressed() {
		Intent returnIntent = new Intent();
		returnIntent.putExtra("backpressed", "");
		setResult(RESULT_CANCELED, returnIntent);
		finish();
	}

	/**
	 * Prepares the list of best wishes and inserts this list in HashMap
	 */
	private void bestWishesList() {
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();

		// Adding child data
		listDataHeader.add(c.getResources().getString(R.string.new_year));
		listDataHeader.add(c.getResources().getString(R.string.xmax));
		listDataHeader.add(c.getResources().getString(R.string.eid));
		listDataHeader.add(c.getResources().getString(R.string.happy_birthday));
		listDataHeader.add(c.getResources().getString(R.string.anniversary));

		// Adding child data
		List<String> NewYear = new ArrayList<String>();
		NewYear.add(c.getResources().getString(R.string.new_year_wish));

		List<String> Eid = new ArrayList<String>();
        Eid.add(c.getResources().getString(R.string.eid_wish));

        List<String> Christmas = new ArrayList<String>();
		Christmas.add(c.getResources().getString(R.string.xmas_wish));

		List<String> HappyBirthday = new ArrayList<String>();
		HappyBirthday.add(c.getResources().getString(R.string.hbd_one));
		HappyBirthday.add(c.getResources().getString(R.string.hbd_two));
		HappyBirthday.add(c.getResources().getString(R.string.hdb_three));

		List<String> Anniversary = new ArrayList<String>();
		Anniversary.add(c.getResources().getString(R.string.anniversary_wish));

		listDataChild.put(listDataHeader.get(0), NewYear); // Header, Child data
		listDataChild.put(listDataHeader.get(1), Eid);
		listDataChild.put(listDataHeader.get(2), Christmas);
		listDataChild.put(listDataHeader.get(3), HappyBirthday);
		listDataChild.put(listDataHeader.get(4), Anniversary);

	}
}