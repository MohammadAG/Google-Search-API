package com.mohammadag.googlesearchapi;

import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class IntroActivityReceiver extends BroadcastReceiver {

	private static final String TRIGGER_TEXT = "google now api intro";

	@Override
	public void onReceive(Context context, Intent intent) {
		String queryText = intent.getStringExtra(GoogleSearchApi.KEY_QUERY_TEXT);
		if (queryText.toLowerCase(Locale.ENGLISH).contains(TRIGGER_TEXT)) {
			Intent launchIntent = new Intent(context, IntroActivity.class);
			launchIntent.putExtra(GoogleSearchApi.KEY_VOICE_TYPE,
					intent.getBooleanExtra(GoogleSearchApi.KEY_VOICE_TYPE, false));
			launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(launchIntent);
		}
	}

}
