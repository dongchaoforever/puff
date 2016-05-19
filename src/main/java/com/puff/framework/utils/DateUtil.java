package com.puff.framework.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.puff.framework.time.FastDateFormat;

/**
 * 一些时间计算的方便方法
 * 
 */
public class DateUtil {

	/**
	 * 计算当前时间前N个时间单位的时间
	 * 
	 * @param delay
	 *            往前推的时间数值.
	 * @param amount
	 *            时间粒度，是秒，分还是小时．取的是Calendar的静态变量。
	 * @return 当前时间延时后的时间
	 */
	public static Date getNowDelay(int delay, int amount) {
		int delayTime = Math.abs(delay);
		Calendar time = new GregorianCalendar();
		time.add(amount, delayTime * -1);
		return time.getTime();
	}

	/**
	 * 计算指定时间前或后N个时间单位的时间
	 * 
	 * @param date
	 *            指定时间
	 * @param delay
	 *            往前推的时间数值.如果向前推移则大于0，如果向后推移则小于0。
	 * @param amount
	 *            时间粒度，是秒，分还是小时．取的是Calendar的静态变量。
	 * @return 指定时间经过推移后的时间
	 */
	public static Date getDateDelay(Date date, int delay, int amount) {
		Calendar time = Calendar.getInstance();
		time.setTime(date);
		time.add(amount, delay * -1);
		return time.getTime();
	}

	/**
	 * 获得当前的日期，忽略时间
	 * 
	 * @return 返回当前的日期
	 */
	public static Date getToday() {
		Calendar time = Calendar.getInstance();
		time.set(Calendar.HOUR_OF_DAY, 0);
		time.set(Calendar.MINUTE, 0);
		time.set(Calendar.SECOND, 0);
		time.set(Calendar.MILLISECOND, 0);

		return time.getTime();
	}

	/**
	 * 判断指定的日期时间是否在指定的开始时间和结束时间之间。 判断的规则是start &lt;= purse &lt;=end.
	 * 
	 * @param purseDate
	 *            需要判断的日期时间。
	 * @param startDate
	 *            时间范围的开始时间。
	 * @param endDate
	 *            时间范围的结束时间。
	 * @return 给定时间是否在指定时间范围内，<tt>ture</tt>是在指定时间范围内,<tt>false</tt>不在指定时间范围内。
	 */
	public static boolean isContainDate(Date purseDate, Date startDate, Date endDate) {
		if (purseDate != null && startDate != null && endDate != null) {
			boolean startFlag = false;
			boolean endFlag = false;

			if (purseDate.compareTo(startDate) >= 0) {
				startFlag = true;
			}
			if (purseDate.compareTo(endDate) <= 0) {
				endFlag = true;
			}

			return (startFlag && endFlag) ? true : false;
		}

		return false;
	}

	/**
	 * 判断两个日期对像代表的日期是否相等,此方法忽略时间只比较日期.
	 * 
	 * @param firstDate
	 *            第一个日期.
	 * @param secondDate
	 *            第二个日期.
	 * @return 是否相等.<tt>true</tt>相等,<tt>false</tt>不相等.
	 */
	public static boolean isEquation(Date firstDate, Date secondDate) {
		Calendar first = Calendar.getInstance();
		first.setTime(firstDate);

		Calendar second = Calendar.getInstance();
		second.setTime(secondDate);

		if (first.get(Calendar.YEAR) == second.get(Calendar.YEAR)) {
			if (first.get(Calendar.MONTH) == second.get(Calendar.MONTH)) {
				if (first.get(Calendar.DAY_OF_MONTH) == second.get(Calendar.DAY_OF_MONTH)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * 根据给定日期时间解析出需要的值的字符串表示。
	 * 
	 * @param date
	 *            需要解析的日期时间对象。
	 * @param type
	 *            Calendar的静态变量值，表示关注的字段。
	 * @return 相应的关注时间字段的字符串表示。
	 */
	public static String findDateAndTimeStr(Date date, int type) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		int filed = cal.get(type);
		return Integer.toString(filed);
	}

	/**
	 * 找出指定日期星期一的日期时间。从0:0:0开始。
	 * 
	 * @param time
	 *            时间。
	 * @return 星期一的时间。
	 */
	public static Date findFirstWeekDate(Date time) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}

	/**
	 * 找到指定时间所在的星期的星期日的日期。时间为23:59:59。
	 * 
	 * @param time
	 *            指定的时间。
	 * @return 星期日的时间。
	 */
	public static Date findLastWeekDate(Date time) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		cal.add(Calendar.DAY_OF_WEEK, 6);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		return cal.getTime();
	}

	/**
	 * 找出指定时间所在月的第一天日期。时间为0:0:0.
	 * 
	 * @param time
	 *            时间。
	 * @return 月第一天的时间。
	 */
	public static Date findMonthFirstDay(Date time) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}

	/**
	 * 找出指定日期所在月的最后一天。时间设为23:59:59.
	 * 
	 * @param time
	 *            日期时间。
	 * @return 月最后一天日期。
	 */
	public static Date findMonthLastDay(Date time) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		return cal.getTime();
	}

	/**
	 * 返回两个时间的毫秒的差距，算法为第一个时间减去第二个时间。
	 * 
	 * @param first
	 *            时间。
	 * @param second
	 *            时间。
	 * @return 两者相关的毫秒。
	 */
	public static Long timeGap(Date first, Date second) {
		return Long.valueOf(first.getTime() - second.getTime());
	}

	/**
	 * 比较两个时间，忽略日期，判断第一个时间是否在第二个时间之后
	 * 
	 * @param firstDate
	 *            第一个时间
	 * @param secondDate
	 *            第二个时间
	 * @return 判断第一个时间是否在第二个时间之后
	 */
	public static boolean afterIgnoreDate(Date firstDate, Date secondDate) {
		Calendar first = Calendar.getInstance();
		first.setTime(firstDate);

		Calendar second = Calendar.getInstance();
		second.setTime(secondDate);

		first.set(Calendar.DAY_OF_YEAR, 1);
		first.set(Calendar.YEAR, 1970);

		second.set(Calendar.DAY_OF_YEAR, 1);
		second.set(Calendar.YEAR, 1970);

		return first.after(second);
	}

	public static String long2Time(long time) {
		Date date = new Date(time);
		FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date);
	}

	public static String format(long time, String pattern) {
		Date date = new Date(time);
		FastDateFormat sdf = FastDateFormat.getInstance(pattern);
		return sdf.format(date);
	}
	
	public static void main(String[] args) {
		
		System.out.println(long2Time(System.currentTimeMillis()));
	}

}
