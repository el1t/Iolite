package com.el1t.iolite;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.el1t.iolite.item.NewsPost;

public class NewsDetailActivity extends AppCompatActivity implements NewsDetailFragment.OnFragmentInteractionListener {
	private static final String TAG = "NewsDetailActivity";
	private static final String ARG_NEWS_POST = "post";
	private static final String ARG_FRAGMENT = "fragment";
	private NewsDetailFragment mNewsDetailFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_detail);
		final Intent intent = getIntent();

		// Check if restoring from previously destroyed instance that matches the BID
		if (savedInstanceState == null) {
			mNewsDetailFragment = NewsDetailFragment.newInstance(intent.<NewsPost>getParcelableExtra(ARG_NEWS_POST));
			getFragmentManager().beginTransaction()
					.replace(R.id.container, mNewsDetailFragment)
					.commit();
		} else {
			mNewsDetailFragment = (NewsDetailFragment) getFragmentManager().getFragment(savedInstanceState, ARG_FRAGMENT);
		}

		// Use material design toolbar
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null) {
			setSupportActionBar(toolbar);
		}
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		getFragmentManager().putFragment(savedInstanceState, ARG_FRAGMENT, mNewsDetailFragment);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
