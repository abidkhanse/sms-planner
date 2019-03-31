package project.planner.adapters;

import java.util.List;

/**
 * 
 * Array adapter class helps to create item for list_view.
 * 
 * @author KHAN
 *
 */

import project.planner.models.GroupInfo;
import project.planner.sms.R;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class SMSHistoryAdapter extends ArrayAdapter<GroupInfo>  {

	private final Activity context;
	int resourceid;
	CheckBox chkbox;
	List<GroupInfo> list = null;

	public SMSHistoryAdapter(Activity context, List<GroupInfo> list) 
	{
		super(context, R.layout.smshistoryrow, list);
		this.context = context;
		this.list = list;
	}

	@Override
	public View getView(int position, View convertview, ViewGroup viewgroup)
	{
		View view = null;
		if (convertview == null)
		{
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.smshistoryrow, null);

			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.text1 = (TextView) view.findViewById(R.id.smsgroupname);
			viewHolder.text2 = (TextView) view.findViewById(R.id.smsdetail);
			viewHolder.text3 = (TextView) view.findViewById(R.id.smstime);
			
			
			view.setTag(viewHolder);
		}
		else
		{
			view = convertview;
		}
		ViewHolder holder = (ViewHolder) view.getTag();
		String Text = "";
 
		Text = list.get(position).getName();
		holder.text1.setText(Text);
		
		Text = list.get(position).getTimeStamp();
		
		String delimiter = ">";
		
		String []temp = Text.split(delimiter);
		holder.text2.setText(maketimestring(temp[0]));
		
		Text = list.get(position).getID();
		holder.text3.setText(temp[1]);
		
		return view;
	}
	
	private String maketimestring(String time)
	{
		String []temp  = time.split(" ");
		if(temp[1].isEmpty())
			return time;
		
			String []t = temp[1].split(":");
			if(t[0].length()==1)
				t[0] = "0"+t[0];
			
			if(t[1].length()==1)
				t[1] = "0"+t[1];
		
		
		time = temp[0]+" "+t[0]+":"+t[1];
		return time;
	
	}

	static class ViewHolder
	{
		protected TextView text1;
		protected TextView text2;
		protected TextView text3;
	
	}

}
