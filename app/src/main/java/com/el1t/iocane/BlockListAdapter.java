package com.el1t.iocane;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by El1t on 10/24/14.
 */
public class BlockListAdapter extends ArrayAdapter<EighthBlockItem>
{
	// View lookup cache
	private static class ViewHolder {
		TextView blockDate;
		TextView blockActivityName;
	}

	public BlockListAdapter(Context context, ArrayList<EighthBlockItem> values) {
		super(context, 0, values);
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
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// Set fields
		EighthBlockItem item = getItem(position);
		viewHolder.blockDate.setText(item.getDisp());
		viewHolder.blockActivityName.setText(item.getEighth().getName());

		return convertView;
	}
}
