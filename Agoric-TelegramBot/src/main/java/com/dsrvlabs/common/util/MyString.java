package com.dsrvlabs.common.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tomcat.util.codec.binary.Base64;

public class MyString {

	public static void main(String[] args) throws UnsupportedEncodingException {
		String s = "Aladdin:OpenSesame";
		
		s.getBytes("UTF-8");
		
		System.out.println(encodeBase64(s));
	}
	
	public static String urlStringToLinkTag(String text) {
		Pattern httpLinkPattern = Pattern.compile("(http[s]?)://(www\\.)?([\\S&&[^.@]]+)(\\.[\\S&&[^@]]+)");
		Pattern wwwLinkPattern = Pattern.compile("(?<!http[s]?://)(www\\.+)([\\S&&[^.@]]+)(\\.[\\S&&[^@]]+)");
		Pattern mailAddressPattern = Pattern.compile("[\\S&&[^@]]+@([\\S&&[^.@]]+)(\\.[\\S&&[^@]]+)");
		//String textWithHttpLinksEnabled = "ajdhkas www.dasda.pl/asdsad?asd=sd www.absda.pl maiandrze@asdsa.pl klajdld http://dsds.pl httpsda http://www.onet.pl https://www.onsdas.plad/dasda";
		String textWithHttpLinksEnabled = text;
		if (Objects.nonNull(textWithHttpLinksEnabled)) {

			Matcher httpLinksMatcher = httpLinkPattern.matcher(textWithHttpLinksEnabled);
			textWithHttpLinksEnabled = httpLinksMatcher.replaceAll("<a href=\"$0\" target=\"_blank\">$0</a>");

			final Matcher wwwLinksMatcher = wwwLinkPattern.matcher(textWithHttpLinksEnabled);
			textWithHttpLinksEnabled = wwwLinksMatcher.replaceAll("<a href=\"http://$0\" target=\"_blank\">$0</a>");

			final Matcher mailLinksMatcher = mailAddressPattern.matcher(textWithHttpLinksEnabled);
			textWithHttpLinksEnabled = mailLinksMatcher.replaceAll("<a href=\"mailto:$0\">$0</a>");

			System.out.println(textWithHttpLinksEnabled);
		}

		return textWithHttpLinksEnabled;
	}
	
	public static String nvl(String first, String second) {
		if(first == null) {
			return second;
		}
		return first;
	}
	
	public static String comma(String number) {
		DecimalFormat df = new DecimalFormat("###,###");
		return df.format(Integer.parseInt(number));
	}
	
	public static String comma(int number) {
		return comma( number + "");
	}
	
	public static String[] getCoinName(String coinFullName) {
		if( coinFullName == null || coinFullName.length() == 0 || coinFullName.trim().length() == 0 ) {
			return new String[3];
		}
		
		String[] returnValue = new String[3]; 
		String[] coinNameArray = coinFullName.split("\\(");
		
		returnValue[0] = coinNameArray[0].trim();
		returnValue[1] = coinNameArray[1].replaceAll("\\)", "").trim();
		returnValue[2] = coinFullName.trim();
		return returnValue;
	}
	
	public static String getOnlyNumber(String mixedNumber) {
		
		// $ 제거
		mixedNumber = mixedNumber.replaceAll("\\$", "").trim();
		// % 제거
		mixedNumber = mixedNumber.replaceAll("\\%", "").trim();
		// , 제거
		mixedNumber = mixedNumber.replaceAll("\\,", "").trim();
		
		if( mixedNumber.equals("?") ) {
			mixedNumber = "0"; 
		}
		return mixedNumber;
	}
	
	public static String encodeBase64(String text) {
		byte[] encodedBytes = Base64.encodeBase64(text.getBytes());
		return new String(encodedBytes);
	}
	
	public static String decodeBase64(String text) {
		byte[] decodedBytes = Base64.decodeBase64(text.getBytes());
		return new String(decodedBytes);
	}
	
	public static String byteToGigabyte(String byteString, int roundPosition) {
		BigDecimal bd = new BigDecimal(byteString).divide(new BigDecimal("1024")).divide(new BigDecimal("1024")).divide(new BigDecimal("1024"), 7, RoundingMode.HALF_UP); // 0, RoundingMode.HALF_UP
		return bd.setScale(roundPosition, RoundingMode.HALF_UP).toPlainString();
	}
}
