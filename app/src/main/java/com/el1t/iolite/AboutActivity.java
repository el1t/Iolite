package com.el1t.iolite;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by El1t on 11/19/14.
 */
public class AboutActivity extends ActionBarActivity implements AboutFragment.OnFragmentInteractionListener
{
	private final static String TAG = "About Activity";
	private AboutFragment mAboutFragment;
	private LicenseFragment mLicenseFragment;
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
			mAboutFragment = (AboutFragment) getFragmentManager().getFragment(savedInstanceState, "fragment");
		}

		// Use material design toolbar
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if(toolbar != null) {
			setSupportActionBar(toolbar);
		}
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
				mLicenseFragment = new LicenseFragment();
				mLicenseFragment.setArguments(getLicense());
			}
			getFragmentManager().beginTransaction()
					.replace(R.id.container, mLicenseFragment)
					.commit();
		}
		licenseShowing = !licenseShowing;
	}

	private Bundle getLicense() {
		final Bundle bundle = new Bundle();
		try {
			// Read license into CharSequence
			final InputStream stream = getAssets().open("license.txt");
			final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			final StringBuilder total = new StringBuilder(stream.available());
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				total.append(line).append('\n');
			}
			bundle.putCharSequence("license", total);
		} catch (IOException e) {
			Log.e(TAG, "Error parsing license", e);
		}
		return bundle;
	}
}