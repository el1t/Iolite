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
		public void finish();
	}

	public SignupFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_signup,
				container, false);

		mActivityListAdapter = new ActivityListAdapter(getActivity(), null);
//		Bundle args = getArguments();
		activityList = (ListView) rootView.findViewById(R.id.activityList);
		activityList.setAdapter(mActivityListAdapter);

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
		mActivityListAdapter.addAll(items);
	}
}
