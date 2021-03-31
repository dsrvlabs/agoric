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
	
	private static String defaultBotApiKey = "000000000:AAAAA-BBBBB-CCCCC-DDDDD";
	private static String defaultchannelName = "@xxxxxxxx";
	
	/**
	 * 
	 * @param botApiKey : 889904794:AAHlqKD-ARChV2VfeFO-NMUyCEBFMuJ3XcI
	 * @param channelName : @lunawhale , @jongkwangchanneltest
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
		return sendMsgToChannel(defaultBotApiKey, id, msg);
	}

	public static HashMap sendMsgToChannel(String msg) {
		return sendMsgToChannel(defaultBotApiKey, defaultchannelName, msg);
	}

	public static void main(String[] args) {
		HashMap result = TelegramMsgSender.sendMsgToChannel("000000000:AAAAA-BBBBB-CCCCC-DDDDD", "@xxxxxxx", "Hello");
		System.out.println(result.get("ok"));

	}

}
