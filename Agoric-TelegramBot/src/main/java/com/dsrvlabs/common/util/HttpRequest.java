package com.dsrvlabs.common.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpRequest {
	
	static ObjectMapper mapper = new ObjectMapper();
	static Logger logger = Logger.getLogger(HttpRequest.class.getName()); // Log4J
	
	public static void main(String[] args) throws Exception {
	}

	public static String readBody(HttpServletRequest request) {
		StringBuilder builder = new StringBuilder();
		try {
	        BufferedReader input = new BufferedReader(new InputStreamReader(request.getInputStream()));
	        String buffer;
	        while ((buffer = input.readLine()) != null) {
	            if (builder.length() > 0) {
	                builder.append("\n");
	            }
	            builder.append(buffer);
	        }
        } catch( IOException ioe ) {
        	ioe.printStackTrace();
        	logger.error(ioe.getMessage());
        }
        return builder.toString();
    }
	
	
	
	public static String sendRequestRaw(String GET_or_POST, String url, Map headerMap, Map bodyMap) {
		// Result to return
		StringBuffer result = new StringBuffer();
		
		try {
			URL obj = new URL(url);
			HttpURLConnection con = null;
			
			// HTTPS or HTTP 구분하여 객체 생성
			if( url.startsWith("https://") ) {
				con = (HttpsURLConnection) obj.openConnection();
			} else {
				con = (HttpURLConnection) obj.openConnection();
			}
			
			// Set GET/POST Method
			con.setRequestMethod(GET_or_POST);	// GET or POST
			con.setConnectTimeout(60 * 1000);
			con.setReadTimeout(60 * 1000);
			
			// Add request header
			con.setRequestProperty("Host", "hostdomain.com");
			con.setRequestProperty("Accept", "*/*");
			con.setRequestProperty("Connection", "keep-alive");
			con.setRequestProperty("Cache-Control", "no-cache");
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 OPR/45.0.2552.882");
			
			for( Object key : headerMap.keySet() ){
	            logger.debug( String.format("키 : %s, 값 : %s", key.toString(), headerMap.get(key.toString())) );
	            con.setRequestProperty( key.toString(), headerMap.get(key.toString()).toString());    
	        }
			
			// POST 방식 파라미터 셋팅
			if( bodyMap != null ) {
				String paramsBody = mapper.writeValueAsString(bodyMap);
				
				con.setRequestProperty("Content-Length", paramsBody.getBytes("UTF-8").length + "");
				logger.debug("##### length : " + paramsBody.getBytes("UTF-8").length);
				
				con.setDoOutput(true);
				DataOutputStream wr = new DataOutputStream(con.getOutputStream());
				logger.debug(paramsBody);
				
				wr.writeBytes(paramsBody);	// paramsBody 셋팅
				wr.flush();
				wr.close();
			}
			
			// Get responseCode (200, 404, 501)
			int responseCode = con.getResponseCode();
			logger.debug("### responseCode : " + responseCode);
			
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
	
	
	// HTTP GET request
	public static String sendHttpRequest(String url, Map params, String GET_OR_POST) {
		return sendHttpRequest(url, params, GET_OR_POST, 3, 60 * 1000);
	}
	public static String sendHttpRequest(String url, Map params, String GET_OR_POST, int max_retries, int timeout) {
		
		for (int tries = 0; tries < max_retries /* MAX_HTTP_REQUEST_TRIES */; tries++) {
			
			// Result to return
			StringBuffer result = new StringBuffer();
			
			String full_url = url;
			
			// GET방식이고 파라미터가 있으면 URL에 붙인다. 
			if( GET_OR_POST.equals("GET") && params != null ) {
				full_url = url + "?" + mapToParameterString(params);
			}
			
			try {
				// HTTP 연결 생성
				URL obj = new URL(full_url);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
				
				// Set GET/POST Method
				con.setRequestMethod(GET_OR_POST);	// GET or POST
				
				// Add request header
				con.setRequestProperty("Connection", "Keep-Alive");
				con.setRequestProperty("Accept", "*/*");
				con.setRequestProperty("Host", "hostdomain.com");
				con.setRequestProperty("Referer", "http://blog.naver.com/PostView.nhn?");
				con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; Win32; WinHttp.WinHttpRequest.5)");
				con.setRequestProperty("Content-Length", "0");
				
				con.setConnectTimeout(timeout);
				con.setReadTimeout(timeout);
				
				// POST 방식 파라미터 셋팅
				if( GET_OR_POST.equals("POST") && params != null ) {
					String urlParameters = mapToParameterString(params);
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
				continue; // retry
			}
			return result.toString();
		}
		
		return null;
	}
	
	// HTTP GET request
	public static String sendHttpsRequest(String url, Map params, String GET_OR_POST) {
		
		// Result to return
		StringBuffer result = new StringBuffer();
		
		// GET방식이고 파라미터가 있으면 URL에 붙인다. 
		if( GET_OR_POST.equals("GET") && params != null ) {
			url = url + "?" + mapToParameterString(params);
		}
		
		try {
			// HTTP 연결 생성
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
	
			// Set GET/POST Method
			con.setRequestMethod(GET_OR_POST);	// GET or POST
	
			// Add request header
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Accept", "*/*");
			con.setRequestProperty("Host", "hostdomain.com");
			con.setRequestProperty("Referer", "http://blog.naver.com/PostView.nhn?");
			con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; Win32; WinHttp.WinHttpRequest.5)");
			con.setRequestProperty("Content-Length", "0");
			
			con.setConnectTimeout(10000);
			con.setReadTimeout(10000);
	
			// POST 방식 파라미터 셋팅
			if( GET_OR_POST.equals("POST") && params != null ) {
				String urlParameters = mapToParameterString(params);
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
	
	public static String mapToParameterString(Map map) {
		StringBuilder sb = new StringBuilder();

		Set<?> set = map.keySet();
		Iterator<?> it = set.iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			if (key != null) {
				try {
					sb.append(key + "=" + URLEncoder.encode(map.get(key).toString(), "UTF-8") + "&");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
		}
		
		if (sb.length() != 0) return sb.toString().substring(0, sb.toString().length()-1);
		else return "";
	}

}
