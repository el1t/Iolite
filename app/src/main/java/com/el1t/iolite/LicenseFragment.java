package com.el1t.iolite;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by El1t on 12/31/2014.
 */
public class LicenseFragment extends Fragment
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_license, container, false);
		final Bundle args = getArguments();
		if (args != null) {
			((TextView) rootView.findViewById(R.id.license)).setText(args.getCharSequence("license", ""));
		} else {
			throw new IllegalArgumentException();
		}

		return rootView;
	}
}
