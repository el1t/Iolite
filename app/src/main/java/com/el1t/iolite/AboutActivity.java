package com.el1t.iolite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;

/**
 * Created by El1t on 11/19/14.
 */
public class AboutActivity extends ActionBarActivity
{
	private AboutFragment mAboutFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		// Check if restoring from previously destroyed instance
		if (savedInstanceState == null) {
			mAboutFragment = new AboutFragment();
			getFragmentManager().beginTransaction()
					.replace(R.id.container, mAboutFragment)
					.commit();
		} else {
			mAboutFragment = (AboutFragment) getFragmentManager().getFragment(savedInstanceState, "fragment");
		}

		// Use material design toolbar
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if(toolbar != null) {
			setSupportActionBar(toolbar);
		}
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		finish();
		return true;
	}
}
