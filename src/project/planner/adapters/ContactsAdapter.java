package project.planner.adapters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import project.planner.models.ContactInfo;
import project.planner.sms.R;

 

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * 
 * Array adapter class helps to create an item for list_view for mobile contact information 
 * 
 * @author KHAN
 *
 */
public class ContactsAdapter extends ArrayAdapter<ContactInfo> 
{

	private final Activity context;
	List<ContactInfo> list = null;
	private ArrayList<ContactInfo> arraylist;

	public ContactsAdapter(Activity context, List<ContactInfo> list) {
		super(context, R.layout.contactrow, list);
		this.context = context;
		this.list = list;
		this.arraylist = new ArrayList<ContactInfo>();
		this.arraylist.addAll(list);
	}

	@Override
	public View getView(int position, View convertview, ViewGroup viewgroup){
		View view = null;
		if(convertview == null){
			LayoutInflater inflater = context.getLayoutInflater();
			view = inflater.inflate(R.layout.contactrow, null);
			ContactHolder holder = new ContactHolder();

			holder.txtviewfirstname = (TextView)view.findViewById(R.id.firstname);
			holder.txtviewphone = (TextView)view.findViewById(R.id.phone);
			view.setTag(holder);
		}
		else{

			view = convertview;
		}

		ContactHolder holder2 = (ContactHolder) view.getTag();
		
		if(list.get(position).selected==1)
		{
			holder2.txtviewfirstname.setTextColor(Color.parseColor("#03A7E0"));
			holder2.txtviewfirstname.setTypeface(null, Typeface.BOLD);
		}
		else
		{
			holder2.txtviewfirstname.setTextColor(Color.parseColor("#000000"));
			holder2.txtviewfirstname.setTypeface(null, Typeface.NORMAL);
		}
				
		holder2.txtviewfirstname.setText(list.get(position).firstname);
		holder2.txtviewphone.setText(list.get(position).phonenumber);

		return view;
	}
	
	public void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		list.clear();
		if (charText.length() == 0) {
			list.addAll(arraylist);
		} 
		else 
		{
			for (ContactInfo ci : arraylist) 
			{
				if (ci.GetFirstName().toLowerCase(Locale.getDefault()).contains(charText)) 
				{
					list.add(ci);
				}
			}
		}
		notifyDataSetChanged();
	}

	final class ContactHolder
	{
		TextView txtviewfirstname;
		TextView txtviewphone;
	}
}

