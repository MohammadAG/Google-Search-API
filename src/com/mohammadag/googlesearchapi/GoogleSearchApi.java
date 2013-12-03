package com.mohammadag.googlesearchapi;

import android.content.Context;
import android.content.Intent;

public class GoogleSearchApi {
	public static final String INTENT_NEW_SEARCH = "com.mohammadag.googlesearchapi.NEW_SEARCH";
	public static final String INTENT_REQUEST_SPEAK = "com.mohammadag.googlesearchapi.SPEAK";

	public static final String KEY_VOICE_TYPE = "is_voice_search";
	public static final String KEY_QUERY_TEXT = "query_text";
	public static final String KEY_TEXT_TO_SPEAK = "text_to_speak";

	/* This method allows you to do TTS without implementing your own activity.
	 * Note that this may not work if Google Search is not in the foreground.
	 */
	public static void speak(Context context, String text) {
		Intent intent = new Intent(INTENT_REQUEST_SPEAK);
		intent.putExtra(KEY_TEXT_TO_SPEAK, text);
		context.sendBroadcast(intent);
	}
}
