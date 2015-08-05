package com.el1t.iolite.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.el1t.iolite.BlockFragment;
import com.el1t.iolite.R;
import com.el1t.iolite.item.EighthActivity;
import com.el1t.iolite.item.EighthBlock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by El1t on 10/24/14.
 */
public class BlockListAdapter extends RecyclerView.Adapter<BlockListAdapter.ViewHolder>
{
	private EighthBlock[] mItems;
	private ArrayList<EighthBlock> mDisplayItems;
	private final LayoutInflater mLayoutInflater;
	private final int[] mColors;
	private BlockFragment.OnFragmentInteractionListener mListener;

	public enum Block {
		A, B, C, D, E, F, G, H, I, J
	}

	public enum Colors {
		INDIGO, LIGHT_BLUE, GREY, GREY1, GREY2, RED, DARK_RED, BLACK, TEXT
	}

	// View lookup cache
	public static class ViewHolder extends RecyclerView.ViewHolder {
		TextView title;
		TextView sponsors;
		TextView room;
		TextView description;
		ImageView circle;
		TextView letter;

		public ViewHolder(View itemView) {
			super(itemView);
			title = (TextView) itemView.findViewById(R.id.title);
			sponsors = (TextView) itemView.findViewById(R.id.sponsors);
			room = (TextView) itemView.findViewById(R.id.room);
			description = (TextView) itemView.findViewById(R.id.description);
			circle = (ImageView) itemView.findViewById(R.id.circle);
			letter = (TextView) itemView.findViewById(R.id.letter);
		}
	}

	public BlockListAdapter(Context context, EighthBlock[] items, Activity listener) {
		mListener = (BlockFragment.OnFragmentInteractionListener) listener;
		mDisplayItems = new ArrayList<>();
		update(items);
		mLayoutInflater = LayoutInflater.from(context);

		// Cache colors
		final Resources resources = context.getResources();
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
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		return new ViewHolder(mLayoutInflater.inflate(viewType, viewGroup, false));
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, final int position) {
		final EighthBlock blockItem = mDisplayItems.get(position);
		final EighthActivity activityItem = blockItem.getEighth();

		if (blockItem.isHeader()) {
			// Note: superscript does not work in header
			viewHolder.title.setText(blockItem.getDisp());
		} else {
			Colors color;
			if (activityItem != null) {
				viewHolder.title.setText(activityItem.getName());
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
					if (activityItem.getRooms().isEmpty()) {
						viewHolder.room.setVisibility(View.GONE);
					} else {
						viewHolder.room.setVisibility(View.VISIBLE);
						viewHolder.room.setText(activityItem.getRooms());
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
			}

			// Set color of circle
			final String letter = String.valueOf(blockItem.getBlock());
			if (blockItem.isLocked()) {
				color = Colors.RED;
			} else if (activityItem != null && activityItem.isCancelled()) {
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
			viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					final EighthBlock item = mDisplayItems.get(position);
					mListener.select(item.getBID());
				}
			});
		}
	}

	@Override
	public int getItemViewType(int position) {
		return mDisplayItems.get(position).isHeader() ? R.layout.row_header : R.layout.row_block;
	}

	@Override
	public int getItemCount() {
		return mDisplayItems == null ? 0 : mDisplayItems.size();
	}

//	@Override TODO: set enabled
	public boolean isEnabled(int position) {
		return !mDisplayItems.get(position).isHeader();
	}

	public void update(EighthBlock[] items) {
		mItems = items;
		mDisplayItems.clear();
		mDisplayItems.addAll(Arrays.asList(mItems));
		addHeaders();
		notifyDataSetChanged();
	}

	private void addHeaders() {
		// Count will increment every time a header is added
		int count = mItems.length;
		if (count > 0) {
			// Dates must be used because blocks can start on any letter
			Date date = mDisplayItems.get(0).getDate();
			Date nextDate;
			mDisplayItems.add(0, new EighthBlock(date));
			count++;
			for (int i = 1; i < count; i++) {
				nextDate = mDisplayItems.get(i).getDate();
				if (!nextDate.equals(date)) {
					date = nextDate;
					mDisplayItems.add(i++, new EighthBlock(date));
					count++;
				}
			}
		}
	}

	public EighthBlock get(int i) {
		return mDisplayItems.get(i);
	}
}
