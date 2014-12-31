package com.el1t.iolite;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by El1t on 11/19/14.
 */
public class AboutFragment extends Fragment
{
	public OnFragmentInteractionListener mListener;

	public interface OnFragmentInteractionListener {
		public void changeView();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_about, container, false);
		((TextView) rootView.findViewById(R.id.version)).setText("Version " + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")");
		rootView.findViewById(R.id.license).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mListener.changeView();
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
}