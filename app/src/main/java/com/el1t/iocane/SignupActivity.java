package com.el1t.iocane;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * Created by El1t on 10/21/14.
 */
public class SignupActivity extends Activity implements SignupFragment.OnFragmentInteractionListener
{
	private SignupFragment mSignupFragment;
	private String AID;
	private String BID;
	private CookieStore mCookieStore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		mSignupFragment = new SignupFragment();
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, mSignupFragment)
					.commit();
		}

		Intent intent = getIntent();
		mCookieStore = new BasicCookieStore();
		ArrayList<SerializedCookie> cookieArray = (ArrayList<SerializedCookie>) intent.getSerializableExtra("cookies");
		for(SerializedCookie c : cookieArray) {
			mCookieStore.addCookie(c.toCookie());
		}

		getList();
	}

	public void submit(int AID, int BID) {

	}

	private void getList() {
		try {
			mSignupFragment.addAll(new EighthActivityXmlParser().parse(new FileInputStream(new File("testActivityList.xml"))));
		} catch(Exception e) {
			Log.e("hi", e.toString());
		}
	}
}