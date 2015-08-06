package com.el1t.iolite;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by El1t on 11/19/14.
 */
public class AboutFragment extends Fragment {
	private static final String ARG_LICENSE = "license";
	private OnFragmentInteractionListener mListener;
	private String license;

	public interface OnFragmentInteractionListener {
		void changeView();
	}

	/**
	 * Used for creating a license display
	 *
	 * @param license License to display
	 * @return Fragment with license as bundled argument
	 */
	public static AboutFragment newInstance(String license) {
		final AboutFragment fragment = new AboutFragment();
		final Bundle bundle = new Bundle();
		bundle.putString(ARG_LICENSE, license);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle args = getArguments();
		license = args == null ? null : args.getString(ARG_LICENSE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView;
		if (license == null) {
			rootView = inflater.inflate(R.layout.fragment_about, container, false);
			((TextView) rootView.findViewById(R.id.version)).setText("Version " +
					BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")");
			rootView.findViewById(R.id.license).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mListener.changeView();
				}
			});
		} else {
			rootView = inflater.inflate(R.layout.fragment_license, container, false);
			((TextView) rootView.findViewById(R.id.license)).setText(license);
		}

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