package com.qipingli.yujian.mc;


import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;

import com.qipingli.yujian.mc.R;
import com.qipingli.yujian.mc.entity.UserLocation;

public class ShareLocationActivity extends Activity implements LocationSource,
		AMapLocationListener, OnMarkerClickListener,OnInfoWindowClickListener, 
		InfoWindowAdapter, OnClickListener {
	private AMap aMap;
	private MarkerOptions markerOption;
	private MapView mapView;
	private EditText usernameText;
	private EditText friendText;
	private String usernameStr;
	private String friendStr;
	private Button linkBtn;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private Marker myMarker;
	private List<Marker> markerList;
	private Intent intent;
	private LatLng preLocation;
	private final String LOG_TAG="yujian";
	private final Double LOC_MIN_DELTA = 0.0005;
	private BroadcastReceiver markerReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent){
			Bundle bundle = intent.getExtras();
			UserLocation userLocation = new UserLocation();
			userLocation.setLatitude(bundle.getString("latitude"));
			userLocation.setLongitude(bundle.getString("longitude"));
			userLocation.setUsername(bundle.getString("username"));
			userLocation.setTimestamp(0);
			
			Log.v(LOG_TAG,"broadcastReceiver!");
			addMarkersToMap(userLocation);
			
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.share_location_section);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// �˷���������д
		init();
		
		Bundle bundle = this.getIntent().getExtras();
		usernameStr = bundle.getString("username");
		usernameText.setText(usernameStr);
		
		//addMarkersToMap();
	}

	/**
	 * ��ʼ��AMap����
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
			preLocation = new LatLng(0.0,0.0);
			usernameText = (EditText) findViewById(R.id.user);
			friendText = (EditText)findViewById(R.id.friend);
			linkBtn = (Button) findViewById(R.id.link);
			linkBtn.setOnClickListener(this);
		}
	}

	/**
	 * ����һЩamap������
	 */
	private void setUpMap() {
		// �Զ���ϵͳ��λС����
//		MyLocationStyle myLocationStyle = new MyLocationStyle();
//		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
//				.fromResource(R.drawable.location_marker));// ����С�����ͼ��
//		myLocationStyle.strokeColor(Color.BLACK);// ����Բ�εı߿���ɫ
//		// myLocationStyle.radiusFillColor(color)//����Բ�ε������ɫ
//		// myLocationStyle.anchor(int,int)//����С�����ê��
//		myLocationStyle.strokeWidth(0.1f);// ����Բ�εı߿��ϸ
//		aMap.setMyLocationStyle(myLocationStyle);
//		aMap.setMyLocationRotateAngle(180);
		aMap.setLocationSource(this);// ���ö�λ����
		aMap.setOnMarkerClickListener((OnMarkerClickListener) this);// ���õ��marker�¼�������
		aMap.setOnInfoWindowClickListener((OnInfoWindowClickListener) this);// ���õ��infoWindow�¼�������
		aMap.setInfoWindowAdapter((InfoWindowAdapter) this);// �����Զ���InfoWindow��ʽ
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// ����Ĭ�϶�λ��ť�Ƿ���ʾ
		aMap.setMyLocationEnabled(true);// ����Ϊtrue��ʾ��ʾ��λ�㲢�ɴ�����λ��false��ʾ���ض�λ�㲢���ɴ�����λ��Ĭ����false
	}

	/**
	 * ����������д
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
		registerReceiver(markerReceiver, new IntentFilter(LocationService.ShareLocationActivityNotification));

	}

	/**
	 * ����������д
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		deactivate();
		
		//reset preLocation to trigger Location info request
		preLocation = new LatLng(0.0,0.0);
		unregisterReceiver(markerReceiver);
	}

	/**
	 * ����������д
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * ����������д
	 */
	@Override
	protected void onDestroy() {
//		unregisterReceiver(markerReceiver);
		super.onDestroy();
		mapView.onDestroy();
	}

	/**
	 * �˷����Ѿ�����
	 */
	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	/**
	 * ��λ�ɹ���ص�����
	 */
	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		
		if (mListener != null && aLocation != null) {
			mListener.onLocationChanged(aLocation);// ��ʾϵͳС����
			
			float bearing = aMap.getCameraPosition().bearing;
			aMap.setMyLocationRotateAngle(bearing);// ����С������ת�Ƕ�
			
			usernameStr = usernameText.getText().toString().trim();
			if(usernameStr == null||usernameStr.length()<= 0)
			{
				Log.d(LOG_TAG, "Location update, but not startService due to username check!");
				return;
			}
		
			LatLng curLocation = new LatLng(aLocation.getLatitude(),
					aLocation.getLongitude());

			// ����λ�õ����ݿ�,�Ժ�����service���
			// addLocation(aLocation);
			// �����жϣ����ǰ�����εľ��Ȳ��ά�Ȳ����һ����Χ��
			// �Ͳ�����λ�ø���
			if ( validateLocation(curLocation)) {
				
				if(myMarker != null)
				{
					myMarker.destroy();
				}
				drawMarkers();
				
				intent = new Intent("com.qipingli.yujian.mc.LocationService");
				Bundle bundle = new Bundle();
				bundle.putString("type", "addLocation");
				bundle.putDouble("longitude", aLocation.getLongitude());
				bundle.putDouble("latitude", aLocation.getLatitude());
				bundle.putString("username",usernameStr);
				intent.putExtras(bundle);
				this.startService(intent);
				Log.v(LOG_TAG, "startService");
				preLocation = curLocation;
			} else {
				Log.d(LOG_TAG, "Location update, but not startService due to range check!");
			}
		}
	}
	
	private boolean validateLocation(LatLng curLocation)
	{
		if( Math.abs(curLocation.latitude - preLocation.latitude) >= LOC_MIN_DELTA ||
			Math.abs(curLocation.longitude - preLocation.longitude) >= LOC_MIN_DELTA)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * ���λ
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			/*
			 * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2�汾��������������true��ʾ��϶�λ�а���gps��λ��false��ʾ�����綨λ��Ĭ����true Location
			 * API��λ����GPS�������϶�λ��ʽ
			 * ����һ�������Ƕ�λprovider���ڶ�������ʱ�������5000���룬������������������λ���ף����ĸ������Ƕ�λ������
			 */
			//mAMapLocationManager.setGpsEnable(true);
			mAMapLocationManager.requestLocationUpdates(
					LocationProviderProxy.AMapNetwork, 10000, 10, this);
			//LocationManagerProxy.GPS_PROVIDER
			//LocationProviderProxy.AMapNetwork
			
		}
	}

	/**
	 * ֹͣ��λ
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
	}
	

	
	private void addMarkersToMap(UserLocation userLocation) {

		markerOption = new MarkerOptions();
		markerOption.position(new LatLng(Double.parseDouble(userLocation.getLatitude()), 
				Double.parseDouble(userLocation.getLongitude())));
		markerOption.title(userLocation.getUsername()).snippet(userLocation.getLatitude()+","+userLocation.getLongitude());
		markerOption.perspective(false);
		markerOption.draggable(false);
//		markerOption.icon(BitmapDescriptorFactory
//				.fromResource(R.drawable.arrow));
		markerOption.icon(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
		aMap.addMarker(markerOption);
	}
	
	/**
	 * ����ϵͳĬ�ϵ�1��marker����ͼƬ
	 */
	public void drawMarkers() {
		ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
		giflist.add(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
		giflist.add(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_RED));
		giflist.add(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
		
		Location loc = aMap.getMyLocation();
		LatLng latlng = new LatLng(loc.getLatitude(),loc.getLongitude());
		myMarker = aMap.addMarker(new MarkerOptions()
				.position(latlng)
				.title("�ҵ�λ��")
				.icons(giflist)
				.perspective(false).draggable(false));
		myMarker.setRotateAngle(180);// ����marker��ת180��
		myMarker.showInfoWindow();// ����Ĭ����ʾһ��infowinfow
	}
	public boolean onMarkerClick(final Marker marker) {
		if( marker.isInfoWindowShown())
		{
			marker.hideInfoWindow();
		}
		else
		{
			marker.showInfoWindow();
		}
		
		jumpPoint(marker);
		
		return true;
	}
	
	/**
	 * marker���ʱ����һ��
	 */
	public void jumpPoint(final Marker marker) {
		final Handler handler = new Handler();
		final long start = SystemClock.uptimeMillis();
		Projection proj = aMap.getProjection();
		final LatLng endLatLng = marker.getPosition();
//		final LatLng endLatLng = new LatLng(23.127585, 113.374279);
		Point endPoint = proj.toScreenLocation(endLatLng);
		endPoint.offset(0, -100);
		final LatLng startLatLng = proj.fromScreenLocation(endPoint);
		final long duration = 1500;
		marker.setPosition(endLatLng);
		
		final Interpolator interpolator = new BounceInterpolator();
		handler.post(new Runnable() {
			@Override
			public void run() {
				long elapsed = SystemClock.uptimeMillis() - start;
				float t = interpolator.getInterpolation((float) elapsed
						/ duration);
				double lng = t * endLatLng.longitude + (1 - t)
						* startLatLng.longitude;
				double lat = t * endLatLng.latitude + (1 - t)
						* startLatLng.latitude;
				marker.setPosition(new LatLng(lat, lng));
				if (t < 1.0) {
					handler.postDelayed(this, 16);
				}
			}
		});
	}

	
	/**
	 * �����Զ���infowindow���ڵ�infocontents�¼��ص�
	 */
	@Override
	public View getInfoContents(Marker marker) {

		View infoContent = getLayoutInflater().inflate(
				R.layout.custom_info_contents, null);
		
		String title = marker.getTitle();
		TextView titleUi = ((TextView) infoContent.findViewById(R.id.title));
		if (title != null) {
			SpannableString titleText = new SpannableString(title);
			titleText.setSpan(new ForegroundColorSpan(Color.RED), 0,
					titleText.length(), 0);
			titleUi.setTextSize(15);
			titleUi.setText(titleText);

		} else {
			titleUi.setText("");
		}
		
		String snippet = marker.getSnippet();
		TextView snippetUi = ((TextView) infoContent.findViewById(R.id.snippet));
		if (snippet != null) {
			SpannableString snippetText = new SpannableString(snippet);
			snippetText.setSpan(new ForegroundColorSpan(Color.GREEN), 0,
					snippetText.length(), 0);
			snippetUi.setTextSize(20);
			snippetUi.setText(snippetText);
		} else {
			snippetUi.setText("");
		}
		return infoContent;
	}

	/**
	 * �����Զ���infowindow���ڵ�infowindow�¼��ص�
	 */
	@Override
	public View getInfoWindow(Marker marker) {

		return getInfoContents(marker);
	}

	/**
	 * �������infowindow�����¼��ص�
	 */
	@Override
	public void onInfoWindowClick(Marker marker) {
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.link:
			usernameStr = usernameText.getText().toString().trim();
			friendStr = friendText.getText().toString().trim();
			break;

		default:
			break;
		}
	}
}
