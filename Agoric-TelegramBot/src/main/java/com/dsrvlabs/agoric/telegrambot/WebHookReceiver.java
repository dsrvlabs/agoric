package com.dsrvlabs.agoric.telegrambot;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.dsrvlabs.common.db.CommonDao;
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
	
	private HashMap<String, HashMap<String, String>> userMap = new HashMap<String, HashMap<String, String>>();
       
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=utf-8");
		HashMap<String, String> p = ServletUtil.paramToHashMapUtf8(request);
		
		// request info : fromId, text
		String body = MyString.getBodyFormHttpRequest(request);
		ObjectMapper jacksonMapper = new ObjectMapper();
		HashMap map = jacksonMapper.readValue(body, HashMap.class);	// jsonText to HashMap
		String fromId = ((HashMap)((HashMap)map.get("message")).get("from")).get("id").toString();
		String cmd = ((HashMap)map.get("message")).get("text").toString();
		
		// init userMap
		logger.debug("### fromId : " + fromId);
		logger.debug("### userMap.get(\"fromId\") : " + userMap.get("fromId"));
		if( userMap.get("fromId") == null ) {
			logger.debug("### 10");
			HashMap<String, String> _map = new HashMap<String, String>();
			_map.put("menu", "start");
			userMap.put(fromId, _map);
			
			logger.debug("### 20");
		}
		logger.debug("### 30");
		String menu = userMap.get("fromId").get("menu");
		
		logger.debug("### menu : " + menu);
		
		if( menu.equals("MyReward") ) {
			commandMyReward(fromId, cmd);
			
		} else {
			
			if( cmd.equals("/start") || cmd.equals("/help") ) {
				caseStartOrHelp(fromId);
				
			} else if ( cmd.equals("/MyReward") ) {
				commandMyReward(fromId, cmd);
				
			} else if ( cmd.equals("/BlockchainData") ) {
				goSeleniumWorker(fromId, cmd);
				
			} else if ( cmd.equals("/ValidatorList") ) {
				goSeleniumWorker(fromId, cmd);
				
			} else if ( cmd.startsWith("/agoricv") ) {
				goSeleniumWorker(fromId, cmd);
				
			} else {
				caseElse(fromId);
			}
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
	}

	private void commandMyReward(String fromId, String cmd) {
		
		logger.debug("### fromId : " + fromId);
		logger.debug("### cmd : " + cmd);
		
		// set userMap
		userMap.get(fromId).put("menu", "MyReward");
		
		logger.debug("### menu of commandMyReward : " + userMap.get(fromId).get("menu"));
		
		
		// check address
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("fromId", fromId);
		CommonDao dao = new CommonDao();
		HashMap dbMap = dao.commonSelectOne("DbMapper.WebHookReceiver_commandMyReward_getAddress", params);
		logger.debug(dbMap);
		
		
		String msg;
		msg = "commandMyReward.\n\n";
		msg += "*Hello*\n";
		msg += "- fromId : " + fromId + "\n";
		msg += "- cmd : " + cmd + "\n";
		TelegramMsgSender.sendMsgToChannel(fromId, msg);
	}

	private void caseWatch(String fromId, String cmd) {
		String msg;
		msg = "Sorry. This feature is still under development.\n\n";
		msg += "*Features*\n";
		msg += "- Check every 3 minutes\n";
		msg += "- Push alarm in case of failurel\n";
		TelegramMsgSender.sendMsgToChannel(fromId, msg);
	}

	private void caseElse(String fromId) {
		String msg;
		msg = "Sorry. I did not understand your command. Please use the following commans.\n\n";
		msg += "*Features*\n";
		msg += "/start - Get help manual\n";
		msg += "/help - Get help manual\n";
		msg += "/MyReward - Get unclaimed reward\n";
		msg += "/BlockchainData - Get Agoric blockchain data\n";
		msg += "/ValidatorList - Get validator data\n";
		msg += "/ValidatorAddress - Get validator data\n";
		msg += "/agoricvaloper1ns570lyx8lxevgtva6xdunjp0d35y3z32w3z6c - Sample\n";
		//msg += "/Watch - Watch node status and push notification\n";
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
		msg += "/start - Get help manual\n";
		msg += "/help - Get help manual\n";
		msg += "/BlockchainData - Get Agoric blockchain data\n";
		msg += "/ValidatorList - Get validator data\n";
		msg += "/ValidatorAddress - Get validator data\n";
		msg += "/agoricvaloper1ns570lyx8lxevgtva6xdunjp0d35y3z32w3z6c - Sample\n";
		msg += "/Watch - Watch node status and push notification\n";
		TelegramMsgSender.sendMsgToChannel(fromId, msg);
	}
}
