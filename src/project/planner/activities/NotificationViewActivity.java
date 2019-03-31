package project.planner.activities;

import project.planner.sms.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * 
 * A simple activity to display notification detail. 
 * 
 * @author KHAN
 *
 */
public class NotificationViewActivity extends Activity
{
	TextView contacts;
	TextView message;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notificationviewactivity);
		
		contacts =	(TextView) findViewById(R.id.contactsinfo);
		message =	(TextView) findViewById(R.id.messageinfo);
				
		String msg = getIntent().getStringExtra("message");
		String con = getIntent().getStringExtra("contacts");
			
		contacts.setText(con);
		message.setText(msg);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
	
}