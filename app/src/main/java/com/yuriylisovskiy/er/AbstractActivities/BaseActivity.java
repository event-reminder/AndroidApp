package com.yuriylisovskiy.er.AbstractActivities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.yuriylisovskiy.er.DataAccess.Interfaces.IPreferencesRepository;
import com.yuriylisovskiy.er.DataAccess.Repositories.PreferencesRepository;
import com.yuriylisovskiy.er.Interfaces.INetworkStateListener;
import com.yuriylisovskiy.er.R;
import com.yuriylisovskiy.er.BackgroundService.Receivers.NetworkStateReceiver;
import com.yuriylisovskiy.er.Util.ThemeHelper;

public abstract class BaseActivity extends AppCompatActivity {

	private View _progressBar;

	protected IPreferencesRepository prefs = PreferencesRepository.getInstance();
	protected Integer activityView;
	protected Integer progressBarLayout;
	protected Toolbar toolbar;

	private NetworkStateReceiver _networkStateReceiver;

	protected void setupPreferences(IPreferencesRepository preferencesRepository) {
		this.prefs = preferencesRepository;
	}

	protected void initialSetup() {}

	protected void initLayouts() {
		this.activityView = null;
		this.progressBarLayout = null;
	}

	protected void onCreate() {}

	protected void configureToolBar(Toolbar toolbar) {}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.prefs.Initialize(this);

		this.initialSetup();
		assert this.prefs != null;

		ThemeHelper.setTheme(this.prefs.idDarkTheme());
		ThemeHelper.onActivityCreateSetTheme(this);

		this.initLayouts();

		assert this.activityView != null;
		this.setContentView(this.activityView);
		this.toolbar = this.findViewById(R.id.toolbar);

		this.configureToolBar(this.toolbar);

		this.setSupportActionBar(this.toolbar);
		this.toolbar.setNavigationOnClickListener(bp -> onBackPressed());

		ActionBar actionBar = this.getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowHomeEnabled(true);
		}

		if (this.progressBarLayout != null) {
			this._progressBar = this.findViewById(this.progressBarLayout);
		}

		this.onCreate();

		if (this._progressBar != null) {
			this._progressBar.setVisibility(View.GONE);
		}
	}

	private void toggleProgressBar(boolean show) {
		int shortAnimTime = this.getResources().getInteger(android.R.integer.config_shortAnimTime);
		this._progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
		this._progressBar.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(
			new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					_progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
				}
			}
		);
	}

	protected void showProgressBar() {
		this.toggleProgressBar(true);
	}

	protected void hideProgressBar() {
		this.toggleProgressBar(false);
	}

	protected void registerNetworkStateReceiver(INetworkStateListener networkStateListener) {
		this._networkStateReceiver = new NetworkStateReceiver(networkStateListener);
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		this.registerReceiver(this._networkStateReceiver, filter);
	}

	protected void unregisterNetworkStateReceiver() {
		if (this._networkStateReceiver != null) {
			this.unregisterReceiver(this._networkStateReceiver);
		}
	}
}
