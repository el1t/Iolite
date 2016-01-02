package com.el1t.iolite.adapter;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.el1t.iolite.ui.BlockFragment;
import com.el1t.iolite.R;
import com.el1t.iolite.model.EighthActivity;
import com.el1t.iolite.model.EighthBlock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by El1t on 10/24/14.
 */
public class BlockAdapter extends RecyclerView.Adapter<BlockAdapter.ViewHolder> {
	private EighthBlock[] mItems;
	private final ArrayList<EighthBlock> mItemList;
	private final LayoutInflater mLayoutInflater;
	private final int[] mColors;
	private final BlockFragment.OnFragmentInteractionListener mListener;

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
		RelativeLayout container;

		public ViewHolder(View itemView) {
			super(itemView);
			title = (TextView) itemView.findViewById(R.id.title);
			// If inflating R.layout.row_block
			if (itemView.getId() == R.id.container) {
				sponsors = (TextView) itemView.findViewById(R.id.sponsors);
				room = (TextView) itemView.findViewById(R.id.room);
				description = (TextView) itemView.findViewById(R.id.description);
				circle = (ImageView) itemView.findViewById(R.id.circle);
				letter = (TextView) itemView.findViewById(R.id.letter);
				container = (RelativeLayout) itemView.findViewById(R.id.container);
			}
		}
	}

	public BlockAdapter(Activity context, EighthBlock[] items) {
		mListener = (BlockFragment.OnFragmentInteractionListener) context;
		mItemList = new ArrayList<>();
		mItems = null;
		update(items);
		mLayoutInflater = LayoutInflater.from(context);

		// Cache colors
		mColors = new int[10];
		mColors[Colors.INDIGO.ordinal()] = ContextCompat.getColor(context, R.color.primary_400);
		mColors[Colors.LIGHT_BLUE.ordinal()] = ContextCompat.getColor(context, R.color.accent_400);
		for (int i = 2; i < 6; i++) {
			mColors[i] = ContextCompat.getColor(context, R.color.grey);
		}
		mColors[Colors.RED.ordinal()] = ContextCompat.getColor(context, R.color.red_400);
		mColors[Colors.DARK_RED.ordinal()] = ContextCompat.getColor(context, R.color.red_600);
		mColors[Colors.BLACK.ordinal()] = ContextCompat.getColor(context, R.color.primary_text_default_material_light);
		mColors[Colors.TEXT.ordinal()] = ContextCompat.getColor(context, R.color.secondary_text_default_material_light);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		return new ViewHolder(mLayoutInflater.inflate(viewType, viewGroup, false));
	}

	@Override
	public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
		final EighthBlock blockItem = mItemList.get(position);
		final EighthActivity activityItem = blockItem.getEighth();

		if (viewHolder.getItemViewType() == R.layout.row_header) {
			// Note: superscript does not work in header
			viewHolder.title.setText(blockItem.getDisp());
			return;
		}
		Colors color;
		if (activityItem == null) {
			viewHolder.title.setText("Loading");
			viewHolder.title.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
			viewHolder.title.setTextColor(mColors[Colors.BLACK.ordinal()]);
			viewHolder.title.setAlpha(.87f);

			viewHolder.sponsors.setVisibility(View.GONE);
			viewHolder.room.setVisibility(View.GONE);
			viewHolder.description.setText("");
			viewHolder.circle.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mListener.select(blockItem.getBID());
				}
			});
		} else {
			final float alpha;
			if (activityItem.isNull()) {
				// Hide empty fields
				viewHolder.sponsors.setVisibility(View.GONE);
				viewHolder.room.setVisibility(View.GONE);
				viewHolder.description.setText("Please select an activity.");

				// Format title
				viewHolder.title.setText("No Activity Selected");
				viewHolder.title.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
				color = Colors.BLACK;
				alpha = 1f;
				viewHolder.circle.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						mListener.select(blockItem.getBID());
					}
				});
			} else {
				viewHolder.title.setText(activityItem.getName());
				// Show fields
				if (activityItem.hasRooms()) {
					viewHolder.room.setVisibility(View.VISIBLE);
					viewHolder.room.setText(activityItem.getRooms());
				} else {
					viewHolder.room.setVisibility(View.GONE);
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

				// Format title
				if (activityItem.isCancelled()) {
					viewHolder.title.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
					viewHolder.description.setText("Cancelled!");
					color = Colors.DARK_RED;
					alpha = 1f;
				} else {
					viewHolder.title.setTypeface(Typeface.SANS_SERIF);
					// Set description
					viewHolder.description.setText(activityItem.getDescription());
					color = Colors.BLACK;
					alpha = .87f;
				}
				viewHolder.circle.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						mListener.viewDetails(activityItem);
					}
				});
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
			switch (Block.valueOf(letter)) {
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
		viewHolder.circle.setColorFilter(mColors[color.ordinal()], PorterDuff.Mode.SRC);
		viewHolder.letter.setText(letter);
		viewHolder.container.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mListener.select(blockItem.getBID());
			}
		});
	}

	@Override
	public int getItemViewType(int position) {
		return mItemList.get(position).isHeader() ? R.layout.row_header : R.layout.row_block;
	}

	@Override
	public int getItemCount() {
		return mItemList.size();
	}

	public void update(EighthBlock[] items) {
		mItems = items;
		mItemList.clear();
		mItemList.addAll(Arrays.asList(mItems));
		addHeaders();
		notifyDataSetChanged();
	}

	public void update(EighthActivity[] items) {
		for (EighthBlock block : mItems) {
			for (EighthActivity activity : items) {
				if (block.getBID() == activity.getBID()) {
					block.setEighth(activity);
					break;
				} else {
					block.setEighth(new EighthActivity.Builder().AID(-1).build());
				}
			}
		}
		notifyDataSetChanged();
	}

	private void addHeaders() {
		// Count will increment every time a header is added
		int count = mItems.length;
		if (count > 0) {
			// Dates must be used because blocks can start on any letter
			Date date = mItemList.get(0).getDate();
			Date nextDate;
			mItemList.add(0, new EighthBlock(date));
			count++;
			for (int i = 1; i < count; i++) {
				nextDate = mItemList.get(i).getDate();
				if (!nextDate.equals(date)) {
					date = nextDate;
					mItemList.add(i++, new EighthBlock(date));
					count++;
				}
			}
		}
	}

	public EighthBlock get(int i) {
		return mItemList.get(i);
	}
}
