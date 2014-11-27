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
	 * 判断参数string是否不为空(null||length==0||全部空格)
	 * @param str
	 * 		字符串参数
	 * @return
	 * 		true:非空<br>
	 * 		false:空
	 */
	public static boolean notBlank(String str){
		return !isBlank(str);
	}
	/**
	 * 判断参数string是否为空(null||length==0||全部空格)
	 * @param str
	 * 		字符串参数
	 * @return
	 * 		true:空<br>
	 * 		false:非空
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
	 * 判断参数string是否不为空(null||length==0)
	 * @param str
	 * 		字符串参数
	 * @return
	 * 		true:非空<br>
	 * 		false:空
	 */
	public static boolean notEmpty(String str){
		return !isEmpty(str);
	}
	/**
	 * 判断参数string是否为空(null||length==0)
	 * @param str
	 * 		字符串参数
	 * @return
	 * 		true:空<br>
	 * 		false:非空
	 */
	public static boolean isEmpty(String str){
		return (str==null)||(str.length()==0);
	}
	/**
	 * 将参数str中的换行符替换为指定的内容
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
	 * 去掉字符串中的空格(包括中文空格)
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
	 * 去掉空行(去掉连续的空行)
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
	 * 变为一行
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
	 * 将字符串escape,保证html脚本中不会出现非法字符(htmlEscape)
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
	 * 将字符串escape,保证javascript脚本中不会出现非法字符(javaScriptEscape)
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
	 * 将字符串escape,保证html或javascript脚本中不会出现非法字符(htmlEscape&&javaScriptEscape)
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
	 * 替换字符串中匹配regex的内容为replace
	 * @param str
	 * 		要替换的字符串
	 * @param regex
	 * 		匹配
	 * @param replace
	 * 		如果为null则在此方法变为空字符串:""
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
		//oracle数据库查询返回的列名都是大写
		if (src!=null) {
			return src.toUpperCase();
		}
		return null;
	}
	/**
	 * 判断括号"()"是否匹配,并不会判断排列顺序是否正确
	 * 
	 * @param text
	 *            要判断的文本
	 * @return 如果匹配返回true,否则返回false
	 */
	public static boolean isBracketCanPartnership(String text) {
		if (text == null
				|| (getAppearCount(text, '(') != getAppearCount(text, ')'))) {
			return false;
		}
		return true;
	}
	/**
	 * 得到一个字符在另一个字符串中出现的次数
	 * @param text	文本
	 * @param ch    字符
	 */
	public static int getAppearCount(String text, char ch) {
		int count = 0;
		for (int i = 0; i < text.length(); i++) {
			count = (text.charAt(i) == ch) ? count + 1 : count;
		}
		return count;
	}
	/**
	 * 得到一个字符在另一个字符串中出现的次数
	 * @param text	文本
	 * @param ch    字符
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
	 * (字符串前后没有逗号的情况下)将字符串前后加上逗号
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
	 * 将sql变为1行<br>
	 * 1.sql过滤规则:<br>
	 * 	 1).换行替换为1个空格<br>
	 * 	 2).连续的1个以上空格替换为1个空格<br>
	 * 	 3).去掉:{左右小括号,逗号,等号}后紧跟的空格<br>
	 * @param sql
	 * @return
	 */
	public static String getLineSql(String sql) {
		if (sql==null) {
			return "";
		}
		return sql.
			//将所有空格(包括中文空格),tab之类替换为英文空格
			replaceAll("\\s+", " ").
			//换行替换为1个空格,连续的1个以上空格替换为1个空格
			replaceAll("[\r\n]", " ").replaceAll("\\s{2,}", " ")
			//左括号前后的空格
			.replaceAll("\\(\\s+", "(")
			.replaceAll("\\s+\\(", "(")
			//右括号前后的空格
			.replaceAll("\\s+\\)", ")")
			.replaceAll("\\)\\s+", ")")
			//逗号前后的空格
			.replaceAll(",\\s+", ",")
			.replaceAll("\\s+,", ",")
			//等号前后的空格
			.replaceAll("\\s+=", "=")
			.replaceAll("=\\s+", "=")
			//转为小写
			/*.toLowerCase()*/;
	}
	/**
	 * 去掉url前面的"/",保证最后url不以"/"开头
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
	 * 将对象转换为json字符串(用于在后台构造发往前台的json字符串)
	 * @param obj
	 * 		任何java对象
	 * @return
	 * 		1.如果obj为null返回字符串"null",<br>
	 * 		2.Map(或常规对象:非public属性需要有getter方法)格式的返回:<code>{键名/属性名:值}</code>,<br>
	 * 		3.List(集合)格式的返回:<code>[集合中各个元素的json字符串]</code>
	 */
	public static String toJSON(Object obj){
		return JSON.toJSONString(obj);
	}
	/**
	 * 根据传入字符串 获得  转为驼峰后的 bean名
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
	 * 根据传入字符串 去除下划线后 转为get set方法
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
	 * 验证传入字符串  是否  是数字
	 * @param str
	 * @return	如果是 数字 返回 true
	 * 			为空或不是数字 返回 false
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
	 * 验证传入字符串  是否 是 数字组合  
	 * @param str	1,2,3,4,5,6		中间不可以有数字为空
	 * @return	如果符合  返回 true
	 * 			为空 或者不符合 返回 false
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
	 * 验证传入字符串  是否 是 数字组合  
	 * @param str	1,2,,4,5,6		中间可以有数字为空
	 * @return	如果符合  返回 true
	 * 			为空 或者不符合 返回 false
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
	 * 将表名截取  截取 每个下划线前面2个字符
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
     * 将对象转换为json字符串(用于在后台构造发往前台的json字符串),如果对象里面有日期类型数据，会按照dateFormat格式化
     * @param obj
     *      任何java对象,
     *      dateFormat 日期格式 
     *       
     * @return String or null;
     */
    public static String toJSON(Object obj, String dateFormat){
        return JSON.toJSONStringWithDateFormat(obj, dateFormat);
    }
    
    /**
     * 将对象转换为json字符串(用于在后台构造发往前台的json字符串),顺带替换对象里面的数据字典值
     * @param obj
     *      Map对象,
     *      replaceK 替换的key
     *      dataList 数据字典值 
     * @return String or null;
     
    public static String toJSON(Map<String, Object> obj, List<String> replaceK, Map<Object, DataDictionary> dataList){
        replaceDic(obj, replaceK, dataList);
        return JSON.toJSONString(obj);
    }*/
    
    /**
     * 将对象转换为json字符串(用于在后台构造发往前台的json字符串),顺带替换对象里面的数据字典值
     * @param obj
     *      Map对象,
     *      replaceK 替换的key
     *      dataList 数据字典值 
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
     * replaceDic 为map对象替换数据字典值,如果replaceK或者dataList为空，则不替换信息
     *      Map对象,
     *      replaceK 替换的key
     *      dataList 数据字典值 
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
                        "0".equals(t.toString())?"否":"是":dataList.get(Integer.parseInt(t.toString()))==null?"已无":dataList.get(Integer.parseInt(t.toString())).getDicName());
            }
        }
    }
    
     */
    /**
     * 将List对象转换为json字符串(用于在后台构造发往前台的json字符串),顺带替换对象里面的数据字典值，顺带格式化日期
     * @param obj
     *      Map对象,
     *      replaceK 替换的key
     *      dataList 数据字典值 
     *      dateFormat 格式化日期
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
	 * 从insert语句中获取出表名
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
	 * 获取insert Sql中的 所有字段信息
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
	 * 从update语句中获取出表名
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
	 * 从update语句中获取出别名
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
	 * 从update语句中获取出 修改的 属性名
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
	 * 获取 update sql中where后面的
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
	 * 获取tableName  根据 查询sql
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
	 * 根据传入字符串 拼接 查询正则
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
