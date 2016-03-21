package com.lxsj.myheadline.activities;

import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.letv.component.upgrade.bean.UpgradeInfo;
import com.letv.component.upgrade.core.upgrade.CheckUpgradeController;
import com.letv.component.upgrade.core.upgrade.UpgradeCallBack;
import com.letv.component.upgrade.core.upgrade.UpgradeManager;
import com.letv.component.upgrade.utils.AppUpgradeConstants;
import com.letv.ugc.common.model.ApiCommonJsonResponse;
import com.letv.ugc.common.utils.JsonUtils;
import com.letvugc.component.core.common.callback.LetvCallback;
import com.letvugc.component.core.util.DebugLog;
import com.lxsj.myheadline.R;
import com.lxsj.myheadline.R.layout;
import com.lxsj.myheadline.application.MyHeadlineApplication;
import com.lxsj.myheadline.helpclass.ServerConstants;
import com.lxsj.myheadline.helpclass.DataGetRequest;
import com.lxsj.myheadline.helpclass.UtilMethod;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class TypeNameActivity extends BaseActivity {
	
	private Button butConfirm;
	private EditText editText;
	private MyHeadlineApplication application;
	private ProgressBar checkNameProgress;
	private String name;
	private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_type_name);
		context=this;
		butConfirm = (Button) findViewById(R.id.button_confirm);
		editText = (EditText) findViewById(R.id.edit_name);
		application = (MyHeadlineApplication) getApplication();
		showSoftTypeCard();// 
		checkUpgrade();
		checkNameProgress=(ProgressBar)findViewById(R.id.typeGrogressBar);
		checkNameProgress.setVisibility(View.GONE);
		butConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String name=editText.getText().toString();
				if(name.replaceAll("[\u4e00-\u9fa5]*[a-z]*[A-Z]*\\d*-*_*\\s*","").length()!=0){ 		        
					UtilMethod.showDialog(context,
							getResources().getString(R.string.name_cannot_use));
			    }else{
			    	//不包含特殊字符 
					if("".equalsIgnoreCase(name)){
						UtilMethod.showDialog(context,
								getResources().getString(R.string.input_alert));
					}else if(name.contains(" ")){
						UtilMethod.showDialog(context,
								getResources().getString(R.string.name_cannot_use));
					}else{
						try {
							name=new String(name.getBytes(),"utf-8");
							name=URLEncoder.encode(name, "UTF-8"); //"UTF-8"
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						requestGet(name);
					}
			    }				
			}
		});
	}

	private void showSoftTypeCard() {
		Timer timer = new Timer();

		timer.schedule(new TimerTask() {
			public void run() {
				InputMethodManager inputManager = (InputMethodManager) editText
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(editText, 0);
			}
		}, 500);
	}

	private void requestGet(String param) {
		checkNameProgress.setVisibility(View.VISIBLE);
		final DataGetRequest request = new DataGetRequest();

		request.setParam(ServerConstants.REQUEST_REPLACE_NAME+ param);
		request.execute(null, new LetvCallback.OnLetvSuccessListener() {

			@Override
			public void onSuccess(Object data) {
				checkNameProgress.setVisibility(View.GONE);
				if (data != null) {
					ApiCommonJsonResponse response = JsonUtils
							.unmarshalFromString(data.toString(),
									ApiCommonJsonResponse.class);
					if(response.isRet()){
						application.setName(editText.getText().toString());
						
						String  bundle=getIntent().getStringExtra("素材信息");
						Intent intent = new Intent();
						if(bundle!=null&&"".equalsIgnoreCase(bundle)){
							intent.putExtra("素材信息", bundle);
//							intent.setClass(getApplication(), HeadLineActivity.class);
//							startActivity(intent);
//							finish();
						}
						intent.setClass(getApplication(), TabLineActivity.class);
						startActivity(intent);
						finish();
					}else{
						UtilMethod.showDialog(context,response.getErrMsg().toString());
					}
				}
			}
		}, new LetvCallback.OnLetvErrorListener() {

			@Override
			public void onError(Exception error) {
//				DebugLog.logErr(TAG, error.getMessage());
				checkNameProgress.setVisibility(View.GONE);
				if(error.getCause() instanceof SocketTimeoutException){
					UtilMethod.showDialog(context,"连接服务器超时");
				}else{
					UtilMethod.showDialog(context,"服务器异常");
				}
			}
		});
	}
	

	
	private void checkUpgrade() {
		String[] channelInfo = AppUpgradeConstants.getChannelInfo(this);
		final String pcode = channelInfo[0];
		final String appkey = channelInfo[1];
		UpgradeManager upgradeManager = UpgradeManager.getInstance();
		upgradeManager.init(this, pcode, true, appkey,
				R.layout.upgrade_dialog_view, R.style.share_dialog_style);
		upgradeManager
				.upgrade(new UpgradeCallBack() {
					@Override
					public void exitApp() {
						finish();
						System.exit(0);
					}


					@Override
					public void setUpgradeState(int upgradeType) {
					}

					@Override
					public void upgradeData(UpgradeInfo info,
							int selectRelatedAppCount, int upgradeState) {
					}

					@Override
					public void setUpgradeType(int upgradeType,
							int downloadState) {
						if (UpgradeInfo.UPTYPE_FORCE == upgradeType) {
							finish();
						}
					}

					@Override
					public void setUpgradeData(UpgradeInfo info) {

					}

					@Override
					public void setUpgradeDialog(int value, UpgradeInfo info) {

					}
				}, CheckUpgradeController.CHECK_BY_SELF,
						AppUpgradeConstants.AUTO_CHECK);

	}

}
