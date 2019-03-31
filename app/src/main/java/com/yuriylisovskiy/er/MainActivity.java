package com.yuriylisovskiy.er;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.yuriylisovskiy.er.settings.Prefs;
import com.yuriylisovskiy.er.settings.Theme;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	private CalendarView calendar;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Prefs prefs = new Prefs(this);

		Theme.setTheme(prefs.idDarkTheme());
		Theme.onActivityCreateSetTheme(this);

		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Snackbar.make(
					v, "Replace with your own action", Snackbar.LENGTH_LONG
				).setAction("Action", null).show();
			}
		});
		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		final Switch switchItem = (Switch) navigationView.getMenu().findItem(R.id.nav_switch).getActionView();
		switchItem.setChecked(prefs.idDarkTheme());

		switchItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				setNewTheme(isChecked);
				prefs.setIsDarkTheme(isChecked);
			}
		});

		final SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy", Locale.US);
		final TextView selectedDate = findViewById(R.id.selected_date_label);
		final Calendar calendarInstance = Calendar.getInstance();
		selectedDate.setText(format.format(calendarInstance.getTime()));

		calendar = findViewById(R.id.calendar);
		calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
			@Override
			public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
				calendarInstance.set(year, month, dayOfMonth);
				selectedDate.setText(format.format(calendarInstance.getTime()));
			}
		});

//		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View contentView = inflater.inflate(R.layout.nav_header_main, null, false);
//		drawer.addView(contentView, 0);
	}

	private void setNewTheme(boolean isChecked) {
		Theme.setTheme(isChecked);
		Theme.changeTheme(this);
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);

		menu.findItem(R.id.action_now).setTitle(
			sdf.format(Calendar.getInstance().getTime())
		);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if (id == R.id.action_now) {

			calendar.setDate(Calendar.getInstance().getTimeInMillis(), true, true);

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if (id == R.id.nav_calendar) {
			// TODO: Handle the calendar action
		} else if (id == R.id.nav_settings) {
			// TODO: Handle the settings action
		} else if (id == R.id.nav_backup_and_restore) {
			// TODO: Handle the backup and restore action
		} else if (id == R.id.nav_account) {
			// TODO: Handle the account action
		} else if (id == R.id.nav_about) {
			// TODO: Handle the about action
		} else if (id == R.id.nav_switch) {
			return true;
		}

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}
}
