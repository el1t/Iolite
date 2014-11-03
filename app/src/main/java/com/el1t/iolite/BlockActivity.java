package com.el1t.iolite;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by El1t on 10/24/14.
 */

public class BlockActivity extends Activity implements BlockFragment.OnFragmentInteractionListener
{
	private final String TAG = "Block Activity";

	private BlockFragment mBlockFragment;
	private ArrayList<SerializedCookie> mCookies;
	private boolean fake;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_block);
		Intent intent = getIntent();

		// Check if restoring from previously destroyed instance
		if (savedInstanceState == null) {
			// Retrieve cookies from previous activity
			mCookies = (ArrayList<SerializedCookie>) intent.getSerializableExtra("cookies");

			// Check if fake information should be used
			if (fake = intent.getBooleanExtra("fake", false)) {
				Log.d(TAG, "Loading fake info");
				// Pretend fake list was received
				postRequest(getList());
			}
		}

		// Remove up button
		getActionBar().setDisplayHomeAsUpEnabled(false);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!fake) {
			// Load list of blocks from web
			refresh();
		}
	}

	// Select a BID to display activities for
	public void select(int BID) {
		// Send data to SignupActivity
		Intent intent = new Intent(this, SignupActivity.class);
		intent.putExtra("BID", Integer.toString(BID));
		intent.putExtra("cookies", mCookies);
		intent.putExtra("fake", fake);
		startActivity(intent);
	}

	// Get a fake list of blocks for debugging
	private ArrayList<EighthBlockItem> getList() {
		try {
			return EighthBlockXmlParser.parse(getAssets().open("testBlockList.xml"), getApplicationContext());
		} catch(Exception e) {
			Log.e(TAG, "Error parsing block xml", e);
		}
		return null;
	}

	protected void refresh() {
		// This should not be called if items are fake
		assert(!fake);

		// Set loading fragment
		getFragmentManager().beginTransaction()
				.replace(R.id.container, new LoadingFragment())
				.commit();

		// Retrieve list of bids using cookies
		new BlockListRequest().execute("https://iodine.tjhsst.edu/api/eighth/list_blocks");
	}

	private void postRequest(ArrayList<EighthBlockItem> result) {
		// Sort the array by BID, same as date (for now)
		Collections.sort(result, new BIDSortComp());
		// Check if creating a new fragment is necessary
		// This should probably be done in onCreate, without a bundle
		if (mBlockFragment == null) {
			// Create the content view
			mBlockFragment = new BlockFragment();
			// Add ArrayList to the ListView in BlockFragment
			Bundle args = new Bundle();
			args.putSerializable("list", result);
			mBlockFragment.setArguments(args);
		} else {
			mBlockFragment.updateContent(result);
		}

		// Switch to BlockFragment view, remove LoadingFragment
		getFragmentManager().beginTransaction()
				.replace(R.id.container, mBlockFragment)
				.commit();
	}

	// Get list of blocks
	private class BlockListRequest extends AsyncTask<String, Void, ArrayList<EighthBlockItem>> {
		private static final String TAG = "Block List Connection";

		@Override
		protected ArrayList<EighthBlockItem> doInBackground(String... urls) {
			assert(urls.length == 1);

			HttpURLConnection urlConnection;
			ArrayList<EighthBlockItem> response = null;
			try {
				urlConnection = (HttpURLConnection) new URL(urls[0]).openConnection();
				// Add cookies to header
				for(SerializedCookie cookie : mCookies) {
					urlConnection.setRequestProperty("Cookie", cookie.getName() + "=" + cookie.getValue());
				}
				// Begin connection
				urlConnection.connect();
				// Parse xml from server
				response = EighthBlockXmlParser.parse(urlConnection.getInputStream(), getApplicationContext());
				// Close connection
				urlConnection.disconnect();
			} catch (XmlPullParserException e) {
				Log.e(TAG, "XML error.", e);
			} catch (Exception e) {
				Log.e(TAG, "Connection error.", e);
			}
			return response;
		}

		@Override
		protected void onPostExecute(ArrayList<EighthBlockItem> result) {
			super.onPostExecute(result);
			postRequest(result);
		}
	}
}

// Sort by BID (which also happens to sort by date)
class BIDSortComp implements Comparator<EighthBlockItem>
{
	@Override
	public int compare(EighthBlockItem e1, EighthBlockItem e2) {
		// Double, because Integer does not have compare prior to Java 7
		return Double.compare(e1.getBID(), e2.getBID());
	}
}