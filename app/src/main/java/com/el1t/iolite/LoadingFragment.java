package com.el1t.iolite;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by El1t on 10/25/14.
 */
public class LoadingFragment extends Fragment
{
	public LoadingFragment() { }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_loading, container, false);
	}
}
