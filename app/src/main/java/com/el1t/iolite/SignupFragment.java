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
import android.widget.ProgressBar;

import java.util.ArrayList;

/**
 * Created by El1t on 10/21/14.
 */
public class SignupFragment extends Fragment
{
	private final String TAG = "Signup Fragment";

	private OnFragmentInteractionListener mListener;
	private ActivityListAdapter mActivityListAdapter;

	public interface OnFragmentInteractionListener {
		public void submit(int AID, int BID);
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
			mActivityListAdapter = new ActivityListAdapter(getActivity(), (ArrayList<EighthActivityItem>) args.getSerializable("list"));
		} else {
			throw new IllegalArgumentException();
		}

		final ListView activityList = (ListView) rootView.findViewById(R.id.activityList);
		activityList.setAdapter(mActivityListAdapter);

		// Submit activity selection on click
		activityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
				final EighthActivityItem item = (EighthActivityItem) parent.getItemAtPosition(position);
				mListener.submit(item.getAID(), item.getBid());
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

	@Override
	public void onStop() {
		super.onPause();
		// This garbage-collects for Android to prevent frame skips
		mActivityListAdapter.clear();
	}

	@Override
	public void onStart() {
		super.onResume();
		mActivityListAdapter.restore();
	}
}