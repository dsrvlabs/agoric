package com.dsrvlabs.agoric.telegrambot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.dsrvlabs.common.db.CommonDao;
import com.dsrvlabs.common.util.HttpRequest;
import com.dsrvlabs.common.util.MyString;
import com.dsrvlabs.common.util.PropertiesManager;
import com.dsrvlabs.common.util.ServletUtil;
import com.dsrvlabs.common.util.TelegramMsgSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@WebServlet("/server/WebHookReceiver")
public class WebHookReceiver extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String CONFIG_FILE = "/var/www/agoric-telegrambot.dsrvlabs.net/WEB-INF/classes/Config.properties";
	
	static ObjectMapper mapper = new ObjectMapper();
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
		String fromId = ((HashMap)((HashMap)map.get("message")).get("from")).get("id").toString();	// sample : 166492353
		String cmd = ((HashMap)map.get("message")).get("text").toString();	// sample : agoric1ns570lyx8lxevgtva6xdunjp0d35y3z36kztxe
		
		// init userMap
		logger.debug("### fromId : " + fromId);
		logger.debug("### userMap.get(\"fromId\") : " + userMap.get(fromId));
		if( userMap.get(fromId) == null ) {
			logger.debug("### 10");
			HashMap<String, String> _map = new HashMap<String, String>();
			_map.put("menu", "start");
			userMap.put(fromId, _map);
			
			logger.debug("### 20");
		}
		logger.debug("### 30");
		String menu = userMap.get(fromId).get("menu");
		
		logger.debug("### menu : " + menu);
		logger.debug("### cmd : " + cmd);
		logger.debug("### cmd.length : " + cmd.length());
		
		if( menu.equals("MyReward") ) {
			
			if( cmd.startsWith("agoric") && cmd.length() == 45) {	// is it agoric address?
				commandMyReward_getReward(fromId, cmd);
			} else {
				commandMyReward(fromId, cmd);
				
			}
			
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

	private void commandMyReward_getReward(String fromId, String address) {
		PropertiesManager propertiesManager = new PropertiesManager(CONFIG_FILE);
		String RPC_SERVER_URL = propertiesManager.getKey("RPC_SERVER_URL");
		logger.debug(RPC_SERVER_URL);
		
		String text = HttpRequest.sendHttpRequest(RPC_SERVER_URL + "/distribution/delegators/"+address+"/rewards", null, "GET");
		Map resultMap = null;
		try {
			resultMap = mapper.readValue(text, Map.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		
		System.out.println("### height : " + resultMap.get("height"));
		System.out.println("### result : " + resultMap.get("result"));
		
		HashMap rewardsMap = (HashMap)resultMap.get("result");
		ArrayList rewardsList = (ArrayList)rewardsMap.get("rewards");
		ArrayList totalList = (ArrayList)rewardsMap.get("total");
		
		String msg = "Your address is ["+address+"](https://testnet.explorer.agoric.net/account/"+address+")\n\n";
		msg += "## Validator List\n";
		for( int i=0; i<rewardsList.size(); i++ ) {
			HashMap map = (HashMap)rewardsList.get(i);
			String validatorAddress = map.get("validator_address").toString();
			
			msg += "[" + validatorAddress.substring(0, 20) + "...](https://testnet.explorer.agoric.net/validator/"+address+")\n";
			
			ArrayList list = (ArrayList)map.get("reward");
			for( int j=0; j<list.size(); j++ ) {
				HashMap innerMap = (HashMap)list.get(j);
				String amountAndUnit = "  > " + innerMap.get("amount").toString() + " " + innerMap.get("denom").toString() + "\n";
				msg += amountAndUnit;
			}
		}
		
		msg += "\n";
		msg += "## Total\n";
		for( int i=0; i<totalList.size(); i++ ) {
			HashMap innerMap = (HashMap)totalList.get(i);
			String amountAndUnit = "  > " + innerMap.get("amount").toString() + " " + innerMap.get("denom").toString() + "\n";
			msg += amountAndUnit;
		}
		System.out.println("### msg  : " + msg);
		
		// reset menu
		userMap.get(fromId).put("menu", "start");	
		
		TelegramMsgSender.sendMsgToChannel(fromId, msg);
		
		
	}
	

	private void commandMyReward(String fromId, String cmd) {
		String msg;
		
		logger.debug("### fromId : " + fromId);
		logger.debug("### cmd : " + cmd);
		
		// set userMap
		userMap.get(fromId).put("menu", "MyReward");
		
		logger.debug("### menu of commandMyReward : " + userMap.get(fromId).get("menu"));
		
		
		// check address
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(fromId, fromId);
		CommonDao dao = new CommonDao();
		HashMap dbMap = dao.commonSelectOne("DbMapper.WebHookReceiver_commandMyReward_getAddress", params);
		logger.debug(dbMap);
		
		if( dbMap == null ) {
			logger.debug("### dbMap == null : " + dbMap == null);
			
			msg = "What is you Agoric address?\n\n";
			TelegramMsgSender.sendMsgToChannel(fromId, msg);
		} else {

			
			msg = "Your Address is .\n\n";
			msg += dbMap.get("address");
			TelegramMsgSender.sendMsgToChannel(fromId, msg);
		}
		
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
		//msg = "Hello, Welcome to *Agoric TelegramBot!* \n\n";
		msg = "*Currently under construction until today* \n\n";
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
}
