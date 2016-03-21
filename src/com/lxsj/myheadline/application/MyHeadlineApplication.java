package com.lxsj.myheadline.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.letv.component.upgrade.core.AppDownloadConfiguration;
import com.letv.component.upgrade.core.AppDownloadConfiguration.DBSaveManage;
import com.letv.component.upgrade.core.AppDownloadConfiguration.DataCallbackCategory;
import com.letv.component.upgrade.core.AppDownloadConfiguration.DownloadServiceManage;
import com.letv.component.upgrade.core.service.DownLoadFunction;
import com.letv.component.upgrade.utils.AppUpgradeConstants;
import com.lxsj.myheadline.helpclass.MapBean;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;

public class MyHeadlineApplication extends Application {
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;
	private String name;
	private List<String> categoryList;//
	private Map<String, Map<String, MapBean>> newsData = null;//
	private long nativeNewsVersion;
	private boolean versionFirstSetup = true;

	private AppDownloadConfiguration config;
	public String path;

	@Override
	public void onCreate() {
		super.onCreate();
		initAppDownLoadParams();
		// PgyCrashManager.register(this);
		sharedPreferences = getSharedPreferences("headlinesData", 0);
		editor = sharedPreferences.edit();
		name = sharedPreferences.getString("name", "");
		nativeNewsVersion = sharedPreferences.getLong("version", 0);
		versionFirstSetup = sharedPreferences.getBoolean("versionFirstSetup",
				true);

		categoryList = new ArrayList<String>();
		categoryList.add("春节");
		categoryList.add("春运");
		categoryList.add("娱乐");
		categoryList.add("情感");
		categoryList.add("社会");
		categoryList.add("学校");
		categoryList.add("职场");
		categoryList.add("女生");

	}

	public void setVersionFirstSetupFalse() {
		versionFirstSetup = false;
		editor.putBoolean("versionFirstSetup", false);
		editor.commit();
	}

	public boolean getVersionFirstSetup() {
		return versionFirstSetup;
	}

	public void setNewsData(Map<String, Map<String, MapBean>> data) {
		newsData = data;
	}

	public List<String> getCategoryList() {
		return categoryList;
	}

	public Map<String, Map<String, MapBean>> getNewsData() {
		return newsData;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		editor.putString("name", name);
		editor.commit();
	}

	public void setNativeNewsVersion(long version) {
		nativeNewsVersion = version;
		editor.putLong("version", version);
		editor.commit();
	}

	public long getNativeNewsVersion() {
		return nativeNewsVersion;
	}

	@SuppressWarnings("deprecation")
	private void initAppDownLoadParams() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			path = getDir(
					"updata",
					Context.MODE_WORLD_WRITEABLE | Context.MODE_WORLD_READABLE
							| Context.MODE_PRIVATE).getPath();
		} else {
			path = AppUpgradeConstants.DOWLOAD_LOCATION;
		}

		config = new AppDownloadConfiguration.ConfigurationBuild(
				getApplicationContext())
				.downloadTaskNum(AppUpgradeConstants.DOWNLOAD_JOB_NUM_LIMIT)
				.downloadTaskThreadNum(
						AppUpgradeConstants.DOWNLOAD_RESOURCE_THREAD_COUNT)
				.limitSdcardSize(AppUpgradeConstants.DEFAULT_SDCARD_SIZE)
				.notifyIntentAction(AppUpgradeConstants.NOTIFY_INTENT_ACTION)
				.pathDownload(path)
				.setCallbackCategoty(DataCallbackCategory.BROADCAST)
				.downloadServiceType(DownloadServiceManage.LOCALSERVICE)
				.isOnStartAddTaskToDB(DBSaveManage.START_ADD_TO_DB).build();
		DownLoadFunction.getInstance(getApplicationContext())
				.initDownLoadConfig(config);

	}

	public String getUmengChannelKeyFromManifest() {
		ApplicationInfo appInfo;
		String umengChannelKey = "";
		try {
			appInfo = this.getPackageManager().getApplicationInfo(
					getPackageName(), PackageManager.GET_META_DATA);
			umengChannelKey = appInfo.metaData.getString("UMENG_CHANNEL")
					.trim();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return umengChannelKey;
	}
}