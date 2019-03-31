package project.planner.activities;

import project.planner.db.DBAdapter;
import project.planner.db.DataBaseOperations;
import project.planner.db.GroupInfoTable;
import project.planner.sms.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
 
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * An activity that creates groups for contacts   
 */
public class CreateGroupActivity extends Activity implements OnClickListener {

	ImageView 	imgSave;
	ImageView 	imgCancel;
	EditText 	edittxtName;
	
	String destPath;
	TextView textviewcreategroup;
	Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.creategroupactivity);
		context = getApplicationContext();

		textviewcreategroup = (TextView) findViewById(R.id.textviewcreategroup);
		
		/**
		 * Set Font type
		 */
		Typeface font = Typeface.createFromAsset(getAssets(), "CopperplateGothicLight.ttf");
		textviewcreategroup.setTypeface(font);

		SetObjectsForOnClickListner();
	}
	
	/**
	 * initialize view controls
	 */
	private void SetObjectsForOnClickListner() {
		imgSave = 		(ImageView) findViewById(R.id.imgsave);
		imgCancel = 	(ImageView) findViewById(R.id.imgcancel);
		edittxtName = 	(EditText) findViewById(R.id.groupname);
	}
	
	private void CancelPressed() {
		finish();
	}
	
	private void SaveGroup() {
		String groupname = edittxtName.getText().toString().trim();
		
		/**
		 * check if group name string is empty of string length is more than 20 characters
		 */

		if (groupname.isEmpty() || groupname.length() > 20) {
			Toast.makeText(this, "Group name length 20 character", Toast.LENGTH_SHORT)
			.show();

		} else {
			DataBaseOperations operations = DataBaseOperations
					.getInstance(getApplicationContext());
			
			/**
			 * check if group name already exists, save otherwise. 
			 */
			int count = operations.IsRecordExists(GroupInfoTable.GROUP_GROUPNAME + " = '" + groupname + "'",
					GroupInfoTable.TABLE_NAME);
			;
			if (count == 0) {
				long i = operations.InsertGroupName(groupname);

				edittxtName.setText("");
				if (i > -1){
					    Toast.makeText(this,edittxtName.getText().toString().trim() + 
                    " " +
                    context.getResources().getString(R.string.groupsaved)
                                    , Toast.LENGTH_SHORT).show();
				}
				else{
					Toast.makeText(this,edittxtName.getText().toString().trim() + 
                    " " +
                    context.getResources().getString(R.string.db_error)
                                    , Toast.LENGTH_SHORT).show();
				}

				CancelPressed();
			} else {
				Toast.makeText(this, edittxtName.getText().toString().trim() + 
                " " +
                 context.getResources().getString(R.string.already_exists), Toast.LENGTH_SHORT).show();
				
			}
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgsave:
			SaveGroup();

			break;

		case R.id.imgcancel:
			CancelPressed();
			break;
		default:
			break;
		}
	}
}
