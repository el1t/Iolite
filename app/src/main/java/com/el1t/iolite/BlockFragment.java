package com.el1t.iolite;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by El1t on 10/24/14.
 */
public class BlockFragment extends Fragment {
	private final String TAG = "Block Fragment";

	private OnFragmentInteractionListener mListener;
	private BlockListAdapter mBlockListAdapter;
	private ListView blockList;
	private ArrayList<EighthBlockItem> mItems;

	public interface OnFragmentInteractionListener {
		public void select(int BID);
	}

	public BlockFragment() { }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_block,
				container, false);

		// Check if list was provided from login activity to setup custom ListAdapter
		final Bundle args = getArguments();
		if (args != null && args.getSerializable("list") != null) {
			Log.d(TAG, "Block list received");
			mItems = (ArrayList<EighthBlockItem>) args.getSerializable("list");
			mBlockListAdapter = new BlockListAdapter(getActivity(), mItems);
		} else {
			throw new IllegalArgumentException();
		}

		blockList = (ListView) rootView.findViewById(R.id.blockList);
		blockList.setAdapter(mBlockListAdapter);

		// Select block selection on click
		blockList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
				final EighthBlockItem item = (EighthBlockItem) parent.getItemAtPosition(position);
				mListener.select(item.getBID());
			}
		});

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

	protected void setListItems(ArrayList<EighthBlockItem> items) {
		mBlockListAdapter.setListItems(items);
	}

	@Override
	public void onStop() {
		super.onPause();
		// This garbage-collects for Android to prevent frame skips
		mBlockListAdapter.clear();
	}

	@Override
	public void onStart() {
		super.onResume();
		mBlockListAdapter.restore();
	}
}