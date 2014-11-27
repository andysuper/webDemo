package com.soft.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.JavaScriptUtils;

import com.alibaba.fastjson.JSON;

public class MyStringUtils {
	/**
	 * �жϲ���string�Ƿ�Ϊ��(null||length==0||ȫ���ո�)
	 * @param str
	 * 		�ַ�������
	 * @return
	 * 		true:�ǿ�<br>
	 * 		false:��
	 */
	public static boolean notBlank(String str){
		return !isBlank(str);
	}
	/**
	 * �жϲ���string�Ƿ�Ϊ��(null||length==0||ȫ���ո�)
	 * @param str
	 * 		�ַ�������
	 * @return
	 * 		true:��<br>
	 * 		false:�ǿ�
	 */
	public static boolean isBlank(String str){
		if(isEmpty(str)){
			return true;
		}
		int strLen=str.length();
		for(int i=0;i<strLen;i++){
			if(!Character.isWhitespace(str.charAt(i))){
				return false;
			}
		}
		return true;
	}
	/**
	 * �жϲ���string�Ƿ�Ϊ��(null||length==0)
	 * @param str
	 * 		�ַ�������
	 * @return
	 * 		true:�ǿ�<br>
	 * 		false:��
	 */
	public static boolean notEmpty(String str){
		return !isEmpty(str);
	}
	/**
	 * �жϲ���string�Ƿ�Ϊ��(null||length==0)
	 * @param str
	 * 		�ַ�������
	 * @return
	 * 		true:��<br>
	 * 		false:�ǿ�
	 */
	public static boolean isEmpty(String str){
		return (str==null)||(str.length()==0);
	}
	/**
	 * ������str�еĻ��з��滻Ϊָ��������
	 * @param str
	 * @param replace
	 * @return
	 */
	public static String replaceBr(String str,String replace){
		if(isEmpty(str)){
			return str;
		}
		return replaceAll(str,"\r\n|\n",replace);
	}
	/**
	 * ȥ���ַ����еĿո�(�������Ŀո�)
	 * @param str
	 * @return
	 */
	public static String delKongge(String str){
		if(isEmpty(str)){
			return str;
		}
		return replaceAll(str.replace((char)12288,' '),"\\s+","");
	}
	/**
	 * ȥ������(ȥ�������Ŀ���)
	 * @param str
	 * @return
	 */
	public static String deleteEmptyLine(String str){
		if(isEmpty(str)){
			return str;
		}
		return replaceAll(str,"(\r?\n(\\s*\r?\n)+)","\r\n");
	}
	/**
	 * ��Ϊһ��
	 * @param str
	 * @return
	 */
	public static String toSingleLine(String str){
		if(isEmpty(str)){
			return str;
		}
		return replaceAll(str,"[\n\t\r]+","");
	}
	/**
	 * ���ַ���escape,��֤html�ű��в�����ַǷ��ַ�(htmlEscape)
	 * @param str
	 * @return
	 */
	public static String escape2Html(Object str){
		if(str!=null){
			return HtmlUtils.htmlEscapeHex(str.toString());
		}
		return null;
	}
	/**
	 * ���ַ���escape,��֤javascript�ű��в�����ַǷ��ַ�(javaScriptEscape)
	 * @param str
	 * @return
	 */
	public static String escape2Js(Object str){
		if(str!=null){
			return JavaScriptUtils.javaScriptEscape(str.toString());
		}
		return null;
	}
	/**
	 * ���ַ���escape,��֤html��javascript�ű��в�����ַǷ��ַ�(htmlEscape&&javaScriptEscape)
	 * @param str
	 * @return
	 */
	public static String escape2HtmlJs(Object str){
		if(str!=null){
			return escape2Html(escape2Js(str));
		}
		return null;
	}
	/**
	 * �滻�ַ�����ƥ��regex������Ϊreplace
	 * @param str
	 * 		Ҫ�滻���ַ���
	 * @param regex
	 * 		ƥ��
	 * @param replace
	 * 		���Ϊnull���ڴ˷�����Ϊ���ַ���:""
	 * @return
	 */
	public static String replaceAll(String str,String regex,String replace){
		if(str==null||regex==null){
			return str;
		}
		if(replace==null){
			replace="";
		}
		return str.replaceAll(regex,replace);
	}
	public static String replace(String str,String regex,String replace){
		if(str==null||regex==null){
			return str;
		}
		if(replace==null){
			replace="";
		}
		if(str.indexOf(regex) >= 0) {
			str = str.replace(regex,replace);
			return replace(str, regex, replace);
		} else {
			return str;
		}
	}
	
