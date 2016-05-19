package com.puff.framework.utils;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.TimeZone;

import com.puff.framework.time.FastDateFormat;
import com.puff.log.Log;
import com.puff.log.LogFactory;

public final class DateTime {
	public static final String DATE_SEPARATOR = "-";
	public static final String TIME_SEPARATOR = ":";
	public static final String DATE_TIME_SEPARATOR = " ";
	public static final String DATE_FORMAT_STR = "yyyy-MM-dd";
	public static final String TIME_FORMAT_STR = "HH:mm:ss";
	public static final String TIMESTAMP_STR = "yyyy-MM-dd HH:mm:ss";
	public static final String MSEL_FORMAT_STR_1 = "yyyy-MM-dd HH:mm:ss,SSS";
	public static final String MSEL_FORMAT_STR_2 = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final int DATE_FORMAT_STR_LENGTH = 10;
	public static final int TIMESTAMP_STR_LENGTH = 19;
	public static final int MSEL_FORMAT_STR_LENGTH = 23;
	public static final long MILLIS_IN_ONE_MIN = 60000L;
	public static final long MILLIS_IN_ONE_HOUR = 3600000L;
	public static final long MILLIS_IN_ONE_DAY = 86400000L;
	public static final int YEAR = 1;
	public static final int MONTH = 2;
	public static final int DAY = 3;
	public static final int HOUR = 4;
	public static final int MINUTE = 5;
	public static final int SECOND = 6;
	public static final int MILLISECOND = 7;
	public static final int QUATER = 11;
	public static final int WEEK = 12;
	public static final int DAYS_OF_MONTH = 13;
	public static final int WEEKS_OF_MONTH = 14;
	public static final int DAYS_OF_YEAR = 15;
	public static final int WEEKS_OF_YEAR = 16;
	public static final int[] DAYS = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

	protected static final Log LOG = LogFactory.get(DateTime.class);

	public static java.util.Date toDate(java.sql.Date date) {
		return new java.util.Date(date.getTime());
	}

	public static java.util.Date toDate(Timestamp timestamp) {
		return timestamp;
	}

	public static java.util.Date toDate(Object obj) {
		if (obj == null) {
			return null;
		}
		if (((obj instanceof java.util.Date)) || ((obj instanceof java.sql.Date)) || ((obj instanceof Timestamp))) {
			return (java.util.Date) obj;
		}
		if ((obj instanceof String)) {
			return toDate(obj.toString());
		}
		throw new RuntimeException("the parameter [" + obj + " can't be converted to java.util.Date");
	}

	public static java.util.Date toDate(String input) {
		String dealStr = input.trim();
		int pos = dealStr.indexOf("'");
		if (pos == 0) {
			dealStr = dealStr.substring(1, dealStr.length());
		}
		pos = dealStr.lastIndexOf("'");
		if (pos == dealStr.length() - 1) {
			dealStr = dealStr.substring(0, dealStr.length() - 1);
		}
		int length = dealStr.length();
		FastDateFormat sdf = null;
		if ((length == DATE_FORMAT_STR_LENGTH) && (dealStr.charAt(4) == '-') && (dealStr.charAt(7) == '-')) {
			sdf = FastDateFormat.getInstance(DATE_FORMAT_STR);
		} else if ((length == TIMESTAMP_STR_LENGTH) && (dealStr.charAt(4) == '-') && (dealStr.charAt(7) == '-') && (dealStr.charAt(10) == ' ') && (dealStr.charAt(13) == ':')
				&& (dealStr.charAt(16) == ':')) {
			sdf = FastDateFormat.getInstance(TIMESTAMP_STR);
		} else if ((length == MSEL_FORMAT_STR_LENGTH) && (dealStr.charAt(4) == '-') && (dealStr.charAt(7) == '-') && (dealStr.charAt(10) == ' ') && (dealStr.charAt(13) == ':')
				&& (dealStr.charAt(16) == ':') && ((dealStr.charAt(19) == ',') || (dealStr.charAt(19) == '.'))) {
			sdf = FastDateFormat.getInstance(MSEL_FORMAT_STR_1);
			if (dealStr.charAt(19) == '.') {
				sdf = FastDateFormat.getInstance(MSEL_FORMAT_STR_2);
			}
		}
		if (null == sdf)
			throw new IllegalArgumentException("the format of [" + input + "] is not supported...");
		try {
			return sdf.parse(dealStr);
		} catch (ParseException e) {
			LOG.warn("error occured when parsing String [" + input + "] to java.util.Date with the format [" + sdf + "].");
		}
		return null;
	}

	public static java.util.Date toDate(String date, String pattern) {
		try {
			return FastDateFormat.getInstance(pattern).parse(date);
		} catch (ParseException e) {
		}
		return null;
	}

