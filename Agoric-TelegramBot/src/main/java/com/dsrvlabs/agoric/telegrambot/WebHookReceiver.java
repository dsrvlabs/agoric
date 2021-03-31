package com.dsrvlabs.agoric.telegrambot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.dsrvlabs.common.db.CommonDao;
import com.dsrvlabs.common.util.ServletUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@WebServlet("/WebHookReceiver.dsrv")
public class WebHookReceiver extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	// create GSON object
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();	
	static Logger logger = Logger.getLogger(WebHookReceiver.class.getName()); // Log4J
       
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=utf-8");
		HashMap<String, String> p = ServletUtil.paramToHashMapUtf8(request);
		
		HashMap returnMap = new HashMap();
		
		CommonDao dao = new CommonDao();
		ArrayList list = dao.commonSelectList("DbMapper.TestSelect", p);
		System.out.println(list.get(0));
		
		HashMap map = (HashMap)list.get(0);
		System.out.println(map.get("address"));
		
			
		response.getWriter().write(gson.toJson(returnMap));
	}
}
