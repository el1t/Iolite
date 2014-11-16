package com.el1t.iolite;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by El1t on 10/21/14.
 */
public class ActivityListAdapter extends ArrayAdapter<EighthActivityItem> implements Filterable
{
	// This is to hold all items without filtering
	protected ArrayList<EighthActivityItem> mItems;
	private ArrayList<EighthActivityItem> headers;
	private DefaultSortComp mComp;
	private LayoutInflater mLayoutInflater;
	private Bitmap ICON_DASH;
	private Bitmap ICON_LOCK;
	private Bitmap ICON_STAR;
	private Bitmap ICON_FAVE;

	// View lookup cache
	private static class ViewHolder {
		TextView title;
		TextView description;
		ImageView circle;
		ImageView icon;
		TextView letter;
		ProgressBar capacity;
	}

	public enum ActivityHeaderType {
		FAVORITE, SPECIAL, GENERAL
	}

	public ActivityListAdapter(Context context, ArrayList<EighthActivityItem> items) {
		super(context, 0);
		// Headers
		headers = new ArrayList<EighthActivityItem>();
		headers.add(new EighthActivityItem("Favorites", ActivityHeaderType.FAVORITE));
		headers.add(new EighthActivityItem("Special", ActivityHeaderType.SPECIAL));
		headers.add(new EighthActivityItem("Activities", ActivityHeaderType.GENERAL));

		mComp = new DefaultSortComp();
		mItems = items;
		sort();
		mLayoutInflater = LayoutInflater.from(context);

		// Cache icons
		ICON_DASH = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_remove_circle_white_48dp);
		ICON_LOCK = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_lock_circle_white_48dp);
		ICON_STAR = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_stars_white_48dp);
		ICON_FAVE = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_favorite_circle_white_48dp);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Cache the views for faster performance
		ViewHolder viewHolder;
		EighthActivityItem item = getItem(position);

		if (convertView == null) {
			// Initialize viewHolder and convertView
			viewHolder = new ViewHolder();
			// Save IDs inside ViewHolder and attach the ViewHolder to convertView
			if (item.isHeader()) {
				convertView = mLayoutInflater.inflate(R.layout.row_header, parent, false);
				viewHolder.title = (TextView) convertView.findViewById(R.id.headerName);
			} else {
				convertView = mLayoutInflater.inflate(R.layout.row_signup, parent, false);
				viewHolder.title = (TextView) convertView.findViewById(R.id.title);
				viewHolder.description = (TextView) convertView.findViewById(R.id.description);
				viewHolder.capacity = (ProgressBar) convertView.findViewById(R.id.capacity);
				viewHolder.circle = (ImageView) convertView.findViewById(R.id.circle);
				viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
				viewHolder.letter = (TextView) convertView.findViewById(R.id.letter);
			}
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// Set fields
		viewHolder.title.setText(item.getName());
		if (!item.isHeader()) {
			if (item.hasDescription()) {
				viewHolder.description.setVisibility(View.GONE);
			} else {
				viewHolder.description.setText(item.getDescription());
				viewHolder.description.setVisibility(View.VISIBLE);
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
			int color = -1;
			if (item.isCancelled()) {
				color = Color.parseColor("#F44336");            // Red
				viewHolder.icon.setImageBitmap(ICON_DASH);
			} else if (item.isRestricted()) {
				color = Color.parseColor("#FF5722");            // Deep orange
				viewHolder.icon.setImageBitmap(ICON_LOCK);
			} else if (item.isFavorite()) {
				color = Color.parseColor("#E91E63");            // Pink
				viewHolder.icon.setImageBitmap(ICON_FAVE);
			} else if (item.isSpecial()) {
				color = Color.parseColor("#FF9800");            // Orange
				viewHolder.icon.setImageBitmap(ICON_STAR);
			} else {
				// Tint background to green when letter is used
				viewHolder.circle.setColorFilter(Color.parseColor("#4CAF50"));
				viewHolder.icon.setVisibility(View.INVISIBLE);
				viewHolder.letter.setText(item.getFirstChar());
				viewHolder.letter.setVisibility(View.VISIBLE);
			}
			// Tint icon if icon is used
			if (color != -1) {
				viewHolder.circle.setColorFilter(Color.parseColor("white"));
				viewHolder.icon.setColorFilter(color);
				viewHolder.icon.setVisibility(View.VISIBLE);
				viewHolder.letter.setVisibility(View.INVISIBLE);
			}
		}

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

	public void sort() {
		Collections.sort(mItems, mComp);
		clear();
		addAll(mItems);
		addHeaders();
		notifyDataSetChanged();
	}

	// Sort items and add to both lists
	protected void setListItems(ArrayList<EighthActivityItem> items) {
		mItems = items;
		sort();
	}

	private void addHeaders() {
		ActivityHeaderType index = ActivityHeaderType.FAVORITE;
		EighthActivityItem item;
		int count = getCount();
		for (int i = 0; i < count; i++) {
			item = getItem(i);
			switch (index) {
				case FAVORITE:
					if (item.isFavorite()) {
						insert(headers.get(index.ordinal()), i);
						index = ActivityHeaderType.SPECIAL;
						continue;
					} else {
						index = ActivityHeaderType.SPECIAL;
					}
					break;
				case SPECIAL:
					if (!item.isFavorite()) {
						if (item.isSpecial()) {
							insert(headers.get(index.ordinal()), i);
							index = ActivityHeaderType.GENERAL;
						} else {
							// Skip if no special activities
							index = ActivityHeaderType.GENERAL;
						}
						continue;
					}
					break;
				case GENERAL:
					if (!(item.isFavorite() || item.isSpecial())) {
						insert(headers.get(index.ordinal()), i);
						index = ActivityHeaderType.SPECIAL;
						return;
					}
					break;
			}
		}
	}

	// Assumes mItems is already sorted
	protected void restore() {
		if(getCount() == 0 && mItems != null) {
			addAll(mItems);
			notifyDataSetChanged();
		}
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

	// Sort by favorites, alphabetically
	private class DefaultSortComp implements Comparator<EighthActivityItem>
	{
		@Override
		public int compare(EighthActivityItem e1, EighthActivityItem e2) {
			// Compare by name if both or neither are favorites, or return the favorite
			if (e1.isFavorite()) {
				if (e2.isFavorite())
					return e1.getName().compareToIgnoreCase(e2.getName());
				return -1;
			}
			if (e2.isFavorite())
				return 1;

			// Check for special
			if (!(e1.isSpecial() ^ e2.isSpecial()))
				return e1.getName().compareToIgnoreCase(e2.getName());
			if (e1.isSpecial())
				return -1;
			return 1;
		}
	}
}