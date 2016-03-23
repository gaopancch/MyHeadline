package com.lxsj.myheadline.activities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.xml.sax.XMLReader;

import com.baidu.mobstat.StatService;
import com.letv.component.share.ShareManager;
import com.letv.component.userlogin.utils.LetvConstant;
import com.letv.component.userlogin.utils.LoginUtil;
import com.letv.ugc.common.model.ApiCommonJsonResponse;
import com.letv.ugc.common.utils.JsonUtils;
import com.letvugc.component.core.common.callback.LetvCallback;
import com.letvugc.component.core.util.DebugLog;
import com.lxsj.myheadline.R;
import com.lxsj.myheadline.application.MyHeadlineApplication;
import com.lxsj.myheadline.helpclass.ServerConstants;
import com.lxsj.myheadline.helpclass.DataGetRequest;
import com.lxsj.myheadline.helpclass.MapBean;
import com.lxsj.myheadline.helpclass.SystemBarTintManager;
import com.lxsj.myheadline.helpclass.UtilMethod;
//import com.pgyersdk.update.PgyUpdateManager;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler.Response;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.LinearGradient;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.Html.TagHandler;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class NewsShowActivity extends BaseActivity implements Response {

	private TextView title;//
	private TextView time;//
	private TextView commentsNumber;
	private TextView text;//
	private static ImageView image;//
	private MyHeadlineApplication application;
	private String nowName;//
	private Context mContext;
	private ImageButton qqButton;
	private ImageButton weiboButton;
	private ImageButton weixinFriendButton;
	private ImageButton weixinCommentButton;
	private ImageButton backButton;//
	private ImageButton changeNameButton;//
	private IWeiboShareAPI mWeiboShareAPI;
	private MapBean dataMapBean;//
	private String newsText;//
	private String titleString;//
	private static Bitmap textBitmap;
	private OnClickListener buttonClickListener;//
	private int plaformId = 0;// 分享平台 id 1:wechat 2:comment 3:qq 4:weibo
	private ProgressBar checkNameProgress;
	private String name;// 该参数解决改名后乱码问题
	private String secondStr;
	private String imageUrl;
	private boolean isClickTest = false;
	private static int LOAD_IMAGE = 1;
	private Dialog changeDialog;
	private Dialog shareDialog;
	private boolean nameDetectResult = false;
	private boolean secondDetectResult = false;
	private TextView guideText;
	private LinearLayout guideLinearLayout;
	private ImageButton guideButtonCancel;
	private int mFiled = 1;// 需要修改名字或学校等的字段码
	private String secondFiled = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_show);
		mContext = this;
		changeDialog = new Dialog(this);
		shareDialog = new Dialog(this);
		shareDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		changeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		findViews();
		initNewsData();
		// 把QQ分享用到的图片搞到本地
		// saveBitmapToNative(shareBitmap);
		// imageUrl = Environment.getExternalStorageDirectory()
		// + "/newsDataFile" + File.separator + "qqshareimage.png";
		shareInadeately();
	}

	private void initNewsData() {
		dataMapBean = (MapBean) getIntent().getParcelableExtra("mapBean");// get data from former intent
		setDataToActivity(dataMapBean);
		new Thread(new Runnable() {
			@Override
			public void run() {
				textBitmap = returnBitMap(dataMapBean.getImageUrl());
				if (textBitmap == null) {
					textBitmap = BitmapFactory.decodeResource(getResources(),
							R.drawable.mic_launcher);
					image.setImageBitmap(textBitmap);
				}
				// 把QQ分享用到的图片搞到本地
				UtilMethod.saveBitmapToNative(textBitmap);
				imageUrl = Environment.getExternalStorageDirectory()
						+ "/newsDataFile" + File.separator + "qqshareimage.png";
			}
		}).start();
		// 自己选择图片？
		// image.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// Intent intent = new Intent();
		// intent.setClass(NewsShowActivity.this,
		// ImageChoiceActivity.class);
		// startActivity(intent);
		// }
		// });
	}
	
	private void setDataToActivity(MapBean mapBean){
		titleString = dataMapBean.getTitle2().toString()
				.replaceAll(nowName, application.getName().toString());
		dataMapBean.setTitle2(titleString);
		// Html.fromHtml("<font color=\"red\">Hello wold<font>");
		title.setText(titleString);
		time.setText(getDataTime());//
		newsText = dataMapBean.getText().toString().replaceAll(nowName,
						UtilMethod.changeColorAndAddClickable(application
								.getName().toString(), "red"));

		dataMapBean.setText(newsText);
		nowName = application.getName().toString();
		text.setText(Html.fromHtml(newsText, null,
				new ClickableTagHandler(this)));// set with Html.fromHtml to
												// show the color
		text.setClickable(true);
		text.setMovementMethod(LinkMovementMethod.getInstance());
		commentsNumber.setText("评论数：" + dataMapBean.getCommentNumber());
		mFiled = dataMapBean.getFiled();
	}
	
	private void setOnClicListener() {
		// TODO Auto-generated method stub
		buttonClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.back:
					finish();
					break;
				case R.id.changeName:
					changeNameV2(mFiled);
					break;
				case R.id.weixinFriendNow:
				case R.id.weixinFriend:
					plaformId = 1;
					requestGetForTargetUrl();
					break;
				case R.id.commentNow:
				case R.id.comment:
					plaformId = 2;
					requestGetForTargetUrl();
					break;
				case R.id.qqButtonNow:
				case R.id.qqButtom:
					plaformId = 3;
					requestGetForTargetUrl();
					break;
				case R.id.weiboButtonNow:
				case R.id.weiboButton:
					plaformId = 4;
					requestGetForTargetUrl();
					break;
				case R.id.guidText:
				case R.id.guideButtonCancel:
					// guideText.setVisibility(View.INVISIBLE);
					guideLinearLayout.setVisibility(View.INVISIBLE);
					break;
				default:
					break;
				}
			}
		};
		backButton.setOnClickListener(buttonClickListener);
		changeNameButton.setOnClickListener(buttonClickListener);
		qqButton.setOnClickListener(buttonClickListener);
		weiboButton.setOnClickListener(buttonClickListener);
		weixinFriendButton.setOnClickListener(buttonClickListener);
		weixinCommentButton.setOnClickListener(buttonClickListener);
		guideButtonCancel.setOnClickListener(buttonClickListener);
		guideText.setOnClickListener(buttonClickListener);

	}

	private void findViews() {
		backButton = (ImageButton) findViewById(R.id.back);
		changeNameButton = (ImageButton) findViewById(R.id.changeName);
		title = (TextView) findViewById(R.id.textTitle);
		time = (TextView) findViewById(R.id.textTime);
		text = (TextView) findViewById(R.id.textReal);
		guideLinearLayout = (LinearLayout) findViewById(R.id.guideLinearLayout);
		guideText = (TextView) findViewById(R.id.guidText);
		guideButtonCancel = (ImageButton) findViewById(R.id.guideButtonCancel);
		commentsNumber = (TextView) findViewById(R.id.commentNumber);
		image = (ImageView) findViewById(R.id.textImage);
		qqButton = (ImageButton) findViewById(R.id.qqButtom);
		weiboButton = (ImageButton) findViewById(R.id.weiboButton);
		weixinFriendButton = (ImageButton) findViewById(R.id.weixinFriend);
		weixinCommentButton = (ImageButton) findViewById(R.id.comment);
		checkNameProgress = (ProgressBar) findViewById(R.id.newsShowProgressBar);
		checkNameProgress.setVisibility(View.GONE);
		application = (MyHeadlineApplication) getApplication();
		nowName = "\\$姓名\\$";
		setOnClicListener();
	}

	private String getDataTime() {
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy/MM/dd HH:mm:ss ");
		Date curDate = new Date(System.currentTimeMillis());
		return formatter.format(curDate);
	}

	private void setDataMapBeanTopage(MapBean dataMapBean) {
		titleString = dataMapBean.getTitle2().toString()
				.replaceAll(nowName, application.getName().toString());
		title.setText(titleString);
		dataMapBean.setTitle2(titleString);
		newsText = dataMapBean.getText().toString()
				.replaceAll(nowName, application.getName().toString());
		dataMapBean.setText(newsText);//
		nowName = application.getName().toString();
		text.setText(Html.fromHtml(newsText));
	}

	// private void changeName() {
	// LayoutInflater inflater2 = LayoutInflater.from(this);
	// View view = inflater2.inflate(R.layout.change_name_dialog, null);
	// final EditText tv = (EditText) view.findViewById(R.id.changeNameText);
	// Button btSure = (Button) view.findViewById(R.id.butSurChange);
	// Button btCancel = (Button) view.findViewById(R.id.butCancelChange);
	// final Dialog dialog = new Dialog(this);
	// dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	// dialog.setContentView(view);
	// dialog.show();
	// btSure.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	//
	// name = tv.getText().toString();
	// if (name.replaceAll("[\u4e00-\u9fa5]*[a-z]*[A-Z]*\\d*-*_*\\s*",
	// "").length() != 0) {
	// UtilMethod.showDialog(mContext,
	// getResources().getString(R.string.name_cannot_use));
	// } else {
	// // 不包含特殊字符
	// if ("".equalsIgnoreCase(name)) {
	// UtilMethod.showDialog(mContext, getResources()
	// .getString(R.string.input_alert));
	// } else if (name.contains(" ")) {
	// UtilMethod.showDialog(mContext, getResources()
	// .getString(R.string.name_cannot_use));
	// } else {
	// try {
	// String nameRequest = new String(name.getBytes(),
	// "utf-8");
	// nameRequest = URLEncoder.encode(name, "UTF-8"); // "UTF-8"
	// requestGet(nameRequest, dialog);
	// } catch (UnsupportedEncodingException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }
	// }
	// });
	//
	// btCancel.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// dialog.dismiss();
	// }
	// });
	// }

	private void changeNameV2(final int filed) {
		LayoutInflater inflater2 = LayoutInflater.from(this);
		View view = inflater2.inflate(R.layout.v2change_name_dialog, null);
		final EditText etvName = (EditText) view
				.findViewById(R.id.changeNameText);
		final EditText etvSecond = (EditText) view
				.findViewById(R.id.changeSecondText);
		final TextView tvNameAlert = (TextView) view
				.findViewById(R.id.nameAlertText);
		final TextView tvSecondAlert = (TextView) view
				.findViewById(R.id.secondAlertText);
		final TextView secondTextTitle = (TextView) view
				.findViewById(R.id.secondText);
		Button btSure = (Button) view.findViewById(R.id.butSurChange);
		changeDialog.getWindow().setBackgroundDrawableResource(
				R.drawable.circle_bg);
		changeDialog.setContentView(view);
		changeDialog.show();
		setSecondFiled(filed,etvSecond,secondTextTitle,tvSecondAlert);
		btSure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				nameDetectResult = false;
				secondDetectResult = false;
				switch (filed) {
				case 1:// just name filed
					name = etvName.getText().toString();
					tvNameAlert.setText("");//
					detectStringFromServer(name, tvNameAlert, nameDetectResult,
							0);
					break;
				case 12:// name + school
				case 13:
				case 14:
				case 15:
					secondStr = etvSecond.getText().toString();
					tvSecondAlert.setText("");
					detectStringFromServer(secondStr, tvSecondAlert,
							secondDetectResult, 1);
					name = etvName.getText().toString();
					tvNameAlert.setText("");//
					detectStringFromServer(name, tvNameAlert, nameDetectResult,
							0);
					break;
				default:
					break;
				}

			}
		});
	}
	
	private void setSecondFiled(int filed,EditText eText,TextView titleText,TextView alert){
		switch (filed) {
		case 1:// just name filed
			eText.setVisibility(View.GONE);
			alert.setVisibility(View.GONE);
			titleText.setVisibility(View.GONE);
			break;
		case 12:// name + school
			secondFiled = "学校：";
			break;
		case 13:// name + school
			secondFiled = "公司：";
			break;
		case 14:// name + school
			secondFiled = "家乡：";
			break;
		case 15:// name + school
			secondFiled = "？？：";
			break;
		default:
			break;
			
		}
		titleText.setText(secondFiled);
	}
	
	private void shareInadeately() {
		LayoutInflater inflater2 = LayoutInflater.from(this);
		View view = inflater2.inflate(R.layout.share_imadelately, null);
		ImageButton btWX = (ImageButton) view
				.findViewById(R.id.weixinFriendNow);
		ImageButton btWxComment = (ImageButton) view
				.findViewById(R.id.commentNow);
		ImageButton btQq = (ImageButton) view.findViewById(R.id.qqButtonNow);
		ImageButton btSina = (ImageButton) view
				.findViewById(R.id.weiboButtonNow);
		shareDialog.getWindow().setBackgroundDrawableResource(
				R.drawable.circle_bg);
		shareDialog.setContentView(view);
		shareDialog.show();
		btWX.setOnClickListener(buttonClickListener);
		btWxComment.setOnClickListener(buttonClickListener);
		btSina.setOnClickListener(buttonClickListener);
		btQq.setOnClickListener(buttonClickListener);
	}

	private void detectStringFromServer(String str, TextView textView,
			boolean detectRet, int c) {
		if (str.replaceAll("[\u4e00-\u9fa5]*[a-z]*[A-Z]*\\d*-*_*\\s*", "")
				.length() != 0) {
			// UtilMethod.showDialog(mContext,
			// getResources().getString(R.string.name_cannot_use));
			textView.setText(getResources().getString(R.string.name_cannot_use));
			detectRet = false;
		} else {
			// 不包含特殊字符
			if ("".equalsIgnoreCase(str)) {
				// UtilMethod.showDialog(mContext, getResources()
				// .getString(R.string.input_alert));
				textView.setText(getResources().getString(R.string.input_alert));
				detectRet = false;
			} else if (str.contains(" ")) {
				// UtilMethod.showDialog(mContext, getResources()
				// .getString(R.string.name_cannot_use));
				textView.setText(getResources().getString(
						R.string.name_cannot_use));
				detectRet = false;
			} else {
				try {
					String nameRequest = new String(str.getBytes(), "utf-8");
					nameRequest = URLEncoder.encode(str, "UTF-8"); // "UTF-8"
					requestGet(nameRequest, textView, c);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					detectRet = false;
					e.printStackTrace();
				}
			}
		}
	}

	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		mWeiboShareAPI.handleWeiboResponse(intent, this);
	}

	@Override
	public void onResponse(BaseResponse arg0) {
		// TODO Auto-generated method stub
		switch (arg0.errCode) {
		case WBConstants.ErrorCode.ERR_OK:
			Log.i(UtilMethod.TAG, "WBConstants.ErrorCode.ERR_OK");
			break;
		case WBConstants.ErrorCode.ERR_CANCEL:
			Log.i(UtilMethod.TAG, "WBConstants.ErrorCode.ERR_CANCEL");
			break;
		case WBConstants.ErrorCode.ERR_FAIL:
			Log.i(UtilMethod.TAG, "WBConstants.ErrorCode.ERR_FAIL:"
					+ arg0.errMsg);
			break;
		}
	}

	private void requestGet(final String iname, final TextView textView,
			final int catory) throws UnsupportedEncodingException {
		checkNameProgress.setVisibility(View.VISIBLE);
		final DataGetRequest request = new DataGetRequest();
		request.setParam(ServerConstants.REQUEST_REPLACE_NAME + iname);
		request.execute(null, new LetvCallback.OnLetvSuccessListener() {

			@Override
			public void onSuccess(Object data) {
				checkNameProgress.setVisibility(View.GONE);
				if (data != null) {
					ApiCommonJsonResponse response = JsonUtils
							.unmarshalFromString(data.toString(),
									ApiCommonJsonResponse.class);
					if (response.isRet()) {
						if (catory == 0) {// name
							nameDetectResult = true;
						} else {// second
							secondDetectResult = true;
						}
						application.setName(name);
						setDataMapBeanTopage(dataMapBean);
						if (nameDetectResult && secondDetectResult) {
							changeDialog.dismiss();
						}

					} else {
						// UtilMethod.showDialog(mContext, response.getErrMsg()
						// .toString());
						textView.setText(response.getErrMsg().toString());
					}
				}
			}
		}, new LetvCallback.OnLetvErrorListener() {

			@Override
			public void onError(Exception error) {
				DebugLog.logErr(UtilMethod.TAG, error.getMessage());
				checkNameProgress.setVisibility(View.GONE);
				if (error.getCause() instanceof SocketTimeoutException) {
					UtilMethod.showDialog(
							mContext,
							getResources().getString(
									R.string.connect_server_timeout));
				} else {
					UtilMethod.showDialog(
							mContext,
							getResources().getString(
									R.string.no_internet_connnect));
				}
			}
		});
	}

	private void requestGetForTargetUrl() {
		checkNameProgress.setVisibility(View.VISIBLE);
		String nameHere = null;
		try {
			nameHere = new String(nowName.getBytes(), "utf-8");
			nameHere = URLEncoder.encode(nameHere, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String param = ServerConstants.REQUEST_TARGET_URL_ID
				+ dataMapBean.getId() + ServerConstants.REQUEST_TARGET_URL_NAME
				+ nameHere;
		final DataGetRequest request = new DataGetRequest();
		request.setParam(param);
		request.execute(null, new LetvCallback.OnLetvSuccessListener() {

			@Override
			public void onSuccess(Object data) {
				checkNameProgress.setVisibility(View.GONE);
				if (data != null) {
					ApiCommonJsonResponse response = JsonUtils
							.unmarshalFromString(data.toString(),
									ApiCommonJsonResponse.class);
					if (response.isRet()) {
						Map<String, Object> map = response.getData();
						dataMapBean.setTargetUrl(map.get("targetURL")
								.toString());
						shareToByIdState(plaformId);
					} else {
						UtilMethod.showDialog(mContext, response.getErrMsg()
								.toString());
					}
				}
			}
		}, new LetvCallback.OnLetvErrorListener() {

			@Override
			public void onError(Exception error) {
				DebugLog.logErr(UtilMethod.TAG, error.getMessage());
				checkNameProgress.setVisibility(View.GONE);
				UtilMethod
						.showDialog(
								mContext,
								getResources().getString(
										R.string.no_internet_connnect));
			}
		});
	}

	private void shareToByIdState(int id) {
		textBitmap = compressThumbBmp(textBitmap);
		switch (id) {
		case 1:
			ShareManager.getInstance().shareWXWebPageObjectToWX(this, 0,
					"http://" + dataMapBean.getTargetUrl(),
					// "http://www.baidu.com",
					dataMapBean.getTitle2(), dataMapBean.getSummary(),
					textBitmap);
			if (isClickTest) {
				StatService.onEvent(this, "swxf1test", "wx friend share", 1);
			} else {
				StatService.onEvent(this, "swxf1", "wx friend share", 1);
			}
			break;
		case 2:
			ShareManager.getInstance().shareWXWebPageObjectToWX(this, 1,
					"http://" + dataMapBean.getTargetUrl(),
					dataMapBean.getTitle2(), dataMapBean.getSummary(),
					textBitmap);
			if (isClickTest) {
				StatService.onEvent(this, "swxc2test", "comment share", 1);
			} else {
				StatService.onEvent(this, "swxc2", "comment share", 1);
			}
			break;
		case 3:
			ShareManager.getInstance().shareToQQ(this, dataMapBean.getTitle2(),
					dataMapBean.getSummary(),
					"http://" + dataMapBean.getTargetUrl(), imageUrl);//
			if (isClickTest) {
				StatService.onEvent(this, "sqq3test", "qq share", 1);
			} else {
				StatService.onEvent(this, "sqq3", "qq share", 1);
			}
			// LoginUtil.loginByAct(this, "qq", null);
			break;
		case 4:
			mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(
					NewsShowActivity.this, LetvConstant.getSinaAppKey());
			mWeiboShareAPI.registerApp();
			ShareManager.getInstance().shareSinaBySSO(this, mWeiboShareAPI,
					dataMapBean.getTitle2(), dataMapBean.getTitle2(),
					textBitmap, "http://" + dataMapBean.getTargetUrl());
			if (isClickTest) {
				StatService.onEvent(this, "swb4test", "wb share", 1);
			} else {
				StatService.onEvent(this, "swb4", "wb share", 1);
			}
			break;
		default:
			break;
		}

	}

	public Bitmap compressThumbBmp(Bitmap bitmap) {
		Bitmap thumbBmp2 = null;
		if (bitmap != null && !bitmap.isRecycled()) {
			thumbBmp2 = Bitmap.createScaledBitmap(bitmap, 80, 80, true);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			thumbBmp2.compress(Bitmap.CompressFormat.PNG, 100, baos);
		}
		return thumbBmp2;
	}

	public static Bitmap returnBitMap(String url) {
		URL myFileUrl = null;
		// Bitmap imageBitmap = null;
		try {
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			textBitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			// 浏览10-15次新闻，进入新闻详情页，出现闪退，每次都有.出现该异常
			e.printStackTrace();
		}
		Message msg = new Message();
		msg.what = LOAD_IMAGE;
		mMainHandler.sendMessage(msg);
		return textBitmap;
	}

	public static Handler mMainHandler = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case 1:
				image.setImageBitmap(textBitmap);
				break;
			case 2:
				final String imagePath = msg.obj.toString();
				Bitmap temBitmap = BitmapFactory.decodeFile(imagePath);
				image.setImageBitmap(temBitmap);
				break;
			default:
				break;
			}
		}
	};

	public class ClickableTagHandler implements TagHandler {
		private int sIndex = 0;
		private int eIndex = 0;

		public ClickableTagHandler(Context context) {
			mContext = context;
		}

		@Override
		public void handleTag(boolean opening, String tag, Editable output,
				XMLReader xmlReader) {
			if (tag.toLowerCase().equals("clickable")) {
				if (opening) {
					sIndex = output.length();
				} else {
					eIndex = output.length();
					output.setSpan(new MxgsaSpan(), sIndex, eIndex,
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
		}
	}

	public class MxgsaSpan extends ClickableSpan implements OnClickListener {

		@Override
		public void onClick(View widget) {
			changeNameV2(mFiled);
		}
	}
}
