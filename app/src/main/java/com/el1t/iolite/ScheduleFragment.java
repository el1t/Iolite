package com.el1t.iolite;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.el1t.iolite.adapter.ScheduleCardAdapter;
import com.el1t.iolite.item.Schedule;

import java.util.ArrayList;

/**
 * Created by El1t on 12/11/14.
 */
public class ScheduleFragment extends Fragment
{
	private static final String TAG = "Schedule Fragment";

	private OnFragmentInteractionListener mListener;
	private ScheduleCardAdapter mScheduleCardAdapter;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private LinearLayoutManager mLayoutManager;
	private int mTotalItemCount;
	private boolean mLoading;

	public interface OnFragmentInteractionListener {
		public void refresh();
		public void load();
		public void queue(int i);
	}

	public ScheduleFragment() { }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);

		// Check if list was provided from login activity to setup custom ListAdapter
		final Bundle args = getArguments();
		final Schedule schedule;
		if (args != null && (schedule = args.getParcelable("schedule")) != null) {
			Log.d(TAG, "Schedule received");
			final ArrayList<Schedule> list = new ArrayList<>();
			list.add(schedule);
			mScheduleCardAdapter = new ScheduleCardAdapter(getActivity(), list);
		} else {
			Log.e(TAG, "Schedule not received", new IllegalArgumentException());
		}

		mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mSwipeRefreshLayout.setRefreshing(true);
				mListener.refresh();
			}
		});
		mSwipeRefreshLayout.setColorSchemeResources(R.color.blue, R.color.red_600,  R.color.amber, R.color.green_600);
		mSwipeRefreshLayout.setRefreshing(args != null && args.getBoolean("refreshing", false));

		final RecyclerView scheduleList = (RecyclerView) rootView.findViewById(R.id.list);
		mLayoutManager = new LinearLayoutManager(inflater.getContext());
		scheduleList.setLayoutManager(mLayoutManager);
		scheduleList.setItemAnimator(new DefaultItemAnimator());
		scheduleList.setAdapter(mScheduleCardAdapter);
		scheduleList.setOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
			}

			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				final int lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();

				if (!mSwipeRefreshLayout.isRefreshing() && lastVisibleItem >= mTotalItemCount - 2) {
					mTotalItemCount += 5;
					if (mLoading) {
						mListener.queue(5);
					} else {
						mLoading = true;
						mListener.load();
					}
				}
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

	void reset(Schedule schedule) {
		mScheduleCardAdapter.clear();
		mScheduleCardAdapter.addItem(schedule);
		mTotalItemCount = 14;
	}

	void addSchedule(Schedule schedule) {
		mScheduleCardAdapter.addItem(schedule);
	}

	void setRefreshing(boolean refreshing) {
		mLoading = refreshing;
		mSwipeRefreshLayout.setRefreshing(refreshing);
	}

	Schedule getLastDay() {
		return mScheduleCardAdapter.getLastItem();
	}
}