	public static java.sql.Date toSqlDate(java.util.Date date) {
		return new java.sql.Date(date.getTime());
	}

	public static java.sql.Date toSqlDate(Object obj) {
		if (obj == null) {
			return null;
		}
		if ((obj instanceof java.sql.Date)) {
			return (java.sql.Date) obj;
		}
		java.util.Date date = toDate(obj);
		return new java.sql.Date(date.getTime());
	}

	public static Timestamp toTimestamp(java.util.Date date) {
		return new Timestamp(date.getTime());
	}

	public static Timestamp toTimestamp(Object obj) {
		if (obj == null) {
			return null;
		}
		if ((obj instanceof Timestamp)) {
			return (Timestamp) obj;
		}
		java.util.Date date = toDate(obj);
		return new Timestamp(date.getTime());
	}

	public static Time toTime(Object obj) {
		if (obj == null) {
			return null;
		}
		if ((obj instanceof Time))
			return (Time) obj;
		if ((obj instanceof Timestamp))
			return new Time(((Timestamp) obj).getTime());
		if ((obj instanceof java.sql.Date))
			return new Time(((Timestamp) obj).getTime());
		if ((obj instanceof java.util.Date))
			return new Time(((java.util.Date) obj).getTime());
		if ((obj instanceof String)) {
			return Time.valueOf(obj.toString());
		}
		throw new IllegalArgumentException("the parameter {@obj[" + obj + "]} is a bad Argument, which can't be converted to java.sql.Time.");
	}

	public static java.util.Date now() {
		return new java.util.Date(System.currentTimeMillis());
	}

	public static String nowIdentity() {
		return getFormat(now(), "yyyyMMddHHmmssSSS");
	}

	/** @deprecated */
	public static java.util.Date parse(String inputStr) {
		String dealStr = inputStr.trim();

		int pos = dealStr.indexOf("'");
		if (pos == 0) {
			dealStr = dealStr.substring(1, dealStr.length());
		}

		pos = dealStr.lastIndexOf("'");
		if (pos == dealStr.length() - 1) {
			dealStr = dealStr.substring(0, dealStr.length() - 1);
		}

		int length = dealStr.length();
		StringTokenizer tokens = null;
		if (length == DATE_FORMAT_STR_LENGTH) {
			tokens = new StringTokenizer(dealStr, "-", false);

			int year = new Integer(tokens.nextToken()).intValue();
			int month = new Integer(tokens.nextToken()).intValue();
			int day = new Integer(tokens.nextToken()).intValue();

			Calendar c = Calendar.getInstance();
			c.set(year, month - 1, day);
			java.util.Date date = c.getTime();
			return date;
		}
		if (length == TIMESTAMP_STR_LENGTH) {
			String dateStr = dealStr.substring(0, dealStr.indexOf(" "));
			String timeStr = dealStr.substring(dealStr.indexOf(" ") + 1);

			tokens = new StringTokenizer(dateStr, "-", false);
			int year = new Integer(tokens.nextToken()).intValue();
			int month = new Integer(tokens.nextToken()).intValue();
			int day = new Integer(tokens.nextToken()).intValue();

			tokens = new StringTokenizer(timeStr, ":", false);
			int hour = new Integer(tokens.nextToken()).intValue();
			int minute = new Integer(tokens.nextToken()).intValue();
			int second = new Integer(tokens.nextToken()).intValue();

			Calendar c = Calendar.getInstance();
			c.set(year, month - 1, day, hour, minute, second);
			java.util.Date date = c.getTime();
			return date;
		}
		if (length == MSEL_FORMAT_STR_LENGTH) {
			String dateStr = dealStr.substring(0, dealStr.indexOf(" "));
			String timeStr = dealStr.substring(dealStr.indexOf(" ") + 1, 19);
			int i = dealStr.lastIndexOf(",");
			if (i < 0) {
				i = dealStr.lastIndexOf(".");
			}
			String mselStr = dealStr.substring(i + 1);

			tokens = new StringTokenizer(dateStr, "-", false);
			int year = new Integer(tokens.nextToken()).intValue();
			int month = new Integer(tokens.nextToken()).intValue();
			int day = new Integer(tokens.nextToken()).intValue();

			tokens = new StringTokenizer(timeStr, ":", false);
			int hour = new Integer(tokens.nextToken()).intValue();
			int minute = new Integer(tokens.nextToken()).intValue();
			int second = new Integer(tokens.nextToken()).intValue();
			int msel = new Integer(mselStr).intValue();
			Calendar c = Calendar.getInstance();
			c.set(year, month - 1, day, hour, minute, second);
			c.set(14, msel);
			java.util.Date date = c.getTime();
			return date;
		}
		return null;
	}

