package com.el1t.iolite.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.el1t.iolite.R;
import com.el1t.iolite.item.EighthActivityItem;
import com.el1t.iolite.item.EighthBlockItem;
import com.el1t.iolite.item.Schedule;
import com.el1t.iolite.item.ScheduleItem;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by El1t on 12/11/14.
 */
public class ScheduleListAdapter extends ArrayAdapter<ScheduleItem>
{
	private final LayoutInflater mLayoutInflater;

	// View lookup cache
	private static class ViewHolder {
		TextView title;
		TextView times;
	}

	public ScheduleListAdapter(Context context, Schedule schedule) {
		super(context, 0);
		setSchedule(schedule);

		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Cache the views for faster performance
		ViewHolder viewHolder;
		final ScheduleItem scheduleItem = getItem(position);

		if (convertView == null) {
			// Initialize viewHolder and convertView
			viewHolder = new ViewHolder();
			// Save IDs inside ViewHolder and attach the ViewHolder to convertView
			if (scheduleItem.isHeader()) {
				convertView = mLayoutInflater.inflate(R.layout.row_schedule_header, parent, false);
				viewHolder.title = (TextView) convertView.findViewById(R.id.day);
				viewHolder.times = (TextView) convertView.findViewById(R.id.type);
			} else {
				convertView = mLayoutInflater.inflate(R.layout.row_schedule, parent, false);
				viewHolder.title = (TextView) convertView.findViewById(R.id.title);
				viewHolder.times = (TextView) convertView.findViewById(R.id.times);
			}
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// Set fields
		viewHolder.title.setText(scheduleItem.getName());
		// As a header, getTimes will give the day type
		viewHolder.times.setText(scheduleItem.getTimes());

		return convertView;
	}

	@Override
	public int getItemViewType(int position) {
		return getItem(position).isHeader() ? 1 : 0;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public boolean isEnabled(int position) {
		return !getItem(position).isHeader();
	}

	public void setSchedule(Schedule schedule) {
		final ArrayList<ScheduleItem> list = schedule.getItems();
		Collections.sort(list);
		clear();
		addAll(list);

		// Add headers
		insert(new ScheduleItem(schedule.getDay(), schedule.getType()), 0);
		notifyDataSetChanged();
	}
}