	public static String dbColumnCase(String src){
		//oracle���ݿ��ѯ���ص��������Ǵ�д
		if (src!=null) {
			return src.toUpperCase();
		}
		return null;
	}
	/**
	 * �ж�����"()"�Ƿ�ƥ��,�������ж�����˳���Ƿ���ȷ
	 * 
	 * @param text
	 *            Ҫ�жϵ��ı�
	 * @return ���ƥ�䷵��true,���򷵻�false
	 */
	public static boolean isBracketCanPartnership(String text) {
		if (text == null
				|| (getAppearCount(text, '(') != getAppearCount(text, ')'))) {
			return false;
		}
		return true;
	}
	/**
	 * �õ�һ���ַ�����һ���ַ����г��ֵĴ���
	 * @param text	�ı�
	 * @param ch    �ַ�
	 */
	public static int getAppearCount(String text, char ch) {
		int count = 0;
		for (int i = 0; i < text.length(); i++) {
			count = (text.charAt(i) == ch) ? count + 1 : count;
		}
		return count;
	}
	/**
	 * �õ�һ���ַ�����һ���ַ����г��ֵĴ���
	 * @param text	�ı�
	 * @param ch    �ַ�
	 */
	public static boolean compareAppearCount(String text, char ch, char ch1) {
		int count = 0;
		int count1 = 0;
		for (int i = 0; i < text.length(); i++) {
			count = (text.charAt(i) == ch) ? count + 1 : count;
			count1 = (text.charAt(i) == ch1) ? count1 + 1 : count1;
		}
		if(count == count1) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * (�ַ���ǰ��û�ж��ŵ������)���ַ���ǰ����϶���
	 * @param str
	 * @return
	 * 		"1,2,3"=>",1,2,3,"
	 */
	public static String commaBeginEnd(String str){
		if (str!=null) {
			StringBuffer buff=new StringBuffer();
			if (!str.startsWith(",")) {
				buff.append(",");
			}
			buff.append(str);
			if (!str.endsWith(",")) {
				buff.append(",");
			}
			return buff.toString();
		}
		return null;
	}
	/**
	 * ��sql��Ϊ1��<br>
	 * 1.sql���˹���:<br>
	 * 	 1).�����滻Ϊ1���ո�<br>
	 * 	 2).������1�����Ͽո��滻Ϊ1���ո�<br>
	 * 	 3).ȥ��:{����С����,����,�Ⱥ�}������Ŀո�<br>
	 * @param sql
	 * @return
	 */
	public static String getLineSql(String sql) {
		if (sql==null) {
			return "";
		}
		return sql.
			//�����пո�(�������Ŀո�),tab֮���滻ΪӢ�Ŀո�
			replaceAll("\\s+", " ").
			//�����滻Ϊ1���ո�,������1�����Ͽո��滻Ϊ1���ո�
			replaceAll("[\r\n]", " ").replaceAll("\\s{2,}", " ")
			//������ǰ��Ŀո�
			.replaceAll("\\(\\s+", "(")
			.replaceAll("\\s+\\(", "(")
			//������ǰ��Ŀո�
			.replaceAll("\\s+\\)", ")")
			.replaceAll("\\)\\s+", ")")
			//����ǰ��Ŀո�
			.replaceAll(",\\s+", ",")
			.replaceAll("\\s+,", ",")
			//�Ⱥ�ǰ��Ŀո�
			.replaceAll("\\s+=", "=")
			.replaceAll("=\\s+", "=")
			//תΪСд
			/*.toLowerCase()*/;
	}
	/**
	 * ȥ��urlǰ���"/",��֤���url����"/"��ͷ
	 * @param url
	 * @return
	 */
	public static String urlNoStartSlash(String url) {
		if (url!=null) {
			while(url.startsWith("/")){
				url=url.substring(1);
			}
		}
		return url;
	}
	/**
	 * ������ת��Ϊjson�ַ���(�����ں�̨���췢��ǰ̨��json�ַ���)
	 * @param obj
	 * 		�κ�java����
	 * @return
	 * 		1.���objΪnull�����ַ���"null",<br>
	 * 		2.Map(�򳣹����:��public������Ҫ��getter����)��ʽ�ķ���:<code>{����/������:ֵ}</code>,<br>
	 * 		3.List(����)��ʽ�ķ���:<code>[�����и���Ԫ�ص�json�ַ���]</code>
	 */
	public static String toJSON(Object obj){
		return JSON.toJSONString(obj);
	}
	/**
	 * ���ݴ����ַ��� ���  תΪ�շ��� bean��
	 * @param s
	 * @return
	 */
	public static String getClassName( String s) {
		String[] ss = s.split("_");
		String result = "";
		for (int i = 0; i < ss.length; i++) {
			result += ss[i].substring(0, 1).toUpperCase() + ss[i].substring(1);
		}
		return result;
	}
	/**
	 * ���ݴ����ַ��� ȥ���»��ߺ� תΪget set����
	 * @param s		user_id
	 * @param s2	"get" "set"
	 * @return
	 */
	public static String  getMethodName(String s , String s2){
		return s2+getClassName(s);
	}
	
	/**
	 * 
	 * @param charInterval
	 * @param input
	 * @return
	 */
	public static String intervalByChar(String charInterval, String input) {
		if(input != null) {
			char[] tempChar = input.toCharArray();
			Pattern pattern = Pattern.compile(charInterval);
			StringBuffer result = new StringBuffer("");
			for (int i = 0; i < tempChar.length; i++) {
				char temp = tempChar[i];
				if(i != 0) {
					if(Character.isLowerCase(temp)) {
						result.append(temp);
					} else {
						result.append("_" + temp);
					}
				} else {
					result.append(temp);
				}
			}
			return result.toString();
		} else {
			return null;
		}
	}

	/**
	 * ��֤�����ַ���  �Ƿ�  ������
	 * @param str
	 * @return	����� ���� ���� true
	 * 			Ϊ�ջ������� ���� false
	 */
	public static boolean isNumeric(String str){ 
		if(str != null) {
			Pattern pattern = Pattern.compile("^[0-9]+$"); 
			return pattern.matcher(str).matches();
		} else {
			return false;
		}
	} 
	
	/**
	 * ��֤�����ַ���  �Ƿ� �� �������  
	 * @param str	1,2,3,4,5,6		�м䲻����������Ϊ��
	 * @return	�������  ���� true
	 * 			Ϊ�� ���߲����� ���� false
	 */
	public static boolean isComposeNumeric(String str) {
		if(str != null) {
			Pattern pattern = Pattern.compile("^([0-9]+,)*[0-9]+$"); 
			return pattern.matcher(str).matches();
		} else {
			return false;
		}
	}
	
	/**
	 * ��֤�����ַ���  �Ƿ� �� �������  
	 * @param str	1,2,,4,5,6		�м����������Ϊ��
	 * @return	�������  ���� true
	 * 			Ϊ�� ���߲����� ���� false
	 */
	public static boolean isComposeOrNullNumeric(String str) {
		if(str != null) {
			Pattern pattern = Pattern.compile("^([0-9]*,)*[0-9]+$"); 
			return pattern.matcher(str).matches();
		} else {
			return false;
		}
	}
	
	public static String splitMailType(String mailName) {
		if(notBlank(mailName) && mailName.indexOf("@") >= 0 && mailName.indexOf(".") > mailName.indexOf("@")) {
			String[] tmpArr = mailName.split("@");
			if(tmpArr.length>1) {
				String[] tmp = tmpArr[1].split("\\.");
				if(tmp.length>0) {
					return tmp[0];
				}
			}
		}
		return null;
	}
	
	/**
	 * ��������ȡ  ��ȡ ÿ���»���ǰ��2���ַ�
	 * @param fieldName
	 * @return
	 */
	public static StringBuffer splitTableName(String fieldName) {
		Pattern p = Pattern.compile("_", Pattern.CASE_INSENSITIVE); 
		Matcher matcher = p.matcher(fieldName);
		StringBuffer last = new StringBuffer("");
		int end = 0;
		int count = 0;
		while(matcher.find()) {
			end = matcher.start();
			last.append(fieldName.substring(end-2,end) + "_");
			count ++;
		}
		if(count==0) {
			end = fieldName.length()-1;
			last.append(fieldName.substring(end-2,end) + "_");
		}
		
		
		if("".equals(last.toString())) {
			last.append(fieldName);
		} else {
			end = fieldName.length();
			last.append(fieldName.substring(end-2,end));
		}
		return last;
	}
	
	 /**
     * ������ת��Ϊjson�ַ���(�����ں�̨���췢��ǰ̨��json�ַ���),������������������������ݣ��ᰴ��dateFormat��ʽ��
     * @param obj
     *      �κ�java����,
     *      dateFormat ���ڸ�ʽ 
     *       
     * @return String or null;
     */
    public static String toJSON(Object obj, String dateFormat){
        return JSON.toJSONStringWithDateFormat(obj, dateFormat);
    }
    
    /**
     * ������ת��Ϊjson�ַ���(�����ں�̨���췢��ǰ̨��json�ַ���),˳���滻��������������ֵ�ֵ
     * @param obj
     *      Map����,
     *      replaceK �滻��key
     *      dataList �����ֵ�ֵ 
     * @return String or null;
     
    public static String toJSON(Map<String, Object> obj, List<String> replaceK, Map<Object, DataDictionary> dataList){
        replaceDic(obj, replaceK, dataList);
        return JSON.toJSONString(obj);
    }*/
    
    /**
     * ������ת��Ϊjson�ַ���(�����ں�̨���췢��ǰ̨��json�ַ���),˳���滻��������������ֵ�ֵ
     * @param obj
     *      Map����,
     *      replaceK �滻��key
     *      dataList �����ֵ�ֵ 
     * @return String or null;
     
    public static String toJSON(List<Map<String, Object>> obj, List<String> replaceK, Map<Object, DataDictionary> dataList){
        if(obj!=null && obj.size()>0){
            for(int i=0;i<obj.size(); i++){
                replaceDic(obj.get(i), replaceK, dataList);
            }
        }
        return JSON.toJSONString(obj);
    }*/
    
    /**
     * replaceDic Ϊmap�����滻�����ֵ�ֵ,���replaceK����dataListΪ�գ����滻��Ϣ
     *      Map����,
     *      replaceK �滻��key
     *      dataList �����ֵ�ֵ 
     * 
    public static void replaceDic(Map<String, Object> obj, List<String> replaceK, Map<Object, DataDictionary> dataList){
        if(replaceK==null || dataList==null || replaceK.size()==0 || dataList.size()==0){
            return ;
        }
        String tmp = null;
        Object t = null;
        for(int i=0; i<replaceK.size(); i++){
            tmp = replaceK.get(i); 
            t = obj.get(tmp);
            if(t!=null && !"".equals(t.toString().trim())){
                obj.put(tmp+"Name", "0".equals(t.toString())|| "1".equals(t.toString())? 
                        "0".equals(t.toString())?"��":"��":dataList.get(Integer.parseInt(t.toString()))==null?"����":dataList.get(Integer.parseInt(t.toString())).getDicName());
            }
        }
    }
    
     */
    /**
     * ��List����ת��Ϊjson�ַ���(�����ں�̨���췢��ǰ̨��json�ַ���),˳���滻��������������ֵ�ֵ��˳����ʽ������
     * @param obj
     *      Map����,
     *      replaceK �滻��key
     *      dataList �����ֵ�ֵ 
     *      dateFormat ��ʽ������
     * @return String or null;
    
    public static String toJSON(List<Map<String, Object>> obj, List<String> replaceK, Map<Object, DataDictionary> dataList, String dateFormat){
        if(obj!=null && obj.size()>0){
            for(int i=0;i<obj.size(); i++){
                replaceDic(obj.get(i), replaceK, dataList);
            }
        }
        return JSON.toJSONStringWithDateFormat(obj, dateFormat);
    } */
    
	/**
	 * ��insert����л�ȡ������
	 * @param tempSql
	 * @return
	 */
	public static String getTableNameFromInsertSql(String tempSql) {
		Pattern p = Pattern.compile("INTO [a-zA-Z_]+[\\( ]{1}", Pattern.CASE_INSENSITIVE); 
		Matcher matcher = p.matcher(tempSql);
		String tmpName = "";
		if(matcher.find()) {
			tmpName = matcher.group();
		}
		String tableName = "";
		if(tmpName.length() > 6) {
			tableName = tmpName.substring(5, tmpName.length()-1);
		}
		return tableName;
	}
	
	/**
	 * ��ȡinsert Sql�е� �����ֶ���Ϣ
	 * @param tempSql
	 * @return
	 */
	public static String[] getFieldNameFromInsertSql(String tempSql) {
		Pattern p = Pattern.compile("INTO [a-zA-Z_]+\\([^\\)]+\\)", Pattern.CASE_INSENSITIVE); 
		Matcher matcher = p.matcher(tempSql);
		String fieldSql = "";
		if(matcher.find()) {
			fieldSql = matcher.group();
		}
		String filedStr = "";
		if(fieldSql.length() > 6) {
			Pattern p1 = Pattern.compile("\\([^\\)]+\\)", Pattern.CASE_INSENSITIVE); 
			Matcher matcher1 = p1.matcher(fieldSql);
			
			if(matcher1.find()) {
				filedStr = matcher1.group();
			}
			filedStr = filedStr.substring(1, filedStr.length()-1);
		}
		if(MyStringUtils.notBlank(filedStr)) {
			return filedStr.split(",");
		} else {
			return null;
		}
	}
    
	

	/**
	 * ��update����л�ȡ������
	 * @param tempSql
	 * @return
	 */
	public static String getTableNameFromUpdateSql(String tempSql) {
		Pattern p = Pattern.compile("UPDATE [a-zA-Z_]+ ", Pattern.CASE_INSENSITIVE); 
		Matcher matcher = p.matcher(tempSql);
		String tmpName = "";
		if(matcher.find()) {
			tmpName = matcher.group();
		}
		String tableName = "";
		if(tmpName.length() > 6) {
			tableName = tmpName.substring(7, tmpName.length()-1);
		}
		return tableName;
	}
	/**
	 * ��update����л�ȡ������
	 * @param tempSql
	 * @return
	 */
	public static String getTableAliasNameFromUpdateSql(String tempSql) {
		Pattern p = Pattern.compile(" [a-zA-Z_]+ set", Pattern.CASE_INSENSITIVE); 
		Matcher matcher = p.matcher(tempSql);
		String tmpName = "";
		if(matcher.find()) {
			tmpName = matcher.group();
		}
		String tableName = "";
		if(tmpName.length() > 4) {
			tableName = tmpName.substring(1, tmpName.length()-4);
		}
		return tableName;
	}
	
	/**
	 * ��update����л�ȡ�� �޸ĵ� ������
	 * @param tempSql
	 * @return
	 */
	public static String[] getFieldNameFromUpdateSql(String tempSql) {
		String resultStr = "";
		List<String> resultList = new ArrayList<String>();
		Pattern p = Pattern.compile("SET [\\S]+ WHERE", Pattern.CASE_INSENSITIVE); 
		Matcher matcher = p.matcher(tempSql);
		String fieldSql = "";
		if(matcher.find()) {
			fieldSql = matcher.group();
		}
		if(fieldSql.length() > 8) {
			fieldSql = fieldSql.substring(4, fieldSql.length()-6);
		}
		if(MyStringUtils.notBlank(fieldSql)) {
			String[] fieldSqlArr = fieldSql.split(",");
			for (String string : fieldSqlArr) {
				if(MyStringUtils.notBlank(string)) {
					String[] arr = string.split("=");
					if(arr.length >= 2) {
						if(MyStringUtils.notBlank(arr[0])) {
							resultList.add(arr[0]);
						}
					}
				}
			}
			if(MyCollectionUtils.notEmpty(resultList)) {
				return MyCollectionUtils.joinCollection(resultList, ",").split(",");
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	/**
	 * ��ȡ update sql��where�����
	 * @param tempSql
	 * @return
	 */
	public static String getWhereSqlFromUpdateSql(String tempSql) {
		Pattern p = Pattern.compile("WHERE ", Pattern.CASE_INSENSITIVE); 
		Matcher matcher = p.matcher(tempSql);
		int startIndex = 0;
		while(matcher.find()) {
			startIndex = matcher.start();
			String tmp = tempSql.substring(startIndex, tempSql.length());
			boolean ifEqual = MyStringUtils.compareAppearCount(tmp, "(".toCharArray()[0], ")".toCharArray()[0]);
			if(ifEqual) {
			    break;
			}
		}
		if(startIndex > 0) {
			return tempSql.substring(startIndex, tempSql.length());
		}
		return null;
	}
	
	/**
	 * ��ȡtableName  ���� ��ѯsql
	 * @param sql
	 * @return
	 */
	public static Map<String, String> getTableNameFromSelectSql(String sql) {
		Pattern p = Pattern.compile("from [a-zA-Z_]+ [\\S]+ ", Pattern.CASE_INSENSITIVE); 
		Matcher matcher = p.matcher(sql);
		String tmpName = "";
		Map<String, String> map = new HashMap<String, String>();
		if(matcher.find()) {
			tmpName = matcher.group();
			String nameAndAlias = tmpName.substring(5, tmpName.length()-1);
			String tableName = nameAndAlias;
			String alias = nameAndAlias;
			if(tableName.indexOf(" ") != -1) {
				String[] split = nameAndAlias.split(" ");
				if(split.length > 1) {
					tableName = split[0]; 
					alias = split[1];
					if(!"inner".equals(alias) && !"left".equals(alias) && !"where".equals(alias) && !"group".equals(alias) && !"order".equals(alias)) {
						map.put("alias", alias);
					}
				}
			} else if(tableName.indexOf(",") != -1) {
				tableName = nameAndAlias.split(",")[0]; 
			}
			map.put("tableName", tableName);
		} else {
			int tmpIndex = sql.indexOf("from");
			map.put("tableName", sql.substring(tmpIndex + 5));
		}
		return map;
	}
	
	/**
	 * ���ݴ����ַ��� ƴ�� ��ѯ����
	 * @param schools    1123,1143
	 * @return  (,1123,)?(,1143,)?
	 */
	public static String getRegIds(String schools) {
		if(MyStringUtils.notBlank(schools)) {
	        String[] schArr = schools.split(",");
	        StringBuffer regSchool = new StringBuffer("");
	        for (int i = 0; i < schArr.length; i++) {
	        	String schId = schArr[i];
	        	if(MyStringUtils.notBlank(schId)) {
	        		regSchool.append("(" + MyStringUtils.commaBeginEnd(schId) + "){1}");
	        		if(i!= schArr.length-1) {
	        			regSchool.append("|");
	        		}
	        	}
			}
	        return regSchool.toString();
        }
		return "";
	}
}
