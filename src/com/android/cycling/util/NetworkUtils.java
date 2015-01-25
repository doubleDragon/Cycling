package com.android.cycling.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.android.cycling.CyclingConfig;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkUtils {
	
	/** The tag used to log to adb console. */
    private static final String TAG = "NetworkUtils";
    /** Timeout (in ms) we specify for each http request */
    private static final int HTTP_REQUEST_TIMEOUT_MS = 30 * 1000;
    private static final String REGISTE_URI = "http://api.lovebiking.cn/Home/Registration";
    
    private static final String SEX = "sex";
    private static final String EMAIL = "email";
    private static final String NICKNAME = "nickname";
    private static final String PASSWORD = "password";

    private NetworkUtils() {
    }

    /**
     * Configures the httpClient to connect to the URL provided.
     */
    public static HttpClient getHttpClient() {
        HttpClient httpClient = new DefaultHttpClient();
        final HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, HTTP_REQUEST_TIMEOUT_MS);
        HttpConnectionParams.setSoTimeout(params, HTTP_REQUEST_TIMEOUT_MS);
        ConnManagerParams.setTimeout(params, HTTP_REQUEST_TIMEOUT_MS);
        return httpClient;
    }

    /**
     * Connects to the SampleSync test server, authenticates the provided
     * username and password.
     *
     * @param username The server account username
     * @param password The server account password
     * @return String The authentication token returned by the server (or null)
     */
    public static String registeToServer(boolean isMale,String email, String nickname,
    		String password) {

        final HttpResponse resp;
        final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(SEX, isMale ? "0" : "1"));
        params.add(new BasicNameValuePair(EMAIL, email));
        params.add(new BasicNameValuePair(NICKNAME, nickname));
        params.add(new BasicNameValuePair(PASSWORD, password));
        
        final HttpEntity entity;
        try {
            entity = new UrlEncodedFormEntity(params);
        } catch (final UnsupportedEncodingException e) {
            // this should never happen.
            throw new IllegalStateException(e);
        }
        Log.i(TAG, "Authenticating to: " + REGISTE_URI);
        final HttpPost post = new HttpPost(REGISTE_URI);
        post.addHeader(entity.getContentType());
        post.setEntity(entity);
        try {
            resp = getHttpClient().execute(post);
            String authToken = null;
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                InputStream istream = (resp.getEntity() != null) ? resp.getEntity().getContent()
                        : null;
                if (istream != null) {
                    BufferedReader ireader = new BufferedReader(new InputStreamReader(istream));
//                    authToken = ireader.readLine().trim();
                    authToken = ireader.readLine();
                }
            }
            if ((authToken != null) && (authToken.length() > 0)) {
                Log.v(TAG, "Successful authentication");
                return authToken;
            } else {
                Log.e(TAG, "Error authenticating" + resp.getStatusLine());
                return null;
            }
        } catch (final IOException e) {
            Log.e(TAG, "IOException when getting authtoken", e);
            return null;
        } finally {
            Log.v(TAG, "getAuthtoken completing");
        }
    }
    
    /**
     * 查询数据例子
     * @see <a target=_blank href="http://docs.bmob.cn/restful/developdoc/index.html?menukey=develop_doc&key=develop_restful#index_查询数据">例子</a>
     * */
    public static String getUserFromBmobServer() {
    	String result = null;
    	HttpsURLConnection con = null;
    	BufferedReader reader = null;
    	try {
    		URL postUrl = new URL("https://api.bmob.cn/1/users");
			con = (HttpsURLConnection) postUrl.openConnection();
			con.setRequestMethod("GET");
			con.setDoInput(true);
			con.setInstanceFollowRedirects(true);

			//add header
			con.setRequestProperty("X-Bmob-Application-Id", CyclingConfig.BMOB_APPLICATION_ID);
			con.setRequestProperty("X-Bmob-REST-API-Key", CyclingConfig.BMOB_REST_API_ID);
			con.setRequestProperty("Content-Type", "application/json");

			//receive data
			reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			String line;
			StringBuffer responseText = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				responseText.append(line).append("\r\n");
			}
			
			result = responseText.toString();
			return result;
		} catch (ProtocolException e) {
			Log.e("tag", "getUserForJson ProtocolException error: " + e.getMessage());
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			Log.e("tag", "getUserForJson UnsupportedEncodingException error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("tag", "getUserForJson IOException error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if(reader != null)
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(con != null) {
				con.disconnect();
			}
		}
		
    	return result;
    }
	
	public static  boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}
