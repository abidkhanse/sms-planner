package project.planner.activities;

 

import project.planner.db.DBAdapter;
import project.planner.db.DataBaseOperations;
import project.planner.sms.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DeleteGroupActivity extends Activity implements OnClickListener{

	long rowid;
	TextView txtgroupname;

	ImageView btndelete;
	CheckBox groupcheck;
	CheckBox contactcheck;
	boolean isallowed = false;
	boolean success = false;
	String id ;
	String name ;
	String count;
	Context context;

	public static final String GROUP_GROUPTABLENAME = "groupinfo";
	public static final String CONTACT_CONTACTTABLENAME = "contactinfo"; 

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deletegroupactivity);
		SetObjectsForOnClickListner();
		GetDataFromCaller();
		txtgroupname.setText(name);
		context.getApplicationContext();
	}


	private void SetObjectsForOnClickListner()
	{
		txtgroupname = (TextView)findViewById(R.id.textView1);
		btndelete = (ImageView) findViewById(R.id.groupdelete);
		groupcheck = (CheckBox)findViewById(R.id.groupcheckBox);
		contactcheck = (CheckBox) findViewById(R.id.contactcheckBox);
	}

	private void GetDataFromCaller()
	{
		Intent i = getIntent();
		id = i.getStringExtra("position");
		name = i.getStringExtra("name");

		try
		{
			rowid = Long.parseLong(id);
		}
		catch (NumberFormatException nfe)
		{
			rowid = -1;
		}
	}

	public void onClick(View v)
	{
		switch (v.getId()) {
		case R.id.groupdelete:
		{
			if(groupcheck.isChecked() || contactcheck.isChecked())
			{
				String deletemessage="";
				if(contactcheck.isChecked())
					deletemessage = "Contacts";

				if(groupcheck.isChecked()){
					 deletemessage = context.getResources().getString(R.string.contacts_and_delete);
				}


				AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
				myAlertDialog.setTitle(deletemessage);
				myAlertDialog.setMessage(
					context.getResources().getString(R.string.want_to_delete) + 
					"?"	);
				myAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface arg0, int arg1) {

						DeleteRecords();
						Intent returnIntent = new Intent();
						returnIntent.putExtra("result","deleted");
						Bundle b = new Bundle();
						b.putString("name",name);
						b.putString("id",id);
						b.putBoolean("result",success);
						returnIntent.putExtras(b);
						setResult(RESULT_OK,returnIntent);     
						finish();
					}
				});
				myAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface arg0, int arg1) {

					}});
				myAlertDialog.show();
			}
			else
			{
				Toast.makeText(this, "Select atleast one option", Toast.LENGTH_SHORT)
				.show();
			}
		}
		break;

		case R.id.groupcheckBox:
			isallowed = groupcheck.isChecked();
			if (isallowed)
			{
				contactcheck.setChecked(true);
				Toast.makeText(this,
				context.getResources().getString(R.string.group_delete_warning)
				,Toast.LENGTH_LONG).show();
			}
			break;

		default:
			break;
		}
	}

	public boolean  DeleteRecords()
	{
		DataBaseOperations operations = DataBaseOperations.getInstance(getApplicationContext());
		long count = 0;
		String where = "ID = ";	
		where = where + Long.toString(rowid) ;  
		Cursor c = operations.GetContact(where, GROUP_GROUPTABLENAME, null,null);
		if(c.moveToFirst())
		{
			String wherclause = "groupid = " + Long.toString(rowid);
			count = operations.IsRecordExists(wherclause , CONTACT_CONTACTTABLENAME);
			if(count > 0)
			{
				count = operations.DeleteRecords(CONTACT_CONTACTTABLENAME, wherclause);
			}
			if(isallowed)
			{
				success = operations.DeleteGroupRecord(rowid);
				Toast.makeText(this, c.getString(1) + " with " + Long.toString(count) + " contacts is deleted "  ,Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(this, Long.toString(count) + " contacts of "+ c.getString(1) +" are deleted "  ,Toast.LENGTH_SHORT).show();
			}

		}

		return success;
	}

}


