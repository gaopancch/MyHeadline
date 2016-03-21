package com.lxsj.myheadline.helpclass;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.android.pushservice.PushServiceReceiver;
import com.baidu.android.pushservice.PushMessageReceiver;
import com.lxsj.myheadline.activities.HeadLineActivity;
import com.tencent.utils.SystemUtils;
/*
 *0 - Success
 *10001 - Network Problem
 *10101  Integrate Check Error
 *30600 - Internal Server Error
 *30601 - Method Not Allowed
 *30602 - Request Params Not Valid
 *30603 - Authentication Failed
 *30604 - Quota Use Up Payment Required
 *30605 -Data Required Not Found
 *30606 - Request Time Expires Timeout
 *30607 - Channel Token Timeout
 *30608 - Bind Relation Not Found
 *30609 - Bind Number Too Many
 *
 */
public class BaiduPushReceive extends PushMessageReceiver{

    /** TAG to Log */
    private  String TAG = "MyHeadline";

    @Override
    public void onBind(Context context, int errorCode, String appid,
            String userId, String channelId, String requestId) {
        String responseString = "onBind errorCode=" + errorCode + " appid="
                + appid + " userId=" + userId + " channelId=" + channelId
                + " requestId=" + requestId;
//        Log.i(TAG, responseString);
//
//        if (errorCode == 0) {
//            Log.i(TAG, responseString);
//        }
    }

    @Override
    public void onMessage(Context context, String message,
            String customContentString) {
        String messageString = "message=\"" + message
                + "\" customContentString=" + customContentString;
        Log.i(TAG, messageString);
        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String myvalue = null;
                if (!customJson.isNull("素材更新")) {
                    myvalue = customJson.getString("素材更新");
                    Log.i(TAG,myvalue);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

//        updateContent(context, messageString);
    }

    @Override
    public void onNotificationClicked(Context context, String title,
            String description, String customContentString) {
        String notifyString = "ͨtitle=\"" + title + "\" description=\""
                + description + "\" customContent=" + customContentString;
        Log.i(TAG, notifyString);

        String myvalue = null;
        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                if (!customJson.isNull("素材更新")) {
                    myvalue = customJson.getString("素材更新");
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        updateContent(context, myvalue);
    }


    @Override
    public void onNotificationArrived(Context context, String title,
            String description, String customContentString) {

        String notifyString = "onNotificationArrived  title=\"" + title
                + "\" description=\"" + description + "\" customContent="
                + customContentString;
        Log.i(TAG, notifyString);

        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String myvalue = null;
                if (!customJson.isNull("素材更新")) {
                    myvalue = customJson.getString("素材更新");
                    Log.i(TAG, myvalue);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
//        updateContent(context, notifyString);
    }

    @Override
    public void onSetTags(Context context, int errorCode,
            List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onSetTags errorCode=" + errorCode
                + " sucessTags=" + sucessTags + " failTags=" + failTags
                + " requestId=" + requestId;
        Log.d(TAG, responseString);
//        updateContent(context, responseString);
    }
    @Override
    public void onDelTags(Context context, int errorCode,
            List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onDelTags errorCode=" + errorCode
                + " sucessTags=" + sucessTags + " failTags=" + failTags
                + " requestId=" + requestId;
        Log.d(TAG, responseString);

//        updateContent(context, responseString);
    }


    @Override
    public void onListTags(Context context, int errorCode, List<String> tags,
            String requestId) {
        String responseString = "onListTags errorCode=" + errorCode + " tags="
                + tags;
        Log.d(TAG, responseString);

//        updateContent(context, responseString);
    }

    @Override
    public void onUnbind(Context context, int errorCode, String requestId) {
        String responseString = "onUnbind errorCode=" + errorCode
                + " requestId = " + requestId;
        Log.d(TAG, responseString);

        if (errorCode == 0) {
            Log.d(TAG, "  ");
        }
//        updateContent(context, responseString);
    }

    private void updateContent(Context context, String content) {
    	if(isAppAlive(context,"com.lxsj.myheadline")){
    		Log.i(TAG, "isalive content="+content);
            Intent mainIntent = new Intent(context, HeadLineActivity.class);
            //将MainAtivity的launchMode设置成SingleTask, 或者在下面flag中加上Intent.FLAG_CLEAR_TOP,
            //如果Task栈中有MainActivity的实例，就会把它移到栈顶，把在它之上的Activity都清理出栈，
            //如果Task栈不存在MainActivity实例，则在栈顶创建
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainIntent);
//            ((Activity) context).finish();
    	}else{
    		Log.i(TAG, "is not alive content="+content);
    		Intent launchIntent = context.getPackageManager().
                    getLaunchIntentForPackage("com.lxsj.myheadline");
            launchIntent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            launchIntent.putExtra("素材信息", content);
            context.startActivity(launchIntent);
    	}
   }
    
    public static boolean isAppAlive(Context context, String packageName){
        ActivityManager activityManager =
             (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
         List<ActivityManager.RunningAppProcessInfo> processInfos
                 = activityManager.getRunningAppProcesses();
         for(int i = 0; i < processInfos.size(); i++){
             if(processInfos.get(i).processName.equals(packageName)){
                 return true;
             }
         }
         return false;
     }
}
