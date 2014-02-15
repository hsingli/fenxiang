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
			// 创建JSONObject对象
			JSONObject jsonObject = new JSONObject(jsonString);

			// 获取JSONObject对象的值，该值是一个JSON数组
			JSONArray userLocationArray = jsonObject.getJSONArray(key);
			for (int i = 0; i < userLocationArray.length(); i++) {

				// 获得JSON数组中的每一个JSONObject对象
				JSONObject userLocationObject = userLocationArray
						.getJSONObject(i);
				UserLocation userLocation = new UserLocation();

				// 获得每一个JSONObject对象中的键所对应的值
				String lngStr = userLocationObject.getString("longitude");
				String latStr = userLocationObject.getString("latitude");
				String username = userLocationObject.getString("username");
				long timestamp = userLocationObject.getLong("timestamp");

				// 将解析出来的属性值存入userLocation对象
				userLocation.setLongitude(lngStr);
				userLocation.setLatitude(latStr);
				userLocation.setUsername(username);
				userLocation.setTimestamp(timestamp);

				// 将解析出来的每一个userLocation对象添加到List中
				list.add(userLocation);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
}
