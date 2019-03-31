package project.planner.adapters;

import java.util.List;

import project.planner.models.ContactInfo;
import project.planner.sms.R;

 

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter; 
import android.widget.CheckedTextView;
import android.widget.TextView;



/**
 * 
 * Array adapter class helps to create an item for list_view for active or inactive messages
 * 
 * @author KHAN
 *
 */

public class ActiveSMSAdapter extends ArrayAdapter<ContactInfo> 
{
	
	private final Activity context;
	int resourceid;
	List<ContactInfo> list = null;
	
	public ActiveSMSAdapter(Activity context, List<ContactInfo> list) {
		super(context, R.layout.activesmsrow, list);
		this.context = context;
		this.list = list;
	}
	
	@Override
	public View getView(int position, View convertview, ViewGroup viewgroup){
		View view = null;
		if(convertview == null){
			LayoutInflater inflater = context.getLayoutInflater();
			view = inflater.inflate(R.layout.activesmsrow, null);
			ContactHolder holder = new ContactHolder();
			
			holder.txtviewfirstname = (CheckedTextView)view.findViewById(R.id.firstname);
			holder.txtviewphone = (TextView)view.findViewById(R.id.phone);
			view.setTag(holder);
		}
		else{
			view = convertview;
		}
		
		ContactHolder holder2 = (ContactHolder) view.getTag();
		 
		if(list.get(position).getIsvalid()==1)
		{
			holder2.txtviewfirstname.setText(list.get(position).firstname);
			holder2.txtviewfirstname.setTextColor(Color.parseColor("#03A7E0"));
			holder2.txtviewfirstname.setChecked(true);
			
		}else{
			
			holder2.txtviewfirstname.setText(list.get(position).firstname);
			holder2.txtviewfirstname.setTextColor(Color.parseColor("#000000"));
			holder2.txtviewfirstname.setChecked(false);

		}
				
		
		holder2.txtviewphone.setText(list.get(position).phonenumber);
		
		return view;
	}
			
	final class ContactHolder
	{
		CheckedTextView txtviewfirstname;
		TextView txtviewphone;
	}
}
