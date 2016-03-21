package com.lxsj.myheadline.activities;

import com.baidu.mobstat.StatService;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//setContentView(R.layout.activity_base);
//	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    
//	           setTranslucentStatus(true);    
//	           SystemBarTintManager tintManager = new SystemBarTintManager(this);    
//	           tintManager.setStatusBarTintEnabled(true);    
//	           tintManager.setStatusBarTintResource(R.color.transparent);//
//	       }    
	}  
	  
//	@TargetApi(19)     
//	   private void setTranslucentStatus(boolean on) {    
//	       Window win = getWindow();    
//	       WindowManager.LayoutParams winParams = win.getAttributes();    
//	       final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;    
//	       if (on) {    
//	           winParams.flags |= bits;    
//	       } else {    
//	           winParams.flags &= ~bits;    
//	       }    
//	       win.setAttributes(winParams);    
//
//	}
	
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();  
//        PgyFeedbackShakeManager.setShakingThreshold(1000);
//        PgyFeedbackShakeManager.register(BaseActivity.this);
       // FeedbackActivity.setBarImmersive(true);
//        PgyFeedbackShakeManager.register(MainActivity.this, false);
        StatService.onResume(getApplicationContext());
    }
    @Override 
   protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause(); 
//       PgyFeedbackShakeManager.unregister();
       StatService.onPause (getApplicationContext());
    }
}
