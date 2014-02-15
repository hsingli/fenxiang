package com.qipingli.yujian.mc;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.qipingli.yujian.mc.entity.UserLocation;
import com.qipingli.yujian.mc.util.JsonUtil;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class LocationService extends IntentService {
	private static final String LOG_TAG="yujian";
	public static final String ShareLocationActivityNotification="com.qipingli.yujian.mc.ShareLocationActivity";
	public static final String PolylineActivityNotification="com.qipingli.yujian.mc.PolylineActivity";
	
	
	public LocationService() {
		super("LocationService");
		// TODO Auto-generated constructor stub
	}

//	@Override
//	public IBinder onBind(Intent arg0) {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
	@Override
	protected void onHandleIntent(Intent intent) 
	{
		Bundle bundle = intent.getExtras();
		String typeStr = bundle.getString("type");
		//according to the service type header to decode the intent.
		if(typeStr.equals("addLocation"))
		{
			Double longitude = bundle.getDouble("longitude");
			Double latitude = bundle.getDouble("latitude");
			String username = bundle.getString("username");
			addLocation(longitude, latitude,username);
		}
		else if(typeStr.equals("requestUserTrack"))
		{
			String username = bundle.getString("username");
			requestUserTrack(username);
		}
	}
//	@Override
//	public void onStart(Intent intent, int startId) {
//		if (intent != null)
//		{
//			Bundle bundle = intent.getExtras();
//			String typeStr = bundle.getString("type");
//			//according to the service type header to decode the intent.
//			Double longitude = bundle.getDouble("longitude");
//			Double latitude = bundle.getDouble("latitude");
//			String username = bundle.getString("username");
//			addLocation(longitude, latitude,username);
//		}
//	}
	
	public void updateData(ArrayList<NameValuePair> data, String servAction)
	{
		   try
		    {
			   	String typeStr = servAction.substring(servAction.lastIndexOf("/")+1);
			   	Log.v(LOG_TAG, typeStr);
		        HttpClient httpclient = new DefaultHttpClient();
		        HttpPost httppost = new HttpPost(servAction);
		        httppost.setEntity(new UrlEncodedFormEntity(data));
		        HttpResponse response = httpclient.execute(httppost);
		        
		        //根据response.getStatueLine的结果调用处理函数
		        Log.v(LOG_TAG, response.getStatusLine().toString());
		        
		        //处理response 结果
		        if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
		        {
		        	String resStr = EntityUtils.toString(response.getEntity());
		        	Log.v(LOG_TAG,"res:"+resStr);

		        	if(typeStr.equals("addLocation"))
		        	{
		        		List<UserLocation> userLocationList = JsonUtil
		        				.getListUserLocation("locationList", resStr);
		        		for (UserLocation userLocation : userLocationList) {
		        			Log.v(LOG_TAG,"userLocation: "+userLocation.getUsername());
		        			notifyLocationChange(userLocation);
		        		}
		        	}
		        	else if(typeStr.equals("requestUserTrack"))
		        	{
		        		List<UserLocation> userLocationList = JsonUtil
		        				.getListUserLocation("locationList", resStr);
		        		for (UserLocation userLocation : userLocationList) {
		        			Log.v(LOG_TAG,"userLocation: "+userLocation.getUsername());
		        			notifyPolylineChange(userLocation);
		        		}
		        	}
		        }
		    }
		    catch(Exception e)
		    {
		        Log.e(LOG_TAG, "Error:  "+e.toString());
		    }  	
	}
	
	public void notifyLocationChange(UserLocation userLocation)
	{
		Intent intent = new Intent(ShareLocationActivityNotification);
		Bundle bundle = new Bundle();
		bundle.putString("longitude", userLocation.getLongitude());
		bundle.putString("latitude", userLocation.getLatitude());
		bundle.putString("username", userLocation.getUsername());
		intent.putExtras(bundle);
		sendBroadcast(intent);
	}
	
	public void notifyPolylineChange(UserLocation userLocation)
	{
		Intent intent = new Intent(PolylineActivityNotification);
		Bundle bundle = new Bundle();
		bundle.putString("longitude", userLocation.getLongitude());
		bundle.putString("latitude", userLocation.getLatitude());
		bundle.putString("username", userLocation.getUsername());
		intent.putExtras(bundle);
		sendBroadcast(intent);
	}
	
	
	public void addLocation(Double longitude, Double latitude, String username)
	{
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("longitude", longitude.toString()));
		nameValuePairs.add(new BasicNameValuePair("latitude", latitude.toString()));
		nameValuePairs.add(new BasicNameValuePair("username", username));

		String servAction="http://qipingli.xicp.net:8090/yujianAppServ/addLocation";
		updateData(nameValuePairs, servAction);
	}
	
	public void requestUserTrack(String username)
	{
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("longitude", "0.0"));
		nameValuePairs.add(new BasicNameValuePair("latitude", "0.0"));
		nameValuePairs.add(new BasicNameValuePair("username", username));

		String servAction="http://qipingli.xicp.net:8090/yujianAppServ/requestUserTrack";
		updateData(nameValuePairs, servAction);
	}

	public void addUser(String username)
	{
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("username", username));
		
		String servAction="http://qipingli.xicp.net:8090/yujianAppServ/addUser";
		updateData(nameValuePairs, servAction);
	}
}
