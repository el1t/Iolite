package com.el1t.iolite;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.el1t.iolite.adapter.NewsCardAdapter;
import com.el1t.iolite.decoration.CardDecoration;
import com.el1t.iolite.item.NewsPost;

/**
 * Created by El1t on 8/4/15.
 */
public class NewsFragment extends Fragment {
	private static final String TAG = "Schedule Fragment";
	private static final String ARG_NEWS = "news";

	private OnFragmentInteractionListener mListener;
	private NewsCardAdapter mNewsCardAdapter;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private LinearLayoutManager mLayoutManager;

	public interface OnFragmentInteractionListener {
		void refresh();
		void select(NewsPost post, View view);
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param news The news to display
	 * @return A new instance of fragment NewsFragment.
	 */
	public static NewsFragment newInstance(NewsPost[] news) {
		final NewsFragment fragment = new NewsFragment();
		final Bundle args = new Bundle();
		args.putParcelableArray(ARG_NEWS, news);
		fragment.setArguments(args);
		return fragment;
	}

	public NewsFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle args = getArguments();
		if (args != null) {
			mNewsCardAdapter = new NewsCardAdapter(getActivity(), (NewsPost[]) args.getParcelableArray(ARG_NEWS));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_news, container, false);

		mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mSwipeRefreshLayout.setRefreshing(true);
				mListener.refresh();
			}
		});
		mSwipeRefreshLayout.setColorSchemeResources(R.color.blue);

		final RecyclerView newsList = (RecyclerView) rootView.findViewById(R.id.list);
		mLayoutManager = new LinearLayoutManager(inflater.getContext());
		newsList.addItemDecoration(new CardDecoration());
		newsList.setLayoutManager(mLayoutManager);
		newsList.setItemAnimator(new DefaultItemAnimator());
		newsList.setAdapter(mNewsCardAdapter);

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onPause() {
		super.onDetach();
		// Stop refreshing animation to fix overlay bug
		mSwipeRefreshLayout.setRefreshing(false);
		mSwipeRefreshLayout.clearAnimation();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	void updateAdapter(NewsPost[] news) {
		mNewsCardAdapter.update(news);
		mSwipeRefreshLayout.setRefreshing(false);
	}
}
