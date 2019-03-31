package project.planner.activities;

import project.planner.db.DBAdapter;
import project.planner.db.DataBaseOperations;
import project.planner.db.GroupInfoTable;
import project.planner.models.globalVariables;
import project.planner.services.ContactXmlCreator;
import project.planner.services.GroupXmlCreator;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * An activity that allow user to store mobile contacts and group contacts in XML file.
 * Class uses multi-threading technique to save xml files in local mobile Download directory      
 */
public class ExtraFeaturesActivity extends Activity implements OnClickListener {

	ImageView imgcreategroupxml;
	ImageView imgloadgroupxml;
	ImageView imgcreatecontatxml;
	ImageView imgloadcontactxml;

	TextView textviewextrafeatures;
	boolean contactsinprogress = false;
	boolean groupsinprogress = false;
    Context context;
 
	String helptext; 

	private static final Uri URI = ContactsContract.Contacts.CONTENT_URI;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.extrafeaturesactivity);
        context = getApplicationContext();
		setObjectsForOnClickListner();
	}
	
	/**
	 * initialize view controls
	 */
	private void setObjectsForOnClickListner()
	{
		imgcreategroupxml = (ImageView) findViewById(R.id.imgcreategroupxml);
		imgloadgroupxml = (ImageView) findViewById(R.id.imgloadgroupxml);
		imgcreatecontatxml = (ImageView) findViewById(R.id.imgcreatecontatxml);
		imgloadcontactxml = (ImageView) findViewById(R.id.imgloadcontactxml);

		textviewextrafeatures = (TextView) findViewById(R.id.textviewextrafeatures);
		Typeface font = Typeface.createFromAsset(getAssets(),"CopperplateGothicLight.ttf");
		textviewextrafeatures.setTypeface(font);
        helptext = context.getResources().getString(R.string.extra_features_helptext);
	}
	

	/**
	 * Check if any group is created by user.
     *   
     * @return Return true if Group found, otherwise false.
     * 
     */
	public boolean isGroupAvailable() {
		DataBaseOperations operations = DataBaseOperations
				.getInstance(getApplicationContext());
		Cursor c = operations.GetAllRecords(GroupInfoTable.TABLE_NAME, null, null);
		return c.moveToFirst();
	}

		
	
	/**
	 * Check if any contact information is available in mobile phone.
     *   
     * @return Return true if found, otherwise false.
     * 
     */
	public boolean isContactAvailable() {
		ContentResolver contentResolver = getContentResolver();
		Cursor cursor = contentResolver.query(URI, null, null, null, null);
		return cursor.getCount() > 0 ?  true : false;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {


		/**
		 * Display help box.
	     */
		case R.id.help:

			AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
			alertbox.setMessage(helptext);
			alertbox.setTitle(context.getResources().getString(R.string.help));
			alertbox.setNeutralButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {

						}
					});
			alertbox.show();
		break;

		/**
		 * Ask if user wants to create XML file for groups
	     */
		case R.id.imgcreategroupxml:
			if (isGroupAvailable()) {
				if (!globalVariables.creategroupxmlinprogress) {

					AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
							this);
					myAlertDialog.setTitle(context.getResources().getString(R.string.continue_progress));
					myAlertDialog
							.setMessage                         
                            (
                                context.getResources().getString(R.string.want_to_create) +
                                context.getResources().getString(R.string.group_contacts_xml)
                            );
                            
					myAlertDialog.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface arg0,
										int arg1) {
									startGroupXMLProgress();

								}
							});
					myAlertDialog.setNegativeButton(context.getResources().getString(R.string.cancel),
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface arg0,
										int arg1) {
								}
							});
					myAlertDialog.show();

				} else {
					Toast.makeText(this, "Group context file in progress", Toast.LENGTH_SHORT)
					.show();
				}
			} else {
				Toast.makeText(this, "No Group found", Toast.LENGTH_SHORT)
				.show();
			}
			break;

			/**
			 * Ask if user wants to load Group XML file in phone
		     */
		case R.id.imgloadgroupxml:
			if (!globalVariables.loadgroupxmlinprogress) {
				startActivity(new Intent(this, FileChooserActivity.class));
			}
		break;

			/**
			 * Ask if user wants to create XML file for phone contacts
		     */

		case R.id.imgcreatecontatxml:
			if (isContactAvailable()
					&& !globalVariables.createcontactxmlinprogress) {
				Cursor cursor = this.getContentResolver().query(
						ContactsContract.Contacts.CONTENT_URI, null, null,
						null, null);
				String message;
				String heading = context.getResources().getString(R.string.no_group_found) + "\n" + 
                                 context.getResources().getString(R.string.continue_progress);
				if (cursor.getCount() > 100)
					message = "You have " + cursor.getCount() + 
                    context.getResources().getString(R.string.contact) +
                    context.getResources().getString(R.string.take_a_minute); 
				else
					message = cursor.getCount() + " contacts";

				MessageAlert(message, heading);
			} else {
				Toast.makeText(this,
                context.getResources().getString(R.string.phone_contacts_xml) +
                context.getResources().getString(R.string.in_progress),
                Toast.LENGTH_SHORT).show();
			}
			break;

			/**
			 * Ask if user wants to load Contacts XML file in phone
		     */
		case R.id.imgloadcontactxml:
			if (!globalVariables.loadcontactxmlinprogress) {
				startActivity(new Intent(this, FileChooserForContacts.class));
			} else {
				Toast.makeText(this,
                context.getResources().getString(R.string.loading_contacts),
                Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
	}

	/**
	 * Message box for user 
     */
	private void createOKDialog(String message) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(message);
        
		alertDialog.setMessage(context.getResources().getString(R.string.feel_free_use_device));
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		});

		alertDialog.show();
	}

	void MessageAlert(String msg, String heading) {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle(heading);
		alertDialogBuilder
				.setMessage(msg)
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								startContactXMLProgress();
								createOKDialog(
                                            context.getResources().getString(R.string.phone_contacts_xml) +
                                            context.getResources().getString(R.string.in_progress)
                                            );
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}


	/**
	 * Thread start for creating Group XML file  
     */
	public void startGroupXMLProgress() {

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				GroupXmlCreator xmlcreator = new GroupXmlCreator(getApplicationContext());
				if (xmlcreator.LoadGroupList()) {
					globalVariables.creategroupxmlinprogress = true;
					globalVariables.creategroupxmlinprogress = xmlcreator
							.LoadContactsList();
				}
			}
		};
		new Thread(runnable).start();
	}

	/**
	 * Thread start for creating Contact XML file  
     */
	public void startContactXMLProgress() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {

				ContactXmlCreator slc = new ContactXmlCreator(getBaseContext());
				globalVariables.createcontactxmlinprogress = true;
				globalVariables.createcontactxmlinprogress = slc.CreateXMLFile();
			}
		};
		new Thread(runnable).start();
	}

}
