package com.el1t.iolite;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.el1t.iolite.adapter.SignupListAdapter;
import com.el1t.iolite.item.EighthActivity;

/**
 * Created by El1t on 10/21/14.
 */
public class SignupFragment extends Fragment
{
	private static final String TAG = "Signup Fragment";
	private static final String ARG_ACTIVITIES = "activities";

	private OnFragmentInteractionListener mListener;
	private SignupListAdapter mAdapter;
	private SwipeRefreshLayout mSwipeRefreshLayout;

	public interface OnFragmentInteractionListener {
		void submit(EighthActivity item);
		void viewDetails(EighthActivity activityItem);
		void favorite(int AID, int BID, EighthActivity item);
		void refresh();
	}

	public static SignupFragment newInstance(EighthActivity[] activities) {
		final SignupFragment fragment = new SignupFragment();
		final Bundle args = new Bundle();
		args.putParcelableArray(ARG_ACTIVITIES, activities);
		fragment.setArguments(args);
		return fragment;
	}

	public SignupFragment() { }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Check if list was provided to setup custom ListAdapter
		final Bundle args = getArguments();
		if (args != null) {
			mAdapter = new SignupListAdapter(getActivity(), (EighthActivity[]) args.getParcelableArray(ARG_ACTIVITIES));
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_signup, container, false);
		final ListView activityList = (ListView) rootView.findViewById(R.id.activity_list);
		activityList.setAdapter(mAdapter);

		// Submit activity selection on click
		activityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
				final EighthActivity item = (EighthActivity) parent.getItemAtPosition(position);
				mListener.submit(item);
			}
		});

		// Display menu on long click
		registerForContextMenu(activityList);

		mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mSwipeRefreshLayout.setRefreshing(true);
				mListener.refresh();
			}
		});
		mSwipeRefreshLayout.setColorSchemeResources(R.color.blue);

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
		mAdapter.clear();
	}

	@Override
	public void onStart() {
		super.onStart();
		mAdapter.restore();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenuInfo menuInfo) {
		final AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) menuInfo;
		final EighthActivity item = (EighthActivity) ((ListView) v).getItemAtPosition(acmi.position);
		getActivity().getMenuInflater().inflate(R.menu.context_menu_signup, menu);
		if (item.isFavorite()) {
			menu.findItem(R.id.context_favorite).setTitle("Unfavorite");
		}
		if (item.isRestricted() || item.isCancelled() || item.isFull()) {
			menu.findItem(R.id.context_select).setEnabled(false);
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		final EighthActivity activityItem = mAdapter.getItem(info.position);
		switch (item.getItemId()) {
			case R.id.context_select:
				mListener.submit(activityItem);
				return true;
			case R.id.context_info:
				mListener.viewDetails(activityItem);
				return true;
			case R.id.context_favorite:
				mListener.favorite(activityItem.getAID(), activityItem.getBID(), activityItem);
				mAdapter.sort();
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	public void updateAdapter() {
		mAdapter.sort();
	}

	void updateAdapter(EighthActivity[] items) {
		mAdapter.update(items);
		mSwipeRefreshLayout.setRefreshing(false);
	}

	void filter(String query) {
		mAdapter.getFilter().filter(query);
	}
}