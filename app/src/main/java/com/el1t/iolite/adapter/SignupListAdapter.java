package com.el1t.iolite.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.el1t.iolite.R;
import com.el1t.iolite.SignupFragment;
import com.el1t.iolite.item.EighthActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by El1t on 10/21/14.
 */
public class SignupListAdapter extends ArrayAdapter<EighthActivity> implements Filterable
{
	// This is to hold all items without filtering
	ArrayList<EighthActivity> mItems;
	private final ArrayList<EighthActivity> headers;
	private final DefaultSortComp mComp;
	private final LayoutInflater mLayoutInflater;
	private final SignupFragment.OnFragmentInteractionListener mContext;
	private final int[] mColors;
	private final Bitmap ICON_DASH;
	private final Bitmap ICON_LOCK;
	private final Bitmap ICON_STAR;
	private final Bitmap ICON_FAVE;
	private final Bitmap ICON_OKAY;

	// View lookup cache
	private static class ViewHolder {
		TextView title;
		TextView room;
		TextView sponsors;
		TextView description;
		ImageView circle;
		ImageView icon;
		ProgressBar capacity;
	}

	public enum ActivityHeaderType {
		FAVORITE, SPECIAL, GENERAL
	}

	public enum Colors {
		RED, DEEP_ORANGE, PINK, ORANGE, GREEN, WHITE
	}

