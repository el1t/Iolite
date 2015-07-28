package com.el1t.iolite;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by El1t on 10/20/14.
 */
public class LoginFragment extends Fragment
{
	private OnFragmentInteractionListener mListener;
	private TextInputLayout username;
	private TextInputLayout password;
	private CheckBox remember;

	public interface OnFragmentInteractionListener {
		void submit(String username, String password);
	}

	public enum ErrorType {
		EMPTY_USERNAME, EMPTY_PASSWORD, EMPTY_BOTH, INVALID
	}

	public LoginFragment() { }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_login
				, container, false);

		// Get fields
		username	= (TextInputLayout) rootView.findViewById(R.id.username);
		password	= (TextInputLayout) rootView.findViewById(R.id.password);
		remember    = (CheckBox) rootView.findViewById(R.id.remember);

		// Submit button
		final Button login = (Button) rootView.findViewById(R.id.login);
		login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				submit();
			}
		});
		password.getEditText().setOnEditorActionListener(new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					submit();
					return true;
				}
				return false;
			}
		});

		// Restore remembered username
		final Bundle args = getArguments();
		if (args != null) {
			remember.setChecked(args.getBoolean("remember", false));
			final String name = args.getString("username", null);
			if (name == null) {
				username.requestFocus();
			} else {
				username.getEditText().setText(name);
				password.requestFocus();
			}
		}

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

	private void submit() {
		final String un = username.getEditText().getText().toString().trim();
		final String pw = password.getEditText().getText().toString().trim();
		mListener.submit(un, pw);
	}

	public void showError(ErrorType error) {
		switch(error) {
			case EMPTY_USERNAME:
				username.setError("Empty username");
				password.setErrorEnabled(false);
				break;
			case EMPTY_PASSWORD:
				password.setError("Empty password");
				username.setErrorEnabled(false);
				break;
			case EMPTY_BOTH:
				username.setError("Empty username");
				password.setError("Empty password");
				break;
			case INVALID:
				username.setErrorEnabled(false);
				password.setError("Invalid username or password");
				break;
		}
	}

	public boolean isCreated() {
		return username != null && password != null && remember != null;
	}

	public boolean isChecked() {
		return remember.isChecked();
	}

	public void setChecked(boolean checked) {
		remember.setChecked(checked);
	}
}