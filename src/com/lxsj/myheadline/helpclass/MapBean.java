package com.lxsj.myheadline.helpclass;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;


public class MapBean implements Parcelable,Serializable {
    private static final long serialVersionUID = 175L;
	private String title="is null";//
	private String title2="is null";//
	private String commentNumber="7890";//
	private String imageUrl=null;
	private String summary="ta竟然上头条？吓死宝宝了！我滴小伙伴都惊了~~真是城会玩！";
	private String text=null;//
	private String targetUrl=null;//
	private String appName = "一起上头条";
	private long id=0;
	private int position=0;
	private int assort=0;//0 common  1 man  2 woman
	private int filed=14;
	
	public void setFiled(int str){
		filed=str;
	}
	
	public int getFiled(){
		return filed;
	}
	
	public void setAssort(int ass){
		assort=ass;
	}
	
	public int getAssort(){
		return assort;
	}

	public void setPosition(int p){
		position=p;
	}
	
	public int getPosition(){
		return position;
	}
	
	public void setCommentNumber(String n){
		commentNumber=n;
	}
	
	public String getCommentNumber(){
		return commentNumber;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public String getAppName() {
		return appName;
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String tarUrl) {
		targetUrl = tarUrl;
	}

	public String getSummary() {
		return summary;
	}

//	public void setSummary(String sum) {
//		summary = sum;
//	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String str) {
		title = str;
	}
	
	public String getTitle2() {
		return title2;
	}

	public void setTitle2(String str) {
		title2 = str;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String str) {
		imageUrl = str;
	}

	public String getText() {
		return text;
	}

	public void setText(String str) {
		text = str;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(title);
		dest.writeString(title2);
		dest.writeString(commentNumber);
		dest.writeString(imageUrl);
		dest.writeString(summary);
		dest.writeString(text);
//		dest.writeString(targetUrl);
		dest.writeInt(filed);
		dest.writeLong(id);
		dest.writeInt(position);
		dest.writeInt(assort);
		dest.writeString(appName);
	}

	public static final Parcelable.Creator<MapBean> CREATOR = new Creator<MapBean>() {

		@Override
		public MapBean createFromParcel(Parcel source) {
			MapBean mMapBeann = new MapBean();
			mMapBeann.title=source.readString();
			mMapBeann.title2=source.readString();
			mMapBeann.commentNumber=source.readString();
			mMapBeann.imageUrl=source.readString();
			mMapBeann.summary=source.readString();
			mMapBeann.text=source.readString();
//			mMapBeann.targetUrl=source.readString();
			mMapBeann.filed=source.readInt();
			mMapBeann.id = source.readLong();
			mMapBeann.position=source.readInt();
			mMapBeann.assort=source.readInt();
			mMapBeann.appName=source.readString();
			return mMapBeann;
		}

		@Override
		public MapBean[] newArray(int size) {
			// TODO Auto-generated method stub
			return new MapBean[size];
		}
	};
}
