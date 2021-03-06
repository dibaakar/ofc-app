package com.ofcampus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class Util {
	
	public static int connectTimeout=10000;
	public static int socketTimeout=30000;
	public static int delaytime = 2000;
	public static int servicesyncInterval = 180000;
	private static String baseUrl = "http://205.147.110.176:8080/api/";
	
	
	public static enum userType{
		Normal,Gmail,Facebook
	}
	
	public static String getLoginUrl() {
		return baseUrl+"user/login";
	}
	
	public static String getSignUp() {
		return baseUrl+"user/signUp";
	}
	
	
	public static String getInstituteUrl() {
		return baseUrl+"institute/all";
	}
	
	
	/**
	 * Show Alert Toast message.
	 */
	public static void ShowToast(Context context,String msg){
		Toast mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		mToast.setGravity(Gravity.CENTER, 0, 0);
		mToast.show();
	}
	
	public static SharedPreferences getPrefs(Context context) {
		return context.getSharedPreferences("OfCampus", Activity.MODE_PRIVATE);
	}

	
	/**
	 * Check device Internet connection.
	 */
	public static boolean hasConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
						Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetwork != null && wifiNetwork.isConnected()) {
			return true;
		}

		NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mobileNetwork != null && mobileNetwork.isConnected()) {
			return true;
		}

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {
			return true;
		}
		return false;
	}
	
	public static void HideKeyBoard(Context context,View v){
		InputMethodManager imm = (InputMethodManager)context.getSystemService( Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}
	
	public final static boolean isValidEmail(String email) {
		if (email == null) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
		}
	}
	public final static boolean isValidEmail_again(String email) {
		String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}


	public static String Gender(String gender){
		if (gender.equalsIgnoreCase("male")) {
			return "0";
		}else {
			return "1";
		}
	}
	
	
	/**
	 * Name Value pair request.
	 */
	 public static String[] PostRequest(List<NameValuePair> postData,String url) {
			String res = "";
			String[] responData = { "", "" };
			try {
				HttpPost httppost = new HttpPost(url);
				httppost.setHeader("Content-Type","application/x-www-form-urlencoded");
				httppost.setEntity(new UrlEncodedFormEntity(postData, HTTP.UTF_8));
				HttpClient httpclient = new DefaultHttpClient();
				httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectTimeout);
				httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, socketTimeout);
				HttpResponse httpResponse = httpclient.execute(httppost);
				// httpResponse.getStatusLine();
				// HttpEntity entity = httpResponse.getEntity();

				res = EntityUtils.toString(httpResponse.getEntity());
				responData[0] = "200";
				responData[1] = res;
				
			} catch (ConnectTimeoutException e) {
				responData[0] = "205";
				e.printStackTrace();
			} catch (SocketTimeoutException e) {
				responData[0] = "205";
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				responData[0] = "205";
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				responData[0] = "205";
				e.printStackTrace();
			} catch (ParseException e) {
				responData[0] = "205";
				e.printStackTrace();
			} catch (IOException e) {
				responData[0] = "205";
				e.printStackTrace();
			}
			return responData;
		}
	 
	// HTTP GET request
	public static String[] sendGet(String url) throws Exception {
		String Response = "";

		String[] responData = { "", "" };

		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setConnectTimeout(connectTimeout); // set timeout to 5 seconds
			con.setReadTimeout(socketTimeout);

			// optional default is GET
			con.setRequestMethod("GET");
			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			// print result
			Response = response.toString();
			System.out.println(response.toString());
			responData[0] = "200";
			responData[1] = Response;
		} catch (java.net.SocketTimeoutException e) {
			e.printStackTrace();
			responData[0] = "205";
		} catch (Exception e) {
			e.printStackTrace();
			responData[0] = "205";
		}

		return responData;
	}
		
	
	 public static  String[] POST(String url, JSONObject jsonObject){
	      InputStream inputStream = null;
	      String result = "";
	      String[] responData = { "", "" };
	      try {
	          HttpClient httpclient = new DefaultHttpClient();
	          httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectTimeout);
	          httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, socketTimeout);
	          HttpPost httpPost = new HttpPost(url);
	          String json = "";
	          json = jsonObject.toString();
	          StringEntity se = new StringEntity(json);
	          httpPost.setEntity(se); 
	          httpPost.setHeader("Accept", "application/json");
	          httpPost.setHeader("Content-type", "application/json");
	          HttpResponse httpResponse = httpclient.execute(httpPost);
	          inputStream = httpResponse.getEntity().getContent();
	          if(inputStream != null)
	              result = convertInputStreamToString(inputStream);
	          else
	              result = "Did not work!";
	          responData[0] = "200";
	          responData[1] = result;

	      } catch (Exception e) {
	    	  responData[0] = "205";
	          Log.d("InputStream", e.getLocalizedMessage());
	      }
	      return responData;
	  }
	 
	  private static String convertInputStreamToString(InputStream inputStream) throws IOException{
	      BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
	      String line = "";
	      String result = "";
	      while((line = bufferedReader.readLine()) != null)
	          result += line;
	      inputStream.close();
	      return result;

	  }   
	 
	public static String getJsonValue(JSONObject jsObject, String Key) {
		String value = "";
		try {
			if (jsObject.has(Key)) {
				value = jsObject.getString(Key);
			} else {
				value = "";
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return value;
	}
}
