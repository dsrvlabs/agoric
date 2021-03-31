package com.dsrvlabs.agoric.telegrambot;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.dsrvlabs.common.util.Discord;
import com.dsrvlabs.common.util.MyString;
import com.dsrvlabs.common.util.ServletUtil;
import com.dsrvlabs.common.util.TelegramMsgSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@WebServlet("/server/WebHookReceiver")
public class WebHookReceiver extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	// create GSON object
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();	
	static Logger logger = Logger.getLogger(WebHookReceiver.class.getName()); // Log4J
       
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=utf-8");
		HashMap<String, String> p = ServletUtil.paramToHashMapUtf8(request);
		
		// request info : fromId, text
		String body = MyString.getBodyFormHttpRequest(request);	// Telegram 의 WebHook 은 body 에 jsonText 가 담겨서 오기 때문에 body를 읽는다.
		ObjectMapper jacksonMapper = new ObjectMapper();
		HashMap map = jacksonMapper.readValue(body, HashMap.class);	// jsonText to HashMap
		String fromId = ((HashMap)((HashMap)map.get("message")).get("from")).get("id").toString();	// 봇에게 메세지를 보내는사람의 Telegram ID
		String cmd = ((HashMap)map.get("message")).get("text").toString();	// 봇에게 보낸 메세지
		
		String msg = null;
		if( cmd.equals("/start") || cmd.equals("/help") ) {
			caseStartOrHelp(fromId);
			
		} else if ( cmd.equals("/BlockchainData") ) {
			goSeleniumWorker(fromId, cmd);
			
		} else if ( cmd.equals("/ValidatorList") ) {
			goSeleniumWorker(fromId, cmd);
			
		} else if ( cmd.startsWith("/watch ") ) {
			goSeleniumWorker(fromId, cmd);
			
		} else {
			caseElse(fromId);
		}
		
		//Discord.sendMsg("Test", "### END");
		
		response.setStatus(HttpServletResponse.SC_OK);
	}

	private void caseElse(String fromId) {
		String msg;
		msg = "Sorry. I did not understand your command. Please use the following commans.\n";
		msg += "*Features*\n";
		msg += "/start - get help manual\n";
		msg += "/help - get help manual\n";
		msg += "/BlockchainData - get Agoric blockchain data\n";
		msg += "/ValidatorList - get validator data\n";
		msg += "/ValidatorAddress - get validator data\n";
		msg += "/agoricvaloper1ns570lyx8lxevgtva6xdunjp0d35y3z32w3z6c - get validator data\n";
		TelegramMsgSender.sendMsgToChannel(fromId, msg);
	}


	private void goSeleniumWorker(String fromId, String cmd) {
		
		SeleniumWorker worker = new SeleniumWorker(fromId, cmd);
		TelegramMsgSender.sendMsgToChannel(fromId, "Please wait");
		worker.run();
	}

	private void caseStartOrHelp(String fromId) {
		String msg;
		msg = "Hello, Welcome to *Agoric TelegramBot!* \n\n";
		msg += "*Features*\n";
		msg += "/start - get help manual\n";
		msg += "/help - get help manual\n";
		msg += "/AgoricData - get Agoric blockchain data\n";
		msg += "/ValidatorList - get validator data\n";
		msg += "/ValidatorAddress - get validator data\n";
		msg += "/agoricvaloper1ns570lyx8lxevgtva6xdunjp0d35y3z32w3z6c - get validator data\n";
		TelegramMsgSender.sendMsgToChannel(fromId, msg);
	}
}
