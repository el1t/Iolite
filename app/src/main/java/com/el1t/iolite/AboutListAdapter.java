package com.el1t.iolite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by El1t on 11/19/14.
 */
public class AboutListAdapter extends ArrayAdapter<AboutItem>
{
	private final LayoutInflater mLayoutInflater;

	private class ViewHolder {
		TextView title;
		TextView description;
	}

	public AboutListAdapter(Context context, ArrayList<AboutItem> items) {
		super(context, 0, items);
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final AboutItem item = getItem(position);
		final ViewHolder viewHolder;
		if (convertView == null) {
			// Initialize viewHolder and convertView
			viewHolder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.row_about, parent, false);
			viewHolder.title = (TextView) convertView.findViewById(R.id.title);
			viewHolder.description = (TextView) convertView.findViewById(R.id.description);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.title.setText(item.getTitle());
		viewHolder.description.setText(item.getDescription());
		return convertView;
	}
}
