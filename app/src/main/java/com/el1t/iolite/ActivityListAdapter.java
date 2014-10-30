package com.el1t.iolite;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by El1t on 10/21/14.
 */
public class ActivityListAdapter extends ArrayAdapter<EighthActivityItem> implements Filterable
{
	// This is to hold all items without filtering
	private ArrayList<EighthActivityItem> mItems;

	// View lookup cache
	private static class ViewHolder {
		TextView activityName;
		TextView activityDescription;
		ImageView circle;
		ImageView icon;
		ProgressBar capacity;
	}

	public ActivityListAdapter(Context context, ArrayList<EighthActivityItem> items) {
		super(context, 0, items);
		mItems = items;
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
			viewHolder.circle = (ImageView) convertView.findViewById(R.id.circle);
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
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

		// Capacity bar
		if (item.getCapacity() > 0) {
			viewHolder.capacity.setMax(item.getCapacity());
			viewHolder.capacity.setProgress(item.getMemberCount());
			viewHolder.capacity.setVisibility(View.VISIBLE);
		} else {
			viewHolder.capacity.setVisibility(View.GONE);
		}

		// Set color
		int color = Color.parseColor("#259b24");			// Green
		int icon  = R.drawable.ic_info_white_48dp;
		if (item.isCancelled()) {
			color = Color.parseColor("#e51c23");			// Red
			icon = R.drawable.ic_remove_circle_white_48dp;
		} else {
			if (item.isRestricted()) {
				color = Color.parseColor("#ffc107");		// Amber
				icon = R.drawable.ic_lock_circle_white_48dp;
			} else {
				if (item.isFavorite()) {
					color = Color.parseColor("#f48fb1");	// Pink
					icon = R.drawable.ic_stars_white_48dp;
				}
			}
		}
		viewHolder.icon.setColorFilter(new
				PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
		viewHolder.icon.setImageResource(icon);

		return convertView;
	}

	@Override
	public Filter getFilter() {

		return new Filter() {
			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				if (results.count == 0) {
					notifyDataSetInvalidated();
				} else {
					ActivityListAdapter.this.clear();
					ActivityListAdapter.this.addAll((ArrayList<EighthActivityItem>) results.values);
					notifyDataSetChanged();
				}
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();

				// Empty constraints display all items
				if(constraint == null || constraint.toString().trim().length() == 0){
					results.values = mItems;
					results.count = mItems.size();
				} else {
					ArrayList<EighthActivityItem> FilteredArrayNames = new ArrayList<EighthActivityItem>();
					constraint = constraint.toString().toLowerCase();
					// This should preserve the sort of the items
					for (EighthActivityItem item : mItems) {
						// Match activity name, and room number todo: match sponsors
						if (item.getName().toLowerCase().contains(constraint) ||
								item.getBlockRoomString().replace(" ", "").contains(constraint.toString().replace(" ", ""))) {
							FilteredArrayNames.add(item);
						}
					}
					results.values = FilteredArrayNames;
					results.count = FilteredArrayNames.size();
				}
				return results;
			}
		};
	}
}
