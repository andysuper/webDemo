package com.soft.util;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyDateUtils {

	/**
	 * yyyy-MM-dd
	 */
	public static final DateFormat df_1 = new SimpleDateFormat("yyyy-MM-dd");
	/**
	 * yy-MM-dd
	 */
	public static final DateFormat df_2 = new SimpleDateFormat("yy-MM-dd");
	/**
	 * yyyy年MM月dd日
	 */
	public static final DateFormat dfCn_1 = new SimpleDateFormat("yyyy年MM月dd日");
	/**
	 * yy年MM月dd日
	 */
	public static final DateFormat dfCn_2 = new SimpleDateFormat("yy年MM月dd日");
	/**
	 * 简写星期的名称
	 */
	public static final DateFormatSymbols shortWeekSymbols;
	/**
	 * 全写星期的名称
	 */
	public static final DateFormatSymbols longWeekSymbols;
	static {
		shortWeekSymbols = DateFormatSymbols.getInstance();
		shortWeekSymbols.setShortWeekdays(new String[] { "", "日", "一", "二",
				"三", "四", "五", "六" });
		longWeekSymbols = DateFormatSymbols.getInstance();
		longWeekSymbols.setShortWeekdays(new String[] { "", "星期日", "星期一",
				"星期二", "星期三", "星期四", "星期五", "星期六" });
	}

	/**
	 * 日期转换为格式化的字符串
	 * 
	 * @param date
	 *            日期(Date or Calendar)
	 * @param format
	 *            格式字符串或 DateFormat 对象
	 * @return
	 */
	public static String date2String(Object date, Object format) {
		if (date != null) {
			Date d = innerToDate(date);
			if (d == null) {
				return "";
			}
			DateFormat df = innerToDateFormat(format);
			try {
				return df != null ? df.format(d) : (d.toString());
			} catch (Exception e) {
				MyLogUtils.logError("日期(" + d + ")转换为格式化的字符串(" + format
						+ ")-异常", e);
				return "";
			}
		}
		return "";
	}

	/**
	 * 字符串转换为日期
	 * 
	 * @param date
	 *            日期字符串
	 * @param format
	 *            转换格式字符串或 DateFormat 对象
	 * @return
	 * @throws Exception
	 */
	public static Date string2Date(String date, Object format) {
		if (MyStringUtils.notBlank(date)) {
			try {
				DateFormat df = innerToDateFormat(format);
				return df != null ? df.parse(date) : null;
			} catch (Exception e) {
				MyLogUtils.logError("字符串(" + date + ")按照格式(" + format
						+ ")转换为日期-异常", e);
				return null;
			}
		}
		return null;
	}

	/**
	 * 对象转换为日期
	 * 
	 * @param date
	 *            要转换的对象(本身是Date or Calendar或一定格式的字符串)
	 * @param format
	 *            转换格式字符串或 DateFormat 对象
	 * @return
	 * @throws Exception
	 */
	public static Date obj2Date(Object date, Object format) {
		if (date != null) {
			Class<?> clazz = date.getClass();
			if (Date.class.isAssignableFrom(clazz)) {
				return (Date) date;
			} else if (Calendar.class.isAssignableFrom(clazz)) {
				return ((Calendar) date).getTime();
			}
			return string2Date((String) date, format);
		}
		return null;
	}

	/**
	 * 功能描述:将日期2011-8-15设置为2011-8-15 23:59:59.999<br>
	 * 在数据库查询时可能会用到(比如一段时期内的数据)
	 * 
	 * @param d1
	 *            Date or Calendar
	 */
	public static Date date2MidNight(Object d1) {
		if (d1 != null) {
			Calendar c1 = innerToCalendar(d1);
			if (c1 == null) {
				return null;
			}
			c1.set(Calendar.HOUR_OF_DAY, 23);
			c1.set(Calendar.MINUTE, 59);
			c1.set(Calendar.SECOND, 59);
			c1.set(Calendar.MILLISECOND, 999);
			// d1.setTime(c1.getTimeInMillis());
			return c1.getTime();
		}
		return null;
	}

	/**
	 * 功能描述:将日期2011-8-15设置为2011-8-15 00:00:00.000<br>
	 * 在数据库查询时可能会用到(比如一段时期内的数据)
	 * 
	 * @param d1
	 */
	public static Date date2Daybreak(Object d1) {
		if (d1 != null) {
			Calendar c1 = innerToCalendar(d1);
			if (c1 == null) {
				return null;
			}
			calendarHms2Zero(c1);
			// d1.setTime(c1.getTimeInMillis());
			return c1.getTime();
		}
		return null;
	}

	/**
	 * @param preDays
	 *            可提前天数>0
	 * @param canDays
	 *            可操作天数>0
	 * @param minDate
	 * @param maxDate
	 */
	public static void celtics(int preDays, int canDays, Calendar minDate,
			Calendar maxDate) {
		if (minDate != null) {
			hms2Zero(minDate);
			minDate.set(Calendar.DAY_OF_MONTH,
					minDate.get(Calendar.DAY_OF_MONTH) + preDays);
		}
		if (maxDate != null) {
			if (minDate != null) {
				maxDate.setTime(minDate.getTime());
			}
			hms2Zero(maxDate);
			maxDate.set(Calendar.DAY_OF_MONTH,
					maxDate.get(Calendar.DAY_OF_MONTH) + canDays - 1);
		}
	}

	/**
	 * 将日期的 时:分:秒 毫秒 设置为0
	 * 
	 * @param date
	 *            Date or Calendar
	 */
	public static void hms2Zero(Object date) {
		if (date != null) {
			Calendar c = null;
			Class<?> clazz = date.getClass();
			if (Date.class.isAssignableFrom(clazz)) {
				c = Calendar.getInstance();
				Date d = (Date) date;
				c.setTime(d);
				calendarHms2Zero(c);
				d.setTime(c.getTimeInMillis());
			} else if (Calendar.class.isAssignableFrom(clazz)) {
				c = (Calendar) date;
				calendarHms2Zero(c);
			}
		}
	}

	/**
	 * 后1个日期段和前1个日期段的交集<br>
	 * (eg:2012-05-01~2012-03-25,2012-01-12~2012-04-12==>2012-03-25~2012-04-12)<br>
	 * 4个参数每2个为一组,每组中至少1个不是null
	 * 
	 * @param beginDate0
	 * @param endDate0
	 * @param beginDate1
	 * @param endDate1
	 * @return Map{error: 2个日期段没有交集, begin: 交集的开始日期, end: 交集的结束日期}
	 */
	public static Map<String, Object> rockets(Date beginDate0, Date endDate0,
			Date beginDate1, Date endDate1) {
		Map<String, Object> result = new HashMap<String, Object>();
		if ((beginDate0 != null || endDate0 != null)
				&& (beginDate1 != null || endDate1 != null)) {
			if (beginDate0 == null) {
				beginDate0 = endDate0;
			} else if (endDate0 == null) {
				endDate0 = beginDate0;
			}
			if (beginDate1 == null) {
				beginDate1 = endDate1;
			} else if (endDate1 == null) {
				endDate1 = beginDate1;
			}
			// 开始日期大于结束日期则两者互换
			if (beginDate0.after(endDate0)) {
				long endLongTime = endDate0.getTime();
				endDate0.setTime(beginDate0.getTime());
				beginDate0.setTime(endLongTime);
			}
			if (beginDate1.after(endDate1)) {
				long endLongTime1 = endDate1.getTime();
				endDate1.setTime(beginDate1.getTime());
				beginDate1.setTime(endLongTime1);
			}
			hms2Zero(beginDate0);
			hms2Zero(endDate0);
			hms2Zero(beginDate1);
			hms2Zero(endDate1);
			//
			if (endDate0.before(beginDate1) || beginDate0.after(endDate1)) {
				result.put("error", 1);
				return result;
			}
			if (beginDate0.before(beginDate1)) {
				beginDate0.setTime(beginDate1.getTime());
			}
			if (endDate0.after(endDate1)) {
				endDate0.setTime(endDate1.getTime());
			}
			result.clear();
			result.put("begin", beginDate0);
			result.put("end", endDate0);
			return result;
		}
		result.put("error", 1);
		return result;
	}

	/**
	 * 日期格式带星期("星期一 2012-08-13")
	 * 
	 * @param date
	 * @return
	 */
	public static String getLongWeek(Object date, String ymdHms) {
		if (date != null) {
			Date d = innerToDate(date);
			if (d == null) {
				return null;
			}
			if (ymdHms == null) {
				ymdHms = "yyyy-MM-dd HH:mm:ss";
			}
			SimpleDateFormat sdf = new SimpleDateFormat("E " + ymdHms);
			sdf.setDateFormatSymbols(MyDateUtils.longWeekSymbols);
			return sdf.format(d);
		}
		return null;
	}

	/**
	 * 日期格式带星期("一 2012-08-13")
	 * 
	 * @param date
	 * @return
	 */
	public static String getShortWeek(Object date, String ymdHms) {
		if (date != null) {
			Date d = innerToDate(date);
			if (d == null) {
				return null;
			}
			if (ymdHms == null) {
				ymdHms = "yyyy-MM-dd HH:mm:ss";
			}
			SimpleDateFormat sdf = new SimpleDateFormat("E " + ymdHms);
			sdf.setDateFormatSymbols(MyDateUtils.shortWeekSymbols);
			return sdf.format(d);
		}
		return null;
	}

	/**
	 * 取得一段时间中的 周一或 周二或 周三或 周四或 周五或 周六或 周日
	 * 
	 * @param startDate
	 *            时间段的开始日期
	 * @param endDate
	 *            时间段的结束日期
	 * @param dayOfWeek
	 *            周一 1 或 周二 2 或 周三 3 或 周四 4 或 周五 5 或 周六 6 或 周日 7
	 * @return
	 */
	public static List<Date> getDateByTimeInterval(Date startDate,
			Date endDate, int dayOfWeek) {
		List<Date> list = new ArrayList<Date>();
		if (startDate != null && endDate != null) {
			Calendar startCal = Calendar.getInstance();
			Calendar endCal = Calendar.getInstance();
			startCal.setTime(date2Daybreak(startDate));
			endCal.setTime(date2MidNight(endDate));

			Calendar tempAdd = Calendar.getInstance();
			tempAdd.setTime(date2Daybreak(startDate));
			int startWeek = startCal.get(Calendar.WEEK_OF_YEAR);
			int endWeek = endCal.get(Calendar.WEEK_OF_YEAR);
			System.out.println("startWeek==" + startWeek
					+ "------endWeek=======" + endWeek);

			for (int i = 0; i <= endWeek - startWeek; i++) {
				tempAdd.set(Calendar.WEEK_OF_YEAR, startWeek + i);
				int tempDayOfWeek = 0;
				if (dayOfWeek == 7) {
					tempDayOfWeek = 1;
				} else {
					tempDayOfWeek = dayOfWeek + 1;
				}
				tempAdd.set(Calendar.DAY_OF_WEEK, tempDayOfWeek);
				if ((tempAdd.getTime().after(startCal.getTime()) && tempAdd
						.getTime().before(endCal.getTime()))
						|| tempAdd.getTime().equals(startCal.getTime())
						|| tempAdd.getTime().equals(endCal.getTime())) {
					list.add(tempAdd.getTime());
				}
			}
		}
		return list;
	}

	/**
	 * @param d1
	 *            Date or Calendar
	 * @return
	 */
	private static Calendar innerToCalendar(Object d1) {
		Calendar c1 = null;
		if (d1 != null) {
			Class<?> clazz = d1.getClass();
			if (Date.class.isAssignableFrom(clazz)) {
				c1 = Calendar.getInstance();
				c1.setTime((Date) d1);
			} else if (Calendar.class.isAssignableFrom(clazz)) {
				c1 = (Calendar) d1;
			}
		}
		return c1;
	}

	/**
	 * @param date
	 *            Date or DateFormat
	 * @return
	 */
	private static Date innerToDate(Object date) {
		Date d = null;
		if (date != null) {
			Class<?> clazz = date.getClass();
			if (Date.class.isAssignableFrom(clazz)) {
				d = (Date) date;
			} else if (Calendar.class.isAssignableFrom(clazz)) {
				d = ((Calendar) date).getTime();
			}
		}
		return d;
	}

	/**
	 * @param format
	 *            String or DateFormat
	 * @return
	 */
	private static DateFormat innerToDateFormat(Object format) {
		DateFormat df = null;
		if (format != null) {
			Class<?> clazz = format.getClass();
			if (String.class.isAssignableFrom(clazz)) {
				df = new SimpleDateFormat((String) format);
			} else if (DateFormat.class.isAssignableFrom(clazz)) {
				df = (DateFormat) format;
			}
		}
		return df;
	}

	/**
	 * @param c
	 *            Calendar
	 */
	private static void calendarHms2Zero(Calendar c) {
		if (c != null) {
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
		}
	}
}