	public static String getDateFormat(java.util.Date date) {
		return getFormat(date, DATE_FORMAT_STR);
	}

	public static String getTimeFormat(java.util.Date date) {
		return getFormat(date, TIME_FORMAT_STR);
	}

	public static String getTimeStampFormat(java.util.Date date) {
		return getFormat(date, TIMESTAMP_STR);
	}

	public static String currentDate() {
		return getDateFormat(now());
	}

	public static String currentTime() {
		return getTimeFormat(now());
	}

	public static String currentTimeStamp() {
		return getTimeStampFormat(now());
	}

	public static String currentMselFormat() {
		return getFormat(now(), MSEL_FORMAT_STR_1);
	}

	public static String getFormat(java.util.Date date, String parseFormat) {
		if (null == date) {
			return null;
		}
		if ((null == parseFormat) || ("".equalsIgnoreCase(parseFormat))) {
			return date.toString();
		}
		return FastDateFormat.getInstance(parseFormat).format(date);
	}

	public static long getIntervalDays(Calendar startday, Calendar endday) {
		if (startday.after(endday)) {
			Calendar cal = startday;
			startday = endday;
			endday = cal;
		}
		long sl = startday.getTimeInMillis();
		long el = endday.getTimeInMillis();

		long cl = el - sl;
		return cl / MILLIS_IN_ONE_DAY;
	}

	public static long getIntervalDays(java.util.Date startday, java.util.Date endday) {
		if (startday.after(endday)) {
			java.util.Date cal = startday;
			startday = endday;
			endday = cal;
		}
		long sl = startday.getTime();
		long el = endday.getTime();
		long cl = el - sl;
		return cl / MILLIS_IN_ONE_DAY;
	}

	public static boolean isLeapYear(int year) {
		return ((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0);
	}

	public static java.util.Date backward(java.util.Date date, long millis) {
		java.util.Date d = new java.util.Date();
		d.setTime(date.getTime() - millis);
		return d;
	}

	public static java.util.Date forward(java.util.Date date, long millis) {
		java.util.Date d = new java.util.Date();
		d.setTime(date.getTime() + millis);
		return d;
	}

	public static int get(java.util.Date date, int field) throws RuntimeException {
		if (date == null) {
			throw new RuntimeException("date is null");
		}

		GregorianCalendar cal = toCalendar(date);
		switch (field) {
		case 1:
			return cal.get(1);
		case 2:
			return cal.get(2) + 1;
		case 3:
			return cal.get(5);
		case 4:
			return cal.get(11);
		case 5:
			return cal.get(12);
		case 6:
			return cal.get(13);
		case 7:
			return cal.get(14);
		case 12:
			return (cal.get(7) - 2 + 7) % 7;
		case 13:
			return cal.getActualMaximum(5);
		case 14:
			return cal.getActualMaximum(4);
		case 15:
			return cal.getActualMaximum(6);
		case 16:
			return cal.getActualMaximum(3);
		case 8:
		case 9:
		case 10:
		case 11:
		}
		throw new RuntimeException("invalid date field " + field);
	}

	public static java.util.Date add(java.util.Date date, int field, int amount) {
		Calendar cal = toCalendar(date);
		int nCalendarField;
		switch (field) {
		case 1:
			nCalendarField = 1;
			break;
		case 2:
			nCalendarField = 2;
			break;
		case 12:
			nCalendarField = 3;
			break;
		case 3:
			nCalendarField = 6;
			break;
		case 4:
			nCalendarField = 10;
			break;
		case 5:
			nCalendarField = 12;
			break;
		case 6:
			nCalendarField = 13;
			break;
		case 7:
			nCalendarField = 14;
			break;
		case 8:
		case 9:
		case 10:
		case 11:
		default:
			throw new RuntimeException("invalid date time field: " + field);
		}
		cal.add(nCalendarField, amount);
		return cal.getTime();
	}

	public java.util.Date add(java.util.Date date, int day) {
		java.util.Date newDate = null;

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		cal.add(5, day);
		newDate = cal.getTime();
		return newDate;
	}

	public static Calendar getCalendar(int year, int month, int day) {
		if ((year < 2000) || (year > 2100))
			throw new IllegalArgumentException();
		if ((month < 1) || (month > 12))
			throw new IllegalArgumentException();
		if (day < 1)
			throw new IllegalArgumentException();
		if ((month == 2) && (isLeapYear(year))) {
			if (day > 29)
				throw new IllegalArgumentException();
		} else if (day > DAYS[(month - 1)]) {
			throw new IllegalArgumentException();
		}
		month--;
		Calendar c = Calendar.getInstance();
		c.set(1, year);
		c.set(2, month);
		c.set(5, day);
		c.set(11, 0);
		c.set(12, 0);
		c.set(13, 0);
		c.set(14, 0);
		return c;
	}

	public static int[] getToday() {
		return getDate(Calendar.getInstance());
	}

	public static int[] getDate(long t) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(t);
		return getDate(c);
	}

