package com.mohammadag.googlesearchapi;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PluginsFragment extends Fragment {
	static class ViewHolder {
		TextView app_name;
		TextView app_package;
		ImageView app_icon;
		int position;
		ApplicationInfo app_info;
	}

	private AppListAdaptor mAdapter;

	private ArrayList<ApplicationInfo> appList = new ArrayList<ApplicationInfo>();
	private ListView mListView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.plugins_fragment, container, false);
		mListView = (ListView) view.findViewById(R.id.listView);
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {			
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				String pkgName = ((TextView) view.findViewById(R.id.app_package)).getText().toString();
				Uri packageUri = Uri.parse(String.format("package:%s", pkgName));
				Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
				startActivity(uninstallIntent);
				return true;
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String pkgName = ((TextView) view.findViewById(R.id.app_package)).getText().toString();

				Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(pkgName);
				if (intent == null) {
					Toast.makeText(getActivity(), R.string.no_ui_plugin, Toast.LENGTH_SHORT).show();
				} else {
					startActivity(intent);
				}
			}
		});

		return view;
	}

	public void handlePackageState(Context context, Intent intent) {
		if (mListView == null)
			return;

		appList.clear();
		new PrepareAppsAdapterTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser && appList.size() == 0) {
			new PrepareAppsAdapterTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	@SuppressLint("DefaultLocale")
	class AppListAdaptor extends ArrayAdapter<ApplicationInfo> {

		@SuppressLint("DefaultLocale")
		public AppListAdaptor(Context context, List<ApplicationInfo> items) {
			super(context, R.layout.app_list_item, new ArrayList<ApplicationInfo>(items));

			appList.addAll(items);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// Load or reuse the view for this row
			View row = convertView;
			if (row == null) {
				row = getActivity().getLayoutInflater().inflate(R.layout.app_list_item, parent, false);
			}

			ApplicationInfo app = appList.get(position);

			if (row.getTag() == null) {
				ViewHolder holder = new ViewHolder();
				holder.app_icon = (ImageView) row.findViewById(R.id.app_icon);        
				holder.app_name = (TextView) row.findViewById(R.id.app_name);
				holder.app_package = (TextView) row.findViewById(R.id.app_package);
				holder.position = position;
				holder.app_info = app;
				row.setTag(holder);
			}

			ViewHolder holder = (ViewHolder) row.getTag();

			holder.app_name.setText(app.name == null ? "" : app.name);
			holder.app_package.setText(app.packageName);
			holder.app_icon.setTag(app.packageName);
			holder.app_icon.setVisibility(View.INVISIBLE);

			new ImageLoader(holder.app_icon, app.packageName).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
					app);

			return row;
		}
	}

	class ImageLoader extends AsyncTask<Object, Void, Drawable> {
		private ImageView imageView;
		private String mPackageName;

		public ImageLoader(ImageView view, String packageName) {
			mPackageName = packageName;
			imageView = view;
		}

		@Override
		protected Drawable doInBackground(Object... params) {
			ApplicationInfo info = (ApplicationInfo) params[0];
			return getActivity().getPackageManager().getApplicationIcon(info);
		}

		@Override
		protected void onPostExecute(Drawable result) {
			super.onPostExecute(result);
			if (imageView.getTag().toString().equals(mPackageName)) {
				imageView.setImageDrawable(result);
				imageView.setVisibility(View.VISIBLE);
			}
		}
	}

	// Handle background loading of apps
	private class PrepareAppsAdapterTask extends AsyncTask<Void,Void,AppListAdaptor> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(mListView.getContext());
			dialog.setMessage("Loading");
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected AppListAdaptor doInBackground(Void... params) {
			if (appList.size() == 0) {
				loadApps(dialog);
			}
			return null;
		}

		@Override
		protected void onPostExecute(final AppListAdaptor result) {
			mAdapter = new AppListAdaptor(getActivity(), appList);
			mListView.setAdapter(mAdapter);

			try {
				dialog.dismiss();
			} catch (Exception e) {

			}
		}
	}

	@SuppressLint("DefaultLocale")
	private void loadApps(ProgressDialog dialog) {
		appList.clear();

		PackageManager pm = getActivity().getPackageManager();
		List<ApplicationInfo> apps = pm.getInstalledApplications(0);
		dialog.setMax(apps.size());
		int i = 1;
		for (ApplicationInfo appInfo : apps) {
			dialog.setProgress(i++);
			if (PackageManager.PERMISSION_GRANTED == pm
					.checkPermission(Constants.PERMISSION, appInfo.packageName)) {

				appInfo.name = appInfo.loadLabel(pm).toString();
				if (!appInfo.packageName.equals("com.mohammadag.googlesearchapi"))
					appList.add(appInfo);
			}
		}
	}
}
