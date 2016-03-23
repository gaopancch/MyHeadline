package com.lxsj.myheadline.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.letv.ugc.common.model.ApiCommonJsonResponse;
import com.letv.ugc.common.model.Information;
import com.letv.ugc.common.utils.JsonUtils;
import com.letvugc.component.core.common.callback.LetvCallback;
import com.letvugc.component.core.util.DebugLog;
import com.lxsj.myheadline.R;
import com.lxsj.myheadline.R.layout;
import com.lxsj.myheadline.application.MyHeadlineApplication;
import com.lxsj.myheadline.helpclass.CategoryAdapter;
import com.lxsj.myheadline.helpclass.DataGetRequest;
import com.lxsj.myheadline.helpclass.MapBean;
import com.lxsj.myheadline.helpclass.ServerConstants;
import com.lxsj.myheadline.helpclass.UtilMethod;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TabLineActivity extends BaseActivity {
	private TabHost tab;
	private ListView showListAll, showListBoy, showListGirl;

	private List<String> categoryList;//
	private Map<String, Map<String, MapBean>> newsData;//
	private MyHeadlineApplication application;
	private long newsVersion = 0;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_line);
		init();
		if (newsData == null) {
			newsData = new HashMap<String, Map<String, MapBean>>();
			requestGet("/informations.do");
		} else {
			setNativeFileDataToListView();
		}
	}

	private void init() {
		tab = (TabHost) findViewById(R.id.tabhost);
		tab.setup();
		context = this;
		application = (MyHeadlineApplication) getApplication();
		categoryList = application.getCategoryList();
		newsData = application.getNewsData();
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		layoutInflater.inflate(R.layout.tab1, tab.getTabContentView());
		layoutInflater.inflate(R.layout.tab2, tab.getTabContentView());// 动态载入XML，而不需要Activity
		layoutInflater.inflate(R.layout.tab3, tab.getTabContentView());
		tab.addTab(tab.newTabSpec("tab1").setIndicator("全部")
				.setContent(R.id.LinearLayoutTabAll));
		tab.addTab(tab.newTabSpec("tab2").setIndicator("男生")
				.setContent(R.id.LinearLayoutBoy));
		tab.addTab(tab.newTabSpec("tab3").setIndicator("女生")
				.setContent(R.id.LinearLayoutGirl));
		showListAll = (ListView) findViewById(R.id.show_list_all);
		showListBoy = (ListView) findViewById(R.id.show_list_boy);
		showListGirl = (ListView) findViewById(R.id.show_list_girl);
	}

	private void setNativeFileDataToListView() {
		setNewsDataToList();
		// PgyUpdateManager.register(this);//
		showListAll.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ListView listView = (ListView) parent;
				String title = (String) listView.getItemAtPosition(position)
						.toString();
				for (int i = 0; i < categoryList.size(); i++) {
					Map<String, MapBean> item = new HashMap<String, MapBean>();
					item = newsData.get(categoryList.get(i).toString());
					for (int n = 0; n < item.size(); n++) {
						MapBean mapBean;
						mapBean = item.get("item" + n);
						if (title.equals(mapBean.getTitle())) {
							Intent intent = new Intent();
							intent.setClass(TabLineActivity.this,
									NewsShowActivity.class);
							Bundle mBundle = new Bundle();
							mBundle.putParcelable("mapBean", mapBean);
							intent.putExtras(mBundle);
							startActivity(intent);
						}
					}
				}
			}
		});

	}

	private void setNewsDataToList() {
		for (int i = 0; i < categoryList.size(); i++) {
			List<String> titleList = new ArrayList<String>();
			List<String> titleListBoy = new ArrayList<String>();
			List<String> titleListGirl = new ArrayList<String>();
			Map<String, MapBean> item = new HashMap<String, MapBean>();
			item = newsData.get(categoryList.get(i).toString());
			for (int n = 0; n < item.size(); n++) {
				MapBean mapBean;// = new MapBean();
				mapBean = item.get("item" + n);
				String title = mapBean.getTitle();
				titleList.add(title);
				switch (mapBean.getAssort()) {
				case 1:
					titleListBoy.add(title);
					break;
				case 2:
					titleListGirl.add(title);
					break;
				default:
					break;
				}
			}

			mCategoryAdapter.addCategory(categoryList.get(i),
					new ArrayAdapter<String>(this,
							R.layout.my_simple_list_item, titleList));
			mCategoryAdapterBoy.addCategory(categoryList.get(i),
					new ArrayAdapter<String>(this,
							R.layout.my_simple_list_item, titleListBoy));
			mCategoryAdapterGirl.addCategory(categoryList.get(i),
					new ArrayAdapter<String>(this,
							R.layout.my_simple_list_item, titleListGirl));
		}
		showListAll.setAdapter(mCategoryAdapter);
		showListBoy.setAdapter(mCategoryAdapterBoy);
		showListGirl.setAdapter(mCategoryAdapterGirl);
	}

	private void requestGet(String param) {

		final DataGetRequest request = new DataGetRequest();
		request.setParam(param);
		request.execute(null, new LetvCallback.OnLetvSuccessListener() {

			@Override
			public void onSuccess(Object data) {
				if (data != null) {
					ApiCommonJsonResponse response = JsonUtils
							.unmarshalFromString(data.toString(),
									ApiCommonJsonResponse.class);
					if (response.isRet()) {
						Map<String, Object> map = response.getData();
						Object informations = map.get("informations");

						Map<String, List<Object>> mapList = new HashMap<String, List<Object>>();

						mapList = JsonUtils.unmarshalFromString(
								informations.toString(), mapList.getClass());
						for (int j = 0; j < categoryList.size(); j++) {
							List<Object> list = mapList.get(categoryList.get(j));
							Map<String, MapBean> item = new HashMap<String, MapBean>();
							for (int i = 0; i < list.size(); i++) {
								Information information = JsonUtils
										.unmarshalFromString(list.get(i)
												.toString(), Information.class);
								item.put("item" + i, 
										UtilMethod.informationToMapBean(information));
							}
							newsData.put(categoryList.get(j), item);
						}
						saveNewsDataToNative();
					} else {
						UtilMethod.showDialog(context, response.getErrMsg()
								.toString());
					}

				}
			}
		}, new LetvCallback.OnLetvErrorListener() {

			@Override
			public void onError(Exception error) {
				DebugLog.logErr(UtilMethod.TAG, "" + error.getMessage());
				if (error.getCause() instanceof SocketTimeoutException) {
					UtilMethod.showDialog(
							context,
							getResources().getString(
									R.string.connect_server_timeout));
				} else {
					UtilMethod.showDialog(
							context,
							getResources().getString(
									R.string.no_internet_connnect));
				}
			}
		});
	}

	private void saveNewsDataToNative() {
		FileOutputStream fileOutputStream = null;
		ObjectOutputStream objectOutputStream = null;
		try {
			File file = new File(Environment.getExternalStorageDirectory()
					+ "/newsDataFile" + File.separator + "newsData.dat");
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}

			if (!file.exists()) {
				file.createNewFile();
			}

			fileOutputStream = new FileOutputStream(file.toString());
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(newsData);
			application.setNewsData(newsData);
			requestNewsVersion();

		} catch (Exception e) {
			Log.i(UtilMethod.TAG, "保存到本地失败" + e.getMessage());
		} finally {
			if (objectOutputStream != null) {
				try {
					objectOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void requestNewsVersion() {
		final DataGetRequest request = new DataGetRequest();
		request.setParam(ServerConstants.REQUEST_VERSION);
		request.execute(null, new LetvCallback.OnLetvSuccessListener() {

			@Override
			public void onSuccess(Object data) {
				if (data != null) {
					ApiCommonJsonResponse response = JsonUtils
							.unmarshalFromString(data.toString(),
									ApiCommonJsonResponse.class);
					if (response.isRet()) {
						Map<String, Object> map = response.getData();
						newsVersion = Long.parseLong(map.get("version")
								.toString());
						application.setNativeNewsVersion(newsVersion);
						if (newsVersion > application.getNativeNewsVersion()) {
							requestGet("/informations.do");
							getNewsDataFromNative();
						}
						getNewsDataFromNative();
					} else {
						UtilMethod.showDialog(context, response.getErrMsg()
								.toString());
					}

				}
			}
		}, new LetvCallback.OnLetvErrorListener() {

			@Override
			public void onError(Exception error) {
				DebugLog.logErr(UtilMethod.TAG, "" + error.getMessage());
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.cannot_get_version),
						Toast.LENGTH_SHORT).show();
				getNewsDataFromNative();
			}
		});
	}

	private void getNewsDataFromNative() {
		FileInputStream fileInputStream = null;
		ObjectInputStream objectInputStream = null;

		try {
			File file = new File(Environment.getExternalStorageDirectory()
					+ "/newsDataFile" + File.separator + "newsData.dat");
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}

			if (!file.exists()) {
				file.createNewFile();
			}
			fileInputStream = new FileInputStream(file.toString());
			objectInputStream = new ObjectInputStream(fileInputStream);
			newsData = null;
			newsData = (Map<String, Map<String, MapBean>>) objectInputStream
					.readObject();
			setNativeFileDataToListView();

		} catch (Exception e) {
			Log.i(UtilMethod.TAG, "获取本地数据失败" + e.getMessage());
		} finally {
			if (objectInputStream != null) {
				try {
					objectInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private CategoryAdapter mCategoryAdapterBoy = new CategoryAdapter() {
		@Override
		protected View getTitleView(String caption, int index,
				View convertView, ViewGroup parent) {
			TextView titleView;

			if (convertView == null) {
				titleView = (TextView) getLayoutInflater().inflate(
						R.layout.headline_category, null);
			} else {
				titleView = (TextView) convertView;
			}
			titleView.setText(caption);

			return titleView;
		}
	};
	
	private CategoryAdapter mCategoryAdapterGirl = new CategoryAdapter() {
		@Override
		protected View getTitleView(String caption, int index,
				View convertView, ViewGroup parent) {
			TextView titleView;

			if (convertView == null) {
				titleView = (TextView) getLayoutInflater().inflate(
						R.layout.headline_category, null);
			} else {
				titleView = (TextView) convertView;
			}
			titleView.setText(caption);

			return titleView;
		}
	};
	
	private CategoryAdapter mCategoryAdapter = new CategoryAdapter() {
		@Override
		protected View getTitleView(String caption, int index,
				View convertView, ViewGroup parent) {
			TextView titleView;

			if (convertView == null) {
				titleView = (TextView) getLayoutInflater().inflate(
						R.layout.headline_category, null);
			} else {
				titleView = (TextView) convertView;
			}
			titleView.setText(caption);

			return titleView;
		}
	};
}
