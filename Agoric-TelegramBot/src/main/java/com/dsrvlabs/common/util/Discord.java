package com.dsrvlabs.common.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Discord {
	
	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();	
	static Logger logger = Logger.getLogger(Discord.class.getName()); // Log4J
	
	private static String WEBHOOK_URL = null;
	
	public static void main(String[] args) {
		//SendDiscordMsg.sendMsg("Platform Bot", "Test by Jongkwang");
		Discord.sendMsg("Agoric TelegramBot", "Test1");
	}
	
	public static void sendMsg(String username, String content) {
		
		if( WEBHOOK_URL == null ) {
			PropertiesManager propertiesManager = new PropertiesManager("/var/www/agoric-telegrambot.dsrvlabs.net/WEB-INF/classes/Config.properties");
			WEBHOOK_URL = propertiesManager.getKey("WEBHOOK_URL");
			logger.debug("#### Load WebhookURL");
		}
		
		HashMap map = new HashMap();
		map.put("username", username);
		map.put("content", content);
		
		String result = sendHttpsRequest(WEBHOOK_URL, map, "POST");
	}
	
	// HTTP GET request
	public static String sendHttpsRequest(String url, Map params, String GET_OR_POST) {
		
		// Result to return
		StringBuffer result = new StringBuffer();
		
		try {
			// HTTP 연결 생성
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
	
			// Set GET/POST Method
			con.setRequestMethod(GET_OR_POST);	// GET or POST
	
			// Add request header
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "*/*");
			
			
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Host", "blog.naver.com");
			con.setRequestProperty("Referer", "http://blog.naver.com/PostView.nhn?");
			con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; Win32; WinHttp.WinHttpRequest.5)");
			con.setRequestProperty("Content-Length", "0");
			
			con.setConnectTimeout(10000);
			con.setReadTimeout(10000);
	
			// POST 방식 파라미터 셋팅
			if( GET_OR_POST.equals("POST") && params != null ) {
				String urlParameters = gson.toJson(params);
				con.setDoOutput(true);
				DataOutputStream wr = new DataOutputStream(con.getOutputStream());
				wr.writeBytes(urlParameters);
				wr.flush();
				wr.close();
			}
	
			// Get responseCode (200, 404, 501)
			int responseCode = con.getResponseCode();
			logger.debug(responseCode);
			
			// Get response text
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				result.append(inputLine);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return result.toString();
	}
}
