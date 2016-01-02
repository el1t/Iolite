package com.el1t.iolite.ui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.el1t.iolite.R;
import com.el1t.iolite.adapter.SignupAdapter;
import com.el1t.iolite.decoration.ListDecoration;
import com.el1t.iolite.model.EighthActivity;

/**
 * Created by El1t on 10/21/14.
 */
public class SignupFragment extends Fragment {
	private static final String TAG = "Signup Fragment";
	private static final String ARG_ACTIVITIES = "activities";

	private OnFragmentInteractionListener mListener;
	private SignupAdapter mAdapter;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private LinearLayoutManager mLayoutManager;

	public interface OnFragmentInteractionListener {
		void submit(EighthActivity item);
		void viewDetails(EighthActivity activityItem);
		void refresh();
	}

	public static SignupFragment newInstance(EighthActivity[] activities) {
		final SignupFragment fragment = new SignupFragment();
		final Bundle args = new Bundle();
		args.putParcelableArray(ARG_ACTIVITIES, activities);
		fragment.setArguments(args);
		return fragment;
	}

	public SignupFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle args = getArguments();
		if (args != null) {
			mAdapter = new SignupAdapter(getActivity(), (EighthActivity[]) args.getParcelableArray(ARG_ACTIVITIES));
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_signup, container, false);
		final RecyclerView activityList = (RecyclerView) rootView.findViewById(R.id.list);
		mLayoutManager = new LinearLayoutManager(inflater.getContext());
		activityList.addItemDecoration(new ListDecoration(ContextCompat.getDrawable(getActivity(), R.drawable.abc_list_divider_mtrl_alpha)));
		activityList.setLayoutManager(mLayoutManager);
		activityList.setItemAnimator(new DefaultItemAnimator());
		activityList.setAdapter(mAdapter);

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
	public void onAttach(Context context) {
		super.onAttach(context);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mListener = (OnFragmentInteractionListener) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	void updateAdapter(EighthActivity[] items) {
		mAdapter.update(items);
		mSwipeRefreshLayout.setRefreshing(false);
	}

	void filter(String query) {
		mAdapter.getFilter().filter(query);
	}
}