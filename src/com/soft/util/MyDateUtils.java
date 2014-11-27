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
	 * yyyy��MM��dd��
	 */
	public static final DateFormat dfCn_1 = new SimpleDateFormat("yyyy��MM��dd��");
	/**
	 * yy��MM��dd��
	 */
	public static final DateFormat dfCn_2 = new SimpleDateFormat("yy��MM��dd��");
	/**
	 * ��д���ڵ�����
	 */
	public static final DateFormatSymbols shortWeekSymbols;
	/**
	 * ȫд���ڵ�����
	 */
	public static final DateFormatSymbols longWeekSymbols;
	static {
		shortWeekSymbols = DateFormatSymbols.getInstance();
		shortWeekSymbols.setShortWeekdays(new String[] { "", "��", "һ", "��",
				"��", "��", "��", "��" });
		longWeekSymbols = DateFormatSymbols.getInstance();
		longWeekSymbols.setShortWeekdays(new String[] { "", "������", "����һ",
				"���ڶ�", "������", "������", "������", "������" });
	}

	/**
	 * ����ת��Ϊ��ʽ�����ַ���
	 * 
	 * @param date
	 *            ����(Date or Calendar)
	 * @param format
	 *            ��ʽ�ַ����� DateFormat ����
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
				MyLogUtils.logError("����(" + d + ")ת��Ϊ��ʽ�����ַ���(" + format
						+ ")-�쳣", e);
				return "";
			}
		}
		return "";
	}

	/**
	 * �ַ���ת��Ϊ����
	 * 
	 * @param date
	 *            �����ַ���
	 * @param format
	 *            ת����ʽ�ַ����� DateFormat ����
	 * @return
	 * @throws Exception
	 */
	public static Date string2Date(String date, Object format) {
		if (MyStringUtils.notBlank(date)) {
			try {
				DateFormat df = innerToDateFormat(format);
				return df != null ? df.parse(date) : null;
			} catch (Exception e) {
				MyLogUtils.logError("�ַ���(" + date + ")���ո�ʽ(" + format
						+ ")ת��Ϊ����-�쳣", e);
				return null;
			}
		}
		return null;
	}

	/**
	 * ����ת��Ϊ����
	 * 
	 * @param date
	 *            Ҫת���Ķ���(������Date or Calendar��һ����ʽ���ַ���)
	 * @param format
	 *            ת����ʽ�ַ����� DateFormat ����
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
	 * ��������:������2011-8-15����Ϊ2011-8-15 23:59:59.999<br>
	 * �����ݿ��ѯʱ���ܻ��õ�(����һ��ʱ���ڵ�����)
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
	 * ��������:������2011-8-15����Ϊ2011-8-15 00:00:00.000<br>
	 * �����ݿ��ѯʱ���ܻ��õ�(����һ��ʱ���ڵ�����)
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
	 *            ����ǰ����>0
	 * @param canDays
	 *            �ɲ�������>0
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
	 * �����ڵ� ʱ:��:�� ���� ����Ϊ0
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
	 * ��1�����ڶκ�ǰ1�����ڶεĽ���<br>
	 * (eg:2012-05-01~2012-03-25,2012-01-12~2012-04-12==>2012-03-25~2012-04-12)<br>
	 * 4������ÿ2��Ϊһ��,ÿ��������1������null
	 * 
	 * @param beginDate0
	 * @param endDate0
	 * @param beginDate1
	 * @param endDate1
	 * @return Map{error: 2�����ڶ�û�н���, begin: �����Ŀ�ʼ����, end: �����Ľ�������}
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
			// ��ʼ���ڴ��ڽ������������߻���
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
	 * ���ڸ�ʽ������("����һ 2012-08-13")
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
	 * ���ڸ�ʽ������("һ 2012-08-13")
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
	 * ȡ��һ��ʱ���е� ��һ�� �ܶ��� ������ ���Ļ� ����� ������ ����
	 * 
	 * @param startDate
	 *            ʱ��εĿ�ʼ����
	 * @param endDate
	 *            ʱ��εĽ�������
	 * @param dayOfWeek
	 *            ��һ 1 �� �ܶ� 2 �� ���� 3 �� ���� 4 �� ���� 5 �� ���� 6 �� ���� 7
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