	public SignupListAdapter(Activity context, EighthActivity[] items) {
		super(context, 0);
		mContext = (SignupFragment.OnFragmentInteractionListener) context;
		// Headers
		headers = new ArrayList<>(items.length + 3);
		headers.add(new EighthActivity("Favorites", ActivityHeaderType.FAVORITE));
		headers.add(new EighthActivity("Special", ActivityHeaderType.SPECIAL));
		headers.add(new EighthActivity("Activities", ActivityHeaderType.GENERAL));

		mComp = new DefaultSortComp();
		if (items == null) {
			mItems = null;
		} else {
			mItems = new ArrayList<>(Arrays.asList(items));
			sort();
		}
		mLayoutInflater = LayoutInflater.from(context);

		// Cache colors
		final Resources resources = context.getResources();
		mColors = new int[6];
		mColors[Colors.RED.ordinal()]           = resources.getColor(R.color.red_400);
		mColors[Colors.DEEP_ORANGE.ordinal()]   = resources.getColor(R.color.deep_orange_400);
		mColors[Colors.PINK.ordinal()]          = resources.getColor(R.color.pink_400);
		mColors[Colors.ORANGE.ordinal()]        = resources.getColor(R.color.orange_400);
		mColors[Colors.GREEN.ordinal()]         = resources.getColor(R.color.green_400);
		mColors[Colors.WHITE.ordinal()]         = resources.getColor(R.color.background);

		// Cache icons
		ICON_DASH = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_remove_circle_white_48dp);
		ICON_LOCK = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_lock_white_48dp);
		ICON_STAR = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_star_rate_white_48dp);
		ICON_FAVE = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_favorite_circle_white_48dp);
		ICON_OKAY = BitmapFactory.decodeResource(context.getResources(), R.drawable.abc_btn_check_to_on_mtrl_015);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Cache the views for faster performance
		final ViewHolder viewHolder;
		final EighthActivity item = getItem(position);

		if (convertView == null) {
			// Initialize viewHolder and convertView
			viewHolder = new ViewHolder();
			// Save IDs inside ViewHolder and attach the ViewHolder to convertView
			if (item.isHeader()) {
				convertView = mLayoutInflater.inflate(R.layout.row_header, parent, false);
				viewHolder.title = (TextView) convertView.findViewById(R.id.title);
			} else {
				convertView = mLayoutInflater.inflate(R.layout.row_signup, parent, false);
				viewHolder.title = (TextView) convertView.findViewById(R.id.title);
				viewHolder.room = (TextView) convertView.findViewById(R.id.room);
				viewHolder.sponsors = (TextView) convertView.findViewById(R.id.sponsors);
				viewHolder.description = (TextView) convertView.findViewById(R.id.description);
				viewHolder.capacity = (ProgressBar) convertView.findViewById(R.id.capacity);
				viewHolder.circle = (ImageView) convertView.findViewById(R.id.circle);
				viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
			}
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// Title
		viewHolder.title.setText(item.getName());
		if (!item.isHeader()) {
			// Description
			viewHolder.description.setText(item.getDescription());
			// Rooms
			if (item.hasRooms()) {
				viewHolder.room.setVisibility(View.VISIBLE);
				viewHolder.room.setText(item.getRooms());
			} else {
				viewHolder.room.setVisibility(View.GONE);
			}
			// Sponsors
			if (item.hasSponsors()) {
				viewHolder.sponsors.setVisibility(View.VISIBLE);
				// Show dash only if needed
				if (viewHolder.room.getVisibility() == View.VISIBLE) {
					viewHolder.sponsors.setText("—" + item.getSponsors());
				} else {
					viewHolder.sponsors.setText(item.getSponsors());
				}
			} else {
				viewHolder.sponsors.setVisibility(View.GONE);
			}
			// Capacity bar
			if (item.getCapacity() > 0) {
				viewHolder.capacity.setMax(item.getCapacity());
				viewHolder.capacity.setProgress(item.getMemberCount());
				viewHolder.capacity.setAlpha(0.05f);
				viewHolder.capacity.setVisibility(View.VISIBLE);
			} else {
				viewHolder.capacity.setVisibility(View.GONE);
			}

			// Set color
			final Colors color;
			if (item.isCancelled()) {
				color = Colors.RED;
				viewHolder.icon.setImageBitmap(ICON_DASH);
			} else if (item.isRestricted()) {
				color = Colors.DEEP_ORANGE;
				viewHolder.icon.setImageBitmap(ICON_LOCK);
			} else if (item.isFavorite()) {
				color = Colors.PINK;
				viewHolder.icon.setImageBitmap(ICON_FAVE);
			} else if (item.isSpecial()) {
				color = Colors.ORANGE;
				viewHolder.icon.setImageBitmap(ICON_STAR);
			} else {
				color = Colors.GREEN;
				viewHolder.icon.setImageBitmap(ICON_OKAY);
			}
			// Tint icon
			viewHolder.circle.setColorFilter(mColors[color.ordinal()]);
			viewHolder.circle.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mContext.viewDetails(item);
				}
			});
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

	@Override
	public boolean isEnabled(int position) {
		return !getItem(position).isHeader();
	}

	/**
	 * Sorts mItems, adds headers, and puts them into the view
	 */
	public void sort() {
		Collections.sort(mItems, mComp);
		clear();
		addAll(mItems);
		addHeaders();
		notifyDataSetChanged();
	}

	// Sort items and add to both lists
	public void update(EighthActivity[] items) {
		if (items != null) {
			mItems.clear();
			mItems.addAll(Arrays.asList(items));
			sort();
		}
	}

	private void addHeaders() {
		ActivityHeaderType index = ActivityHeaderType.FAVORITE;
		EighthActivity item;
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
						// Skip if no favorites
						i--;
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
							i--;
							index = ActivityHeaderType.GENERAL;
						}
						continue;
					}
					break;
				case GENERAL:
					if (!(item.isFavorite() || item.isSpecial())) {
						insert(headers.get(index.ordinal()), i);
						return;
					}
					break;
			}
		}
	}

	// Assumes mItems is already sorted
	public void restore() {
		if(getCount() == 0 && mItems != null) {
			addAll(mItems);
			addHeaders();
			notifyDataSetChanged();
		}
	}

	@Override
	public Filter getFilter() {
		return new Filter() {
			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				clear();
				if (results.count != 0) {
					addAll((ArrayList<EighthActivity>) results.values);
					addHeaders();
				}
				notifyDataSetChanged();
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				final FilterResults results = new FilterResults();

				// Empty constraints display all items
				if(constraint == null || constraint.length() == 0) {
					results.values = mItems;
					results.count = mItems.size();
				} else {
					final ArrayList<EighthActivity> FilteredArrayNames = new ArrayList<>();
					final String temp = constraint.toString().toLowerCase().replace(" ", "");
					// This should preserve the sort of the items
					for (EighthActivity item : mItems) {
						// Match activity name, and room number todo: match sponsors
						if (!item.isHeader() && (item.getName().toLowerCase().replace(" ", "").contains(temp) ||
								item.getSponsorsNoDelim().toLowerCase().replace(" ", "").contains(temp) ||
								item.getRoomsNoDelim().toLowerCase().replace(" ", "").contains(temp))) {
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
	private class DefaultSortComp implements Comparator<EighthActivity> {
		@Override
		public int compare(EighthActivity e1, EighthActivity e2) {
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