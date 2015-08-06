package com.el1t.iolite;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.el1t.iolite.adapter.ScheduleCardAdapter;
import com.el1t.iolite.item.Schedule;

/**
 * Created by El1t on 12/11/14.
 */
public class ScheduleFragment extends Fragment {
	private static final String TAG = "Schedule Fragment";
	private static final String ARG_SCHEDULES = "schedules";

	private OnFragmentInteractionListener mListener;
	private ScheduleCardAdapter mScheduleCardAdapter;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private LinearLayoutManager mLayoutManager;
	private boolean mLoading;

	public interface OnFragmentInteractionListener {
		void refresh();
		void load();
	}

	public static ScheduleFragment newInstance(Schedule[] schedules) {
		final ScheduleFragment fragment = new ScheduleFragment();
		final Bundle args = new Bundle();
		args.putParcelableArray(ARG_SCHEDULES, schedules);
		fragment.setArguments(args);
		return fragment;
	}

	public ScheduleFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle args = getArguments();
		if (args != null) {
			mScheduleCardAdapter = new ScheduleCardAdapter(getActivity(),
					(Schedule[]) args.getParcelableArray(ARG_SCHEDULES));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);

		mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mSwipeRefreshLayout.setRefreshing(true);
				mListener.refresh();
			}
		});
		mSwipeRefreshLayout.setColorSchemeResources(R.color.blue);
		// TODO: This currently does not show the indicator
//		mSwipeRefreshLayout.setRefreshing(args != null && args.getBoolean("refreshing", false));

		final RecyclerView scheduleList = (RecyclerView) rootView.findViewById(R.id.list);
		mLayoutManager = new LinearLayoutManager(inflater.getContext());
		scheduleList.setLayoutManager(mLayoutManager);
		scheduleList.setItemAnimator(new DefaultItemAnimator());
		scheduleList.setAdapter(mScheduleCardAdapter);
		scheduleList.setOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				final int lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();

				if (!mLoading && lastVisibleItem >= mLayoutManager.getItemCount() - 3) {
					mLoading = true;
					mListener.load();
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

	@Override
	public void onPause() {
		super.onDetach();
		// Stop refreshing animation to fix overlay bug
		mSwipeRefreshLayout.setRefreshing(false);
		mSwipeRefreshLayout.clearAnimation();
	}

	void clear() {
		mScheduleCardAdapter.clear();
	}

	void addSchedules(Schedule[] schedules) {
		mScheduleCardAdapter.addAll(schedules);
	}

	void setRefreshing(boolean refreshing) {
		mLoading = refreshing;
		mSwipeRefreshLayout.setRefreshing(refreshing);
	}

	Schedule getLastDay() {
		return mScheduleCardAdapter.getLastItem();
	}
}