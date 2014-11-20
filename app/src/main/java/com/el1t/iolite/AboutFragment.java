package com.el1t.iolite;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by El1t on 11/19/14.
 */
public class AboutFragment extends Fragment
{
	private AboutListAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_about, container, false);
		final ArrayList<AboutItem> items = new ArrayList<AboutItem>();
		items.add(new AboutItem("Iolite", "©2014 Ellis Tsung"));
		items.add(new AboutItem("Version", "0.7b"));
		items.add(new AboutItem("Intranet", "Iodine (v2)"));
		mAdapter = new AboutListAdapter(getActivity(), items);

		final ListView aboutList = (ListView) rootView.findViewById(R.id.about_list);
		aboutList.setAdapter(mAdapter);

		return rootView;
	}
}
