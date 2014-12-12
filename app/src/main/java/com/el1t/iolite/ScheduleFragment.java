package com.el1t.iolite;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.el1t.iolite.adapter.ScheduleListAdapter;
import com.el1t.iolite.item.Schedule;

/**
 * Created by El1t on 12/11/14.
 */
public class ScheduleFragment extends Fragment
{
	private static final String TAG = "Schedule Fragment";

	private OnFragmentInteractionListener mListener;
	private ScheduleListAdapter mScheduleListAdapter;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private Schedule mSchedule;

	public interface OnFragmentInteractionListener {
		public void refresh();
	}

	public ScheduleFragment() { }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_list_refresh, container, false);

		// Check if list was provided from login activity to setup custom ListAdapter
		final Bundle args = getArguments();
		if (args != null && (mSchedule = args.getParcelable("schedule")) != null) {
			Log.d(TAG, "Schedule received");
			mScheduleListAdapter = new ScheduleListAdapter(getActivity(), mSchedule);
		} else {
			Log.e(TAG, "Schedule not received", new IllegalArgumentException());
		}

		final ListView scheduleList = (ListView) rootView.findViewById(R.id.list);
		scheduleList.setAdapter(mScheduleListAdapter);

		mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mSwipeRefreshLayout.setRefreshing(true);
				mListener.refresh();
			}
		});
		mSwipeRefreshLayout.setColorSchemeResources(R.color.blue, R.color.red_600,  R.color.amber, R.color.green_600);

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

	void setSchedule(Schedule schedule) {
		mSchedule = schedule;
		mScheduleListAdapter.setSchedule(schedule);
		mSwipeRefreshLayout.setRefreshing(false);
	}
}
