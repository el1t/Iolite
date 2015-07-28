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
import com.el1t.iolite.item.EighthActivity;

/**
 * Created by El1t on 7/25/15.
 */
public class DetailCardAdapter extends RecyclerView.Adapter<DetailCardAdapter.ViewHolder>
{
	private final LayoutInflater mLayoutInflater;
	private EighthActivity mEighthActivity;

	public enum Types {
		STATUS("Status"),
		DESCRIPTION("Description"),
		ROOMS("Rooms"),
		SPONSORS("Sponsors");

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

	public DetailCardAdapter(Context context, EighthActivity eighthActivity) {
		mEighthActivity = eighthActivity;
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		final View v = mLayoutInflater.inflate(R.layout.row_detail, viewGroup, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int i) {
		// Set fields
		final Types type = Types.values()[i];
		viewHolder.title.setText(type.toString());
		switch(type) {
			case STATUS:
				break;
			case DESCRIPTION:
				viewHolder.info.setText(mEighthActivity.getDescription());
				break;
			case ROOMS:
				viewHolder.info.setText(mEighthActivity.getRooms());
				break;
			case SPONSORS:
				viewHolder.info.setText(mEighthActivity.getSponsors());
				break;
		}
	}

	@Override
	public int getItemCount() {
		return mEighthActivity == null ? 0 : Types.values().length;
	}

	public void clear() {
		mEighthActivity = null;
		notifyDataSetChanged();
	}

	public void update(EighthActivity detail) {
		mEighthActivity = detail;
		notifyDataSetChanged();
	}

	public EighthActivity getEighth() {
		return mEighthActivity;
	}
}
