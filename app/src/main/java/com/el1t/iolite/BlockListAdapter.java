package com.el1t.iolite;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by El1t on 10/24/14.
 */
public class BlockListAdapter extends ArrayAdapter<EighthBlockItem>
{
	private ArrayList<EighthBlockItem> mItems;

	public enum Block {
		A, B, C, D, E, F
	}

	// View lookup cache
	private static class ViewHolder {
		TextView blockDate;
		TextView blockActivityName;
		ImageView circle;
		TextView letter;
	}

	public BlockListAdapter(Context context, ArrayList<EighthBlockItem> items) {
		super(context, 0, items);
		mItems = new ArrayList<EighthBlockItem>(items);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Cache the views for faster performance
		ViewHolder viewHolder;
		if (convertView == null) {
			// Initialize viewHolder and convertView
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_block, parent, false);

			// Save IDs inside ViewHolder and attach the ViewHolder to convertView
			viewHolder.blockDate = (TextView) convertView.findViewById(R.id.blockDate);
			viewHolder.blockActivityName = (TextView) convertView.findViewById(R.id.blockActivityName);
			viewHolder.circle = (ImageView) convertView.findViewById(R.id.circle);
			viewHolder.letter = (TextView) convertView.findViewById(R.id.letter);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// Set fields
		EighthBlockItem item = getItem(position);
		viewHolder.blockDate.setText(item.getShortenedDisp());
		viewHolder.blockActivityName.setText(item.getEighth().getName());

		// Set color
		int color = Color.parseColor("#9e9e9e");			// Gray
		String letter = item.getBlock();
		if(item.isLocked()) {
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

		viewHolder.circle.setColorFilter(new
				PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
		viewHolder.letter.setText(letter);

		return convertView;
	}

	protected void setListItems(ArrayList<EighthBlockItem> items) {
		clear();
		addAll(items);
		mItems = items;
		notifyDataSetChanged();
	}

	protected void restore() {
		if(getCount() == 0 && mItems != null) {
			addAll(mItems);
			notifyDataSetChanged();
		}
	}
}
