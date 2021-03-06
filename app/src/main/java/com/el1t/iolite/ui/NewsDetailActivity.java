package com.el1t.iolite.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.el1t.iolite.R;
import com.el1t.iolite.model.NewsPost;

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

		findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(findViewById(R.id.container), "Sorry, not implemented yet", Snackbar.LENGTH_SHORT).show();
			}
		});

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
