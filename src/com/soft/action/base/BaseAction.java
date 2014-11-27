package com.soft.action.base;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.springframework.beans.BeansException;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionSupport;
import com.soft.util.MyLogUtils;
import com.soft.util.MyProjectUtlis;

@SuppressWarnings("serial")
public class BaseAction extends ActionSupport {

	private static ActionContext context;
	private static HttpServletRequest request;
	private static HttpServletResponse response;
	private static PrintWriter out;
	private static Map<String, Object> session;
	private static Map<String, Object> application;

	/**
	 * 服务器编码(建议为UTF-8)
	 */
	public static final String serverEncoding = "UTF-8";
	static {
		context = ActionContext.getContext();
		request = ServletActionContext.getRequest();
		response = ServletActionContext.getResponse();
		session = context.getSession();
		try {
			out = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		application = context.getApplication();

	}

	/**
	 * 获取前台单一参数值
	 * 
	 * @param name
	 * @return
	 */
	public String getParam(String name) {
		if (!"".equals(name)) {
			return request.getParameter(name);
		}
		return null;
	}

	/**
	 * 获取前台参数数组值
	 * 
	 * @param names
	 * @return
	 */
	public String[] getParamVals(String names) {
		if (!"".equals(names)) {
			return request.getParameterValues(names);
		}
		return null;
	}

	/**
	 * 响应数据到前台
	 * 
	 * @param obj
	 */
	public void respData(Object obj) {
		out.print(obj);
	}

	/**
	 * 获得action的命名空间
	 * 
	 * @return
	 */
	protected String getNamespace() {
		String namespace = null;
		try {
			ActionContext ac = getActionContent();
			if (ac != null) {
				ActionInvocation ai = ac.getActionInvocation();
				if (ai != null) {
					ActionProxy proxy = ai.getProxy();
					if (proxy != null) {
						namespace = proxy.getNamespace();
					}
				}
			}
		} catch (Exception e) {
			MyLogUtils.logError("error:获得action的命名空间", e);
		}
		return namespace == null ? "" : namespace;
	}

	/**
	 * 获得servletContext
	 * 
	 * @return
	 */
	public static ServletContext getServletContext() {
		return ServletActionContext.getServletContext();
	}

	/**
	 * 获得response,并将其字符编码设置为{@link serverEncoding}<br>
	 * <strong>注意:有些操作需要判断此response是否已经committed</strong>
	 * 
	 * @return
	 */
	protected HttpServletResponse getResponse() {
		HttpServletResponse response = null;
		if (false) {
			ActionContext ac = getActionContent();
			if (ac != null) {
				response = (HttpServletResponse) ac
						.get(ServletActionContext.HTTP_RESPONSE);
			}
		} else {
			response = ServletActionContext.getResponse();
		}
		if (response != null) {
			if (!response.isCommitted()) {
				response.setCharacterEncoding(serverEncoding);
			}
		}
		response.setCharacterEncoding(serverEncoding);
		return response;
	}

	/**
	 * 获得request,并将其字符编码设置为{@link serverEncoding}
	 * 
	 * @return
	 */
	protected HttpServletRequest getRequest() {
		HttpServletRequest request = null;
		if (false) {
			ActionContext ac = getActionContent();
			if (ac != null) {
				request = (HttpServletRequest) ac
						.get(ServletActionContext.HTTP_REQUEST);
			}
		} else {
			request = ServletActionContext.getRequest();
		}
		if (request != null) {
			try {
				request.setCharacterEncoding(serverEncoding);
			} catch (Exception e) {
				MyLogUtils.logError("error:获得request,并将其字符编码设置为"
						+ serverEncoding, e);
			}
		}
		return request;
	}

	/**
	 * 获得原始的session<br>
	 * <strong>注意:有些操作需要判断此session是否还有效(即:没有invalidated)</strong>
	 * 
	 * @return
	 */
	protected HttpSession getSession() {
		HttpServletRequest request = getRequest();
		if (request != null) {
			HttpSession session = request.getSession();
			if (session != null) {
				try {
					session.isNew();// 在try块中调用isNew方法来确保返回的session是valid的
				} catch (IllegalStateException e) {
					return null;
				}
				return session;
			}
		}
		return null;
	}

	/**
	 * 获得parameter为name的值数组
	 * 
	 * @param name
	 *            String
	 * @return 如果没有值返回一个空数组,这样在调用返回时就不用判断是否为null
	 */
	protected String[] getParamValues(String name) {
		String[] empty = new String[0];
		if (name == null) {
			return empty;
		}
		HttpServletRequest request = getRequest();
		if (request != null) {
			String[] parameterValues = request.getParameterValues(name);
			if (parameterValues != null) {
				return parameterValues;
			}
		}
		return empty;
	}

	/**
	 * 向application中添加数据
	 * 
	 * @param name
	 *            String 属性
	 * @param value
	 *            Object 属性值
	 */
	protected void add2Application(String name, Object value) {
		if (name != null) {
			ServletContext servletContext = getServletContext();
			if (servletContext != null) {
				servletContext.setAttribute(name, value);
			}
		}
	}

	/**
	 * 从application中删除数据
	 * 
	 * @param name
	 *            String 属性
	 */
	protected void deleteFromApplication(String name) {
		if (name != null) {
			ServletContext servletContext = getServletContext();
			if (servletContext != null) {
				servletContext.removeAttribute(name);
			}
		}
	}

	/**
	 * 从application中获取数据
	 * 
	 * @param name
	 *            String 属性
	 * @param clazz
	 *            Class application中特定属性对应的值的类型(如果不确定则是Object即可)
	 */
	protected <T> T getFromApplication(String name, Class<T> clazz) {
		if (name != null) {
			ServletContext servletContext = getServletContext();
			if (servletContext != null) {
				return (T) servletContext.getAttribute(name);
			}
		}
		return null;
	}

	/**
	 * 向request中添加数据
	 * 
	 * @param name
	 *            String 属性
	 * @param value
	 *            Object 属性值
	 */
	protected void add2Request(String name, Object value) {
		if (name != null) {
			HttpServletRequest request = getRequest();
			if (request != null) {
				request.setAttribute(name, value);
			}
		}
	}

	/**
	 * 从request中删除属性
	 * 
	 * @param name
	 *            String 要删除的属性名称
	 */
	protected void deleteFromRequest(String name) {
		if (name != null) {
			HttpServletRequest request = getRequest();
			if (request != null) {
				request.removeAttribute(name);
			}
		}
	}

	/**
	 * 从request中获取数据
	 * 
	 * @param name
	 *            String 属性名称
	 */
	protected Object getFromRequest(String name) {
		if (name != null) {
			HttpServletRequest request = getRequest();
			if (request != null) {
				return request.getAttribute(name);
			}
		}
		return null;
	}

	/**
	 * 向session中添加数据
	 * 
	 * @param name
	 *            String 属性
	 * @param value
	 *            Object 属性值
	 */
	protected void add2Session(String name, Object value) {
		if (name != null) {
			HttpSession session = getSession();
			if (session != null) {
				try {
					session.setAttribute(name, value);
				} catch (IllegalStateException e) {
					MyLogUtils.logError("add2Session:[name=" + name + ",value="
							+ value + "]-session已经失效:");
				}
			}
		}
	}

	/**
	 * 从session中删除属性等于参数name的对象
	 * 
	 * @param name
	 *            String
	 */
	protected void deleteFromSession(String name) {
		if (name == null) {
			return;
		}
		HttpSession session = getSession();
		if (session != null) {
			try {
				session.removeAttribute(name);
				// session.setAttribute(name,null);
			} catch (IllegalStateException e) {
				// session已经失效
				MyLogUtils.logError("deleteFromSession:[name=" + name
						+ "]-session已经失效:");
			}
		}
	}

	/**
	 * 从session中获取数据
	 * 
	 * @param name
	 *            String 属性名称
	 * @param clazz
	 *            Class session中特定属性对应的值的类型(如果不确定则是Object即可)
	 */
	protected <T> T getFromSession(String name, Class<T> clazz) {
		if (name != null) {
			HttpSession session = getSession();
			if (session != null) {
				try {
					return (T) session.getAttribute(name);
				} catch (IllegalStateException e) {
					MyLogUtils.logError("getFromSession:[name=" + name
							+ "]-session已经失效:");
				}
			}
		}
		return null;
	}

	/**
	 * 清空session
	 */
	protected void clearSession() {
		MyProjectUtlis.clearSession(getSession(), null);
	}

	/**
	 * 创建cookie并发送到客户端
	 * 
	 * @param name
	 *            cookie名称
	 * @param value
	 *            cookie值
	 * @param expiry
	 *            cookie过期时间(以秒计),eg:60*60*24*365=一年过期
	 *            (<0:此cookie不被保存;=0:删除此cookie)
	 */
	protected void addCookie(String name, String value, int expiry) {
		if (name != null && value != null) {
			HttpServletResponse response = getResponse();
			if (response != null) {
				HttpServletRequest request = getRequest();
				// String serverName = request.getServerName();
				String contextPath = request.getContextPath();
				Cookie cookie = new Cookie(name, value);
				// 同一个cookie的name,domain,path必须一致,否则就是两个cookie了
				/*
				 * cookie所属的域,以"."开头,如:".sohu.com",用于跨域访问cookie,
				 * (不设置的话cookie不生效,即此cookie不能成功创建并发送到客户端==>这句好像不对)
				 */
				// cookie.setDomain(serverName);
				/*
				 * 1.设置Cookie路径,不设置的话为当前路径(对于Servlet来说为request.getContextPath()+web
				 * .xml里配置的该Servlet的url-pattern路径部分)
				 * 2.临时文件中显示的"名称",如:cookie:username@host/
				 * 3.删除cookie时需要设置path和添加cookie时设置的path一样才能删除
				 */
				cookie.setPath(contextPath.length() > 0 ? contextPath : "/");
				//
				/*
				 * cookie.setComment("cookie:" + System.getProperty("user.name")
				 * + "@" + string);
				 */
				// cookie.setMaxAge(60*60*24*365);//一年过期
				cookie.setMaxAge(expiry);
				response.addCookie(cookie);
			}
		}
	}

	/**
	 * 得到指定名称的cookie的值
	 * 
	 * @param name
	 *            cookie名称
	 */
	protected String getCookie(String name) {
		if (name != null) {
			HttpServletRequest request = getRequest();
			if (request != null) {
				Cookie[] cookies = request.getCookies();
				if (cookies != null) {
					for (int i = 0; i < cookies.length; i++) {
						Cookie cookie = cookies[i];
						if (cookie != null && name.equals(cookie.getName())) {
							return cookie.getValue();
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * 删除指定名称的cookie
	 * 
	 * @param name
	 *            cookie名称
	 */
	protected void deleteCookie(String name) {
		if (name != null) {
			if (true) {
				// 直接调用addCookie方法并设置过期时间为0来删除cookie
				addCookie(name, "", 0);
				return;
			}
			HttpServletResponse response = getResponse();
			if (response != null) {
				HttpServletRequest request = getRequest();
				Cookie[] cookies = request.getCookies();
				if (cookies != null) {
					String contextPath = request.getContextPath();
					for (int i = 0; i < cookies.length; i++) {
						Cookie cookie = cookies[i];
						if (cookie != null && name.equals(cookie.getName())) {
							/*
							 * 1.setPath:没有时好像删除不了
							 * 2.使用网上大多数的做法:新建一个同名cookie,path为根path
							 * ,设置立即删除,发送到客户端
							 */
							Cookie cookie2 = new Cookie(name, "");
							cookie2.setPath(contextPath.length() > 0 ? contextPath
									: "/");
							cookie2.setMaxAge(0);
							response.addCookie(cookie2);
							return;
						}
					}
				}
			}
		}
	}

	/**
	 * 获得request的IP地址
	 * 
	 * @return
	 */
	protected String getIpAddr() {
		HttpServletRequest request = getRequest();
		// 改用 MyProjectUtils 中的 getRequestIp(request) 方法获得请求ip
		/*
		 * if (request == null) {
		 * MyLogUtils.logError("getIpAddr method HttpServletRequest Object is null"
		 * ,new Exception("")); return null; } //String which="x-forwarded-for";
		 * String ipString = request.getHeader("x-forwarded-for"); if
		 * (MyStringUtils.isBlank(ipString) ||
		 * "unknown".equalsIgnoreCase(ipString)) { ipString =
		 * request.getHeader("Proxy-Client-IP"); //which="Proxy-Client-IP"; } if
		 * (MyStringUtils.isBlank(ipString) ||
		 * "unknown".equalsIgnoreCase(ipString)){
		 * ipString=request.getHeader("WL-Proxy-Client-IP");
		 * //which="WL-Proxy-Client-IP"; } if (ipString!=null) {
		 * //多个路由时,取第一个非unknown的ip String[] arr = ipString.split(","); for
		 * (final String str : arr) { if (!"unknown".equalsIgnoreCase(str)){
		 * ipString = str; break; } } } if (MyStringUtils.isBlank(ipString) ||
		 * "unknown".equalsIgnoreCase(ipString)){
		 * ipString=request.getRemoteAddr(); //which="remoteAddr"; }
		 * //System.out.println(which); return ipString;
		 */
		return MyProjectUtlis.getRequestIp(request);
	}

	/**
	 * response输出
	 * 
	 * @param content
	 *            内容
	 * @param contentType
	 *            内容类型(eg:text/plain)
	 */
	private void render(String content, String contentType) {
		if (content == null) {
			return;
		}
		if (contentType != null) {
			try {
				contentType += ";charset=" + serverEncoding;
				HttpServletResponse response = getResponse();
				if (response != null && !response.isCommitted()) {
					response.resetBuffer();
					noCache();
					response.setContentType(contentType);
					PrintWriter out = response.getWriter();
					if (out != null) {
						out.print(content);
						out.flush();
					}
				}
			} catch (Exception e) {
				MyLogUtils.logError("response输出", e);
			}
		}
	}

	/**
	 * response输出纯文本
	 * 
	 * @param text
	 *            文本内容
	 */
	protected void renderText(Object text) {
		render(text == null ? "" : text.toString(), "text/plain");
	}

	/**
	 * response输出html
	 * 
	 * @param html
	 *            html内容
	 */
	protected void renderHtml(Object html) {
		render(html == null ? "" : html.toString(), "text/html");
	}

	/**
	 * response输出xml
	 * 
	 * @param xml
	 *            xml内容
	 */
	protected void renderXml(Object xml) {
		render(xml == null ? "" : xml.toString(), "text/xml");
	}

	/**
	 * response输出json(<code>String</code>)
	 * 
	 * @param jsonStr
	 *            json内容
	 */
	protected void renderJson(Object jsonStr) {
		/*
		 * text/json其实是根本不存在的 text/javascript在有些时候客户端处理起来会有歧义
		 */
		render(jsonStr == null ? "" : jsonStr.toString(), "application/json");
	}

	/**
	 * response输出jsonp(请求中带有类似于<code>jsoncallback</code>的参数),一般用于js跨域
	 * 
	 * @param callbackname
	 *            callbackname
	 * @param json
	 *            json字符串
	 */
	protected void renderJsonp(String callbackname, Object json) {
		if (json != null) {
			json = callbackname + "(" + json.toString() + ");";
		}
		/*
		 * application/javascript是标准用法, 但是因为很多老旧浏览器并不支持application/javascript,
		 * 而所有浏览器都支持text/javascript. 在标准和广泛的兼容性之间,还是暂且选择后者吧
		 */
		render("text/javascript", json == null ? "" : json.toString());
	}

	/**
	 * 重构下载文件名称(主要是区别IE和Firefox)
	 * 
	 * @param filename
	 *            文件名称(带有后缀名,比如:"word文档.doc")
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	protected String createDownloadFilename(String filename)
			throws UnsupportedEncodingException {
		HttpServletRequest request = getRequest();
		return MyProjectUtlis.createDownloadFilename(filename, request,
				getResponse());
	}

	/**
	 * response不缓存
	 */
	protected void noCache() {
		HttpServletResponse response = getResponse();
		if (response != null) {
			response.setDateHeader("Expires", 0L);
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "no-cache");
		}
	}

	/**
	 * 获得webApplicationContext
	 * 
	 * @return
	 */
	public static WebApplicationContext getWebApplicationContext() {
		WebApplicationContextUtils
				.getWebApplicationContext(getServletContext());
		return ContextLoader.getCurrentWebApplicationContext();
	}

	/**
	 * 获得spring注册的bean
	 * 
	 * @param name
	 *            bean名称(id)
	 * @return
	 */
	protected Object getSpringBean(String name) {
		if (name != null) {
			WebApplicationContext webApplicationContext = getWebApplicationContext();
			if (webApplicationContext != null) {
				try {
					return webApplicationContext.getBean(name);
				} catch (BeansException e) {
					return null;
				}
			}
		}
		return null;
	}

	/**
	 * 将response的状态码设置为500(即:内部服务器错误)
	 */
	protected void response500() {
		HttpServletResponse response = getResponse();
		if (response != null/* &&!response.isCommitted() */) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 将response的状态码设置为200(即:ok)
	 */
	protected void response200() {
		HttpServletResponse response = getResponse();
		if (response != null/* &&!response.isCommitted() */) {
			response.setStatus(HttpServletResponse.SC_OK);
		}
	}

	protected boolean isPostRequest() {
		return "POST".equals(getRequest().getMethod());
	}

	protected boolean isGetRequest() {
		return "GET".equals(getRequest().getMethod());
	}

	protected boolean isPutRequest() {
		return "PUT".equals(getRequest().getMethod());
	}

	/**
	 * 是否是ajax请求
	 * 
	 * @return
	 */
	protected boolean isAjaxRequest() {
		String h = getRequest().getHeader("X-Requested-With");
		// XMLHttpRequest
		return "XMLHttpRequest".equals(h);
	}

	/**
	 * 获得网站的跟路径(最后不带"/"),类似于:http://127.0.0.1:8080/edus
	 * 
	 * @return
	 */
	protected String getWebRootPath() {
		HttpServletRequest req = getRequest();
		if (req != null) {
			StringBuffer rp = new StringBuffer();
			String scheme = req.getScheme();
			String ctx = req.getContextPath();
			int port = req.getServerPort();
			if (port < 0) {
				port = 80;
			}
			rp.append(scheme).append("://").append(req.getServerName());
			if (((scheme.equals("http")) && (port != 80))
					|| ((scheme.equals("https")) && (port != 443))) {
				rp.append(':').append(port);
			}
			rp.append(ctx);
			return rp.toString();
		}
		return null;
	}

	/**
	 * 判断是否是非法请求
	 * 
	 * @param refer
	 *            要判断的请求的合法referer(不带web项目主路径),eg:"/reserve_yuyue.do"(必须不为null)
	 * @param requestMethod
	 *            要判断的请求的method(post/get/put)
	 * @param isAjax
	 *            要判断的请求是否是ajax请求
	 * @return
	 */
	protected boolean isInvalidRequest(String refer, String requestMethod,
			boolean isAjax) {
		// 防止用户非法提交
		if (refer == null) {
			return false;
		}
		String ref = getRequest().getHeader("referer");
		if (ref == null || ref.indexOf(refer) == -1) {
			return true;
		}
		if (requestMethod != null) {
			requestMethod = requestMethod.toUpperCase();
			if ("POST".equals(requestMethod)) {
				if (!isPostRequest()) {
					return true;
				}
			} else if ("GET".equals(requestMethod)) {
				if (!isGetRequest()) {
					return true;
				}
			} else if ("PUT".equals(requestMethod)) {
				if (!isPutRequest()) {
					return true;
				}
			}
		}
		if (isAjax) {
			if (!isAjaxRequest()) {
				return true;
			}
		} else {
			if (isAjaxRequest()) {
				return true;
			}
		}
		return false;
	}

	private ActionContext getActionContent() {
		return ActionContext.getContext();
	}
}
