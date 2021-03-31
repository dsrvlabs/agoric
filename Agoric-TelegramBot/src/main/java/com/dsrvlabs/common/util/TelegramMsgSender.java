package com.dsrvlabs.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TelegramMsgSender {
	
	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	static Logger logger = Logger.getLogger(TelegramMsgSender.class.getName());
	
	private static String defaultBotApiKey = null;
	private static String defaultchannelName = null;
	
	/**
	 * 
	 * @param botApiKey : 88990000094:AAHlqKD-ARChV2VfeFO-NxxxxxxxxXcI
	 * @param channelName : @xxxxxx , @xxxxxxx
	 * @param msg : "Hello"
	 * @return sample : {"ok":true,"result":{"message_id":2,"chat":{"id":-00000000,"title":"My Title","username":"myusername","type":"channel"},"date":1562237619,"text":"Hello"}}
	 */
	public static HashMap sendMsgToChannel(String botApiKey, String channelName, String msg) {
		try {
			msg = URLEncoder.encode(msg, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String jsonText = HttpRequest.sendHttpsRequest("https://api.telegram.org/bot"+ botApiKey +"/sendMessage?parse_mode=markdown&chat_id="+ channelName +"&text=" + msg, null, "GET");
		
		HashMap map = new HashMap();
		map = (HashMap) gson.fromJson(jsonText, map.getClass());
		
		return map;
	}
	
	public static HashMap sendMsgToChannel(String id, String msg) {
		if( defaultBotApiKey == null ) {
			PropertiesManager propertiesManager = new PropertiesManager("/var/www/agoric-telegrambot.dsrvlabs.net/WEB-INF/classes/Config.properties");
			defaultBotApiKey = propertiesManager.getKey("defaultBotApiKey");
		}
		
		return sendMsgToChannel(defaultBotApiKey, id, msg);
	}

	public static HashMap sendMsgToChannel(String msg) {
		if( defaultBotApiKey == null ) {
			PropertiesManager propertiesManager = new PropertiesManager("/var/www/agoric-telegrambot.dsrvlabs.net/WEB-INF/classes/Config.properties");
			defaultBotApiKey = propertiesManager.getKey("defaultBotApiKey");
		}
		if( defaultchannelName == null ) {
			PropertiesManager propertiesManager = new PropertiesManager("/var/www/agoric-telegrambot.dsrvlabs.net/WEB-INF/classes/Config.properties");
			defaultchannelName = propertiesManager.getKey("defaultchannelName");
		}
		return sendMsgToChannel(defaultBotApiKey, defaultchannelName, msg);
	}

	public static void main(String[] args) {
		TelegramMsgSender.sendMsgToChannel("166492352", "Hello TASD");

	}

}
