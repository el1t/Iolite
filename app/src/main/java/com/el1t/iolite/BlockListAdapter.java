package com.el1t.iolite;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
		mColors[Colors.LIGHT_BLUE.ordinal()] = resources.getColor(R.color.light_blue_400);
		for (int i = 2; i < 6; i++) {
			mColors[i] = resources.getColor(R.color.grey);
		}
		mColors[Colors.RED.ordinal()] = resources.getColor(R.color.accent_400);
		mColors[Colors.DARK_RED.ordinal()] = resources.getColor(R.color.accent_600);
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

			// Format title
			Colors color;
			if (activityItem.getAID() == 999) {
				viewHolder.title.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
				color = Colors.BLACK;
			} else if (activityItem.isCancelled()) {
				viewHolder.title.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC);
				color = Colors.DARK_RED;
			} else {
				viewHolder.title.setTypeface(Typeface.SANS_SERIF);
				color = Colors.TEXT;
			}
			viewHolder.title.setTextColor(mColors[color.ordinal()]);

			// TODO: replace with useful room number
			// viewHolder.room.setText();
			viewHolder.description.setText(blockItem.getEighth().getDescription());

			// Set color
			String letter = blockItem.getBlock();
			if (blockItem.isLocked()) {
				color = Colors.RED;
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

	void sort() {
		Collections.sort(mItems, mComp);
		clear();
		addAll(mItems);
		addHeaders();
		notifyDataSetChanged();
	}

	private void addHeaders() {
		EighthBlockItem item;
		// Dates must be used because blocks can start on any letter
		if (getCount() > 0) {
			Date date = getItem(0).getDate();
			insert(new EighthBlockItem(date), 0);
			int count = getCount();
			for (int i = 1; i < count; i++) {
				item = getItem(i);
				if (!item.getDate().equals(date)) {
					date = item.getDate();
					insert(new EighthBlockItem(date), i);
				}
			}
		}
	}

	void setListItems(ArrayList<EighthBlockItem> items) {
		mItems = items;
		sort();
	}

	// Sort by BID (which also happens to sort by date)
	private class BIDSortComp implements Comparator<EighthBlockItem>
	{
		@Override
		public int compare(EighthBlockItem e1, EighthBlockItem e2) {
			// Double, because Integer does not have compare prior to Java 7
			return Double.compare(e1.getBID(), e2.getBID());
		}
	}
}
