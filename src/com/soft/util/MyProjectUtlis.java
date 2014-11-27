package com.soft.util;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.ContextLoader;

import com.mchange.lang.ObjectUtils;
import com.soft.action.base.BaseAction;
import com.soft.entity.DataDictionary;

public class MyProjectUtlis { 
	/**
	 * 重构下载文件名称(主要是区别IE和Firefox)<br>
	 * <strong>commons-codec.jar</strong><br>
	 * @param filename
	 * 		文件名称(带有后缀名,比如:"word文档.doc")
	 * @param request
	 * @param response
	 * 		此方法会设置response中名称为Content-disposition的header
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String createDownloadFilename(String filename,HttpServletRequest request,HttpServletResponse response)
			throws UnsupportedEncodingException{
		String agent=request.getHeader("USER-AGENT");
		if(agent==null){
			//默认为IE
			agent="msie";
		}
		agent=agent.toLowerCase();
		if(filename!=null){
			if (true) {
				//filename.getBytes("UTF-8")处理safari的乱码问题
				byte[] bytes = agent.contains("msie") ? filename.getBytes() : filename.getBytes("UTF-8");
				filename = new String(bytes, "ISO-8859-1");//各浏览器基本都支持ISO编码
			}
			if (response!=null) {
				//文件名外的双引号处理firefox的空格截断问题
				response.setHeader("Content-disposition", String.format("attachment; filename=\"%s\"", filename));
			}
			/*if(-1!=agent.indexOf("msie")){
				//IE
				filename=URLEncoder.encode(filename,EdusAction.serverEncoding);
			}else if(-1!=agent.indexOf("mozilla")&&-1!=agent.indexOf("firefox")){
				//Firefox
				filename=MimeUtility.encodeText(filename,EdusAction.serverEncoding,"B");
			}*/
		}
		return filename;
	}
	/**
	 * 获得请求的ip
	 * @param request
	 * @return
	 */
	public static String getRequestIp(HttpServletRequest request){
		if (request == null) {
			MyLogUtils.logError("getRequestIp method HttpServletRequest Object is null");
			return null;
		}
		//String which="x-forwarded-for";
		String ipString = request.getHeader("x-forwarded-for");
		if (MyStringUtils.isBlank(ipString) || "unknown".equalsIgnoreCase(ipString)) {
			ipString = request.getHeader("Proxy-Client-IP");
			//which="Proxy-Client-IP";
		}
		if (MyStringUtils.isBlank(ipString) || "unknown".equalsIgnoreCase(ipString)){
			ipString=request.getHeader("WL-Proxy-Client-IP");
			//which="WL-Proxy-Client-IP";
		}
		if (ipString!=null) {
			//多个路由时,取第一个非unknown的ip
			String[] arr = ipString.split(",");
			for (final String str : arr) {
				if (!"unknown".equalsIgnoreCase(str)){
					ipString = str;
					break;
				}
			}
		}
		if (MyStringUtils.isBlank(ipString) || "unknown".equalsIgnoreCase(ipString)){
			ipString=request.getRemoteAddr();
			//which="remoteAddr";
		}
		//System.out.println(which);
		return ipString;
	}
	/**
	 * 往session中设置用户可见菜单以及用户可访问url
	 * @param session
	 * @param progs
	 * 		List[Map{id,name,pId,url,shortUrl,openType,relatedUrl,showInMenu}]
	 */
	public static void setSessionProgsAndAuths(Object session,List<Map<String, Object>> progs){
		if (session!=null&&!MyCollectionUtils.isEmpty(progs)) {
			if (HttpSession.class.isAssignableFrom(session.getClass())) {
				HttpSession httpSession=(HttpSession)session;
				List<Map<String,Object>> menus=new ArrayList<Map<String,Object>>();
				List<String> authUrls=new ArrayList<String>();
				lli(progs, menus, authUrls);
				httpSession.setAttribute(Constants.sessionMenus, menus);
				httpSession.setAttribute(Constants.sessionAuthUrls, authUrls);
				httpSession.removeAttribute(Constants.sessionPrograms);
			}else if (Map.class.isAssignableFrom(session.getClass())) {
				Map<String, Object> httpSession=(Map<String, Object>)session;
				List<Map<String,Object>> menus=new ArrayList<Map<String,Object>>();
				List<String> authUrls=new ArrayList<String>();
				lli(progs, menus, authUrls);
				httpSession.put(Constants.sessionMenus, menus);
				httpSession.put(Constants.sessionAuthUrls, authUrls);
				httpSession.remove(Constants.sessionPrograms);
			}
		}
	}
	/**
	 * 登录成功后往session设置数据
	 * @param session
	 * 		原始的 HttpSession 对象,或者struts2的 ActionContext 得到的 Map 格式的session对象
	 * @param loginResult
	 * 		Map{<br>
	 * 			user:{id,loginId,lastLoginDate,lastLoginIp,orgId},<br>
	 * 			programs:[id,pId,name,url,shortUrl,openType]<br>
	 * 			userEduOrgs:[int],<br>
	 * 			userOrgDepts:[int],<br>
	 * 			userDetail:{name,alias,...},<br>
	 * 			userSysSet:{},<br>
	 * 			userIcons:[{}...]
	 * 		}
	 */
	public static void setSessionDataAfterLogin(Object session,Map<String, Object> loginResult){
		if (session!=null&&loginResult!=null) {
			if (HttpSession.class.isAssignableFrom(session.getClass())) {
				HttpSession httpSession=(HttpSession)session;
				Set<Entry<String, Object>> entrySet = loginResult.entrySet();
				for (Entry<String, Object> entry : entrySet) {
					if (entry!=null) {
						if (Constants.sessionPrograms.equals(entry.getKey())) {
							setSessionProgsAndAuths(session, (List<Map<String,Object>>)entry.getValue());
							continue;
						}
						httpSession.setAttribute(entry.getKey(), entry.getValue());
					}
				}
				httpSession.setAttribute(Constants.sessionAuthsUpdatedTime, getAuthUpdatedTime());
			}else if (Map.class.isAssignableFrom(session.getClass())) {
				Map<String, Object> httpSession=(Map<String, Object>)session;
				Set<Entry<String, Object>> entrySet = loginResult.entrySet();
				for (Entry<String, Object> entry : entrySet) {
					if (entry!=null) {
						if (Constants.sessionPrograms.equals(entry.getKey())) {
							setSessionProgsAndAuths(session, (List<Map<String,Object>>)entry.getValue());
							continue;
						}
						httpSession.put(entry.getKey(), entry.getValue());
					}
				}
				httpSession.put(Constants.sessionAuthsUpdatedTime, getAuthUpdatedTime());
			}
		}
	}
	/**
	 * 将现在时间设置为application中的权限更新时间(删除:"角色-权限","用户-角色","用户-权限"中间表记录时调用)
	 */
	public static void setAuthUpdatedTime(){
		ServletContext sc = BaseAction.getServletContext();
		if (sc==null) {
			return;
		}
		sc.setAttribute(Constants.sessionAuthsUpdatedTime, new Date().getTime());
	}
	/**
	 * 获得application中的权限更新时间
	 * @return
	 */
	public static Object getAuthUpdatedTime(){
		ServletContext sc = BaseAction.getServletContext();
		if (sc==null) {
			return null;
		}
		return sc.getAttribute(Constants.sessionAuthsUpdatedTime);
	}
	/**
	 * 获得application中指定属性的值
	 * @param attribute
	 * @return
	 */
	public static Object getFromApplication(String attribute){
		ServletContext sc = BaseAction.getServletContext();
		if (sc==null) {
			return null;
		}
		if (attribute!=null) {
			return sc.getAttribute(attribute);
		}
		return null;
	}
	/**
	 * 设置系统title&版权信息
	 * @param servletContext
	 * @param title
	 * @param copyright
	 */
	public static void setSysTitleCopyright(ServletContext servletContext,String title,String copyright,String loginImg,String emailAccount,
	                                        String emailPwd, String messageAccount, String messagePwd, String messageUrl){
		servletContext.setAttribute("sysTitle", title);
		servletContext.setAttribute("sysCopyright", copyright);
		servletContext.setAttribute("sysLoginImg", loginImg);
        servletContext.setAttribute("sysEmailAccount", emailAccount);
        servletContext.setAttribute("sysEmailPwd", emailPwd);
        servletContext.setAttribute("sysMessageAccount", messageAccount);
        servletContext.setAttribute("sysMessagePwd", messagePwd);
        servletContext.setAttribute("sysMessageUrl", messageUrl);
	}
	/**
	 * 往application中设置相应值
	 * @param names
	 * 		属性名数组(强烈建议使用{@link Constants}中声明的常量)
	 * @param values
	 * 		值数组
	 */
	public static void setVar2Application(String[] names,Object[] values){
		if (names!=null&&names.length>0) {
			ServletContext servletContext=BaseAction.getServletContext();
			if (servletContext!=null) {
				for (int i = 0; i < names.length; i++) {
					servletContext.setAttribute(names[i],MyArrayUtils.getEleInArray(values, i));
				}
			}
		}
		
	}
	/**
	 * 清空session
	 * @param session
	 * 		原始的 HttpSession 对象,或者struts2的 ActionContext 得到的 Map 格式的session对象
	 * @param keepAttrs
	 * 		在清空session时保留的属性,允许null
	 */
	public static void clearSession(Object session,List<String> keepAttrs){
		if (session!=null) {
			boolean keepFlag=keepAttrs!=null&&keepAttrs.size()>0;
			if (HttpSession.class.isAssignableFrom(session.getClass())) {
				HttpSession httpSession=(HttpSession)session;
				Enumeration<?> names = httpSession.getAttributeNames();
				if (names!=null) {
					while(names.hasMoreElements()){
						String next = (String)names.nextElement();
						if (keepFlag&&keepAttrs.contains(next)) {
							continue;
						}
						httpSession.removeAttribute(next);
					}
				}
			}else if (Map.class.isAssignableFrom(session.getClass())) {
				Map<String, Object> httpSession=(Map<String, Object>)session;
				Set<String> keys = httpSession.keySet();
				if (keys!=null) {
					for (String k : keys) {
						if (keepFlag&&keepAttrs.contains(k)) {
							continue;
						}
						httpSession.remove(k);
					}
				}
			}
		}
	}
	/**
	 * 根据父级字典shortname或id得到子级字典
	 * @param key
	 * 		父级字典shortname(string)或id(int)
	 * @return
	 * 		不用判断null,如果没有对应的子级字典则返回size=0的list
	 */
	public static List<DataDictionary> getChildrenDicByParent(Object key){
		ServletContext sc = BaseAction.getServletContext();
		if (sc==null) {
			return new ArrayList<DataDictionary>();
		}
		Map<Object, Map<String,Object>> dicTrees = (Map<Object, Map<String,Object>>)sc.getAttribute(Constants.applicationDicTrees);
		if (dicTrees!=null) {
			Map<String, Object> map = dicTrees.get(String.valueOf(key));
			if (map!=null) {
				List<DataDictionary> subs = (List<DataDictionary>)map.get("subs");
				return subs==null?new ArrayList<DataDictionary>():subs;
			}
		}
		return new ArrayList<DataDictionary>();
	}
	/**
	 * 根据id获得数据字典对象
	 * @param id
	 * @return
	 */
	public static DataDictionary getDicById(Object id){
		if (id!=null) {
			ServletContext sc;
			try {
				sc = BaseAction.getServletContext();
			} catch (Exception e) {
				sc = ContextLoader.getCurrentWebApplicationContext().getServletContext();
			}
			if (sc==null) {
				return null;
			}
			Map<Object, DataDictionary> allDics=(Map<Object, DataDictionary>)sc.getAttribute(Constants.applicationDics);
			if (allDics!=null) {
				DataDictionary dic = null;
				if (Number.class.isAssignableFrom(id.getClass())) {
					dic = allDics.get(((Number)id).intValue());
				}else{
					dic = allDics.get(MyNumberUtils.toInt(id, 10));
				}
				return dic==null?null:dic;
			}
		}
		return null;
	}
	/**
	 * 根据id获得数据字典对象的name
	 * @param id
	 * @return
	 */
	public static String getDicNameById(Object id){
		DataDictionary dic=getDicById(id);
		return dic==null?null:dic.getDicName();
		/*if (id!=null) {
			Map<Object, DataDictionary> allDics=(Map<Object, DataDictionary>)ServletActionContext.getServletContext().getAttribute(Constants.applicationDics);
			if (allDics!=null) {
				DataDictionary dic = null;
				if (Number.class.isAssignableFrom(id.getClass())) {
					dic = allDics.get(((Number)id).intValue());
				}else{
					dic = allDics.get(MyNumberUtils.toInt(id, 10));
				}
				return dic==null?null:dic.getDicName();
			}
		}
		return null;*/
	}
	/**
	 * 生成预约单编号
	 * @param no
	 * 		存放预约单编号的list
	 * @param count
	 * 		要生成的预约单编号数
	 * @return
	 */
	public static void createReserveNo(List<String> no,int count){
		if (no!=null&&count>0) {
			Calendar now = Calendar.getInstance();
			Random r=new Random();
			for (int i = 0; i < count; i++) {
				no.add("R"+MyDateUtils.date2String(/*new Date()*/now.getTime(), "yyMMddHHmmssSSS"));
				/*try {
					Thread.sleep(1);//暂停1毫秒,保证同1秒内生成的字符串不重复
				} catch (Exception e) {
				}*/
				int nextInt = r.nextInt(10)+1;
				now.set(Calendar.MILLISECOND, now.get(Calendar.MILLISECOND)+nextInt);
			}
		}
	}
	/**
	 * 生成预约单培训码
	 * @param trainCodes
	 * 		存放预约单培训码的list
	 * @param count
	 * 		要生成的预约单培训码数
	 * @return
	 */
	public static void createReserveTrainCodes(List<String> trainCodes,int count){
		if (trainCodes!=null&&count>0) {
			for (int i = 0; i < count; i++) {
				String uuid = UUID.randomUUID().toString();
				trainCodes.add(uuid.substring(0, 8));
			}
		}
	}
	private static void lli(List<Map<String, Object>> progs,
			List<Map<String, Object>> menus, List<String> authUrls) {
		if (progs==null) {
			return;
		}
		boolean menuFlag=menus!=null,authUrlFlag=authUrls!=null;
		if (!menuFlag&&!authUrlFlag) {
			return;
		}
		String[] menuKeys={"id","name","pId","url","shortUrl","openType"};
		for (Map<String, Object> prog : progs) {
			if (prog!=null) {
				if (menuFlag) {
					Integer showInMenu = MyNumberUtils.toInt(prog.get("showInMenu"), 10);
					if (showInMenu!=null&&showInMenu==1) {
						//add to menu
						Map<String,Object> menu=new HashMap<String, Object>();
						for (int i = 0; i < menuKeys.length; i++) {
							menu.put(menuKeys[i], prog.get(menuKeys[i]));
						}
						menus.add(menu);
					}
				}
				if (authUrlFlag) {
					String url_=(String)prog.get("url");
					String[] relatedUrls_=MyArrayUtils.splitStrBy(MyStringUtils.delKongge(MyStringUtils.replaceBr((String)prog.get("relatedUrl"), "")), ",");
					if (url_!=null) {
						authUrls.add(MyStringUtils.delKongge(MyStringUtils.replaceBr(url_,"")));
					}
					if (relatedUrls_!=null) {
						for (int i = 0; i < relatedUrls_.length; i++) {
							relatedUrls_[i]=MyStringUtils.urlNoStartSlash(relatedUrls_[i]);
						}
						authUrls.addAll(Arrays.asList(relatedUrls_));
					}
				}
			}
		}
	}
	public static boolean isSessionValid(HttpSession session){
		if (session!=null) {
			if (true) {
				try {
					session.isNew();//在try块中调用isNew方法来判断session是valid的
					return true;
				} catch (IllegalStateException e) {
					return false;
				}
			}
			String clsName = session.getClass().getName();
			if ("org.apache.catalina.session.StandardSessionFacade".equals(clsName)) {
				//tomcat
				try {
					Object session_tomcat = MyReflectUtils.getNonStaticFieldValue(session, "session");
					if (session_tomcat!=null) {
						String clsName_tomcat = session_tomcat.getClass().getName();
						if ("org.apache.catalina.session.StandardSession".equals(clsName_tomcat)) {
							Object isValid_obj=MyReflectUtils.invokeNonStaticMethod(session_tomcat, "isValidInternal", new Class<?>[]{}, new Object[]{});
							if (isValid_obj!=null) {
								return (Boolean)isValid_obj;
							}
						}
					}
				} catch (Exception e) {
					MyLogUtils.logWarn(MyProjectUtlis.class.getName()+":isSessionValid");
				}
			}
		}
		return false;
	}
	/**
	 * 生成树状图的html字符串数据
	 * @param datas
	 * 		List[{id,pId,...}...](datas中的元素为具有父子级的map类型数据)
	 * @param topName
	 * 		生成的树状图的顶层节点显示的名称
	 * @param idKey
	 * 		对应datas参数数中的元素的id
	 * @param pIdKey
	 * 		对应datas参数数中的元素的pId
	 * @param getNameKeys
	 * 		获取树状图中每个节点内容的key名称数组
	 * @return
	 */
	public static String makeChartHtml(List<Map<String, Object>> datas,String topName,String idKey,String pIdKey,String[] getNameKeys){
		if (!MyCollectionUtils.isEmpty(datas)) {
			List<Map<String, Object>> trees = MyCollectionUtils.list2Tree(datas,idKey,pIdKey);
			if (!MyCollectionUtils.isEmpty(trees)) {
				StringBuffer buff=new StringBuffer(2000);
				buff.append("<li>"+MyStringUtils.escape2Html(topName)+"<ul>");
				for (Map<String, Object> n : trees) {
					buff.append(digui4Chart(n,getNameKeys));
				}
				buff.append("</ul></li>");
				return buff.toString();
			}
		}
		return "<li>抱歉,没有信息</li>";
	}
	private static StringBuffer digui4Chart(Map<String, Object> n,String[] getNameKeys){
		StringBuffer buff=new StringBuffer(1000);
		StringBuffer name_=new StringBuffer(10);
		if (getNameKeys!=null) {
			for (int i = 0; i < getNameKeys.length; i++) {
				Object o_ = n.get(getNameKeys[i]);
				name_.append(o_==null?"":o_);
			}
		}
		Object subs = n.get("children");
		List<Map<String, Object>> children=subs==null?null:(List<Map<String,Object>>)subs;
		boolean haveChild=children!=null&&children.size()>0;
		String allName=name_.toString();
		String showName = "";
		if(allName!=null&&allName.length()>11){
			showName = allName.substring(0, 10)+"...";
		}else{
			showName = allName ;
		}
		allName=MyStringUtils.escape2Html(allName);
		showName=MyStringUtils.escape2Html(showName);
		if (haveChild) {
			//有子级
			buff.append("<li><span style='margin-top:3.5px;' class='title_with_child_1'>&nbsp;" +
					"</span><span class='title33' title='"
					+allName+"'>"
					+showName+
					"</span><span style='margin-top:3.5px;' class='title_right_1'>&nbsp;" +
					"</span>");
		}else{
			//无子级
			buff.append("<li><span style='margin-top:3.5px;cursor:pointer;' class='title_without_child_1'>&nbsp;" +
					"</span><span class='title33' title='"
					+allName+"'>"
					+showName+
					"</span><span style='margin-top:3.5px;' class='title_right_1'>&nbsp;" +
							"</span>");
		}
		if (haveChild) {
			buff.append("<ul>");
			for (Map<String,Object> child : children) {
				//调用递归
				buff.append(digui4Chart(child,getNameKeys));
			}
			buff.append("</ul>");
		}
		buff.append("</li>");
		return buff;
	}
	/**
	 * 使用mybatis的分页时,如果有foreach,需要往原来的参数map中加入新参数
	 * @param args
	 * 		mybatis查询时传递的参数map
	 * @param tempArr
	 * 		mybatis中foreach的集合(collection)对象(数组或集合都可以)
	 * @param forEachItem
	 * 		mybatis中foreach的item名称
	 */
	public static void ibatisForeach(Map<String, Object> args, Object tempArr,String forEachItem) {
		if (args==null||tempArr==null||forEachItem==null) {
			return;
		}
		Collection<Object> c = null;
		if (tempArr.getClass().isArray()) {
			Object[] arr=(Object[])tempArr;
			for (int i = 0; i < arr.length; i++) {
				args.put("__frch_"+forEachItem+"_"+i, arr[i]);
			}
		}else if (Collection.class.isAssignableFrom(tempArr.getClass())) {
			c=(Collection<Object>)tempArr;
			if (c!=null) {
				int i=0;
				for (Object o : c) {
					args.put("__frch_"+forEachItem+"_"+i++, o);
				}
			}
		}
	}
	/**
	 * (为发送短信)生成预约(时间段)详细-(模考预约时使用,其他功能请暂勿使用)
	 * @param result
	 * 		Map{dates,intervals,reserveResults}
	 * @param sdf
	 * 		日期格式(not null)
	 * @param insertCount
	 * 		预约成功数>0
	 * @param resvDetailKey
	 * 		预约详细信息对应的key(对应IMesParseService中变量mesandprintKeys的信息)
	 */
	public static void resvDetail4SMS(Map<String, Object> result, SimpleDateFormat sdf, int insertCount, String resvDetailKey){
		//构造预约详细
		StringBuffer resvDetail=new StringBuffer(200);
		List<Date> dates_0 = MyCollectionUtils.getFromMap(result, "dates", List.class);
		if (!MyCollectionUtils.isEmpty(dates_0)) {
			List<String> itvls_0 = MyCollectionUtils.getFromMap(result, "intervals", List.class);
			Object[] resvResults_0 = MyCollectionUtils.getFromMap(result, "reserveResults", Object[].class);
			makeResvDetail(sdf, resvDetail, dates_0, itvls_0, resvResults_0);
		}
		result.put("resvOkCount", insertCount);
		result.put("resvDate", new Date());
		result.put(resvDetailKey, resvDetail);
	}
	/**
	 * (根据日前,时间段,预约结果)生成预约详细信息=>(2012-12-01:09-:00-10:00,11:00-12:00,...)<br>
	 * <strong>(模考预约时使用,其他功能请暂勿使用)</strong>
	 * @param sdf
	 * 		日前格式
	 * @param resvDetail
	 * 		最后生成的预约详细信息
	 * @param dates
	 * 		所有预约的日期(List[date,...])
	 * @param itvls
	 * 		所有预约的时间段(List["09:00-10:00",...])
	 * @param resvResults
	 * 		预约结果(Object[]{"11"=ok,other=no})
	 */
	public static void makeResvDetail(SimpleDateFormat sdf,
			StringBuffer resvDetail, List<Date> dates, List<String> itvls,
			Object[] resvResults) {
		List<Object> allDays=new ArrayList<Object>();
		List<List<Object>> allDayItvls=new ArrayList<List<Object>>();
		//resvDetail4SMS(dates, itvls, resvResults, sdf, allDays, allDayItvls);
		if (!MyCollectionUtils.isEmpty(allDays)) {
			int ix=0;
			for (Object day : allDays) {
				resvDetail.append(ix==0?"":".").append(day).append(":")
					.append(MyCollectionUtils.joinCollection(
								MyCollectionUtils.getFromCollection(allDayItvls, ix, List.class), ","));
				ix++;
			}
		}
	}
	/**
	 * (为发送短信)生成预约(时间段)详细-(模考预约时使用,其他功能请暂勿使用)
	 * @param dates
	 * 		预约的日期
	 * @param itvls
	 * 		预约的时间段
	 * @param resvResults
	 * 		预约结果
	 * @param sdf
	 * 		日期格式(not null)
	 * @param allDays
	 * 		所有(预约)成功的日期(not null)
	 * @param allDayItvls
	 * 		所有(预约)成功(每一天)的时间段(not null)
	 
	@SuppressWarnings("deprecation")
	private static void resvDetail4SMS(List<Date> dates, List<String> itvls,
			Object[] resvResults, SimpleDateFormat sdf, List<Object> allDays,
			List<List<Object>> allDayItvls) {
		String preDay=null,curDay=null;
		Object itvl=null;
		boolean yyFlag=false,isSameDay=false;
		int ix=0,days=0;
		List<Object> dayItvls_tmp=null;
		for (Date d : dates) {
			yyFlag="11".equals(MyArrayUtils.getEleInArray(resvResults, ix));
			if (!yyFlag) {
				ix++;
				continue;
			}
			itvl=MyCollectionUtils.getFromCollection(itvls, ix);
			curDay=MyDateUtils.date2String(d, sdf);
			isSameDay=ObjectUtils.equals(curDay, preDay);
			if (!isSameDay) {
				allDayItvls.add(dayItvls_tmp=new ArrayList<Object>());
				days++;
				preDay=curDay;
				allDays.add(curDay);
			}else{
				dayItvls_tmp=MyCollectionUtils.getFromCollection(allDayItvls, days-1, List.class);
			}
			if (dayItvls_tmp!=null) {
				dayItvls_tmp.add(itvl);
			}
			ix++;
		}
	}*/
	/**
	 * 数据库表名称对应entity类的map
	private static Map<String, String> tableEntityMap=new HashMap<String, String>();
	static{
		String table_entity="accessory\r\n" + 
				"account_groups\r\n" + 
				"account_member\r\n" + 
				"account_org\r\n" + 
				"address_list\r\n" + 
				"advanced_condition,AdvancedCondition\r\n" + 
				"agant\r\n" + 
				"allfield,Allfield\r\n" + 
				"allfield_account\r\n" + 
				"allfield_strategy\r\n" + 
				"allfield_visible\r\n" + 
				"alltable_info,AlltableInfo\r\n" + 
				"appointment,Appointment\r\n" + 
				"appointment_cache\r\n" + 
				"audition_onemany\r\n" + 
				"audition_stu\r\n" + 
				"card_renew_log\r\n" + 
				"casting,authority.Role\r\n" + 
				"casting_limit\r\n" + 
				"chart_set,ChartSet\r\n" + 
				"chart_set_gather,ChartSetGather\r\n" + 
				"classroom\r\n" + 
				"class_course\r\n" + 
				"class_table\r\n" + 
				"compact_product,CompactProduct\r\n" + 
				"comp_agreement,compAgreement\r\n" + 
				"cooperation_unit\r\n" + 
				"cost_info\r\n" + 
				"counselor,Counselor\r\n" + 
				"course,Course\r\n" + 
				"create_folder,CreateFolder\r\n" + 
				"custom_receiver,CustomReceiver\r\n" + 
				"data_dictionary,DataDictionary\r\n" + 
				"department,Department\r\n" + 
				"desk_theme,DeskTheme\r\n" + 
				"distribution_setappointment,DistriSetApp\r\n" + 
				"drop_out,DropOut\r\n" + 
				"educational_settings,EducationalSettings\r\n" + 
				"edu_org,EduOrg\r\n" + 
				"edu_org_user,EduOrgUser\r\n" + 
				"feedback_info\r\n" + 
				"field_privilege\r\n" + 
				"finance_setting,FinanceSetting\r\n" + 
				"flow_basic,FlowBasic\r\n" + 
				"flow_check,FlowCheck\r\n" + 
				"flow_detail,FlowDetail\r\n" + 
				"form_field\r\n" + 
				"having_condition,HavingCondition\r\n" + 
				"having_mark,HavingMark\r\n" + 
				"help_center,HelpCenter\r\n" + 
				"history_send_sms,HistorySendSMS\r\n" + 
				"icon_img,IconImg\r\n" + 
				"imgs,Images\r\n" + 
				"individuation\r\n" + 
				"interval_control,IntervalControl\r\n" + 
				"invoice,Invoice\r\n" + 
				"latent_follow\r\n" + 
				"latent_history\r\n" + 
				"latent_source,LatentSource\r\n" + 
				"lat_bas_inf\r\n" + 
				"lco_history,LcoHistory\r\n" + 
				"lea_cla_many\r\n" + 
				"lea_cou_one,LeaCouOne\r\n" + 
				"limit_append\r\n" + 
				"limit_list,authority.Authority\r\n" + 
				"listing_row,ListingRow\r\n" + 
				"listing_sql,ListingSql\r\n" + 
				"login_info,LoginInfo\r\n" + 
				"logs,Logs\r\n" + 
				"marketing_plan\r\n" + 
				"market_activities\r\n" + 
				"matrix,Matrix\r\n" + 
				"matrix_group,MatrixGroup\r\n" + 
				"matrix_sql,MatrixSql\r\n" + 
				"memo\r\n" + 
				"memo_table\r\n" + 
				"message\r\n" + 
				"message_label,MessageLabel\r\n" + 
				"mes_appointment_machine,mokao.MesAppointmentMachine\r\n" + 
				"mes_appointment_teacher,mokao.MesAppointmentTeacher\r\n" + 
				"mes_counselor\r\n" + 
				"mes_interval_control,mokao.IntervalControl\r\n" + 
				"mes_machine,mokao.MesMachine\r\n" + 
				"mes_mac_edu_org,mokao.MesMacEduOrg\r\n" + 
				"mes_mac_room,mokao.MesMacRoom\r\n" + 
				"mes_product\r\n" + 
				"mes_room_in_charge,mokao.MesRoomInCharge\r\n" + 
				"mes_settings,MesSetings\r\n" + 
				"mes_student,mokao.MStudent\r\n" + 
				"mes_student_machine,mokao.MesStudentMachine\r\n" + 
				"mes_student_teacher,mokao.MesStudentTeacher\r\n" + 
				"mes_stu_coun\r\n" + 
				"mes_stu_course\r\n" + 
				"mes_version\r\n" + 
				"onetomanytest\r\n" + 
				"onetoonetest\r\n" + 
				"one_audition\r\n" + 
				"on_sale,OnSale\r\n" + 
				"operate_mark,OperateMark\r\n" + 
				"organ,Organ\r\n" + 
				"organ_breach,OrganBreach\r\n" + 
				"orgna_department,OrgDept\r\n" + 
				"org_programa,OrganPrograma\r\n" + 
				"other_charges,OtherCharges\r\n" + 
				"otm_appraise\r\n" + 
				"otm_attendance\r\n" + 
				"otom_sys_set,OtomSysSet\r\n" + 
				"oto_appraise,AppraiseOto\r\n" + 
				"pause_study,PauseStudy\r\n" + 
				"pay_bill_manage,PayBillManage\r\n" + 
				"person_specialty,PersonSpecialty\r\n" + 
				"position,Position\r\n" + 
				"print_formwork,PrintFormWork\r\n" + 
				"print_label,PrintLabel\r\n" + 
				"problem_appointment,ProblemAppointment\r\n" + 
				"product,Product\r\n" + 
				"programa,Programa\r\n" + 
				"programa_allfield,ProgramaAllfield\r\n" + 
				"programa_edit,ProgramaEdit\r\n" + 
				"public_customer\r\n" + 
				"public_set,PublicSet\r\n" + 
				"pub_cus_set\r\n" + 
				"receiver_message,ReceiverMessage\r\n" + 
				"receive_account\r\n" + 
				"receive_manage,ReceiveManage\r\n" + 
				"recipient\r\n" + 
				"refund,Refund\r\n" + 
				"refund_course,RefundCourse\r\n" + 
				"relation_person\r\n" + 
				"rev_flo_sta,RevFloSta\r\n" + 
				"safe_ip,SafeIp\r\n" + 
				"schedule\r\n" + 
				"schedule_exit\r\n" + 
				"schedule_member\r\n" + 
				"schedule_public\r\n" + 
				"school_table,SchoolTable\r\n" + 
				"secret_info\r\n" + 
				"small_account\r\n" + 
				"small_member,SmallMember\r\n" + 
				"small_report,SmallReport\r\n" + 
				"sms_formwork\r\n" + 
				"sms_message,SmsMessage\r\n" + 
				"sms_receipt,SmsRecipt\r\n" + 
				"standard_condition,StandardCondition\r\n" + 
				"stoppage,Stoppage\r\n" + 
				"strategy_account\r\n" + 
				"strategy_person\r\n" + 
				"student,Student\r\n" + 
				"student_breach,StudentBreach\r\n" + 
				"student_history,StudentHistory\r\n" + 
				"student_loginset,StudentLoginSet\r\n" + 
				"student_teacher,StudentTeacher\r\n" + 
				"study_plan\r\n" + 
				"stuscores\r\n" + 
				"stu_compact,StuCompact\r\n" + 
				"stu_exp_inf,StuExpInf\r\n" + 
				"stu_extension,StuExtension\r\n" + 
				"stu_lea_info,StuLeaInfo\r\n" + 
				"stu_teacher_set,StuTeacherSet\r\n" + 
				"summary,Summary\r\n" + 
				"summary_sql,SummarySql\r\n" + 
				"table_form\r\n" + 
				"table_sort,TableSort\r\n" + 
				"teacher_breach,TeacherBreach\r\n" + 
				"text_message,TesxMessage\r\n" + 
				"time_bucke_set,TimeBuckeSet\r\n" + 
				"turn_school,TurnSchool\r\n" + 
				"user_casting,UserCasting\r\n" + 
				"user_checkwork\r\n" + 
				"user_course,UserCourse\r\n" + 
				"user_info,UserInfo\r\n" + 
				"user_limit,UserLimit\r\n" + 
				"user_orgna_department,UserOrgnaDepartment\r\n" + 
				"user_pos,UserPos\r\n" + 
				"user_school,UserSchool\r\n" + 
				"user_sys_set,UserSysSet\r\n" + 
				"view_advanced,ViewAdvanced\r\n" + 
				"view_data_type,ViewDataType\r\n" + 
				"view_date,ViewDate\r\n" + 
				"view_member,ViewMember\r\n" + 
				"view_operate,ViewOperate\r\n" + 
				"view_row,ViewRow\r\n" + 
				"view_table,ViewTable\r\n" + 
				"web_appointment,WebAppointment";
		String[] split_3 = table_entity.split("\r\n");
		if (split_3!=null) {
			for (String s_3 : split_3) {
				if (s_3!=null) {
					String[] split_30 = s_3.split(",");
					if (split_30!=null) {
						tableEntityMap.put(MyArrayUtils.getEleInArray(split_30, 0), MyArrayUtils.getEleInArray(split_30, 1));
					}
				}
			}
		}
	}
	 */
	/**
	 * 根据数据库表名称获得对应的entity类
	 * @param tableName
	 * @return
	
	public static Class<?> getClassByTableName(String tableName){
		if (tableName!=null) {
			try {
				return Class.forName("com.edus.entity." + MyStringUtils.getClassName(tableName));
			} catch (ClassNotFoundException e) {
				String clsName = tableEntityMap.get(tableName.toLowerCase());
				if (MyStringUtils.notEmpty(clsName)) {
					try {
						return Class.forName("com.edus.entity."+clsName);
					} catch (Exception e0) {
						MyLogUtils.logError("根据表名获取Class出错----表名：" + tableName);
						return null;
					}
				}
			}
		}
		return null;
	} */
}
