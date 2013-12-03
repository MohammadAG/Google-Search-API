package com.mohammadag.googlesearchapi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class IntroFragment extends Fragment {
	private TextView mStatusTextView;
	private Button mToggleActivityVisibilityButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_intro, container, false);	

		mStatusTextView = (TextView) view.findViewById(R.id.status_text);

		if (UiUtils.isHookActive()) {
			setStatusText(R.string.we_have_liftoff);
		} else {
			setStatusText(R.string.we_have_a_problem);
		}

		Drawable icon = UiUtils.getGoogleSearchIcon(getActivity());
		if (icon == null) {
			setStatusText(R.string.google_search_not_installed);
		} else {
			ImageView iconView = (ImageView) view.findViewById(R.id.gsearch_icon);
			iconView.setImageDrawable(icon);
			//getActionBar().setIcon(icon);
		}

		mToggleActivityVisibilityButton = (Button) view.findViewById(R.id.button1);
		mToggleActivityVisibilityButton.setText(UiUtils.getActivityVisibleInDrawer(getActivity()) ? R.string.hide_app : R.string.show_app);
		mToggleActivityVisibilityButton.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View arg0) {
				if (UiUtils.getActivityVisibleInDrawer(getActivity())) {
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setMessage(R.string.how_do_i_get_back_here);
					builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							UiUtils.setActivityVisibleInDrawer(getActivity(), false);
							mToggleActivityVisibilityButton.setText(R.string.show_app);
						}
					}); 
					AlertDialog dialog = builder.create();
					dialog.show();
				} else {
					UiUtils.setActivityVisibleInDrawer(getActivity(), true);
					mToggleActivityVisibilityButton.setText(R.string.hide_app);
				}
			}
		});

		TextView copyrightView = (TextView) view.findViewById(R.id.copyright_text);
		copyrightView.setSelected(true);

		final SharedPreferences preferences =
				PreferenceManager.getDefaultSharedPreferences(getActivity());

		CheckBox delayBroadcasts = (CheckBox) view.findViewById(R.id.delay_broadcasts_checkbox);
		delayBroadcasts.setChecked(preferences.getBoolean(Constants.KEY_DELAY_BROADCASTS, false));
		delayBroadcasts.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				preferences.edit().putBoolean(Constants.KEY_DELAY_BROADCASTS, isChecked).commit();
				getActivity().sendBroadcast(new Intent(Constants.INTENT_SETTINGS_UPDATED));
			}
		});

		CheckBox preventDuplicates = (CheckBox) view.findViewById(R.id.prevent_duplicates);
		preventDuplicates.setChecked(preferences.getBoolean(Constants.KEY_PREVENT_DUPLICATES, false));
		preventDuplicates.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				preferences.edit().putBoolean(Constants.KEY_PREVENT_DUPLICATES, isChecked).commit();
				getActivity().sendBroadcast(new Intent(Constants.INTENT_SETTINGS_UPDATED));
			}
		});
		return view;
	}

	private void setStatusText(int resId) {
		setStatusText(getString(resId));
	}

	private void setStatusText(String text) {
		mStatusTextView.setText(Html.fromHtml(getString(R.string.status_text, text)));
	}
}
