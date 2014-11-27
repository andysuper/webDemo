package com.soft.util;

import java.util.List;

public class PageList {

	/**
	 * 分页结果集
	 */
	protected List dataList = null;
	/**
	 * 记录总数
	 */
	protected int totalcount = 0;
	/**
	 * 每页显示记录数
	 */
	protected int pageSize = 10;
	/**
	 * 当前页数
	 */
	protected int currentPage = 1;
	/**
	 * 总页数
	 */
	protected int totalPageCount = 1;
	/**
	 * 分页页脚
	 */
	protected String footer;

	/**
	 * 是否使用默认的每页条数
	 */
	protected boolean defaultPageSize = true;

	/**
	 * 是否是特殊分页 进行特殊分页处理
	 */
	protected boolean specialPage = false;

	/**
	 * 汇总信息展示列
	 */
	protected String[] countStr;

	protected Double[] resultOther;

	/**
	 * 汇总信息展示类型 0:sum 1:avg 2:min 3:max
	 */
	protected Integer[] countStrType;

	protected boolean isFlow = false;

	protected boolean notJoinFlow = false;

	public boolean isNotJoinFlow() {
		return notJoinFlow;
	}

	public void setNotJoinFlow(boolean notJoinFlow) {
		this.notJoinFlow = notJoinFlow;
	}

	public String[] getCountStr() {
		return countStr;
	}

	public void setCountStr(String[] countStr) {
		this.countStr = countStr;
	}

	/* 初始化分页组件 */
	public PageList(int totalcount, int pageSize, int currentPage, List dataList) {
		setPageSize(pageSize);
		setTotalcount(totalcount);
		setCurrentPage(currentPage);
		setDataList(dataList);
	}

	/**
	 * @param pageSize
	 *            每页显示的条数
	 * @param currentPage
	 *            当前显示第几页
	 */
	public PageList(int pageSize, int currentPage) {
		setPageSize(pageSize);
		setCurrentPage(currentPage);
	}

	public PageList(String limit, String starts) {
		// pageSize每页显示的记录数，start本页的起始数据
		int start = 0;
		if (limit.endsWith("_flow")) {
			limit = limit.substring(0, limit.length() - 5);
			setFlow(true);
		}
		if (MyStringUtils.notBlank(limit)) {
			pageSize = Integer.parseInt(limit);
			defaultPageSize = false;
			setPageSize(pageSize);
		}
		if (MyStringUtils.notBlank(starts)) {
			start = Integer.parseInt(starts);
		}
		// 算出当前页currentPage
		currentPage = start / pageSize + 1;
		setCurrentPage(currentPage);
	}

	/**
	 * 封装分页栏函数 必需被包含在某个Form之中
	 * 
	 * @return String pages 当前页号 pageSize 每页显示行数
	 */
	public String getFooter() {
		return commonFooter(null);
	}

	public String getFooter(String which) {
		return commonFooter(which);
	}

