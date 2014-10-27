package com.el1t.iolite;

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
	// View lookup cache
	private static class ViewHolder {
		TextView activityName;
		TextView activityDescription;
		ProgressBar capacity;
	}

	public ActivityListAdapter(Context context, ArrayList<EighthActivityItem> values) {
		super(context, 0, values);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Cache the views for faster performance
		ViewHolder viewHolder;
		if (convertView == null) {
			// Initialize viewHolder and convertView
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_signup, parent, false);

			// Save IDs inside ViewHolder and attach the ViewHolder to convertView
			viewHolder.activityName = (TextView) convertView.findViewById(R.id.activityName);
			viewHolder.activityDescription = (TextView) convertView.findViewById(R.id.activityDescription);
			viewHolder.capacity = (ProgressBar) convertView.findViewById(R.id.capacity);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// Set fields
		EighthActivityItem item = getItem(position);
		viewHolder.activityName.setText(item.getName());
		if (item.hasDescription()) {
			viewHolder.activityDescription.setVisibility(View.GONE);
		} else {
			viewHolder.activityDescription.setText(item.getDescription());
			viewHolder.activityDescription.setVisibility(View.VISIBLE);
		}
		if (item.getCapacity() > 0) {
			viewHolder.capacity.setMax(item.getCapacity());
			viewHolder.capacity.setProgress(item.getMemberCount());
			viewHolder.capacity.setVisibility(View.VISIBLE);
		} else {
			viewHolder.capacity.setVisibility(View.GONE);
		}

		return convertView;
	}
}
