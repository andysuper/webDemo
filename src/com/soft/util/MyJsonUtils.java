package com.soft.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.soft.entity.DataDictionary;
import com.soft.util.MyProjectUtlis;;

public class MyJsonUtils {

	public static Object getJsons(Object obj) {
		return JSON.toJSON(obj);
	}

	/**
	 * getJson ͨ���˷������JSON��������
	 * 
	 * @Description ͨ���˷�����ҳ��Ҫ��ʾ������ƴ�ӳ�json�������ݷ���
	 * @return String
	 * @see
	 */
	/**
	 * getJsonForGrid ͨ���˷������JSON��������
	 * 
	 * @Description ͨ���˷�����ҳ��Ҫ��ʾ������ƴ�ӳ�json�������ݷ���
	 * @param count
	 * @param args
	 * @return String
	 * @see
	 * @author fzy
	 * @createDate Feb 18, 2014
	 */
	public static String data(int count,
			List<Map<String, Object>> args) {
		// ����List,���operate����
		ValueFilter valueFilter = new ValueFilter() {
			@Override
			public Object process(Object arg0, String arg1, Object arg2) {
				String dicName = "";
				String[] dicIds = null;
				if (arg1.toUpperCase().startsWith("DIC_")) {
					if (arg2 == null) {
						return MyProjectUtlis.getDicNameById(arg2);
					}
					String dname = "";
					dicIds = arg2.toString().split(",");
					for (int i = 0; i < dicIds.length; i++) {
						if (!dicIds[i].equals("-1") && !dicIds[i].equals("1")) {
							dname = MyProjectUtlis.getDicNameById(dicIds[i]);
							if (MyStringUtils.notBlank(dname)) {
								if (MyStringUtils.notBlank(dicName)) {
									dicName += ",";
								}
								dicName += dname;
							}
						}
					}
					return (Object) dicName;
				} else if (arg1.toUpperCase().startsWith("QJ_")) {
					return (Object) (arg2 == null ? "" : arg2.toString()
							.replace("1", "����").replace("0", "����"));
				} else if (arg1.toUpperCase().startsWith("SF_")) {
					return (Object) (arg2 == null ? "" : arg2.toString()
							.replace("1", "��").replace("0", "��"));
				} else if (arg1.toUpperCase().startsWith("XQ_")) {
					if (arg2 == null) {
						return "";
					}
					dicIds = arg2.toString().split(",");
					for (int i = 0; i < dicIds.length; i++) {
						if (MyStringUtils.notBlank(dicIds[i])) {
							switch (Integer.parseInt(dicIds[i])) {
							case 1:
								dicName = "����һ";
								break;
							case 2:
								dicName = "���ڶ�";
								break;
							case 3:
								dicName = "������";
								break;
							case 4:
								dicName = "������";
								break;
							case 5:
								dicName = "������";
								break;
							case 6:
								dicName = "������";
								break;
							case 7:
								dicName = "������";
								break;
							default:
								dicName = "";
								break;
							}
							if (i < dicIds.length - 1) {
								dicName += ",";
							}
						}
					}
					return (Object) dicName;
				} else {
					return arg2;
				}
			}
		};

		StringBuffer json = new StringBuffer("{\"total\":" + count
				+ ",\"rows\":");
		String string = JSONArray.toJSONString(args, valueFilter,
				SerializerFeature.WriteMapNullValue);
		json.append(string);

		json.append("}");
		// System.out.println(json+"");
		return json + "";
	}

	/**
	 * getJsonForGrid ͨ���˷������JSON��������
	 * 
	 * @Description ͨ���˷�����ҳ��Ҫ��ʾ������ƴ�ӳ�json�������ݷ���
	 * @author pld
	 * @param count
	 * @param args
	 * @return String
	 * @see
	 */
	public static String getJsonForGrid(long count,
			List<Map<String, Object>> args) {
		// ����List,���operate����
		ValueFilter valueFilter = new ValueFilter() {
			@Override
			public Object process(Object arg0, String arg1, Object arg2) {
				if (arg1.toUpperCase().startsWith("DIC_")) {
					return MyProjectUtlis.getDicNameById(arg2);
				} else {
					return arg2;
				}
			}
		};

		StringBuffer json = new StringBuffer("{\"total\":" + count
				+ ",\"rows\":");
		String string = JSONArray.toJSONString(args, valueFilter,
				SerializerFeature.WriteMapNullValue);
		json.append(string);

		json.append("}");
		// System.out.println(json+"");
		return json + "";
	}

	/**
	 * getJsonForGrid ͨ���˷������JSON��������
	 * 
	 * @Description ͨ���˷�����ҳ��Ҫ��ʾ������ƴ�ӳ�json�������ݷ���
	 * @author pld
	 * @param count
	 * @param args
	 * @return String
	 * @see
	 */
	public static String getDbJsonForGrid(long count, List<DBObject> args) {
		ValueFilter valueFilter = new ValueFilter() {
			@Override
			public Object process(Object arg0, String arg1, Object arg2) {
				if (arg1.toUpperCase().startsWith("DIC_")) {
					return MyProjectUtlis.getDicNameById(arg2);
				} else {
					return arg2;
				}
			}
		};

		StringBuffer json = new StringBuffer("{\"total\":" + count
				+ ",\"rows\":");
		String string = JSONArray.toJSONString(args, valueFilter,
				SerializerFeature.WriteMapNullValue);
		json.append(string);

		json.append("}");
		// System.out.println(json+"");
		return json + "";
	}

