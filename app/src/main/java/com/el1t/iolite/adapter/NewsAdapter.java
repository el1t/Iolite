package com.el1t.iolite.adapter;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.el1t.iolite.ui.NewsFragment;
import com.el1t.iolite.R;
import com.el1t.iolite.model.NewsPost;

/**
 * Created by El1t on 8/4/15.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
	private final LayoutInflater mLayoutInflater;
	private NewsFragment.OnFragmentInteractionListener mListener;
	private NewsPost[] mNewsPosts;

	// View lookup cache
	public static class ViewHolder extends RecyclerView.ViewHolder {
		CardView card;
		TextView title;
		TextView content;
		TextView author;
		TextView date;

		public ViewHolder(View itemView) {
			super(itemView);
			card = (CardView) itemView.findViewById(R.id.card);
			title = (TextView) itemView.findViewById(R.id.title);
			content = (TextView) itemView.findViewById(R.id.content);
			author = (TextView) itemView.findViewById(R.id.author);
			date = (TextView) itemView.findViewById(R.id.date);
		}
	}

	public NewsAdapter(Activity context, NewsPost[] eighthActivity) {
		mListener = (NewsFragment.OnFragmentInteractionListener) context;
		mLayoutInflater = LayoutInflater.from(context);
		mNewsPosts = eighthActivity;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		final View v = mLayoutInflater.inflate(R.layout.row_news, viewGroup, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int position) {
		final NewsPost post = mNewsPosts[position];
		viewHolder.title.setText(post.getTitle());
		viewHolder.content.setText(post.getTrimmedContent());
		viewHolder.author.setText("Posted by: " + post.getAuthor());
		viewHolder.date.setText(post.getDateString());
		viewHolder.card.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mListener.select(post, view);
			}
		});
	}

	@Override
	public int getItemCount() {
		return mNewsPosts == null ? 0 : mNewsPosts.length;
	}

	public void update(NewsPost[] news) {
		if (news != null) {
			mNewsPosts = news;
			notifyDataSetChanged();
		}
	}

	public NewsPost get(int position) {
		return mNewsPosts[position];
	}
}
