package com.el1t.iolite;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.el1t.iolite.adapter.BlockListAdapter;
import com.el1t.iolite.decoration.ListDecoration;
import com.el1t.iolite.item.EighthActivity;
import com.el1t.iolite.item.EighthBlock;

/**
 * Created by El1t on 10/24/14.
 */
public class BlockFragment extends Fragment {
	private static final String TAG = "Block Fragment";
	private static final String ARG_BLOCKS = "blocks";

	private OnFragmentInteractionListener mListener;
	private BlockListAdapter mAdapter;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private LinearLayoutManager mLayoutManager;

	public interface OnFragmentInteractionListener {
		void select(int BID);
		void viewDetails(EighthActivity activityItem);
		void clear(int BID);
		void refresh();
	}

	public static BlockFragment newInstance(EighthBlock[] blocks) {
		final BlockFragment fragment = new BlockFragment();
		final Bundle args = new Bundle();
		args.putParcelableArray(ARG_BLOCKS, blocks);
		fragment.setArguments(args);
		return fragment;
	}

	public BlockFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle args = getArguments();
		if (args != null) {
			mAdapter = new BlockListAdapter(getActivity(),
					(EighthBlock[]) args.getParcelableArray(ARG_BLOCKS));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_block, container, false);

		final RecyclerView blockList = (RecyclerView) rootView.findViewById(R.id.list);
		mLayoutManager = new LinearLayoutManager(inflater.getContext());
		blockList.addItemDecoration(new ListDecoration(ContextCompat.getDrawable(getActivity(), R.drawable.abc_list_divider_mtrl_alpha)));
		blockList.setLayoutManager(mLayoutManager);
		blockList.setItemAnimator(new DefaultItemAnimator());
		blockList.setAdapter(mAdapter);

		mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mSwipeRefreshLayout.setRefreshing(true);
				mListener.refresh();
			}
		});
		mSwipeRefreshLayout.setColorSchemeResources(R.color.blue);

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

	void updateAdapter(EighthBlock[] items) {
		if (items == null) {
			mSwipeRefreshLayout.setRefreshing(false);
		} else {
			mAdapter.update(items);
		}
	}

	void updateAdapter(EighthActivity[] items) {
		if (items != null) {
			mAdapter.update(items);
		}
		mSwipeRefreshLayout.setRefreshing(false);
	}
}