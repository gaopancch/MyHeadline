//package com.lxsj.myheadline.activities;
//
//import com.lxsj.NavigationPoint;
//import com.lxsj.myheadline.R;
//import com.lxsj.myheadline.R.id;
//import com.lxsj.myheadline.R.layout;
//import com.lxsj.myheadline.application.MyHeadlineApplication;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.MotionEvent;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.ViewFlipper;
//
//public class FirstSetupActivity extends Activity {
//
//	private ViewFlipper viewFlipper = null;
//    private float startX;
//    private int flipperItem=0;
//    private NavigationPoint nPoint;
//    private boolean firstSetUp=true;
//	private MyHeadlineApplication application;
//	
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);//no title
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//        		WindowManager.LayoutParams.FLAG_FULLSCREEN);//full screen   
//        setContentView(R.layout.activity_first_setup);
//        
//		application = (MyHeadlineApplication) getApplication();
//        getSharedParferencer();
//        nPoint=(NavigationPoint)findViewById(R.id.navigetionPoint);
//        viewFlipper = (ViewFlipper) this.findViewById(R.id.viewFlipper);      
//        if(!firstSetUp){
//        	goAcitvity();
//        }
//    }
//       
//    public void getSharedParferencer(){
//    	firstSetUp=application.getVersionFirstSetup();
//    }
//    
//    public void saveSharedInfo(){
//    	application.setVersionFirstSetupFalse();
//    }
//    
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//        case MotionEvent.ACTION_DOWN:
//            startX = event.getX();
//            break;
//        case MotionEvent.ACTION_UP:
//            if (event.getX() > startX) { // finger to right
//            	if(flipperItem!=0){
//            		leftToRight();
//            	}              
//            } else if (event.getX() < startX) { // finger to left
//            	if(flipperItem<2){
//            		rightToLeft();
//            	}else{
//            		goAcitvity();
//            	}
//            }
//            break;
//        } 
//        return super.onTouchEvent(event);
//    }
//    
//    private void goAcitvity(){
//		Intent intent=new Intent();
//		intent.setClass(FirstSetupActivity.this, SplashActivity.class);
//		startActivity(intent);
//		finish();
//    }
//    
//    public void rightToLeft(){
//        viewFlipper.setInAnimation(getApplicationContext(), R.layout.in_right_left);
//        viewFlipper.setOutAnimation(getApplicationContext(), R.layout.out_right_left);
//        viewFlipper.showNext();
//        ++flipperItem;
//        nPoint.setNumberLight(flipperItem);
//    }
//    
//    public void leftToRight(){
//        viewFlipper.setInAnimation(getApplicationContext(), R.layout.in_left_right);
//        viewFlipper.setOutAnimation(getApplicationContext(), R.layout.out_left_right);
//        viewFlipper.showPrevious();
//        --flipperItem;
//        nPoint.setNumberLight(flipperItem);
//    }
//    
//    public void onDestroy(){
//    	super.onDestroy();		
//    	firstSetUp=false;
//		saveSharedInfo();
//    }
//}
