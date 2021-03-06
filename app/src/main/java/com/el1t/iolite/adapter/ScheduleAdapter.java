package com.el1t.iolite.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.el1t.iolite.R;
import com.el1t.iolite.model.Schedule;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by El1t on 12/16/14.
 */
public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
	private final LayoutInflater mLayoutInflater;
	private ArrayList<Schedule> mSchedules;
	private int[] mColors;

	private enum Days {
		ANCHOR("Anchor Day"),
		BLUE("Blue Day"),
		RED("Red Day"),
		JLC("JLC Blue Day"),
		MODIFIED_BLUE("Modified Blue"),
		MODIFIED_RED("Modified Red"),
		MODIFIED_JLC("Modified JLC"),
		OFF("No School");

		private String name;

		Days(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

		public static int indexOf(String name) {
			if (name.startsWith("Modified")) {
				if (name.contains("Blue")) {
					return name.contains("JLC") ? MODIFIED_JLC.ordinal() : MODIFIED_BLUE.ordinal();
				}
				return MODIFIED_RED.ordinal();
			}
			for (Days day : values()) {
				if (day.name.charAt(0) == name.charAt(0)) {
					return day.ordinal();
				}
			}
			return Days.OFF.ordinal();
		}
	}

	// View lookup cache
	public static class ViewHolder extends RecyclerView.ViewHolder {
		CardView card;
		TextView title;
		TextView blocks;
		TextView times;

		public ViewHolder(View itemView) {
			super(itemView);
			card = (CardView) itemView.findViewById(R.id.card);
			title = (TextView) itemView.findViewById(R.id.day);
			blocks = (TextView) itemView.findViewById(R.id.blocks);
			times = (TextView) itemView.findViewById(R.id.times);
		}
	}

	public ScheduleAdapter(Context context, Schedule[] schedule) {
		if (schedule == null) {
			mSchedules = null;
		} else {
			mSchedules = new ArrayList<>(Arrays.asList(schedule));
		}

		mColors = new int[8];
		mColors[0] = ContextCompat.getColor(context, R.color.deep_purple_300);
		mColors[1] = ContextCompat.getColor(context, R.color.primary_300);
		mColors[2] = ContextCompat.getColor(context, R.color.red_300);
		mColors[3] = ContextCompat.getColor(context, R.color.light_blue_300);
		mColors[4] = ContextCompat.getColor(context, R.color.primary_400);
		mColors[5] = ContextCompat.getColor(context, R.color.red_400);
		mColors[6] = ContextCompat.getColor(context, R.color.light_blue_400);
		mColors[7] = ContextCompat.getColor(context, R.color.grey_100);

		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		final View v = mLayoutInflater.inflate(R.layout.row_schedule, viewGroup, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int i) {
		final Schedule schedule = mSchedules.get(i);

		// Set fields
		final int dayType = Days.indexOf(schedule.getType());

		// Add No School or Custom Message
		if (dayType == Days.OFF.ordinal()) {
			viewHolder.title.setText(schedule.getDay() + "\n" + schedule.getType());
			viewHolder.blocks.setVisibility(View.GONE);
			viewHolder.times.setVisibility(View.GONE);
		} else {
			if (dayType == Days.MODIFIED_BLUE.ordinal() || dayType == Days.MODIFIED_RED.ordinal() || dayType == Days.MODIFIED_JLC.ordinal()) {
				viewHolder.title.setText(schedule.getDay() + "\n" + schedule.getType());
			} else {
				viewHolder.title.setText(schedule.getDay());
			}
			viewHolder.blocks.setVisibility(View.VISIBLE);
			viewHolder.blocks.setText(schedule.getBlocks());
			viewHolder.times.setVisibility(View.VISIBLE);
			viewHolder.times.setText(schedule.getTimes());
		}

		// Get the index of the color by day
		viewHolder.card.setCardBackgroundColor(mColors[dayType]);
	}

	@Override
	public int getItemCount() {
		return mSchedules == null ? 0 : mSchedules.size();
	}

	public void addAll(Schedule[] schedules) {
		if (schedules != null) {
			mSchedules.addAll(Arrays.asList(schedules));
			notifyItemRangeInserted(mSchedules.size() - schedules.length, schedules.length);
		}
	}

	public void clear() {
		mSchedules.clear();
		notifyDataSetChanged();
	}

	public Schedule getLastItem() {
		return mSchedules.get(mSchedules.size() - 1);
	}
}