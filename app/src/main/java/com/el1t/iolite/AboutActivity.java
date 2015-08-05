package com.el1t.iolite;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.el1t.iolite.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by El1t on 11/19/14.
 */
public class AboutActivity extends AppCompatActivity implements AboutFragment.OnFragmentInteractionListener
{
	private final static String TAG = "About Activity";
	private final static String ARG_FRAGMENT = "fragment";
	private final static String ARG_LICENSE_SHOWING = "showing";
	private AboutFragment mAboutFragment;
	private AboutFragment mLicenseFragment;
	private boolean licenseShowing;

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
			licenseShowing = savedInstanceState.getBoolean(ARG_LICENSE_SHOWING);
			if (licenseShowing) {
				mLicenseFragment = (AboutFragment) getFragmentManager().getFragment(savedInstanceState, ARG_FRAGMENT);
			} else {
				mAboutFragment = (AboutFragment) getFragmentManager().getFragment(savedInstanceState, ARG_FRAGMENT);
			}
		}

		// Use material design toolbar
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if(toolbar != null) {
			setSupportActionBar(toolbar);
		}
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putBoolean(ARG_LICENSE_SHOWING, licenseShowing);
		if (licenseShowing) {
			if (mLicenseFragment != null) {
				getFragmentManager().putFragment(savedInstanceState, ARG_FRAGMENT, mLicenseFragment);
			}
		} else if (mAboutFragment != null) {
			getFragmentManager().putFragment(savedInstanceState, ARG_FRAGMENT, mAboutFragment);
		}
	}

	// When back button in actionbar is activated
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		onBackPressed();
		return true;
	}

	@Override
	public void onBackPressed() {
		if (licenseShowing) {
			changeView();
		} else {
			finish();
		}
	}

	public void changeView() {
		if (licenseShowing) {
			getFragmentManager().beginTransaction()
					.replace(R.id.container, mAboutFragment)
					.commit();
		} else {
			if (mLicenseFragment == null) {
				try {
					mLicenseFragment = AboutFragment.newInstance(Utils.inputStreamToString(getAssets().open("license.txt")));
				} catch (IOException e) {
					Log.e(TAG, "Error parsing license", e);
				}
			}
			getFragmentManager().beginTransaction()
					.replace(R.id.container, mLicenseFragment)
					.commit();
		}
		licenseShowing = !licenseShowing;
	}
}