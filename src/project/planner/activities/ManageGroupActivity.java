package project.planner.activities;

import project.planner.db.DBAdapter;
import project.planner.db.DataBaseOperations;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * An activity to create new groups or edit existing one.
 * 
 * @author KHAN
 *
 */
public class ManageGroupActivity extends Activity implements OnClickListener {

	public static final String GROUP_GROUPTABLENAME = "groupinfo";
	private static final Uri URI = ContactsContract.Contacts.CONTENT_URI;
	TextView textviewmanagegroup;
	String helptext;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.managegroupactivity);

		textviewmanagegroup = (TextView) findViewById(R.id.textviewmanagegroup);
		Typeface font = Typeface.createFromAsset(getAssets(),"CopperplateGothicLight.ttf");
		textviewmanagegroup.setTypeface(font);
        helptext = 
        context.getResources().getString(R.string.new_group_text) +
        "\n" +
        context.getResources().getString(R.string.new_group_text_detail) +
        "\n\n" +
        context.getResources().getString(R.string.add_in_group_text) +
        "\n" +
        context.getResources().getString(R.string.add_in_group_text_detail) +
        "\n\n" +
        context.getResources().getString(R.string.manage_group_text) +
        "\n" + 
        context.getResources().getString(R.string.manage_group_text_detail);
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.help:

			AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
			alertbox.setMessage(helptext);
			alertbox.setTitle(context.getResources().getString(R.string.help));
			alertbox.setNeutralButton("OK",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface arg0, int arg1) {

						}
					});

			alertbox.show();
			break;

			/**
			 * 
			 * Create group
			 */
		case R.id.cgbutton: {
			Intent i = new Intent(this, CreateGroupActivity.class);
			startActivity(i);
		}
			break;

			/**
			 * 
			 * Add contacts in group
			 */

		case R.id.addinbutton: {
			if (globalVariables.contactscount > 0) {
				if (isGroupAvailable()) {
					if (!globalVariables.loadingcontactsinprogress) {
						Intent i = new Intent(this, SmsPlannerActivity.class);
						i.putExtra("activityvalue", "mainActivity");
						startActivity(i);
					} else {
						int count = globalVariables.contactscount;
						Toast.makeText(this, "Loading contacts, wait a sec", Toast.LENGTH_SHORT)
						.show();
					}
				} else {
					Toast.makeText(this, "create atleast one group", Toast.LENGTH_SHORT)
					.show();
				}
			} else {
				Toast.makeText(this, "no contact found", Toast.LENGTH_SHORT)
				.show();
			}
		}
			break;

			/**
			 * 
			 * Manage existing group
			 */

		case R.id.managebutton: {
			if (isGroupAvailable())
				startActivity(new Intent(this, GroupListActivity.class));
			else {
				Toast.makeText(this,context.getResources().getString(R.string.no_group_found), Toast.LENGTH_SHORT)
						.show();
			}
		}
			break;
		}
	}
	
	/**
	 * Check if group is already created  
	 * 
	 * @return true if found, otherwise false 
	 */
	private boolean isGroupAvailable() {
		DataBaseOperations operations = DataBaseOperations
				.getInstance(getApplicationContext());
		Cursor c = operations.GetAllRecords(GROUP_GROUPTABLENAME, null, null);
		return c.moveToFirst();
		
	}

}