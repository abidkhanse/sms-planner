package project.planner.activities;



import project.planner.db.DBAdapter;
import project.planner.db.DataBaseOperations;
import project.planner.db.GroupInfoTable;
import project.planner.sms.R;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


/**
 * An activity that edits group name for contacts   
 */
public class EditGroupActivity extends Activity implements OnClickListener{

	long 		rowid;
	EditText 	txtgroupname;
	ImageView 	btnsave;
	Button 		btndelete;
	CheckBox 	groupcheck;
	boolean 	isallowed = false;

	String 		id ;
	String 		name ;
	String 		count;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editgroupactivity);

		setObjectsForOnClickListner();
		ftechDataFromCallerActivity();
		fetchGroupRowFromTable();
	}
	
	/**
	 * initialize view controls
	 */
	private void setObjectsForOnClickListner()
	{
		txtgroupname = (EditText)findViewById(R.id.txtgroupname);
		btnsave = (ImageView) findViewById(R.id.groupsave);
	}

	
	
	
	
	/**
	 * Get record from GroupInfoTable    
	 */
	private void fetchGroupRowFromTable()
	{
		String where = "ID = ";
		DataBaseOperations operations = DataBaseOperations.getInstance(getApplicationContext());
		if(rowid > 0 ) 
		{
			String[] array = {"groupname"};
			where = where + Long.toString(rowid) ; 
			Cursor c = operations.GetContact(where, GroupInfoTable.TABLE_NAME, array,null );
			name = c.getString(0);
			txtgroupname.setText(name);
		}
		else
		{
			Toast.makeText(this, "no record found" ,Toast.LENGTH_SHORT).show();
			
		}
	}



	public void onClick(View v)
	{
		String groupname = txtgroupname.getText().toString().trim();
		if(!groupname.isEmpty() && groupname.length()<=20)
		{			
			switch (v.getId()) { 
			
			/**
			 * Check if newly inserted name does not exist in table, save otherwise and send OK signal back to activity.    
			 */
			case R.id.groupsave:
				if(name.equals(txtgroupname.getText().toString().trim()))
				{
					Toast.makeText(this, "Choose different name" ,Toast.LENGTH_SHORT).show();
				}
				else
				{
					DataBaseOperations operations = DataBaseOperations.getInstance(getApplicationContext());
					String gname = txtgroupname.getText().toString().trim();
					operations.UpdateGroupName(rowid,gname);
					Toast.makeText(this, "group name changed" ,Toast.LENGTH_SHORT).show();
														
					Intent returnIntent = new Intent();
			 		returnIntent.putExtra("result","edited");
			 		Bundle b = new Bundle();
			 		b.putString("name",gname);
			 		b.putString("id",id);
			 		b.putString("count",count);
			 		returnIntent.putExtras(b);
					setResult(RESULT_OK,returnIntent);     
					finish();
				}
				break;
			}
		}
		else
		{
			Toast.makeText(this, "Not empty field or max name size 20 characters" ,Toast.LENGTH_SHORT).show();
		}
	}
	
	private void ftechDataFromCallerActivity()
	{
		Intent i = getIntent();
		id = i.getStringExtra("position");
		name = i.getStringExtra("name");
		count = i.getStringExtra("count");
		try
		{
			rowid = Long.parseLong(id);
		}
		catch (NumberFormatException nfe)
		{
			rowid = -1;
		}
	}
}
