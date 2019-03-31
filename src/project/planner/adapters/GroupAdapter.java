package project.planner.adapters;

import java.util.List;

/**
 * 
 * Array adapter class helps to create item for Group and related contact information
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

public class GroupAdapter extends ArrayAdapter<GroupInfo>  {

	private final Activity context;
	int resourceid;
	CheckBox chkbox;
	List<GroupInfo> list = null;

	public GroupAdapter(Activity context, List<GroupInfo> list) 
	{
		super(context, R.layout.grouprow, list);
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
			view = inflator.inflate(R.layout.grouprow, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.text1 = (TextView) view.findViewById(R.id.groupname);
			viewHolder.text2 = (TextView) view.findViewById(R.id.count);
			view.setTag(viewHolder);
		}
		else
		{
			view = convertview;
		}
		ViewHolder holder = (ViewHolder) view.getTag();
		String Text = "";

		if(!list.get(position).getName().isEmpty() )
			Text = list.get(position).getName();
		holder.text1.setText(Text);
		
		Text = list.get(position).getTimeStamp();
		holder.text2.setText(Text);

		return view;
	}

	static class ViewHolder
	{
		protected TextView text1;
		protected TextView text2;


	}

}
