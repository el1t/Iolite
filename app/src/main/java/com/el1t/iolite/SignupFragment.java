package com.el1t.iolite;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

import com.el1t.iolite.SignupActivity.Response;

import java.util.ArrayList;

/**
 * Created by El1t on 10/21/14.
 */
public class SignupFragment extends Fragment
{
	private final String TAG = "Signup Fragment";

	private OnFragmentInteractionListener mListener;
	private ActivityListAdapter mAdapter;

	public interface OnFragmentInteractionListener {
		public void submit(EighthActivityItem item);
		public void favorite(int AID, int BID);
	}

	public SignupFragment() { }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_signup,
				container, false);

		// Check if list was provided to setup custom ListAdapter
		final Bundle args = getArguments();
		if (args != null && args.getSerializable("list") != null) {
			Log.d(TAG, "Activity list received");
			mAdapter = new ActivityListAdapter(getActivity(), (ArrayList<EighthActivityItem>) args.getSerializable("list"));
		} else {
			throw new IllegalArgumentException();
		}

		final ListView activityList = (ListView) rootView.findViewById(R.id.activityList);
		activityList.setAdapter(mAdapter);

		// Submit activity selection on click
		activityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
				final EighthActivityItem item = (EighthActivityItem) parent.getItemAtPosition(position);
				mListener.submit(item);
			}
		});

		// Display menu on long click
		registerForContextMenu(activityList);

		return rootView;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.context_menu_signup, menu);
		AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) menuInfo;
		EighthActivityItem item = (EighthActivityItem) ((ListView) v).getItemAtPosition(acmi.position);
		if(item.isFavorite()) {
			menu.findItem(R.id.context_favorite).setTitle("Unfavorite");
		}
		if(item.isRestricted() || item.isCancelled() || item.isFull()) {
			menu.findItem(R.id.context_signup).setEnabled(false);
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		final EighthActivityItem activityItem = mAdapter.getItem(info.position);
		switch (item.getItemId()) {
			case R.id.context_signup:
				mListener.submit(activityItem);
				return true;
			case R.id.context_info:
				return true;
			case R.id.context_favorite:
				mListener.favorite(activityItem.getAID(), activityItem.getBid());
				// Broken, doesn't sort
//				activityItem.changeFavorite();
//				mAdapter.mItems.get(info.position).changeFavorite();
//				mAdapter.notifyDataSetChanged();
				return true;
			default:
				return super.onContextItemSelected(item);
		}
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
	public void onStop() {
		super.onPause();
		// This garbage-collects for Android to prevent frame skips
		mAdapter.clear();
	}

	@Override
	public void onStart() {
		super.onResume();
		mAdapter.restore();
	}
}