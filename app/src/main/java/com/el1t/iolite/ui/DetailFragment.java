package com.el1t.iolite.ui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.el1t.iolite.R;
import com.el1t.iolite.adapter.DetailAdapter;
import com.el1t.iolite.decoration.CardDecoration;
import com.el1t.iolite.model.EighthActivity;

/**
 * Activities that contain this fragment must implement the
 * {@link DetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {
	public interface OnFragmentInteractionListener {
	}

	private static final String TAG = "Detail Fragment";
	private static final String ARG_EIGHTH = "eighth";

	private DetailAdapter mAdapter;
	private LinearLayoutManager mLayoutManager;
	private OnFragmentInteractionListener mListener;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param item The initial activity information prior to a more detailed query.
	 * @return A new instance of fragment DetailFragment.
	 */
	public static DetailFragment newInstance(EighthActivity item) {
		final DetailFragment fragment = new DetailFragment();
		final Bundle args = new Bundle();
		args.putParcelable(ARG_EIGHTH, item);
		fragment.setArguments(args);
		return fragment;
	}

	public DetailFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle args = getArguments();
		final EighthActivity eighthActivity;
		if (args != null && (eighthActivity = args.getParcelable(ARG_EIGHTH)) != null) {
			mAdapter = new DetailAdapter(getActivity(), eighthActivity);
		} else {
			Log.e(TAG, "Details not received", new IllegalArgumentException());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
		final RecyclerView activityList = (RecyclerView) rootView.findViewById(R.id.list);
		mLayoutManager = new LinearLayoutManager(inflater.getContext());
		activityList.addItemDecoration(new CardDecoration());
		activityList.setLayoutManager(mLayoutManager);
		activityList.setItemAnimator(new DefaultItemAnimator());
		activityList.setAdapter(mAdapter);
		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mListener = (OnFragmentInteractionListener) getActivity();
		} catch (ClassCastException e) {
			throw new ClassCastException(getActivity().toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public EighthActivity getEighth() {
		return mAdapter.getEighth();
	}

	public void update(EighthActivity detail) {
		mAdapter.update(detail);
	}
}
