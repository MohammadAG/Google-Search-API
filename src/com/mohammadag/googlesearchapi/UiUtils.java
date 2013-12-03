package com.mohammadag.googlesearchapi;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;

public class UiUtils {
	public static void setActivityVisibleInDrawer(Context context, boolean visible) {
		PackageManager pm = context.getPackageManager();
		pm.setComponentEnabledSetting(new ComponentName(context,
				context.getPackageName() + ".IntroActivity-Alias"), 
				visible ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : 
					PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
				);
	}

	public static boolean getActivityVisibleInDrawer(Context context) {
		PackageManager pm = context.getPackageManager();
		int enabled = pm.getComponentEnabledSetting(new ComponentName(
				context, context.getPackageName() + ".IntroActivity-Alias"));

		switch (enabled) {
		case PackageManager.COMPONENT_ENABLED_STATE_ENABLED:
			return true;
		default:
			return false;
		}
	}

	public static boolean isHookActive() {
		return false;
	}

	public static Drawable getGoogleSearchIcon(Context context) {
		try {
			return context.getPackageManager().getApplicationIcon(Constants.GOOGLE_SEARCH_PACKAGE);
		} catch (NameNotFoundException e) {
			return null;
		}
	}
}