	public static int[] getDate(Calendar c) {
		int week = c.get(7) - 1;
		if (week == 0)
			week = 7;
		return new int[] { c.get(1), c.get(2) + 1, c.get(5), week };
	}

	public static int[] getTime(long t) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(t);
		return getTime(c);
	}

	public static int[] getTime(java.util.Date date) {
		return getTime(date.getTime());
	}

	public static int[] getTime(Calendar c) {
		int week = c.get(7) - 1;
		if (week == 0)
			week = 7;
		return new int[] { c.get(1), c.get(2) + 1, c.get(5), week, c.get(11), c.get(12), c.get(13) };
	}

	public static int[] getPreviousDay(int year, int month, int day) {
		day--;
		if (day < 1) {
			month--;
			if (month < 1) {
				year--;
				month = 12;
			}
			int lastDay = DAYS[(month - 1)];
			if ((month == 2) && (isLeapYear(year)))
				lastDay++;
			day = lastDay;
		}
		return new int[] { year, month, day };
	}

	public static int[] getNextDay(int year, int month, int day) {
		day++;
		int max = DAYS[(month - 1)];
		if ((month == 2) && (isLeapYear(year)))
			max++;
		if (day > max) {
			day = 1;
			month++;
			if (month > 12) {
				year++;
				month = 1;
			}
		}
		return new int[] { year, month, day };
	}

	public static GregorianCalendar toCalendar(java.util.Date date) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeZone(TimeZone.getDefault());
		cal.setTime(date);
		return cal;
	}

	public static java.util.Date randomDate(long begin, long end) {
		if (begin >= end) {
			throw new IllegalArgumentException("end must greater than begin...");
		}
		long rtn = (long) (begin + (Math.random() * (end - begin)));
		if ((rtn == begin) || (rtn == end)) {
			return randomDate(begin, end);
		}
		return new java.util.Date(rtn);
	}

	public static java.util.Date randomDate(String beginDate, String endDate) {
		java.util.Date begin = toDate(beginDate);
		java.util.Date end = toDate(endDate);
		if ((null == begin) || (null == end) || (begin.getTime() >= end.getTime())) {
			return null;
		}
		return randomDate(begin.getTime(), end.getTime());
	}

	public static boolean isClosely(java.util.Date date, java.util.Date baseDate, int seconds) {
		long m_time = date.getTime();
		long b_time = baseDate.getTime();
		long ms = seconds * 1000L;

		if (m_time == b_time) {
			return true;
		}
		if (m_time > b_time)
			return b_time + ms > m_time;
		if (m_time < b_time) {
			return m_time + ms > b_time;
		}
		return true;
	}

	public static String timeSpan(long msUsed) {
		if (msUsed < 0L) {
			return String.valueOf(msUsed);
		}
		if (msUsed < 1000L) {
			return String.valueOf(msUsed) + " 毫秒";
		}
		msUsed /= 1000L;
		if (msUsed < 60L) {
			return String.valueOf(msUsed) + " 秒";
		}
		if (msUsed < 3600L) {
			long nMinute = msUsed / 60L;
			long nSecond = msUsed % 60L;
			return String.valueOf(nMinute) + " 分" + String.valueOf(nSecond) + " 秒";
		}
		if (msUsed < 86400L) {
			long nHour = msUsed / 3600L;
			long nMinute = (msUsed - nHour * 3600L) / 60L;
			long nSecond = (msUsed - nHour * 3600L) % 60L;
			return String.valueOf(nHour) + " 小时" + String.valueOf(nMinute) + " 分" + String.valueOf(nSecond) + " 秒";
		}
		long nDay = msUsed / 86400L;
		long nHour = (msUsed - nDay * 86400L) / 3600L;
		long nMinute = (msUsed - nDay * 86400L - nHour * 3600L) / 60L;
		long nSecond = (msUsed - nDay * 86400L - nHour * 3600L) % 60L;
		return String.valueOf(nDay) + " 天" + String.valueOf(nHour) + " 小时" + String.valueOf(nMinute) + " 分" + String.valueOf(nSecond) + " 秒";
	}

	public static void main(String[] args) {
		String input = "2008-05-18 22:18:58";
		java.util.Date date = toDate(input);
		System.out.println(date);

		System.out.println(get(new java.util.Date(), 3));

		System.out.println(nowIdentity());

		System.out.println(toTime(new java.util.Date()));

		System.out.println(timeSpan(System.currentTimeMillis()));
	}
}