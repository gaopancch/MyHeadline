package com.lxsj.myheadline.wxapi;

import com.letv.component.userlogin.utils.LetvConstant;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	    private static final String TAG = "WXEntryActivity";
	    private IWXAPI api;  
	    @Override  
	    protected void onCreate(Bundle savedInstanceState) {  
	        api = WXAPIFactory.createWXAPI(this, LetvConstant.getWxAppId(), false);  
	        api.handleIntent(getIntent(), this);  
	        super.onCreate(savedInstanceState);  
//	        finish();
	    }  
	    @Override  
	    public void onReq(BaseReq arg0) { }  
	  
	    @Override  
	    public void onResp(BaseResp resp) {
	    	Log.i(TAG, resp.errCode+"  "+resp.errStr);
	        switch (resp.errCode) {  
	        case BaseResp.ErrCode.ERR_OK:  
	            break;  
	        case BaseResp.ErrCode.ERR_USER_CANCEL:  
	            break;  
	        case BaseResp.ErrCode.ERR_AUTH_DENIED:  
	            break;  
	        }  
	        finish();
	    }  
	}  