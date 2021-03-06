package com.el1t.iolite.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.el1t.iolite.R;
import com.el1t.iolite.model.NewsPost;
import com.el1t.iolite.util.Utils;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewsDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewsDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsDetailFragment extends Fragment {
	private static final String TAG = "NewsDetailFragment";
	private static final String ARG_NEWS_POST = "post";
	private NewsPost mNewsPost;
	private OnFragmentInteractionListener mListener;

	public interface OnFragmentInteractionListener {

	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param post The post to display.
	 * @return A new instance of fragment NewsDetailFragment.
	 */
	public static NewsDetailFragment newInstance(NewsPost post) {
		final NewsDetailFragment fragment = new NewsDetailFragment();
		final Bundle args = new Bundle();
		args.putParcelable(ARG_NEWS_POST, post);
		fragment.setArguments(args);
		return fragment;
	}

	public NewsDetailFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle args = getArguments();
		if (args != null) {
			mNewsPost = args.getParcelable(ARG_NEWS_POST);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_news_detail, container, false);
		((TextView) rootView.findViewById(R.id.title)).setText(mNewsPost.getTitle());
		((TextView) rootView.findViewById(R.id.author)).setText(mNewsPost.getAuthor());
		((TextView) rootView.findViewById(R.id.date)).setText(mNewsPost.getDateString());
		try {
			((WebView) rootView.findViewById(R.id.web)).loadData("<style>" +
					Utils.inputStreamToString(getActivity().getAssets().open("styles.css")) + "</style>"
					+ mNewsPost.getContent(), "text/html; charset=utf-8", "utf-8");
		} catch (IOException e) {
			Log.e(TAG, "Cannot open styles.css", e);
		}
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
}
