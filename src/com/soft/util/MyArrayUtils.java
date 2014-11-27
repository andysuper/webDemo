package com.soft.util;


public class MyArrayUtils {

	/**
	 * 获得数组中指定索引的值,如果不存在返回null(不适用于基本数据类型数组)
	 * @param array
	 * @param index
	 * @return
	 */
	public static <T> T getEleInArray(T[] array,int index){
		if (array!=null&&index<array.length&&index>-1) {
			return array[index];
		}
		return null;
	}
	/**
	 * split字符串为数组
	 * @param str
	 * @param by
	 * 		相当于String.split(regex)中的regex.
	 * @return
	 * 		如果str=null返回length=0的数组,即:不用判断返回值null的问题,直接使用即可.
	 */
	public static String[] splitStrBy(String str,String by){
		if (str!=null) {
			if (by==null) {
				return new String[]{str};
			}else{
				String[] split = str.split(by);
				return split==null?new String[]{str}:split;
			}
		}
		return new String[]{};
	}
	/**
	 * 将数组用指定连接符连接为字符串
	 * @param array
	 * @param sepa
	 * @return
	 */
	public static String join2Str(Object[] array,String sepa){
		StringBuffer result=new StringBuffer();
		if (array!=null) {
			if (sepa==null) {
				sepa=",";//连接符默认为","
			}
			for (int i = 0; i < array.length; i++) {
				if (i>0) {
					result.append(sepa);
				}
				result.append(array[i]);
			}
		}
		return result.toString();
	}
	/**
	 * 将数组用指定连接符连接为字符串
	 * @param array
	 * @param sepa
	 * @return
	 */
	public static String join2StrNoNull(Object[] array,String sepa){
	    StringBuffer result=new StringBuffer();
	    if (array!=null) {
	        if (sepa==null) {
	            sepa=",";//连接符默认为","
	        }
	        for (int i = 0; i < array.length; i++) {
	            if(array[i] != null && !"".equals(array[i].toString())) {
    	            if (i>0) {
    	                result.append(sepa);
    	            }
    	            result.append(array[i]);
	            }
	        }
	    }
	    return result.toString();
	}
}
