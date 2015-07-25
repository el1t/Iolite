package com.el1t.iolite.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.el1t.iolite.R;
import com.el1t.iolite.item.Detail;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by El1t on 7/25/15.
 */
public class DetailCardAdapter extends RecyclerView.Adapter<DetailCardAdapter.ViewHolder>
{
	private final LayoutInflater mLayoutInflater;
	private ArrayList<Detail> mDetails;

	public enum Types {
		STATUS("Status"),
		DESCRIPTION("Description");

		private String name;

		Types(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	// View lookup cache
	public static class ViewHolder extends RecyclerView.ViewHolder {
		CardView card;
		TextView title;
		TextView info;
		ImageView icon;

		public ViewHolder(View itemView) {
			super(itemView);
			card = (CardView) itemView.findViewById(R.id.card);
			title = (TextView) itemView.findViewById(R.id.title);
			info = (TextView) itemView.findViewById(R.id.info);
			icon = (ImageView) itemView.findViewById(R.id.icon);
		}
	}

	public DetailCardAdapter(Context context, ArrayList<Detail> details) {
		mDetails = details;
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		final View v = mLayoutInflater.inflate(R.layout.row_detail, viewGroup, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int i) {
		final Detail detail = mDetails.get(i);
		// Set fields
		viewHolder.title.setText(detail.getTitle());
		switch(detail.getType()) {
			case STATUS:
				break;
			case DESCRIPTION:
				viewHolder.info.setText(detail.getDescription());
				break;
		}
	}

	@Override
	public int getItemCount() {
		return mDetails == null ? 0 : mDetails.size();
	}

	public void addAll(Detail[] details) {
		mDetails.addAll(Arrays.asList(details));
		notifyItemRangeInserted(mDetails.size() - details.length, details.length);
	}

	public void clear() {
		mDetails.clear();
		notifyDataSetChanged();
	}

	public ArrayList<Detail> getDetails() {
		return mDetails;
	}
}
