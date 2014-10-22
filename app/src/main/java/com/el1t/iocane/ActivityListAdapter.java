package com.el1t.iocane;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by El1t on 10/21/14.
 */
public class ActivityListAdapter extends ArrayAdapter<EighthActivityItem>
{
	private final Context context;

	public ActivityListAdapter(Context context, ArrayList<EighthActivityItem> values) {
		super(context, R.layout.fragment_signup, values);
		this.context = context;
		if (values != null) {
			this.addAll(values);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.row_signup, parent, false);
		TextView activityName = (TextView) rowView.findViewById(R.id.activityName);
		TextView activityDescription = (TextView) rowView.findViewById(R.id.activityDescription);
		ProgressBar capacity = (ProgressBar) rowView.findViewById(R.id.capacity);

		EighthActivityItem item = this.getItem(position);
		activityName.setText(item.getName());
		activityDescription.setText(item.getDescription());

		capacity.setMax(item.getCapacity());
		capacity.setProgress(item.getMemberCount());
		return rowView;
	}
}