	protected String commonFooter(String which) {
		String subfix = "";
		if (MyStringUtils.notBlank(which)) {
			subfix = "_" + which;
		} else {
			which = "";
		}
		StringBuffer pageStr = new StringBuffer("");
		pageStr.append("<center><p class='pages'>");
		int totalPages = getTotalPageCount(); // 总页数
		int prevPage = currentPage - 1; // 上一页
		int nextPage = currentPage + 1; // 下一页
		pageStr.append("<span style='color:#6795B4;'>共有" + totalcount
				+ "条记录</span>&nbsp;&nbsp;");
		pageStr.append("<span style='color:#6795B4;'>第" + currentPage + "页/共"
				+ totalPages + "页</span>&nbsp;&nbsp;");
		if (currentPage > 1)
			pageStr.append("<span><a style='cursor: pointer;text-decoration:underline;color:#6795B4' onclick=\"gotoPage(1,'"
					+ which + "');\">首页</a></span>&nbsp;&nbsp;");
		if (currentPage == 1)
			pageStr.append("<span style='color:#6795B4'>首页</span>&nbsp;&nbsp;&nbsp;&nbsp;");
		if (currentPage > 1)
			pageStr.append("<span><a style='cursor: pointer;text-decoration:underline;color:#6795B4' onclick="
					+ "\"gotoPage("
					+ prevPage
					+ ",'"
					+ which
					+ "');\">上一页</a></span>&nbsp;&nbsp;");
		if (currentPage <= 1)
			pageStr.append("<span style='color:#6795B4'>上一页</span>&nbsp;&nbsp;");
		if (currentPage < totalPages)
			pageStr.append("<span><a style='cursor: pointer;text-decoration:underline;color:#6795B4;' onclick="
					+ "\"gotoPage("
					+ nextPage
					+ ",'"
					+ which
					+ "');\">下一页</a></span>&nbsp;&nbsp;");
		if (currentPage >= totalPages)
			pageStr.append("<span style='color:#6795B4;'>下一页</span>&nbsp;&nbsp;");
		if (currentPage < totalPages)
			pageStr.append("<span><a style='cursor: pointer;text-decoration:underline;color:#6795B4;' onclick="
					+ "\"gotoPage("
					+ totalPages
					+ ",'"
					+ which
					+ "');\">末页</a></span>&nbsp;&nbsp;");
		if (currentPage == totalPages)
			pageStr.append("<span style='color:#6795B4;'>末页</span>&nbsp;&nbsp;");
		pageStr.append("<span style='color:#6795B4;'>跳转至第:<input type='text' style='border:1px solid #7F9DB9' value='"
				+ currentPage
				+ "' id='jumpPageBox"
				+ subfix
				+ "' size='2' onblur=\"checkCurrentPage("
				+ totalPages
				+ ",'"
				+ which
				+ "');\"/>页<input class='right-button02' type='button' value='跳转' onclick=\"jumpByInputedNum('"
				+ which + "');\"/></span>");
		pageStr.append("</p></center>");
		pageStr.append("<input type='hidden' value='" + currentPage
				+ "' name='currentPage' id='currentPage" + subfix + "' />");
		pageStr.append("<input type='hidden' value='" + pageSize
				+ "' name='pageSize' id='pageSize" + subfix + "' />");
		pageStr.append("<input type='hidden' value='" + totalPageCount
				+ "' name='totalPageCount' id='totalPageCount" + subfix
				+ "' />");
		return pageStr.toString();
	}

	public List getDataList() {
		return dataList;
	}

	public void setDataList(List dataList) {
		this.dataList = dataList;
	}

	public int getTotalcount() {
		return totalcount;
	}

	public void setTotalcount(int totalcount) {
		this.totalcount = totalcount;
		// 设置总条数时即计算总页数,如果为0则置为1
		if (totalcount % pageSize == 0) {
			totalPageCount = totalcount / pageSize;
		} else {
			totalPageCount = totalcount / pageSize + 1;
		}
	}

	public void setSpecialTotalcount(int totalcount) {
		this.totalcount = totalcount;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		if (pageSize <= 0) {
			// 默认每页显示10条
			pageSize = 10;
		}
		this.pageSize = pageSize;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		if (currentPage <= 0) {
			currentPage = 1;
		}
		this.currentPage = currentPage;
	}

	public int getTotalPageCount() {
		return totalPageCount;
	}

	public void setTotalPageCount(int totalPageCount) {
		this.totalPageCount = totalPageCount;
	}

	public void setFooter(String footer) {
		this.footer = footer;
	}

	public boolean isDefaultPageSize() {
		return defaultPageSize;
	}

	public void setDefaultPageSize(boolean defaultPageSize) {
		this.defaultPageSize = defaultPageSize;
	}

	public boolean isSpecialPage() {
		return specialPage;
	}

	public void setSpecialPage(boolean specialPage) {
		this.specialPage = specialPage;
	}

	public Integer[] getCountStrType() {
		return countStrType;
	}

	public void setCountStrType(Integer[] countStrType) {
		this.countStrType = countStrType;
	}

	public Double[] getResultOther() {
		return resultOther;
	}

	public void setResultOther(Double[] resultOther) {
		this.resultOther = resultOther;
	}

	public boolean isFlow() {
		return isFlow;
	}

	public void setFlow(boolean isFlow) {
		this.isFlow = isFlow;
	}
}
