package com.lxsj.myheadline.activities;

import java.util.List;

import com.lxsj.myheadline.R;
import com.lxsj.myheadline.R.id;
import com.lxsj.myheadline.R.layout;
import com.lxsj.myheadline.imagescan.ChildAdapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class ShowImageActivity extends Activity {

	   private GridView mGridView;
	    private List<String> list;
	    private ChildAdapter adapter;
	    private Context context;
	    private int CHOSE_IMAGE = 2;
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_show_image);
	        context=this;
	        mGridView = (GridView) findViewById(R.id.child_grid);
	        list = getIntent().getStringArrayListExtra("data");

	        adapter = new ChildAdapter(this, list, mGridView);
	        mGridView.setAdapter(adapter);
	        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

	            @Override
	            public void onItemClick(AdapterView<?> parent, View view,
	                                    int position, long id) {
	            	Message msg=new Message();
	            	msg.what=CHOSE_IMAGE;
	            	msg.obj=list.get(position);
	            	NewsShowActivity.mMainHandler.sendMessage(msg);
	                Toast.makeText(context, "选中 " + list.get(position) + " item", Toast.LENGTH_LONG).show();
	                finish();
	            }
	        });
	    }
}