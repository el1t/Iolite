package com.el1t.iolite.adapter;

import android.content.Context;
import android.content.res.Resources;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by El1t on 10/24/14.
 */
public class BlockListAdapter extends ArrayAdapter<EighthBlockItem>
{
	private ArrayList<EighthBlockItem> mItems;
	private final BIDSortComp mComp;
	private final LayoutInflater mLayoutInflater;
	private final int[] mColors;

	public enum Block {
		A, B, C, D, E, F
	}

	public enum Colors {
		INDIGO, LIGHT_BLUE, GREY, GREY1, GREY2, RED, DARK_RED, BLACK, TEXT
	}

	// View lookup cache
	private static class ViewHolder {
		TextView title;
		TextView sponsors;
		TextView room;
		TextView description;
		ImageView circle;
		TextView letter;
	}

	public BlockListAdapter(Context context, ArrayList<EighthBlockItem> items) {
		super(context, 0);
		mComp = new BIDSortComp();
		mItems = items;
		sort();

		// Cache colors
		Resources resources = context.getResources();
		mColors = new int[10];
		mColors[Colors.INDIGO.ordinal()] = resources.getColor(R.color.primary_400);
		mColors[Colors.LIGHT_BLUE.ordinal()] = resources.getColor(R.color.accent_400);
		for (int i = 2; i < 6; i++) {
			mColors[i] = resources.getColor(R.color.grey);
		}
		mColors[Colors.RED.ordinal()] = resources.getColor(R.color.red_400);
		mColors[Colors.DARK_RED.ordinal()] = resources.getColor(R.color.red_600);
		mColors[Colors.BLACK.ordinal()] = resources.getColor(R.color.primary_text_default_material_light);
		mColors[Colors.TEXT.ordinal()] = resources.getColor(R.color.secondary_text_default_material_light);

		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Cache the views for faster performance
		ViewHolder viewHolder;
		final EighthBlockItem blockItem = getItem(position);
		final EighthActivityItem activityItem = blockItem.getEighth();

		if (convertView == null) {
			// Initialize viewHolder and convertView
			viewHolder = new ViewHolder();
			// Save IDs inside ViewHolder and attach the ViewHolder to convertView
			if (blockItem.isHeader()) {
				convertView = mLayoutInflater.inflate(R.layout.row_header, parent, false);
				viewHolder.title = (TextView) convertView.findViewById(R.id.headerName);
			} else {
				convertView = mLayoutInflater.inflate(R.layout.row_block, parent, false);
				viewHolder.title = (TextView) convertView.findViewById(R.id.title);
				viewHolder.sponsors = (TextView) convertView.findViewById(R.id.sponsors);
				viewHolder.room = (TextView) convertView.findViewById(R.id.room);
				viewHolder.description = (TextView) convertView.findViewById(R.id.description);
				viewHolder.circle = (ImageView) convertView.findViewById(R.id.circle);
				viewHolder.letter = (TextView) convertView.findViewById(R.id.letter);
			}
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// Set fields
		if (blockItem.isHeader()) {
			// Note: superscript does not work in header
			viewHolder.title.setText(blockItem.getDisp());
		} else {
			viewHolder.title.setText(blockItem.getEighth().getName());

			Colors color;
			final float alpha;
			if (activityItem.getAID() == 999) {
				// Hide empty fields
				viewHolder.sponsors.setVisibility(View.GONE);
				viewHolder.room.setVisibility(View.GONE);
				viewHolder.description.setText("Please select an activity.");

				// Format title
				viewHolder.title.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
				color = Colors.BLACK;
				alpha = 1f;
			} else {
				// Show fields
				if (activityItem.getRoom().isEmpty()) {
					viewHolder.room.setVisibility(View.GONE);
				} else {
					viewHolder.room.setVisibility(View.VISIBLE);
					viewHolder.room.setText(activityItem.getRoom());
				}
				if (activityItem.hasSponsors()) {
					viewHolder.sponsors.setVisibility(View.VISIBLE);
					// Show dash only if needed
					if (viewHolder.room.getVisibility() == View.VISIBLE) {
						viewHolder.sponsors.setText("—" + activityItem.getSponsors());
					} else {
						viewHolder.sponsors.setText(activityItem.getSponsors());
					}
				} else {
					viewHolder.sponsors.setVisibility(View.GONE);
				}
				if (activityItem.hasDescription()) {
					viewHolder.description.setText(activityItem.getDescription());
				} else {
					viewHolder.description.setText("No description.");
				}

				// Format title
				if (activityItem.isCancelled()) {
					viewHolder.title.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
					viewHolder.description.setText("Cancelled!");
					color = Colors.DARK_RED;
					alpha = 1f;
				} else {
					viewHolder.title.setTypeface(Typeface.SANS_SERIF);
					color = Colors.BLACK;
					alpha = .87f;
				}
			}
			viewHolder.title.setTextColor(mColors[color.ordinal()]);
			viewHolder.title.setAlpha(alpha);

			// Set color of circle
			final String letter = blockItem.getBlock();
			if (blockItem.isLocked()) {
				color = Colors.RED;
			} else if (activityItem.isCancelled()) {
				color = Colors.DARK_RED;
			} else {
				switch(Block.valueOf(letter)) {
					case A:
						color = Colors.INDIGO;
						break;
					case B:
						color = Colors.LIGHT_BLUE;
						break;
					default:
						color = Colors.GREY;
				}
			}
			// Tint icon
			viewHolder.circle.setColorFilter(mColors[color.ordinal()]);
			viewHolder.letter.setText(letter);
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

	void sort() {
		Collections.sort(mItems, mComp);
		clear();
		addAll(mItems);
		addHeaders();
		notifyDataSetChanged();
	}

	private void addHeaders() {
		// Count will increment every time a header is added
		int count = getCount();
		if (count > 0) {
			// Dates must be used because blocks can start on any letter
			Date date = getItem(0).getDate();
			Date nextDate;
			insert(new EighthBlockItem(date), 0);
			count++;
			for (int i = 1; i < count; i++) {
				nextDate = getItem(i).getDate();
				if (!nextDate.equals(date)) {
					date = nextDate;
					insert(new EighthBlockItem(date), i++);
					count++;
				}
			}
		}
	}

	public void setListItems(ArrayList<EighthBlockItem> items) {
		mItems = items;
		sort();
	}

	// Sort by block date and type
	private class BIDSortComp implements Comparator<EighthBlockItem>
	{
		@Override
		public int compare(EighthBlockItem e1, EighthBlockItem e2) {
			int cmp = e1.getDate().compareTo(e2.getDate());
			if (cmp != 0) {
				return cmp;
			} else {
				return e1.getBlock().compareTo(e2.getBlock());
			}
		}
	}
}
