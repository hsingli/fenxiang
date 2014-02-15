package com.qipingli.yujian.mc.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qipingli.yujian.mc.entity.UserLocation;

public class JsonUtil {

	public static List<UserLocation> getListUserLocation(String key,
			String jsonString) {
		List<UserLocation> list = new ArrayList<UserLocation>();
		try {
			// ����JSONObject����
			JSONObject jsonObject = new JSONObject(jsonString);

			// ��ȡJSONObject�����ֵ����ֵ��һ��JSON����
			JSONArray userLocationArray = jsonObject.getJSONArray(key);
			for (int i = 0; i < userLocationArray.length(); i++) {

				// ���JSON�����е�ÿһ��JSONObject����
				JSONObject userLocationObject = userLocationArray
						.getJSONObject(i);
				UserLocation userLocation = new UserLocation();

				// ���ÿһ��JSONObject�����еļ�����Ӧ��ֵ
				String lngStr = userLocationObject.getString("longitude");
				String latStr = userLocationObject.getString("latitude");
				String username = userLocationObject.getString("username");
				long timestamp = userLocationObject.getLong("timestamp");

				// ����������������ֵ����userLocation����
				userLocation.setLongitude(lngStr);
				userLocation.setLatitude(latStr);
				userLocation.setUsername(username);
				userLocation.setTimestamp(timestamp);

				// ������������ÿһ��userLocation������ӵ�List��
				list.add(userLocation);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
}
