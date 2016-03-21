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
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.mobstat.StatService;
import com.letv.ugc.common.model.ApiCommonJsonResponse;
import com.letv.ugc.common.model.Information;
import com.letv.ugc.common.utils.JsonUtils;
import com.letvugc.component.core.common.callback.LetvCallback;
import com.letvugc.component.core.util.DebugLog;
import com.lxsj.myheadline.R;
import com.lxsj.myheadline.application.MyHeadlineApplication;
import com.lxsj.myheadline.helpclass.DataGetRequest;
import com.lxsj.myheadline.helpclass.MapBean;
import com.lxsj.myheadline.helpclass.ServerConstants;
import com.lxsj.myheadline.helpclass.UtilMethod;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

public class SplashActivity extends Activity {

	private String name;
	private boolean isFirstOn = true;
	private MyHeadlineApplication application;
	private List<String> categoryList;//
	private Map<String, Map<String, MapBean>> newsData;//
	private long newsVersion;

	// private boolean isThisActivityExit=true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		application = (MyHeadlineApplication) getApplication();

		getWindow().setBackgroundDrawableResource(R.drawable.splashimage);
		PushManager.startWork(getApplicationContext(),
				PushConstants.LOGIN_TYPE_API_KEY, "kYZ6qCGDswvlhkG3wm2GLMQf");
		mMainHandler.sendEmptyMessageDelayed(0, 3000);
		name = application.getName();
		newsVersion = application.getNativeNewsVersion();
		categoryList = application.getCategoryList();
		newsData = new HashMap<String, Map<String, MapBean>>();
		if ("".equalsIgnoreCase(name)) {
			isFirstOn = true;
		} else {
			isFirstOn = false;
		}
		if (isFirstOn) {
//			Log.i(TAG, "first login");
			requestGet("/informations.do");
		} else {
			requestNewsVersion();
		}
	}

	private Handler mMainHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String  bundle=getIntent().getStringExtra("素材信息");
			Intent intent = new Intent();
			if(bundle!=null&&"".equalsIgnoreCase(bundle)){
				intent.putExtra("素材信息", bundle);
//				intent.setClass(getApplication(), HeadLineActivity.class);
//				startActivity(intent);
//				finish();
			}
			if (isFirstOn) {
				intent.setClass(getApplication(), TypeNameActivity.class);
			} else {
				//intent.setClass(getApplication(), HeadLineActivity.class);
				intent.setClass(getApplication(), TabLineActivity.class);
			}
			startActivity(intent);
			finish();
		}
	};

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
								MapBean mapBean = new MapBean();
								mapBean.setTitle(information.getTitle());
								mapBean.setCommentNumber(""
										+ information.getTotalcomment());
								mapBean.setText(information.getContent());
								mapBean.setTitle2(information.getSummary());
								mapBean.setImageUrl(information.getImageurl());
								mapBean.setId(Long.parseLong(information
										.getId().toString()));
								item.put("item" + i, mapBean);
							}
							newsData.put(categoryList.get(j), item);
						}
						saveNewsDataToNative();
					} else {
						Toast.makeText(getApplicationContext(),
								response.getErrMsg().toString(),
								Toast.LENGTH_SHORT).show();
					}

				}
			}
		}, new LetvCallback.OnLetvErrorListener() {

			@Override
			public void onError(Exception error) {
				DebugLog.logErr(UtilMethod.TAG, "" + error.getMessage());

				if (error.getCause() instanceof SocketTimeoutException) {
					Toast.makeText(getApplicationContext(), 
							getResources().getString(R.string.connect_server_timeout),
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(),
							getResources().getString(R.string.internet_exception),
							Toast.LENGTH_SHORT).show();
				}

				if (!isFirstOn) {
					getNewsDataFromNative();
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
			// newsVersion=requestNewsVersion();
			application.setNativeNewsVersion(newsVersion);
			getNewsDataFromNative();

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
			application.setNewsData(newsData);
		} catch (Exception e) {
			Log.i(UtilMethod.TAG, "从本地读取失败" + e.getMessage());
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
					;
					if (response.isRet()) {
						Map<String, Object> map = response.getData();
						newsVersion = Long.parseLong(map.get("version")
								.toString());
						if (newsVersion > application.getNativeNewsVersion()) {
							requestGet("/informations.do");
						} else {
							getNewsDataFromNative();
						}
						application.setNativeNewsVersion(newsVersion);
					} else {
						Toast.makeText(getApplicationContext(),
								response.getErrMsg(), Toast.LENGTH_SHORT)
								.show();
						getNewsDataFromNative();
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
				getNewsDataFromNative();// 
			}
		});

	}

	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StatService.onResume(getApplicationContext());
	}

	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(getApplicationContext());
	}
}