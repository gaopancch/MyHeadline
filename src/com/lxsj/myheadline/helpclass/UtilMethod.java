package com.lxsj.myheadline.helpclass;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.lxsj.myheadline.R;

public class UtilMethod {
	public static String TAG="MyHeadline";
	

    
	public static String changeColorAndAddClickable(String inStr,String color){
		
		return "<font color=\""+color+"\"><clickable>"+inStr+"</clickable></font>";
//		return "<clickable>"+inStr+"</clickable>";
	}
	
	public static void  showDialog(Context context,String message) {
		LayoutInflater inflater2 =LayoutInflater.from(context);  
		View view = inflater2.inflate(R.layout.dialog,null);
		TextView tv=(TextView) view.findViewById(R.id.alertText);
		Button bt=(Button) view.findViewById(R.id.alertSureButton);
		final Dialog dialog=new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawableResource(R.drawable.circle_bg);
		tv.setText(message);
		dialog.setContentView(view);		
		dialog.show();
		bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
	}
	
	public static Bitmap returnBitMap(String url) {
		URL myFileUrl = null;
		Bitmap imageBitmap = null;
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
			imageBitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(NullPointerException e){
			//浏览10-15次新闻，进入新闻详情页，出现闪退，每次都有.出现该异常
			e.printStackTrace();
		}
		return imageBitmap;
	}
	
	public static void saveBitmapToNative(Bitmap bm) {
		try {
			File file = new File(Environment.getExternalStorageDirectory()
					+ "/newsDataFile" + File.separator + "qqshareimage.png");
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}

			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream out = new FileOutputStream(file);
			bm.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			Log.i(UtilMethod.TAG, "FileNotFoundException");
			e.printStackTrace();
		} catch (IOException e) {
			Log.i(UtilMethod.TAG, "IOException");
			e.printStackTrace();
		}
	}
}
