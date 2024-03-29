package com.yuriylisovskiy.er.Util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import com.yuriylisovskiy.er.DataAccess.Interfaces.IPreferencesRepository;

import java.util.Locale;

public class LocaleHelper {

	private static IPreferencesRepository _prefs;

	private LocaleHelper() {}

	public static void Initialize(IPreferencesRepository _prefs) {
		LocaleHelper._prefs = _prefs;
	}

	public static boolean IsInitialized() {
		return LocaleHelper._prefs != null;
	}

	public static Context onAttach(Context context) {
		return LocaleHelper.setLocale(context, _prefs.lang());
	}

	public static Context setLocale(Context context, String language) {
		LocaleHelper._prefs.setLang(language);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			return LocaleHelper.updateResources(context, language);
		}

		return LocaleHelper.updateResourcesLegacy(context, language);
	}

	public static Locale getLocale() {
		return new Locale(LocaleHelper._prefs.lang());
	}

	@TargetApi(Build.VERSION_CODES.N)
	private static Context updateResources(Context context, String language) {
		Locale locale = new Locale(language);
		Locale.setDefault(locale);

		Configuration configuration = context.getResources().getConfiguration();
		configuration.setLocale(locale);

		return context.createConfigurationContext(configuration);
	}

	@SuppressWarnings("deprecation")
	private static Context updateResourcesLegacy(Context context, String language) {
		Locale locale = new Locale(language);
		Locale.setDefault(locale);

		Resources resources = context.getResources();

		Configuration configuration = resources.getConfiguration();
		configuration.locale = locale;

		resources.updateConfiguration(configuration, resources.getDisplayMetrics());

		return context;
	}
}
