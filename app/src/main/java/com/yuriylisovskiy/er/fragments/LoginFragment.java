package com.yuriylisovskiy.er.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yuriylisovskiy.er.R;
import com.yuriylisovskiy.er.client.Client;
import com.yuriylisovskiy.er.client.exceptions.RequestError;

import java.io.IOException;

public class LoginFragment extends Fragment {

	private View progressView;
	private EditText usernameView;
	private EditText passwordView;
	private View loginFormView;

	private Client client;

	private AsyncTask<Void, Void, Boolean> authTask;

	public LoginFragment() {}

	public void setArguments(Context ctx) {
		this.client = new Client(ctx);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View view = getView();
		if (view != null) {
			Button loginButton = view.findViewById(R.id.sign_in_button);
			loginButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ProcessAuth();
				}
			});
			this.progressView = view.findViewById(R.id.login_progress);
			this.usernameView = view.findViewById(R.id.username);
			this.usernameView.requestFocus();
			this.passwordView = view.findViewById(R.id.password);
			this.loginFormView = view.findViewById(R.id.login_form);


		} else {
			Toast.makeText(getContext(), "Error: login fragment's view is null", Toast.LENGTH_SHORT).show();
		}
	}

	private boolean isUserNameValid(String username) {
		//TODO: Replace this with your own logic
		return username.length() > 4;
	}

	private boolean isPasswordValid(String password) {
		//TODO: Replace this with your own logic
		return password.length() > 4;
	}

	private void ProcessAuth() {
		if (this.authTask != null) {
			return;
		}

		// Reset errors.
		this.usernameView.setError(null);
		this.passwordView.setError(null);

		// Store values at the time of the login attempt.
		String username = this.usernameView.getText().toString();
		String password = this.passwordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password, if the user entered one.
		if (TextUtils.isEmpty(password)) {
			this.passwordView.setError(getString(R.string.error_field_required));
			focusView = this.passwordView;
			cancel = true;
		} else if (!isPasswordValid(password)) {
			this.passwordView.setError(getString(R.string.error_invalid_password));
			focusView = this.passwordView;
			cancel = true;
		}

		// Check for a valid username.
		if (TextUtils.isEmpty(username)) {
			this.usernameView.setError(getString(R.string.error_field_required));
			focusView = this.usernameView;
			cancel = true;
		} else if (!isUserNameValid(username)) {
			this.usernameView.setError(getString(R.string.error_invalid_username));
			focusView = this.usernameView;
			cancel = true;
		}
		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			showProgress(true);
			this.authTask = new LoginTask(username, password, this);
			this.authTask.execute((Void) null);
		}
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_sign_in, container, false);
	}

	private void showProgress(final boolean show) {
		int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
		loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		loginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(
			new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
				}
			}
		);
		this.progressView.setVisibility(show ? View.VISIBLE : View.GONE);
		this.progressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(
			new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					progressView.setVisibility(show ? View.VISIBLE : View.GONE);
				}
			}
		);
	}

	public static class LoginTask extends AsyncTask<Void, Void, Boolean> {

		private final String username;
		private final String password;

		private final LoginFragment cls;

		LoginTask(String username, String password, LoginFragment cls) {
			this.username = username;
			this.password = password;
			this.cls = cls;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				this.cls.client.Login(this.username, this.password, false);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} catch (RequestError e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			this.cls.authTask = null;
			this.cls.showProgress(false);
			if (success) {
				Toast.makeText(this.cls.getContext(), "Signed in as " + username, Toast.LENGTH_SHORT).show();
			} else {
				this.cls.passwordView.setError(this.cls.getString(R.string.error_incorrect_credentials));
				this.cls.passwordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			this.cls.authTask = null;
			this.cls.showProgress(false);
		}
	}
}
