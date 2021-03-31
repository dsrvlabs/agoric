package com.dsrvlabs.common.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.log4j.Logger;

public class MyDate {
	
	static Logger logger = Logger.getLogger(MyDate.class.getName()); // Log4J

	public static void main(String[] args) throws ParseException {
		System.out.println(MyDate.getCurrnetDate("yyyy-MM-dd"));
		System.out.println(MyDate.addDateTime(MyDate.getCurrnetDate("yyyy-MM-dd"), "yyyy-MM-dd", 0, 0, -2, 0, 0, 0));

		
		System.out.println(getUnixTimestamp(-5));
		System.out.println(getUnixTimestamp(0));
		System.out.println(getUnixTimestamp(5));
		System.out.println(MyDate.getRFC3339(0));
		System.out.println(MyDate.getRFC3339(-10));
		System.out.println(MyDate.getRFC3339(-3600));
		
		long l = getUnixTimestamp(0);
		
		System.out.println( getDateString(l + ""));


	}
	
	/**
	 * 		// 24시간 표시법
		// HH : 0 -23
		// kk : 1 - 24
		// KK : 0 -11
		// hh : 1 - 12
	 * @throws ParseException
	 */
	public void sample1() throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd. HH:mm");
		Date currentTime = new Date();
		String dTime = formatter.format(currentTime);
		System.out.println(dTime);

		String newTime = MyDate.addDateTime(dTime, "yyyy.MM.dd. HH:mm", 0, 0, 0, +2, 0, 0);
		System.out.println(newTime);
		System.out.println(getCurrnetDate("yyyyMMdd_HHmmss"));
	}
	
	 /**
	  * @param  fromDate  fromDate   날짜 문자열
	  * @param  format  format  날짜 포멧
	  * @param addYear 가산・감산할 연
	  * @param addMonth 가산・감산할 월
	  * @param addDate 가산・감산할 일
	  * @param addHour 가산・감산할 시간
	  * @param addMinute 가산・감산할 분
	  * @param addSecond 가산・감산할 초
	  * @return String  날짜 문자열
	  * @throws ParseException
	  * 예)
	  * 1. 하루 후의 날짜 구하기 (fromData = yyyy-MM-dd )
	  * String toDate = addDate(fromDate, "yyyy-MM-dd HH:mm:ss", 0, 0, 1, 0, 0, 0);
	  * 2. 30분 후의 날짜 구하기
	  * String toDate = addDate(fromDate, "yyyy-MM-dd HH:mm:ss", 0, 0, 0, 0, 30, 0);
	  */
	public static String addDateTime(String fromDate, String format, int addYear, int addMonth, int addDate, int addHour, int addMinute, int addSecond) throws ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = sdf.parse(fromDate);
		Calendar cal = new GregorianCalendar();

		cal.setTime(date);
		cal.add(Calendar.YEAR, +addYear);
		cal.add(Calendar.MONTH, +addMonth);
		cal.add(Calendar.DATE, +addDate);
		cal.add(Calendar.HOUR_OF_DAY, +addHour);
		cal.add(Calendar.MINUTE, +addMinute);
		cal.add(Calendar.SECOND, +addSecond);

		SimpleDateFormat sdf2 = new SimpleDateFormat(format);
		String toDate = sdf2.format(cal.getTime());

		return toDate;
	}
	public static String addDate(String fromDate, String format, int addDate) {
		String returnValue = null;
		try {
			returnValue = addDateTime(fromDate, format, 0, 0, addDate, 0, 0, 0);
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		return returnValue;
	}
	public static String addDateFromToday(int addDate) throws ParseException {
		return addDateTime(getCurrnetDate("yyyy-MM-dd"), "yyyy-MM-dd", 0, 0, addDate, 0, 0, 0);
	}

	public static String getCurrnetDate(String format) {

		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String toDate = sdf.format(date);

		return toDate;
	}

	public static String getUTCtoSeoul(String timestamp) {
		Timestamp ts = new Timestamp(Long.parseLong(timestamp));
		Date date = new Date();
		date.setTime(ts.getTime());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC+9"));
		
		return formatter.format(date);
	}

	public static String getUTCZero(String timestamp) {
		Timestamp ts = new Timestamp(Long.parseLong(timestamp));
		Date date = new Date();
		date.setTime(ts.getTime());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC+0"));
		
		return formatter.format(date);
	}
	
	public static long getYYMMDDtoTimestamp(String yyyymmdd) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	    Date parsedDate = null;
		try {
			parsedDate = dateFormat.parse(yyyymmdd);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
	    
	    return timestamp.getTime();
	}
	
	public static long getUnixTimestamp(int addSecond) {
		Date now = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(now);
		cal.add(Calendar.SECOND, addSecond);
		Date adjustedDate = cal.getTime();
		
		long unixTimestamp = adjustedDate.getTime() / 1000L;
		
		return unixTimestamp;
	}
	
	public static String getRFC3339(int addSecond) {
		Date now = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(now);
		cal.add(Calendar.SECOND, addSecond);
		Date adjustedDate = cal.getTime();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		String toDate = sdf.format(adjustedDate);

		return toDate;
	}
	
	public static String getDateString(String unixTimestampString) {
		long timestamp = Long.parseLong(unixTimestampString);
	    Date date = new java.util.Date(timestamp*1000L); 
	    SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	    //sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9")); 
	    String formattedDate = sdf.format(date);
	    return formattedDate;
	}
}