	/**
	 * getJsonForGrid ͨ���˷������JSON��������
	 * 
	 * @Description ͨ���˷�����ҳ��Ҫ��ʾ������ƴ�ӳ�json�������ݷ���
	 * @param count
	 * @param args
	 * @return String
	 * @see
	 * @author fzy
	 * @createDate Feb 18, 2014
	 */
	public static String getJsonForGridStringMap(int count,
			List<Map<String, String>> args) {
		// ����List,���operate����
		ValueFilter valueFilter = new ValueFilter() {
			@Override
			public Object process(Object arg0, String arg1, Object arg2) {
				if (arg1.toUpperCase().startsWith("DIC_")) {
					return MyProjectUtlis.getDicNameById(arg2);
				} else {
					return arg2;
				}
			}
		};

		StringBuffer json = new StringBuffer("{\"total\":" + count
				+ ",\"rows\":");
		String string = JSONArray.toJSONString(args, valueFilter);
		json.append(string);

		json.append("}");
		// System.out.println(json+"");
		return json + "";
	}

	/**
	 * getJsonForGrid ͨ���˷������JSON��������
	 * 
	 * @Description ͨ���˷�����ҳ��Ҫ��ʾ������ƴ�ӳ�json�������ݷ���
	 * @param count
	 * @param args
	 * @return String
	 * @see
	 * @author fzy
	 * @createDate Feb 18, 2014
	 */
	public static String getJsonForGrid(int count,
			List<Map<String, Object>> args, String DataFormat) {
		// ����List,���operate����

		ValueFilter valueFilter = new ValueFilter() {
			@Override
			public Object process(Object arg0, String arg1, Object arg2) {
				if (arg1.toUpperCase().startsWith("DIC_")) {
					return MyProjectUtlis.getDicNameById(arg2);
				} else {
					return arg2;
				}
			}
		};

		StringBuffer json = new StringBuffer("{\"total\":" + count
				+ ",\"rows\":");
		String string = JSONArray.toJSONString(args, valueFilter);
		json.append(string);

		json.append("}");
		// System.out.println(json+"");
		return json + "";
	}

	/**
	 * getJsonForGrid ͨ���˷������JSON��������
	 * 
	 * @Description ͨ���˷�����ҳ��Ҫ��ʾ������ƴ�ӳ�json�������ݷ���
	 * @author pld
	 * @param count
	 * @param args
	 * @param DataFormat
	 * @return String
	 * @see
	 */
	public static String getJsonForGrid(long count,
			List<Map<String, Object>> args, String DataFormat) {
		// ����List,���operate����

		ValueFilter valueFilter = new ValueFilter() {
			@Override
			public Object process(Object arg0, String arg1, Object arg2) {
				if (arg1.toUpperCase().startsWith("DIC_")) {
					return MyProjectUtlis.getDicNameById(arg2);
				} else {
					return arg2;
				}
			}
		};

		StringBuffer json = new StringBuffer("{\"total\":" + count
				+ ",\"rows\":");
		String string = JSONArray.toJSONString(args, valueFilter);
		json.append(string);

		json.append("}");
		// System.out.println(json+"");
		return json + "";
	}

	/**
	 * getJsonForGrid ͨ���˷������JSON��������
	 * 
	 * @Description ͨ���˷�����ҳ��Ҫ��ʾ������ƴ�ӳ�json�������ݷ���
	 * @param count
	 * @param args
	 * @return String
	 * @see
	 * @author fzy
	 * @createDate Feb 18, 2014
	 */
	public static String getJsonForGrid(PageList page,
			List<Map<String, Object>> args, String DataFormat) {
		// ����List,���operate����

		ValueFilter valueFilter = new ValueFilter() {
			@Override
			public Object process(Object arg0, String arg1, Object arg2) {
				if (arg1.toUpperCase().startsWith("DIC_")) {
					return MyProjectUtlis.getDicNameById(arg2);
				} else {
					return arg2;
				}
			}
		};

		StringBuffer json = new StringBuffer("{\"total\":"
				+ page.getTotalcount() + ",\"rows\":");
		String string = JSONArray.toJSONString(args, valueFilter);
		json.append(string);
		String[] countStr = page.getCountStr();
		Double[] countStrType = page.getResultOther();
		if (countStr != null) {
			for (int i = 0; i < countStr.length; i++) {
				json.append(",\"" + countStr[i] + "\":" + countStrType[i]);
			}
		}
		json.append("}");
		// System.out.println(json+"");
		return json + "";
	}

	public static String getJsonForDictionary(List<DataDictionary> list) {
		List<Map<String, Object>> dictionaryList = new ArrayList<Map<String, Object>>();

		// ѭ����ֵ̬��list����type��valueֵ�ŵ�map�У�ת����json��ʽ����ǰ̨ҳ��
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> dynaMap = new HashMap<String, Object>();
				int dicId = MyNumberUtils.toInt(list.get(i).getDicId(), 10);
				String dicName = list.get(i).getDicName();
				dynaMap.put("value", "" + dicId);
				dynaMap.put("text", dicName);
				dictionaryList.add(dynaMap);
			}
		}

		// ����JSON�����࣬���json�������ݸ�ҳ��
		String dynaStr = MyStringUtils.toJSON(dictionaryList);
		return dynaStr;
	}

	public static void main(String[] args) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("aaa", null);
		System.out.println(JSON.toJSONString(map,
				SerializerFeature.WriteMapNullValue));
	}

}
