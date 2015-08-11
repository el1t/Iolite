package com.el1t.iolite.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.el1t.iolite.R;
import com.el1t.iolite.SignupFragment;
import com.el1t.iolite.item.EighthActivity;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by El1t on 10/21/14.
 */
public class SignupListAdapter extends RecyclerView.Adapter<SignupListAdapter.ViewHolder> implements Filterable {
	private final ArrayList<EighthActivity> mItemList;
	private final LayoutInflater mLayoutInflater;
	private final SignupFragment.OnFragmentInteractionListener mListener;
	private final int[] mColors;
	private final Bitmap ICON_DASH;
	private final Bitmap ICON_LOCK;
	private final Bitmap ICON_STAR;
	private final Bitmap ICON_FAVE;
	private final Bitmap ICON_DONE;
	private EighthActivity[] mItems;

	// View lookup cache
	public static class ViewHolder extends RecyclerView.ViewHolder {
		TextView title;
		TextView room;
		TextView sponsors;
		TextView description;
		ImageView circle;
		ImageView icon;
		ProgressBar capacity;
		RelativeLayout container;

		public ViewHolder(View itemView) {
			super(itemView);
			title = (TextView) itemView.findViewById(R.id.title);
			// If inflating R.layout.row_block
			if (itemView.getId() == R.id.container) {
				room = (TextView) itemView.findViewById(R.id.room);
				sponsors = (TextView) itemView.findViewById(R.id.sponsors);
				description = (TextView) itemView.findViewById(R.id.description);
				circle = (ImageView) itemView.findViewById(R.id.circle);
				icon = (ImageView) itemView.findViewById(R.id.icon);
				capacity = (ProgressBar) itemView.findViewById(R.id.capacity);
				container = (RelativeLayout) itemView.findViewById(R.id.content);
			}
		}
	}

	public enum ActivityHeaderType {
		FAVORITE, SPECIAL, GENERAL
	}

	public enum Colors {
		RED, DEEP_ORANGE, PINK, ORANGE, GREEN, GREY
	}

	public SignupListAdapter(Activity context, EighthActivity[] items) {
		mListener = (SignupFragment.OnFragmentInteractionListener) context;
		if (items == null || items.length < 10) {
			mItemList = new ArrayList<>();
		} else {
			mItemList = new ArrayList<>(items.length);
		}
		mItems = null;
		update(items);
		mLayoutInflater = LayoutInflater.from(context);

		// Cache colors
		final Resources resources = context.getResources();
		mColors = new int[6];
		mColors[Colors.RED.ordinal()] = resources.getColor(R.color.red_400);
		mColors[Colors.DEEP_ORANGE.ordinal()] = resources.getColor(R.color.deep_orange_400);
		mColors[Colors.PINK.ordinal()] = resources.getColor(R.color.pink_400);
		mColors[Colors.ORANGE.ordinal()] = resources.getColor(R.color.orange_400);
		mColors[Colors.GREEN.ordinal()] = resources.getColor(R.color.green_400);
		mColors[Colors.GREY.ordinal()] = resources.getColor(R.color.grey_400);

		// Cache icons
		ICON_DASH = BitmapFactory.decodeResource(resources, R.drawable.ic_remove_white_24dp);
		ICON_LOCK = BitmapFactory.decodeResource(resources, R.drawable.ic_lock_white_24dp);
		ICON_STAR = BitmapFactory.decodeResource(resources, R.drawable.ic_star_white_24dp);
		ICON_FAVE = BitmapFactory.decodeResource(resources, R.drawable.ic_favorite_white_24dp);
		ICON_DONE = BitmapFactory.decodeResource(resources, R.drawable.ic_done_white_24dp);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		return new ViewHolder(mLayoutInflater.inflate(viewType, viewGroup, false));
	}

	@Override
	public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
		final EighthActivity item = mItemList.get(position);
		// Title
		viewHolder.title.setText(item.getName());
		if (viewHolder.getItemViewType() == R.layout.row_header) {
			// Do nothing else
			return;
		}
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
		} else if (item.isFull()) {
			color = Colors.GREY;
			viewHolder.icon.setImageBitmap(ICON_LOCK);
		} else {
			color = Colors.GREEN;
			viewHolder.icon.setImageBitmap(ICON_DONE);
		}
		// Tint icon
		viewHolder.circle.setColorFilter(mColors[color.ordinal()], PorterDuff.Mode.SRC);
		viewHolder.circle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mListener.viewDetails(item);
			}
		});
		viewHolder.container.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mListener.submit(item);
			}
		});
	}

	@Override
	public int getItemViewType(int position) {
		return mItemList.get(position).isHeader() ? R.layout.row_header : R.layout.row_signup;
	}

	@Override
	public int getItemCount() {
		return mItemList.size();
	}

	@Override
	public Filter getFilter() {
		return new Filter() {
			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				mItemList.clear();
				if (results.count != 0) {
					mItemList.addAll((ArrayList<EighthActivity>) results.values);
					addHeaders();
				}
				notifyDataSetChanged();
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				final FilterResults results = new FilterResults();

				// Empty constraints display all items
				final String temp;
				if (constraint == null || (temp = constraint.toString()).isEmpty()) {
					results.values = mItems;
					results.count = mItems.length;
				} else {
					final ArrayList<EighthActivity> FilteredArrayNames = new ArrayList<>();
					final String arg = temp.toLowerCase().replace(" ", "");
					// This should preserve the sort of the items
					for (EighthActivity item : mItems) {
						// Match activity name, and room number todo: match sponsors
						if (!item.isHeader() && (item.getName().toLowerCase().replace(" ", "").contains(arg) ||
								item.getSponsorsNoDelim().toLowerCase().replace(" ", "").contains(arg) ||
								item.getRoomsNoDelim().toLowerCase().replace(" ", "").contains(arg))) {
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

	/**
	 * Sorts mItems, adds headers, and puts them into the view
	 */
	public void update(EighthActivity[] items) {
		if (items != null) {
			mItems = items;
			Arrays.sort(mItems);
			mItemList.clear();
			mItemList.addAll(Arrays.asList(mItems));
			addHeaders();
			notifyDataSetChanged();
		}
	}

	private void addHeaders() {
		ActivityHeaderType index = ActivityHeaderType.FAVORITE;
		EighthActivity item;
		int count = mItems.length;
		for (int i = 0; i < count; i++) {
			item = mItemList.get(i);
			switch (index) {
				case FAVORITE:
					if (item.isFavorite()) {
						mItemList.add(i, new EighthActivity("Favorites", ActivityHeaderType.FAVORITE));
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
							mItemList.add(i, new EighthActivity("Special", ActivityHeaderType.SPECIAL));
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
						mItemList.add(i, new EighthActivity("Activities", ActivityHeaderType.GENERAL));
						return;
					}
					break;
			}
		}
	}

	public EighthActivity get(int position) {
		return mItemList.get(position);
	}
}