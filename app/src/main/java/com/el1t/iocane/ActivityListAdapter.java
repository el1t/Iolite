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
	public ActivityListAdapter(Context context, ArrayList<EighthActivityItem> values) {
		super(context, 0, values);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_signup, parent, false);
		}
		TextView activityName = (TextView) convertView.findViewById(R.id.activityName);
		TextView activityDescription = (TextView) convertView.findViewById(R.id.activityDescription);
		ProgressBar capacity = (ProgressBar) convertView.findViewById(R.id.capacity);

		EighthActivityItem item = this.getItem(position);
		activityName.setText(item.getName());
		activityDescription.setText(item.getDescription());

		capacity.setMax(item.getCapacity());
		capacity.setProgress(item.getMemberCount());
		return convertView;
	}
}
