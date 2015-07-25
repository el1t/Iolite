package com.el1t.iolite;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.el1t.iolite.adapter.DetailCardAdapter;
import com.el1t.iolite.item.Detail;
import com.el1t.iolite.item.EighthActivityItem;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Activities that contain this fragment must implement the
 * {@link DetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment
{
	private static final String TAG = "Detail Fragment";
	private static final String ARG_ARRAY = "array";

	private DetailCardAdapter mDetailCardAdapter;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private OnFragmentInteractionListener mListener;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param item The initial activity information prior to a more detailed query.
	 * @return A new instance of fragment DetailFragment.
	 */
	public static DetailFragment newInstance(EighthActivityItem item) {
		final DetailFragment fragment = new DetailFragment();
		final Bundle args = new Bundle();
		args.putParcelableArray(ARG_ARRAY, Detail.fromActivity(item));
		fragment.setArguments(args);
		return fragment;
	}

	public DetailFragment() { }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle args = getArguments();
		final Detail[] array;
		if (args != null && (array = (Detail[]) args.getParcelableArray(ARG_ARRAY)) != null) {
			final ArrayList<Detail> list = new ArrayList<>();
			list.addAll(Arrays.asList(array));
			mDetailCardAdapter = new DetailCardAdapter(getActivity(), list);
		} else {
			Log.e(TAG, "Details not received", new IllegalArgumentException());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
		mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mSwipeRefreshLayout.setRefreshing(true);
				mListener.refresh();
			}
		});
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public interface OnFragmentInteractionListener {
		void refresh();
	}

	public ArrayList<Detail> getDetails() {
		return mDetailCardAdapter.getDetails();
	}
}
