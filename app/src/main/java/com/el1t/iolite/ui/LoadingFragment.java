package com.el1t.iolite.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.el1t.iolite.R;

/**
 * Created by El1t on 10/25/14.
 */
public class LoadingFragment extends Fragment {

	public LoadingFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_loading, container, false);
	}
}
