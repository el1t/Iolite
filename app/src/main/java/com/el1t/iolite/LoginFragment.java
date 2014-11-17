package com.el1t.iolite;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by El1t on 10/20/14.
 */
public class LoginFragment extends Fragment
{
	private OnFragmentInteractionListener mListener;
	private EditText username;
	private EditText password;

	public interface OnFragmentInteractionListener {
		public void submit(String username, String password);
	}

	public LoginFragment() { }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_login
				, container, false);

		// Get fields
		username	= (EditText) rootView.findViewById(R.id.username);
		password	= (EditText) rootView.findViewById(R.id.password);

		// Submit button
		final Button login = (Button) rootView.findViewById(R.id.login);
		login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.submit(username.getText().toString(), password.getText().toString());
			}
		});

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	public void clearPassword() {
		password.setText("");
	}
}
