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
	 * ����������(����ΪUTF-8)
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
	 * ��ȡǰ̨��һ����ֵ
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
	 * ��ȡǰ̨��������ֵ
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
	 * ��Ӧ���ݵ�ǰ̨
	 * 
	 * @param obj
	 */
	public void respData(Object obj) {
		out.print(obj);
	}

	/**
	 * ���action�������ռ�
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
			MyLogUtils.logError("error:���action�������ռ�", e);
		}
		return namespace == null ? "" : namespace;
	}

	/**
	 * ���servletContext
	 * 
	 * @return
	 */
	public static ServletContext getServletContext() {
		return ServletActionContext.getServletContext();
	}

	/**
	 * ���response,�������ַ���������Ϊ{@link serverEncoding}<br>
	 * <strong>ע��:��Щ������Ҫ�жϴ�response�Ƿ��Ѿ�committed</strong>
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
	 * ���request,�������ַ���������Ϊ{@link serverEncoding}
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
				MyLogUtils.logError("error:���request,�������ַ���������Ϊ"
						+ serverEncoding, e);
			}
		}
		return request;
	}

	/**
	 * ���ԭʼ��session<br>
	 * <strong>ע��:��Щ������Ҫ�жϴ�session�Ƿ���Ч(��:û��invalidated)</strong>
	 * 
	 * @return
	 */
	protected HttpSession getSession() {
		HttpServletRequest request = getRequest();
		if (request != null) {
			HttpSession session = request.getSession();
			if (session != null) {
				try {
					session.isNew();// ��try���е���isNew������ȷ�����ص�session��valid��
				} catch (IllegalStateException e) {
					return null;
				}
				return session;
			}
		}
		return null;
	}

	/**
	 * ���parameterΪname��ֵ����
	 * 
	 * @param name
	 *            String
	 * @return ���û��ֵ����һ��������,�����ڵ��÷���ʱ�Ͳ����ж��Ƿ�Ϊnull
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
	 * ��application���������
	 * 
	 * @param name
	 *            String ����
	 * @param value
	 *            Object ����ֵ
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
	 * ��application��ɾ������
	 * 
	 * @param name
	 *            String ����
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
	 * ��application�л�ȡ����
	 * 
	 * @param name
	 *            String ����
	 * @param clazz
	 *            Class application���ض����Զ�Ӧ��ֵ������(�����ȷ������Object����)
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
	 * ��request���������
	 * 
	 * @param name
	 *            String ����
	 * @param value
	 *            Object ����ֵ
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
	 * ��request��ɾ������
	 * 
	 * @param name
	 *            String Ҫɾ������������
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
	 * ��request�л�ȡ����
	 * 
	 * @param name
	 *            String ��������
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
	 * ��session���������
	 * 
	 * @param name
	 *            String ����
	 * @param value
	 *            Object ����ֵ
	 */
	protected void add2Session(String name, Object value) {
		if (name != null) {
			HttpSession session = getSession();
			if (session != null) {
				try {
					session.setAttribute(name, value);
				} catch (IllegalStateException e) {
					MyLogUtils.logError("add2Session:[name=" + name + ",value="
							+ value + "]-session�Ѿ�ʧЧ:");
				}
			}
		}
	}

	/**
	 * ��session��ɾ�����Ե��ڲ���name�Ķ���
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
				// session�Ѿ�ʧЧ
				MyLogUtils.logError("deleteFromSession:[name=" + name
						+ "]-session�Ѿ�ʧЧ:");
			}
		}
	}

	/**
	 * ��session�л�ȡ����
	 * 
	 * @param name
	 *            String ��������
	 * @param clazz
	 *            Class session���ض����Զ�Ӧ��ֵ������(�����ȷ������Object����)
	 */
	protected <T> T getFromSession(String name, Class<T> clazz) {
		if (name != null) {
			HttpSession session = getSession();
			if (session != null) {
				try {
					return (T) session.getAttribute(name);
				} catch (IllegalStateException e) {
					MyLogUtils.logError("getFromSession:[name=" + name
							+ "]-session�Ѿ�ʧЧ:");
				}
			}
		}
		return null;
	}

	/**
	 * ���session
	 */
	protected void clearSession() {
		MyProjectUtlis.clearSession(getSession(), null);
	}

	/**
	 * ����cookie�����͵��ͻ���
	 * 
	 * @param name
	 *            cookie����
	 * @param value
	 *            cookieֵ
	 * @param expiry
	 *            cookie����ʱ��(�����),eg:60*60*24*365=һ�����
	 *            (<0:��cookie��������;=0:ɾ����cookie)
	 */
	protected void addCookie(String name, String value, int expiry) {
		if (name != null && value != null) {
			HttpServletResponse response = getResponse();
			if (response != null) {
				HttpServletRequest request = getRequest();
				// String serverName = request.getServerName();
				String contextPath = request.getContextPath();
				Cookie cookie = new Cookie(name, value);
				// ͬһ��cookie��name,domain,path����һ��,�����������cookie��
				/*
				 * cookie��������,��"."��ͷ,��:".sohu.com",���ڿ������cookie,
				 * (�����õĻ�cookie����Ч,����cookie���ܳɹ����������͵��ͻ���==>�����񲻶�)
				 */
				// cookie.setDomain(serverName);
				/*
				 * 1.����Cookie·��,�����õĻ�Ϊ��ǰ·��(����Servlet��˵Ϊrequest.getContextPath()+web
				 * .xml�����õĸ�Servlet��url-pattern·������)
				 * 2.��ʱ�ļ�����ʾ��"����",��:cookie:username@host/
				 * 3.ɾ��cookieʱ��Ҫ����path�����cookieʱ���õ�pathһ������ɾ��
				 */
				cookie.setPath(contextPath.length() > 0 ? contextPath : "/");
				//
				/*
				 * cookie.setComment("cookie:" + System.getProperty("user.name")
				 * + "@" + string);
				 */
				// cookie.setMaxAge(60*60*24*365);//һ�����
				cookie.setMaxAge(expiry);
				response.addCookie(cookie);
			}
		}
	}

	/**
	 * �õ�ָ�����Ƶ�cookie��ֵ
	 * 
	 * @param name
	 *            cookie����
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
	 * ɾ��ָ�����Ƶ�cookie
	 * 
	 * @param name
	 *            cookie����
	 */
	protected void deleteCookie(String name) {
		if (name != null) {
			if (true) {
				// ֱ�ӵ���addCookie���������ù���ʱ��Ϊ0��ɾ��cookie
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
							 * 1.setPath:û��ʱ����ɾ������
							 * 2.ʹ�����ϴ����������:�½�һ��ͬ��cookie,pathΪ��path
							 * ,��������ɾ��,���͵��ͻ���
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
	 * ���request��IP��ַ
	 * 
	 * @return
	 */
	protected String getIpAddr() {
		HttpServletRequest request = getRequest();
		// ���� MyProjectUtils �е� getRequestIp(request) �����������ip
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
		 * //���·��ʱ,ȡ��һ����unknown��ip String[] arr = ipString.split(","); for
		 * (final String str : arr) { if (!"unknown".equalsIgnoreCase(str)){
		 * ipString = str; break; } } } if (MyStringUtils.isBlank(ipString) ||
		 * "unknown".equalsIgnoreCase(ipString)){
		 * ipString=request.getRemoteAddr(); //which="remoteAddr"; }
		 * //System.out.println(which); return ipString;
		 */
		return MyProjectUtlis.getRequestIp(request);
	}

	/**
	 * response���
	 * 
	 * @param content
	 *            ����
	 * @param contentType
	 *            ��������(eg:text/plain)
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
				MyLogUtils.logError("response���", e);
			}
		}
	}

	/**
	 * response������ı�
	 * 
	 * @param text
	 *            �ı�����
	 */
	protected void renderText(Object text) {
		render(text == null ? "" : text.toString(), "text/plain");
	}

	/**
	 * response���html
	 * 
	 * @param html
	 *            html����
	 */
	protected void renderHtml(Object html) {
		render(html == null ? "" : html.toString(), "text/html");
	}

	/**
	 * response���xml
	 * 
	 * @param xml
	 *            xml����
	 */
	protected void renderXml(Object xml) {
		render(xml == null ? "" : xml.toString(), "text/xml");
	}

	/**
	 * response���json(<code>String</code>)
	 * 
	 * @param jsonStr
	 *            json����
	 */
	protected void renderJson(Object jsonStr) {
		/*
		 * text/json��ʵ�Ǹ��������ڵ� text/javascript����Щʱ��ͻ��˴���������������
		 */
		render(jsonStr == null ? "" : jsonStr.toString(), "application/json");
	}

	/**
	 * response���jsonp(�����д���������<code>jsoncallback</code>�Ĳ���),һ������js����
	 * 
	 * @param callbackname
	 *            callbackname
	 * @param json
	 *            json�ַ���
	 */
	protected void renderJsonp(String callbackname, Object json) {
		if (json != null) {
			json = callbackname + "(" + json.toString() + ");";
		}
		/*
		 * application/javascript�Ǳ�׼�÷�, ������Ϊ�ܶ��Ͼ����������֧��application/javascript,
		 * �������������֧��text/javascript. �ڱ�׼�͹㷺�ļ�����֮��,��������ѡ����߰�
		 */
		render("text/javascript", json == null ? "" : json.toString());
	}

	/**
	 * �ع������ļ�����(��Ҫ������IE��Firefox)
	 * 
	 * @param filename
	 *            �ļ�����(���к�׺��,����:"word�ĵ�.doc")
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
	 * response������
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
	 * ���webApplicationContext
	 * 
	 * @return
	 */
	public static WebApplicationContext getWebApplicationContext() {
		WebApplicationContextUtils
				.getWebApplicationContext(getServletContext());
		return ContextLoader.getCurrentWebApplicationContext();
	}

	/**
	 * ���springע���bean
	 * 
	 * @param name
	 *            bean����(id)
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
	 * ��response��״̬������Ϊ500(��:�ڲ�����������)
	 */
	protected void response500() {
		HttpServletResponse response = getResponse();
		if (response != null/* &&!response.isCommitted() */) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * ��response��״̬������Ϊ200(��:ok)
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
	 * �Ƿ���ajax����
	 * 
	 * @return
	 */
	protected boolean isAjaxRequest() {
		String h = getRequest().getHeader("X-Requested-With");
		// XMLHttpRequest
		return "XMLHttpRequest".equals(h);
	}

	/**
	 * �����վ�ĸ�·��(��󲻴�"/"),������:http://127.0.0.1:8080/edus
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
	 * �ж��Ƿ��ǷǷ�����
	 * 
	 * @param refer
	 *            Ҫ�жϵ�����ĺϷ�referer(����web��Ŀ��·��),eg:"/reserve_yuyue.do"(���벻Ϊnull)
	 * @param requestMethod
	 *            Ҫ�жϵ������method(post/get/put)
	 * @param isAjax
	 *            Ҫ�жϵ������Ƿ���ajax����
	 * @return
	 */
	protected boolean isInvalidRequest(String refer, String requestMethod,
			boolean isAjax) {
		// ��ֹ�û��Ƿ��ύ
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
