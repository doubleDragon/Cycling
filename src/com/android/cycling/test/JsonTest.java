package com.android.cycling.test;

import java.lang.reflect.Type;
import java.util.List;

import com.android.cycling.data.ServerUser;
import com.android.cycling.util.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import android.test.AndroidTestCase;
import android.util.Log;

public class JsonTest extends AndroidTestCase {

	private static final String TAG = JsonTest.class.getSimpleName();

	public void testGetUser() {
		String result = NetworkUtils.getUserFromBmobServer();
		Log.d(TAG, "testGetUserFromBmobServer result: " + result);
		if (result != null) {
			JsonParser parser = new JsonParser();
			JsonObject rootObejct = parser.parse(result).getAsJsonObject();
			JsonElement resultsElement = rootObejct.get("results");

			Gson gson = new Gson();
			Type type = new TypeToken<List<ServerUser>>() {
			}.getType();
			List<ServerUser> users = gson.fromJson(resultsElement, type);
			Log.d(TAG, "testGetUserFromBmobServer users: " + users);
		}
	}

	public void testServerUserToJson() {
		ServerUser user = new ServerUser();
		user.setUsername("uuu");
		user.setPassword("11111");
		user.setEmail("123456789@xxx.com");
		user.setEmailVerified(true);
		user.setMale(true);
		user.setAvatar("");

		Gson gson = new Gson();
		String json = gson.toJson(user);
		Log.d(TAG, "testServerUserToJson toJson: " + json);
		ServerUser u2 = gson.fromJson(json, ServerUser.class);
		Log.d(TAG, "testServerUserToJson fromJson: " + u2.toString());

	}

}
