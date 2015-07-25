package com.el1t.iolite;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.el1t.iolite.adapter.BlockListAdapter;
import com.el1t.iolite.item.EighthActivityItem;
import com.el1t.iolite.item.EighthBlockItem;

import java.util.ArrayList;

/**
 * Created by El1t on 10/24/14.
 */
public class BlockFragment extends Fragment
{
	private static final String TAG = "Block Fragment";

	private OnFragmentInteractionListener mListener;
	private BlockListAdapter mAdapter;
	private SwipeRefreshLayout mSwipeRefreshLayout;

	public interface OnFragmentInteractionListener {
		void select(int BID);
		void clear(int BID);
		void refresh();
	}

	public BlockFragment() { }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_list_refresh,
				container, false);

		// Check if list was provided from login activity to setup custom ListAdapter
		final Bundle args = getArguments();
		final ArrayList<EighthBlockItem> items;
		if (args != null && (items = args.getParcelableArrayList("list")) != null) {
			Log.d(TAG, "Block list received");
			mAdapter = new BlockListAdapter(getActivity(), items);
		} else {
			Log.e(TAG, "No list received", new IllegalArgumentException());
		}

		final ListView blockList = (ListView) rootView.findViewById(R.id.list);
		blockList.setAdapter(mAdapter);

		// Select block selection on click
		blockList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
				final EighthBlockItem item = (EighthBlockItem) parent.getItemAtPosition(position);
				mListener.select(item.getBID());
			}
		});

		// Display menu on long click
		registerForContextMenu(blockList);

		mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mSwipeRefreshLayout.setRefreshing(true);
				mListener.refresh();
			}
		});
		mSwipeRefreshLayout.setColorSchemeResources(R.color.blue, R.color.red_600,R.color.amber, R.color.green_600);

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
		final EighthBlockItem item = (EighthBlockItem) ((ListView) v).getItemAtPosition(acmi.position);
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
		final EighthBlockItem blockItem = mAdapter.getItem(info.position);
		switch (item.getItemId()) {
			case R.id.context_select:
				mListener.select(blockItem.getBID());
				return true;
			case R.id.context_info:
				return true;
			case R.id.context_clear:
				mListener.clear(blockItem.getBID());
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	void setListItems(ArrayList<EighthBlockItem> items) {
		mAdapter.setListItems(items);
		mSwipeRefreshLayout.setRefreshing(false);
	}
}