package com.qipingli.yujian.mc;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.qipingli.yujian.mc.entity.UserLocation;

/**
 * AMapV2地图中简单介绍一些Polyline的用法.
 */
public class PolylineActivity extends Activity  {
	private static final int WIDTH_MAX = 50;
	private static final int HUE_MAX = 255;
	private static final int ALPHA_MAX = 255;
	private static final String LOG_TAG="yujian";

	private AMap aMap;
	private MapView mapView;
	private Polyline polyline;

	
	private String usernameStr;
	private BroadcastReceiver polylineReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent){
			Bundle bundle = intent.getExtras();
			UserLocation userLocation = new UserLocation();
			userLocation.setLatitude(bundle.getString("latitude"));
			userLocation.setLongitude(bundle.getString("longitude"));
			userLocation.setUsername(bundle.getString("username"));
			userLocation.setTimestamp(0);
			
			Log.v(LOG_TAG,"broadcastReceiver!");
			addPointToPolyline(userLocation);
			
		}
	};
	
	private PolylineOptions plo;
	private LatLng preLatLng;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		Bundle bundle = this.getIntent().getExtras();
		usernameStr = bundle.getString("username");
		
		setContentView(R.layout.polyline_section);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
		
		requestUserTrackService();
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {

		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
	}

	private void setUpMap() {

		plo = new PolylineOptions();
		plo.width(10)
		   .setDottedLine(false)
		   .geodesic(true)
		   .color(Color.argb(255, 0, 255, 0));
		
		preLatLng = new LatLng(0.0,0.0);
	}
	
	private void addPointToPolyline(UserLocation userLocation){

		LatLng latLng = new LatLng(Double.parseDouble(userLocation.getLatitude()),
				Double.parseDouble(userLocation.getLongitude()));
		
		aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
				latLng, 10, 0, 0)));
		if (! preLatLng.equals(new LatLng(0.0,0.0)))
		{

			plo.add(preLatLng,latLng);
			polyline = aMap.addPolyline(plo);

		}
		
		preLatLng = latLng;

	}
	
	private void requestUserTrackService(){
		Intent intent = new Intent("com.qipingli.yujian.mc.LocationService");
		Bundle bundle = new Bundle();
		bundle.putString("type", "requestUserTrack");
		bundle.putString("username",usernameStr);
		intent.putExtras(bundle);
		this.startService(intent);
		Log.v(LOG_TAG, "startService");
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
		registerReceiver(polylineReceiver, new IntentFilter(LocationService.PolylineActivityNotification));
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		unregisterReceiver(polylineReceiver);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

}

