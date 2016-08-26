package com.dyz.persist.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateUtil {

	public static final String maskA = "yyyy年MM月dd日 HH:mm";
	public static final String maskB = "yyyy-MM-dd HH:mm";
	public static final String maskC = "yyyy-MM-dd HH:mm:ss";
	public static final String maskD = "chinese";
	public static final String maskE = "yyyyMMdd";
	public static final String maskF = "long10";
	public static final String maskG = "long13";
	public static final String maskH = "HH:mm";
	public static final String maskI = "MM-dd";
	public static final String maskK = "yyyyMMdd HH:mm:ss";
	public static final String maskL = "yyyy-MM-dd";
	public static final String maskYmdhms = "yyyyMMddHHmmss";
	public static final String maskM ="MM月dd日HH:mm";
	public static final String maskN ="yyyy年MM月dd日HH:mm:ss";
	public static final String maskO ="yyyy年MM月dd日 HHmm";
	/**
	 * @throws ParseException
	 *             将指定格式的字符串转换为Date
	 * @Title toMaskDate
	 * @Description TODO
	 * @para @param dateStr
	 * @para @param mask
	 * @para @return
	 * @retur Date
	 * @throws
	 */
	public static Date toMaskDate(String dateStr, String mask)
			throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(mask);
		return format.parse(dateStr);
	}
	/**
	 * @return 
	 * @throws ParseException
	 *            日期之间相差天数,
	 * @Title toMaskDate
	 * @Description TODO
	 * @para @param dateStr
	 * @para @param mask
	 * @para @return
	 * @retur Date
	 * @throws
	 */
	@SuppressWarnings("deprecation")
	public static int compare(Date date, Date annoDate)
			throws ParseException {
		if(date.getYear() ==annoDate.getYear() && date.getMonth() ==annoDate.getMonth()){
			return date.getDate()-annoDate.getDate() ;
		}
		return 0;
	}
	/**
	 * @throws ParseException
	 *             将指定格式的字符串转换为Date
	 * @Title toMaskDate
	 * @Description TODO
	 * @para @param dateStr
	 * @para @param mask
	 * @para @return
	 * @retur Date
	 * @throws
	 */
	public static Date toChangeDate(Date date, String mask)
			throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(mask);
		String dateStr = toDefineString(date, mask);
		return format.parse(dateStr);
	}

	/**
	 * @throws ParseException 
	 * 如果mask为Chinese
	 * 
	 * @Title: toMaskDateChinese
	 * @Description: TODO
	 * @param @param dateStr
	 * @param @return
	 * @return Date
	 * @throws
	 */
	public static Date toMaskDateChinese(String dateStr) throws ParseException {
		Date current = new Date();
		// 5分钟前
		String regexMinutesPre = "(\\d+)\\s*分钟前\\s*";
		// 5小时前
		String regexHourPre = "(\\d+)\\s*小时前\\s*";
		// 5天前
		String regexDayPre = "(\\d+)\\s*天前\\s*";
		// 刚刚
		String regexNowPre = "\\s*刚刚\\s*";
		//今天16:06
		String todayTimePre = "\\s*今天\\s*(\\d{2}:\\d{2})\\s*";
		if (StringUtil.isEmpty(dateStr)) {
			return null;
		}

		int preInt = 0;
		if (dateStr.matches(regexMinutesPre)) {
			preInt = Integer.valueOf(StringUtil.getRegexStr(dateStr,
					regexMinutesPre));
			return addMinute(current, preInt * (-1));
		} else if (dateStr.matches(regexHourPre)) {
			preInt = Integer.valueOf(StringUtil.getRegexStr(dateStr,
					regexHourPre));
			return addHour(current, preInt * (-1));
		} else if (dateStr.matches(regexDayPre)) {
			preInt = Integer.valueOf(StringUtil.getRegexStr(dateStr,
					regexDayPre));
			return addDate(current, preInt * (-1));
		} else if (dateStr.matches(regexNowPre)) {
			return new Date();
		}else if (dateStr.matches(todayTimePre)) {
			String srcDate = StringUtil.getRegexStr(dateStr, todayTimePre);
			srcDate = toDefineString(new Date(), maskL)+" "+srcDate+":00";
			return DateUtil.toMaskDate(srcDate, maskC);
		} else {
			return null;
		}

	}

	/**
	 * 将日期型的日期转为换yyyymmddhhmmss
	 * 
	 * @Title: toYmdhmsString
	 * @Description: TODO
	 * @param @param date
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String toYmdhmsString(Date date) {
		SimpleDateFormat format = new SimpleDateFormat(maskYmdhms);
		return format.format(date);
	}

	/**
	 * 将日期型的日期转为换yyyymmddhhmmss
	 * 
	 * @Title: toYmdhmsString
	 * @Description: TODO
	 * @param @param date
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String toDefineString(Date date, String mask) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat format = new SimpleDateFormat(mask);
		return format.format(date);
	}

	/**
	 * 将日期转换为年月日
	 * 
	 * @Title: toymd000Date
	 * @Description: TODO
	 * @param @param date
	 * @param @return
	 * @return Date
	 * @throws
	 */
	public static Date toymd000Date(Date date) {
		Calendar cl = Calendar.getInstance();
		cl.setTime(date);
		cl.set(Calendar.HOUR_OF_DAY, 0);
		cl.set(Calendar.MINUTE, 0);
		cl.set(Calendar.SECOND, 0);
		cl.set(Calendar.MILLISECOND, 0);
		return cl.getTime();
	}

	/**
	 * 支持多个mask
	 * 
	 * @Title: toDefineMaskDate
	 * @Description: TODO
	 * @param @param dateStr
	 * @param @param mask
	 * @param @return
	 * @param @throws ParseException
	 * @return Date
	 * @throws
	 */
	public static Date toDefineMoreMaskDate(String dateStr, String moreMask)
			throws ParseException {
		if (StringUtil.isEmpty(dateStr) || StringUtil.isEmpty(moreMask)) {
			return null;
		}
		dateStr = dateStr.replaceAll("：", ":");
		String[] masks = moreMask.split("\\|");
		Date date = null;
		for (String mask : masks) {
			try {
				date = toDefineMaskDate(dateStr, mask.trim());
			} catch (Exception e) {
				date = null;
			}
			if(date != null){
				return date;
			}
		}
		
		return date;
	}

	/**
	 * @throws ParseException
	 *             自定义时间mask的解析
	 * 
	 * @Title: toDefineMaskDate
	 * @Description: TODO
	 * @param @param dateStr
	 * @param @param mask
	 * @param @return
	 * @return Date
	 * @throws
	 */
	public static Date toDefineMaskDate(String dateStr, String mask)
			throws ParseException {
		if (StringUtil.isEmpty(dateStr) || StringUtil.isEmpty(mask)) {
			return null;
		}

		if (maskA.equals(mask) || maskB.equals(mask)) {
			mask = mask + ":ss";
			dateStr = dateStr + ":00";
		} else if (maskF.equals(mask)) {
			// 10位的log
			return new Date(Long.valueOf(dateStr) * 1000L);
		} else if (maskG.equals(mask)) {
			// 13位的log
			return new Date(Long.valueOf(dateStr));
		} else if (maskD.equals(mask)) {
			Date d = toMaskDateChinese(dateStr);
			return d;
		} else if(maskH.equals(mask)){
			dateStr = DateUtil.toDefineString(new Date(), maskE)  + " " + dateStr +":00";
			Date d = toMaskDate(dateStr, maskK);
			return d;
		} else if(maskI.equals(mask)){
			dateStr = DateUtil.toDefineString(new Date(), "yyyy")  + "-" + dateStr;
			Date d = toMaskDate(dateStr, maskL);
			return d;
		}else if(maskM.equals(mask)){
			dateStr = DateUtil.toDefineString(new Date(), "yyyy")  + "年" + dateStr+":00";
			Date d = toMaskDate(dateStr, maskN);
			return d;
		}else if (maskO.equals(mask)) {
			dateStr = dateStr.substring(0, dateStr.length()-2)+":"+dateStr.substring(dateStr.length()-2)+":00";
			Date d = toMaskDate(dateStr, maskN);
			return d;
		} 

		return toMaskDate(dateStr, mask);

	}

	/**
	 * 指定日期增加天数
	 * 
	 * @Title: addDate
	 * @Description: TODO
	 * @param @return
	 * @return Date
	 * @throws
	 */
	public static Date addDate(Date date, int dayCount) {

		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		ca.set(Calendar.DAY_OF_MONTH, ca.get(Calendar.DAY_OF_MONTH) + dayCount);
		return ca.getTime();

	}

	public static void main(String[] args) throws ParseException {
		// String mask = "yyyy年MM月dd日hh:mm:ss";
//		 String dateStr = "2015年8月16日 1439";
//		  dateStr = dateStr.substring(0, dateStr.length()-2)+":"+dateStr.substring(dateStr.length()-2)+":00";
//		System.out.println(toDefineMaskDate(dateStr, "yyyy年MM月dd日 HH:mm:ss"));
	}

	/**
	 * 指定日期增加小时数
	 * 
	 * @Title: addDate
	 * @Description: TODO
	 * @param @return
	 * @return Date
	 * @throws
	 */
	public static Date addHour(Date date, int hour) {

		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		ca.set(Calendar.HOUR_OF_DAY, ca.get(Calendar.HOUR_OF_DAY) + hour);
		return ca.getTime();

	}

	/**
	 * 指定日期增加分钟数
	 * 
	 * @Title: addMinute
	 * @Description: TODO
	 * @param @return
	 * @return Date
	 * @throws
	 */
	public static Date addMinute(Date date, int minute) {

		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		ca.set(Calendar.MINUTE, ca.get(Calendar.MINUTE) + minute);
		return ca.getTime();

	}

	/**
	 * 根据秒获取时间间隔
	 * 
	 * @Title: getTimeInterval
	 * @Description: TODO
	 * @param @param time
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String getTimeInterval(int time) {
		String r;
		if (time <= 60) {
			r = time + "秒";
		} else if (time > 60 && time < 3600) {
			r = time / 60 + "分";
			if (time % 60 != 0) {
				r = r + time % 60 + "秒";
			}
		} else {
			r = time / 3600 + "小时";

			time = time - time / 3600 * 3600;

			r = r + time / 60 + "分";

			if (time % 60 != 0) {
				r = r + time % 60 + "秒";
			}
		}

		return r;
	}
	/**
	 * 根据出生日期(字符串(2015-12-11格式))算出年龄
	 * @param String
	 * @return Integer
	 */
	public static Integer  getAge(String birthStr){
		String nowDayStr = toDefineString(new Date(), maskL);
		String [] birthStrs = birthStr.split("-");
		String [] nowDayStrs = nowDayStr.split("-");
		int birthYearNum =  Integer.parseInt(birthStrs[0]);
		int birthMonthNum =  Integer.parseInt(birthStrs[1]);
		int birthDayNum =  Integer.parseInt(birthStrs[2]);
		
		int yearNum =  Integer.parseInt(nowDayStrs[0]);
		int monthNum =  Integer.parseInt(nowDayStrs[1]);
		int dayNum =  Integer.parseInt(nowDayStrs[2]);
		int age = 0;
		if( yearNum > birthYearNum ){
			age =  yearNum - birthYearNum;
			if(birthMonthNum > monthNum){
				age--;
			}
			else if(birthMonthNum == monthNum){
				if( dayNum < birthDayNum ){
					age--;
				}
			}
		}
		
		return age;
	}
}

