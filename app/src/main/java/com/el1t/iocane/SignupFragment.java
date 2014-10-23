package com.el1t.iocane;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by El1t on 10/21/14.
 */
public class SignupFragment extends Fragment
{
	private OnFragmentInteractionListener mListener;
	private ListView activityList;
	private ActivityListAdapter mActivityListAdapter;

	public interface OnFragmentInteractionListener {
		public void submit(int AID, int BID);
	}

	public SignupFragment() { }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_signup,
				container, false);

		// Check if list was provided for fake login
		Bundle args = getArguments();
		if (args != null && args.getSerializable("list") != null) {
			mActivityListAdapter = new ActivityListAdapter(getActivity(), (ArrayList<EighthActivityItem>) args.getSerializable("list"));
		}

		// Setup custom ListAdapter
		mActivityListAdapter = new ActivityListAdapter(getActivity(), new ArrayList<EighthActivityItem>());
		activityList = (ListView) rootView.findViewById(R.id.activityList);
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

	public void addAll(ArrayList<EighthActivityItem> items) {
		// Add items to the ListView
		mActivityListAdapter.addAll(items);
	}
}