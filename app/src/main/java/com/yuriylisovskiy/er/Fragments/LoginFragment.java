package com.yuriylisovskiy.er.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yuriylisovskiy.er.Fragments.Interfaces.IClientFragment;
import com.yuriylisovskiy.er.R;
import com.yuriylisovskiy.er.Services.ClientService.Exceptions.RequestError;
import com.yuriylisovskiy.er.Services.ClientService.IClientService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Objects;

public class LoginFragment extends Fragment implements IClientFragment {

	private TabLayout.Tab tabItem;

	private View progressView;
	private EditText usernameView;
	private EditText passwordView;
	private View loginFormView;
	private CheckBox rememberUser;

	private View userProfile;
	private TextView profileUserName;
	private TextView profileUserEmail;

	private IClientService client;

	private AsyncTask<Void, Void, Boolean> authTask;

	public LoginFragment() {}

	@Override
	public void setClientService(IClientService clientService, Context baseCtx) {
		this.client = clientService;
	}

	public void setArguments(View tabs) {
		this.tabItem = ((TabLayout) tabs).getTabAt(0);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View view = getView();
		if (view != null) {
			Button loginButton = view.findViewById(R.id.sign_in_button);
			loginButton.setOnClickListener(login -> ProcessLogin());
			Button logoutButton = view.findViewById(R.id.sign_out_button);
			logoutButton.setOnClickListener(logout -> ProcessLogout());
			this.progressView = view.findViewById(R.id.login_progress);
			this.usernameView = view.findViewById(R.id.username);
			this.usernameView.requestFocus();
			this.passwordView = view.findViewById(R.id.password);
			this.rememberUser = view.findViewById(R.id.remember_user);
			this.rememberUser.setChecked(true);
			this.loginFormView = view.findViewById(R.id.login_form);

			this.userProfile = view.findViewById(R.id.user_profile);
			this.profileUserName = view.findViewById(R.id.profile_user_name);
			this.profileUserEmail = view.findViewById(R.id.profile_user_email);

		} else {
			Toast.makeText(getContext(), "Error: login fragment's view is null", Toast.LENGTH_SHORT).show();
		}
		showProgress(true, false);
		new GetUserTask(this).execute();
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_sign_in, container, false);
	}

	private void setLoggedInData(boolean isLoggedIn, String username, String email) {
		if (isLoggedIn) {
			this.profileUserName.setText(username);
			this.profileUserEmail.setText(email);
			Objects.requireNonNull(tabItem).setText(username);
		} else {
			Objects.requireNonNull(tabItem).setText(getString(R.string.tab_login));
		}
	}

	private boolean isUserNameValid(String username) {
		//TODO: add password constraints
		return username.length() > 3;
	}

	private boolean isPasswordValid(String password) {
		//TODO: add password constraints
		return password.length() > 5;
	}

	private void ProcessLogin() {
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
			showProgress(true, false);
			this.authTask = new LoginTask(username, password, this.rememberUser.isChecked(), this);
			this.authTask.execute((Void) null);
		}
	}

	private void ProcessLogout() {
		this.showProgress(true, true);
		new LogoutTask(this, getContext()).execute();
	}

	private void showProgress(final boolean show, final boolean isLoggedIn) {
		int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
		if (isLoggedIn) {
			userProfile.setVisibility(show ? View.GONE : View.VISIBLE);
			userProfile.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(
					new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							userProfile.setVisibility(show ? View.GONE : View.VISIBLE);
						}
					}
			);
		} else {
			loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
			loginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(
					new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
						}
					}
			);
		}
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
		private final boolean remember;

		private String profileUsername;
		private String profileEmail;

		private final LoginFragment cls;

		LoginTask(String username, String password, boolean remember, LoginFragment cls) {
			this.username = username;
			this.password = password;
			this.remember = remember;
			this.cls = cls;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean result = true;
			try {
				this.cls.client.Login(this.username, this.password, this.remember);
				JSONObject user = this.cls.client.User();
				this.profileUsername = user.getString("username");
				this.profileEmail = user.getString("email");
			} catch (IOException e) {
				e.printStackTrace();
				result = false;
			} catch (RequestError e) {
				e.printStackTrace();
				result = false;
			} catch (JSONException e) {
				e.printStackTrace();
				result = false;
			}
			return result;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			this.cls.authTask = null;
			if (success) {
				this.cls.setLoggedInData(true, this.profileUsername, this.profileEmail);
			} else {
				this.cls.passwordView.setError(this.cls.getString(R.string.error_incorrect_credentials));
				this.cls.passwordView.requestFocus();
			}
			this.cls.showProgress(false, success);
		}

		@Override
		protected void onCancelled() {
			this.cls.authTask = null;
			this.cls.showProgress(false, false);
		}
	}

	public static class LogoutTask extends AsyncTask<Void, Void, String> {

		private final LoginFragment cls;
		private WeakReference<Context> baseCtx;

		LogoutTask(LoginFragment cls, Context baseCtx) {
			this.cls = cls;
			this.baseCtx = new WeakReference<>(baseCtx);
		}

		@Override
		protected String doInBackground(Void... params) {
			String result = null;
			try {
				this.cls.client.Logout();
			} catch (IOException e) {
				e.printStackTrace();
				result = e.getMessage();
			} catch (RequestError e) {
				e.printStackTrace();
				result = e.getErr();
			}
			return result;
		}

		@Override
		protected void onPostExecute(final String resultMsg) {
			boolean isLoggedIn = false;
			if (resultMsg != null) {
				Toast.makeText(this.baseCtx.get(), resultMsg, Toast.LENGTH_LONG).show();
				isLoggedIn = true;
			} else {
				this.cls.setLoggedInData(false, null, null);
			}
			this.cls.authTask = null;
			this.cls.showProgress(false, isLoggedIn);
		}

		@Override
		protected void onCancelled() {
			this.cls.authTask = null;
			this.cls.showProgress(false, false);
		}
	}

	public static class GetUserTask extends AsyncTask<Void, Void, Boolean> {

		private String username;
		private String email;

		private final LoginFragment cls;

		GetUserTask(LoginFragment cls) {
			this.cls = cls;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean result = false;
			try {
				JSONObject user = this.cls.client.User();
				this.username = user.getString("username");
				this.email = user.getString("email");
				result = true;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (RequestError e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			this.cls.authTask = null;
			this.cls.showProgress(false, success);
			this.cls.setLoggedInData(success, this.username, this.email);
		}

		@Override
		protected void onCancelled() {
			this.cls.authTask = null;
			this.cls.showProgress(false, false);
		}
	}
}