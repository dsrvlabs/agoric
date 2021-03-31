package com.dsrvlabs.agoric.telegrambot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dsrvlabs.common.db.CommonDao;
import com.dsrvlabs.common.util.Discord;
import com.dsrvlabs.common.util.ServletUtil;

/**
 * Servlet implementation class TestServlet
 */
@WebServlet("/server/TestServlet")
public class TestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=utf-8");
		HashMap<String, String> p = ServletUtil.paramToHashMapUtf8(request);
		
		String resultText = "";
		
		CommonDao dao = new CommonDao();
		ArrayList list = dao.commonSelectList("DbMapper.TestSelect", p);
		resultText += list.get(0).toString();
		
		HashMap map = (HashMap)list.get(0);
		System.out.println(map.get("address"));
		resultText += map.get("address");
		
		
		Discord.sendMsg("Test", "### p : " + p.toString() + " : " + resultText);
		
		response.getWriter().append("##### Served at: " + resultText);
	}

}
