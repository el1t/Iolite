package com.el1t.iolite;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.el1t.iolite.adapter.BlockListAdapter;
import com.el1t.iolite.item.EighthActivity;
import com.el1t.iolite.item.EighthBlock;

/**
 * Created by El1t on 10/24/14.
 */
public class BlockFragment extends Fragment
{
	private static final String TAG = "Block Fragment";

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

	public BlockFragment() { }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_block, container, false);

		// Check if list was provided from login activity to setup custom ListAdapter
		final Bundle args = getArguments();
		final EighthBlock[] items;
		if (args != null && (items = (EighthBlock[]) args.getParcelableArray("list")) != null) {
			Log.d(TAG, "Block list received");
			mAdapter = new BlockListAdapter(inflater.getContext(), items, getActivity());
		} else {
			Log.e(TAG, "No list received", new IllegalArgumentException());
		}

		final RecyclerView blockList = (RecyclerView) rootView.findViewById(R.id.list);
		mLayoutManager = new LinearLayoutManager(inflater.getContext());
		blockList.setLayoutManager(mLayoutManager);
		blockList.setItemAnimator(new DefaultItemAnimator());
		blockList.setAdapter(mAdapter);

		// Display menu on long click
//		registerForContextMenu(blockList);

		mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mSwipeRefreshLayout.setRefreshing(true);
				mListener.refresh();
			}
		});
		mSwipeRefreshLayout.setColorSchemeResources(R.color.blue, R.color.red_600,
				R.color.amber, R.color.green_600);

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
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenu.ContextMenuInfo menuInfo) {
		final AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
		final EighthBlock item = (EighthBlock) ((ListView) v).getItemAtPosition(acmi.position);
		if (item.getEighth() != null) {
			getActivity().getMenuInflater().inflate(R.menu.context_menu_block, menu);
			if (item.getEighth().getAID() == 999) {
				menu.findItem(R.id.context_info).setVisible(false);
				menu.findItem(R.id.context_clear).setVisible(false);
			}
			super.onCreateContextMenu(menu, v, menuInfo);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		final EighthBlock blockItem = mAdapter.get(info.position);
		switch (item.getItemId()) {
			case R.id.context_select:
				mListener.select(blockItem.getBID());
				return true;
			case R.id.context_info:
				mListener.viewDetails(blockItem.getEighth());
				return true;
			case R.id.context_clear:
				mListener.clear(blockItem.getBID());
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	void updateAdapter(EighthBlock[] items) {
		mAdapter.update(items);
		mSwipeRefreshLayout.setRefreshing(false);
	}
}