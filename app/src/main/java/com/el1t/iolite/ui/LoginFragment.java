package com.el1t.iolite.ui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.el1t.iolite.R;

/**
 * Created by El1t on 10/20/14.
 */
public class LoginFragment extends Fragment {
	private static final String ARG_REMEMBER = "remember";
	private static final String ARG_USERNAME = "username";
	private OnFragmentInteractionListener mListener;
	private TextInputLayout usernameInput;
	private TextInputLayout passwordInput;
	private CheckBox rememberBox;
	private String username;
	private boolean remember;

	public interface OnFragmentInteractionListener {
		void submit(String username, String password);
	}

	public enum ErrorType {
		EMPTY_USERNAME, EMPTY_PASSWORD, EMPTY_BOTH, INVALID
	}

	public static LoginFragment newInstance(boolean remember, String username) {
		final LoginFragment fragment = new LoginFragment();
		final Bundle args = new Bundle();
		args.putBoolean(ARG_REMEMBER, remember);
		args.putString(ARG_USERNAME, username);
		fragment.setArguments(args);
		return fragment;
	}

	public LoginFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle args = getArguments();
		if (args != null) {
			remember = args.getBoolean("remember");
			username = args.getString("username");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_login, container, false);

		// Get fields
		usernameInput = (TextInputLayout) rootView.findViewById(R.id.username);
		passwordInput = (TextInputLayout) rootView.findViewById(R.id.password);
		rememberBox = (CheckBox) rootView.findViewById(R.id.remember);

		// Submit button
		final Button login = (Button) rootView.findViewById(R.id.login);
		login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				submit();
			}
		});
		passwordInput.getEditText().setOnEditorActionListener(new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					submit();
					return true;
				}
				return false;
			}
		});

		// Restore stored information
		rememberBox.setChecked(remember);
		if (username == null) {
			usernameInput.requestFocus();
		} else {
			usernameInput.getEditText().setText(username);
			passwordInput.requestFocus();
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

	private void submit() {
		final String un = usernameInput.getEditText().getText().toString().trim();
		final String pw = passwordInput.getEditText().getText().toString().trim();
		mListener.submit(un, pw);
	}

	public void showError(ErrorType error) {
		switch (error) {
			case EMPTY_USERNAME:
				usernameInput.setError("Empty username");
				passwordInput.setErrorEnabled(false);
				break;
			case EMPTY_PASSWORD:
				passwordInput.setError("Empty password");
				usernameInput.setErrorEnabled(false);
				break;
			case EMPTY_BOTH:
				usernameInput.setError("Empty username");
				passwordInput.setError("Empty password");
				break;
			case INVALID:
				usernameInput.setErrorEnabled(false);
				passwordInput.setError("Invalid username or password");
				break;
		}
	}

	public boolean isCreated() {
		return usernameInput != null && passwordInput != null && rememberBox != null;
	}

	public boolean isChecked() {
		return rememberBox.isChecked();
	}

	public void setChecked(boolean checked) {
		rememberBox.setChecked(checked);
	}
}