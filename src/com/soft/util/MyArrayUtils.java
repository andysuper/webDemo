package com.soft.util;


public class MyArrayUtils {

	/**
	 * ���������ָ��������ֵ,��������ڷ���null(�������ڻ���������������)
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
	 * split�ַ���Ϊ����
	 * @param str
	 * @param by
	 * 		�൱��String.split(regex)�е�regex.
	 * @return
	 * 		���str=null����length=0������,��:�����жϷ���ֵnull������,ֱ��ʹ�ü���.
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
	 * ��������ָ�����ӷ�����Ϊ�ַ���
	 * @param array
	 * @param sepa
	 * @return
	 */
	public static String join2Str(Object[] array,String sepa){
		StringBuffer result=new StringBuffer();
		if (array!=null) {
			if (sepa==null) {
				sepa=",";//���ӷ�Ĭ��Ϊ","
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
	 * ��������ָ�����ӷ�����Ϊ�ַ���
	 * @param array
	 * @param sepa
	 * @return
	 */
	public static String join2StrNoNull(Object[] array,String sepa){
	    StringBuffer result=new StringBuffer();
	    if (array!=null) {
	        if (sepa==null) {
	            sepa=",";//���ӷ�Ĭ��Ϊ","
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
