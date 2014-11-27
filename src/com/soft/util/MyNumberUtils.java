package com.soft.util;

import java.util.regex.Pattern;

public class MyNumberUtils {

	public static void main(String[] args) {
		System.out.println(can2Int("-02147483236"));
	}

	/**
	 * ����:int����(-2147483648~2147483647)
	 */
	private static final String intRegex = "^[-]?((0\\d{0,10})|([1-9]\\d{0,9})|([0-9a-fA-F]{1,7}))$";
	/**
	 * ����:long����(-9223372036854775808~9223372036854775807)
	 */
	private static final String longRegex = "^[-]?((0\\d{0,19})|([1-9]\\d{0,18})|([0-9a-fA-F]{1,15}))$";
	/**
	 * ����:����(��������&С��)
	 */
	private static final String numberRegex = "^[-]?(0|([1-9]\\d*)|((0\\.)\\d+)|(([1-9](\\d*)\\.)\\d+))$";

	/**
	 * �жϲ���obj�Ƿ����ת��Ϊint����
	 * 
	 * @param obj
	 *            Object
	 * @return
	 */
	public static boolean can2Int(Object obj) {
		if (obj == null) {
			return false;
		}
		String str_1 = obj.toString();
		if (MyStringUtils.notBlank(str_1)) {
			return Pattern.matches(intRegex, str_1);
		}
		return false;
	}

	/**
	 * �жϲ���obj�Ƿ����ת��Ϊ��������
	 * 
	 * @param obj
	 *            Object
	 * @return
	 */
	public static boolean can2Long(Object obj) {
		if (obj == null) {
			return false;
		}
		String str_1 = obj.toString();
		if (MyStringUtils.notBlank(str_1)) {
			return Pattern.matches(longRegex, str_1);
		}
		return false;
	}

	/**
	 * �жϲ���obj�Ƿ����ת��Ϊ����(����,С��)
	 * 
	 * @param obj
	 *            Object
	 * @return
	 */
	public static boolean can2Number(Object obj) {
		if (obj == null) {
			return false;
		}
		String str_1 = obj.toString();
		if (MyStringUtils.notBlank(str_1)) {
			return Pattern.matches(numberRegex, str_1);
		}
		return false;
	}

	/**
	 * 
	 * ����תΪinteger����
	 * 
	 * @param obj
	 * @param radix
	 *            ����(2,8,10,16�ȵ�)
	 * @return ���ʧ�ܷ���null
	 */
	public static Integer toInt(Object obj, int radix) {
		if (can2Int(obj)) {
			if (radix <= 0) {
				radix = 10;
			}
			if (obj instanceof Number) {
				return ((Number) obj).intValue();
			}
			try {
				return Integer.parseInt(obj.toString(), radix);
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	public static Integer toInt(Object obj, int radix, int defaultValue) {
		if (can2Int(obj)) {
			if (radix <= 0) {
				radix = 10;
			}
			if (obj instanceof Number) {
				return ((Number) obj).intValue();
			}
			try {
				return Integer.parseInt(obj.toString(), radix);
			} catch (Exception e) {
				return defaultValue;
			}
		}
		return defaultValue;
	}

	public static Integer toInt(Object obj) {
		return toInt(obj, 10, 0);
	}

	/**
	 * ����תΪlong����
	 * 
	 * @param obj
	 * @param radix
	 *            ����(2,8,10,16�ȵ�)
	 * @return ���ʧ�ܷ���null
	 */
	public static Long toLong(Object obj, int radix) {
		if (can2Long(obj)) {
			if (radix <= 0) {
				radix = 10;
			}
			if (obj instanceof Number) {
				return ((Number) obj).longValue();
			}
			try {
				return Long.parseLong(obj.toString(), radix);
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	/**
	 * ����תΪfloat����
	 * 
	 * @param obj
	 * @return ���ʧ�ܷ���null
	 */
	public static Float toFloat(Object obj) {
		if (obj != null) {
			if (obj instanceof Number) {
				return ((Number) obj).floatValue();
			}
			try {
				return Float.parseFloat(obj.toString());
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	public static Float toFloat(Object obj, Float defaultValue) {
		if (obj != null) {
			if (obj instanceof Number) {
				return ((Number) obj).floatValue();
			}
			try {
				return Float.parseFloat(obj.toString());
			} catch (Exception e) {
				return defaultValue;
			}
		}
		return defaultValue;
	}

	/**
	 * ����תΪdouble����
	 * 
	 * @param obj
	 * @return ���ʧ�ܷ���null
	 */
	public static Double toDouble(Object obj) {
		if (obj != null) {
			if (obj instanceof Number) {
				return ((Number) obj).doubleValue();
			}
			try {
				return Double.parseDouble(obj.toString());
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	/**
	 * objת��ΪNumber
	 * 
	 * @param obj
	 * @return
	 */
	public static Number toNumber(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Number) {
			return (Number) obj;
		}
		try {
			return (Number) obj;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * ��������
	 * 
	 * @param n1
	 *            ����1
	 * @param n2
	 *            ����2
	 * @param count
	 *            С�����ȷ����λ
	 * @param flag
	 *            (n1��ǰ,n2�ں�)1:�ӷ�,2:����,3:�˷�,4:����,5:����
	 * @return
	 */
	public static double sswr(Number n1, Number n2, int count, int flag) {
		if (n1 != null && n2 != null) {
			if (count < 0) {
				count = 0;
			}
			double d1 = n1.doubleValue();
			double d2 = n2.doubleValue();
			Double d = null;
			switch (flag) {
			case 1:
				d = d1 + d2;
				break;
			case 2:
				d = d1 - d2;
				break;
			case 3:
				d = d1 * d2;
				break;
			case 4:
				if (d2 != 0) {
					d = d1 / d2;
				}
				break;
			case 5:
				if (d2 != 0) {
					d = d1 % d2;
				}
				break;
			default:
				break;
			}
			if (d != null) {
				double tmp = Math.pow(10.0, count);
				double result = Math.round(d.doubleValue() * tmp) / tmp;
				return result;
			}
		}
		return 0.0;
	}

	/**
	 * ���ֽ���дת����˼����д��������Ȼ������ʰ�滻���� Ҫ�õ�������ʽ
	 */
	public static String digitUppercase(double n) {
		String fraction[] = { "��", "��" };
		String digit[] = { "��", "Ҽ", "��", "��", "��", "��", "½", "��", "��", "��" };
		String unit[][] = { { "Ԫ", "��", "��" }, { "", "ʰ", "��", "Ǫ" } };

		String head = n < 0 ? "��" : "";
		n = Math.abs(n);

		String s = "";
		for (int i = 0; i < fraction.length; i++) {
			s += (digit[(int) (Math.floor(n * 10 * Math.pow(10, i)) % 10)] + fraction[i])
					.replaceAll("(��.)+", "");
		}
		if (s.length() < 1) {
			s = "��";
		}
		int integerPart = (int) Math.floor(n);

		for (int i = 0; i < unit[0].length && integerPart > 0; i++) {
			String p = "";
			for (int j = 0; j < unit[1].length && n > 0; j++) {
				p = digit[integerPart % 10] + unit[1][j] + p;
				integerPart = integerPart / 10;
			}
			s = p.replaceAll("(��.)*��$", "").replaceAll("^$", "��") + unit[0][i]
					+ s;
		}
		return head
				+ s.replaceAll("(��.)*��Ԫ", "Ԫ").replaceFirst("(��.)+", "")
						.replaceAll("(��.)+", "��").replaceAll("^��$", "��Ԫ��");
	}
}
