package com.el1t.iolite;

import android.content.Context;
import android.graphics.Color;
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
	private BIDSortComp mComp;
	private LayoutInflater mLayoutInflater;

	public enum Block {
		A, B, C, D, E, F
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
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Cache the views for faster performance
		ViewHolder viewHolder;
		EighthBlockItem item = getItem(position);

		if (convertView == null) {
			// Initialize viewHolder and convertView
			viewHolder = new ViewHolder();
			// Save IDs inside ViewHolder and attach the ViewHolder to convertView
			if (item.isHeader()) {
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
		if (item.isHeader()) {
			viewHolder.title.setText(item.getShortenedDisp());
		} else {
			viewHolder.title.setText(item.getEighth().getName());
			// TODO: replace with useful room number
			// viewHolder.room.setText();
			viewHolder.description.setText(item.getEighth().getDescription());

			// Set color
			int color = -1;
			String letter = item.getBlock();
			if (item.isLocked()) {
				color = Color.parseColor("#e51c23");
			} else {
				switch (Block.valueOf(letter)) {
					case A:
						color = Color.parseColor("#00bcd4");        // Cyan
						break;
					case B:
						color = Color.parseColor("#5677fc");        // Blue
						break;
					case C:
					case D:
					case E:
					case F:
						color = Color.parseColor("#9e9e9e");        // Gray
						break;
				}
			}
			// Tint icon
			viewHolder.circle.setColorFilter(color);
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

	public void sort() {
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

	protected void setListItems(ArrayList<EighthBlockItem> items) {
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
