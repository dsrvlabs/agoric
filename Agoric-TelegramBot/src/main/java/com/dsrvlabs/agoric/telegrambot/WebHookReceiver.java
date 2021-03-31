package com.dsrvlabs.agoric.telegrambot;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.dsrvlabs.common.db.CommonDao;
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
		
		Discord.sendMsg("Test", "### Start : " + p.toString());
		
		// request info : fromId, text
		String body = MyString.getBodyFormHttpRequest(request);	// Telegram 의 WebHook 은 body 에 jsonText 가 담겨서 오기 때문에 body를 읽는다.
		ObjectMapper jacksonMapper = new ObjectMapper();
		HashMap map = jacksonMapper.readValue(body, HashMap.class);	// jsonText to HashMap
		String fromId = ((HashMap)((HashMap)map.get("message")).get("from")).get("id").toString();	// 봇에게 메세지를 보내는사람의 Telegram ID
		String cmd = ((HashMap)map.get("message")).get("text").toString();	// 봇에게 보낸 메세지
		
		Discord.sendMsg("Test", "### Text : " + cmd + " : " + fromId);
		
		String msg = null;
		if( cmd.equals("/start") || cmd.equals("/help") ) {
			caseStartOrHelp(fromId);
			
		} else if ( cmd.equals("/block_height") ) {
			caseBlockHeight(fromId);
			
		} else if ( cmd.equals("/list") ) {
			caseList(fromId);
			
		} else if ( cmd.startsWith("/watch ") ) {
			caseWatch(fromId, cmd);
			
		} else {
			caseElse(fromId);
		}
		
		Discord.sendMsg("Test", "### END");
		
		response.setStatus(HttpServletResponse.SC_OK);
	}

	private void caseElse(String fromId) {
		String msg;
		msg = "Sorry. I did not understand your command. Please use the following commans.\n";
		msg += "*Features*\n";
		msg += "/start - get help manual\n";
		msg += "/help - get help manual\n";
		msg += "/price - get price info\n";
		msg += "/list - get validator list\n";
		msg += "/watch 1 - watch the first validator of list. when validator commission rate is chaanged, you'll receive a info\n";
		msg += "/watchingList - get my all watching list\n";
		msg += "/watchingCancel - cancel all watching\n";
		TelegramMsgSender.sendMsgToChannel(fromId, msg);
	}

	private void caseWatch(String fromId, String text) {
		String msg;
		CommonDao dao = new CommonDao();
		String validatorNum = text.split(" ")[1];
		HashMap<String, String> paramWatch = new HashMap<String, String>();
		
		paramWatch.put("validatorNum", validatorNum);
		ArrayList<HashMap> listResult = dao.commonSelectList("WebMapper.TelegramWebHookReceiver_selectValidatorInfo", paramWatch);
		
		String name = listResult.get(listResult.size()-1).get("name").toString();
		String address = listResult.get(listResult.size()-1).get("address").toString();
		
		paramWatch.put("telegramId", fromId);
		paramWatch.put("address", address);
		
		try {
			dao.commonInsert("WebMapper.TelegramWebHookReceiver_insertTelegramWatch", paramWatch);
		} catch (Exception e) {
			// 중복인 경우 아무작업 하지 않음.
		}
		
		msg = "*Watching below validator*\n\n";
		msg += validatorNum + " : " + name + " is watched";
		
		TelegramMsgSender.sendMsgToChannel(fromId, msg);
	}

	private void caseList(String fromId) {
		String msg;
		CommonDao dao = new CommonDao();
		HashMap result = dao.commonSelectOne("WebMapper.TelegramWebHookReceiver_listSum", null);
		ArrayList<HashMap> listResult = dao.commonSelectList("WebMapper.TelegramWebHookReceiver_list", null);
		
		BigDecimal totalToken = new BigDecimal(result.get("totalToken").toString());
		System.out.println( totalToken.toPlainString() );
		
		HashMap row = null;
		BigDecimal token = null;
		BigDecimal votingPower = null;
		BigDecimal commission = null;
		msg = "*Validator List*\n";
		for( int i=0; i<listResult.size(); i++ ) {
			row = listResult.get(i);
			token = new BigDecimal(row.get("tokens").toString());
			votingPower = token.divide(totalToken, 8, BigDecimal.ROUND_UP).multiply(new BigDecimal("100")).divide(BigDecimal.ONE, 2, BigDecimal.ROUND_UP);
			commission = new BigDecimal(row.get("commission").toString()).multiply(new BigDecimal("100")).divide(BigDecimal.ONE, 0, BigDecimal.ROUND_UP);
			
			msg += (i + 1) + " : " + row.get("name").toString() + " (Voting Power : " + votingPower.toPlainString() + "%, Commission Rate : " + commission.toPlainString() + "%)\n";
		}
		
		TelegramMsgSender.sendMsgToChannel(fromId, msg);
	}
	


	private void caseBlockHeight(String fromId) {
		
		SeleniumWorker worker = new SeleniumWorker(fromId, null);
		
		TelegramMsgSender.sendMsgToChannel(fromId, "Start caseBlockHeight");
		worker.run();
		TelegramMsgSender.sendMsgToChannel(fromId, "End caseBlockHeight");
	}

	private void caseStartOrHelp(String fromId) {
		String msg;
		msg = "Hello, I'm the Agoric TelegramBot! \n";
		msg += "*Features*\n";
		msg += "/start - get help manual\n";
		msg += "/block_height - get block height\n";
		msg += "/price - get price info\n";
		msg += "/list - get validator list\n";
		msg += "/watch 1 - watch the first validator of list. when validator commission rate is changed, you'll receive a info\n";
		msg += "/watchingCancel - cancel all watching\n";
		TelegramMsgSender.sendMsgToChannel(fromId, msg);
	}
}
