package com.soft.util;

import java.util.List;

public class PageList {

	/**
	 * ��ҳ�����
	 */
	protected List dataList = null;
	/**
	 * ��¼����
	 */
	protected int totalcount = 0;
	/**
	 * ÿҳ��ʾ��¼��
	 */
	protected int pageSize = 10;
	/**
	 * ��ǰҳ��
	 */
	protected int currentPage = 1;
	/**
	 * ��ҳ��
	 */
	protected int totalPageCount = 1;
	/**
	 * ��ҳҳ��
	 */
	protected String footer;

	/**
	 * �Ƿ�ʹ��Ĭ�ϵ�ÿҳ����
	 */
	protected boolean defaultPageSize = true;

	/**
	 * �Ƿ��������ҳ ���������ҳ����
	 */
	protected boolean specialPage = false;

	/**
	 * ������Ϣչʾ��
	 */
	protected String[] countStr;

	protected Double[] resultOther;

	/**
	 * ������Ϣչʾ���� 0:sum 1:avg 2:min 3:max
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

	/* ��ʼ����ҳ��� */
	public PageList(int totalcount, int pageSize, int currentPage, List dataList) {
		setPageSize(pageSize);
		setTotalcount(totalcount);
		setCurrentPage(currentPage);
		setDataList(dataList);
	}

	/**
	 * @param pageSize
	 *            ÿҳ��ʾ������
	 * @param currentPage
	 *            ��ǰ��ʾ�ڼ�ҳ
	 */
	public PageList(int pageSize, int currentPage) {
		setPageSize(pageSize);
		setCurrentPage(currentPage);
	}

	public PageList(String limit, String starts) {
		// pageSizeÿҳ��ʾ�ļ�¼����start��ҳ����ʼ����
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
		// �����ǰҳcurrentPage
		currentPage = start / pageSize + 1;
		setCurrentPage(currentPage);
	}

	/**
	 * ��װ��ҳ������ ���豻������ĳ��Form֮��
	 * 
	 * @return String pages ��ǰҳ�� pageSize ÿҳ��ʾ����
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
		int totalPages = getTotalPageCount(); // ��ҳ��
		int prevPage = currentPage - 1; // ��һҳ
		int nextPage = currentPage + 1; // ��һҳ
		pageStr.append("<span style='color:#6795B4;'>����" + totalcount
				+ "����¼</span>&nbsp;&nbsp;");
		pageStr.append("<span style='color:#6795B4;'>��" + currentPage + "ҳ/��"
				+ totalPages + "ҳ</span>&nbsp;&nbsp;");
		if (currentPage > 1)
			pageStr.append("<span><a style='cursor: pointer;text-decoration:underline;color:#6795B4' onclick=\"gotoPage(1,'"
					+ which + "');\">��ҳ</a></span>&nbsp;&nbsp;");
		if (currentPage == 1)
			pageStr.append("<span style='color:#6795B4'>��ҳ</span>&nbsp;&nbsp;&nbsp;&nbsp;");
		if (currentPage > 1)
			pageStr.append("<span><a style='cursor: pointer;text-decoration:underline;color:#6795B4' onclick="
					+ "\"gotoPage("
					+ prevPage
					+ ",'"
					+ which
					+ "');\">��һҳ</a></span>&nbsp;&nbsp;");
		if (currentPage <= 1)
			pageStr.append("<span style='color:#6795B4'>��һҳ</span>&nbsp;&nbsp;");
		if (currentPage < totalPages)
			pageStr.append("<span><a style='cursor: pointer;text-decoration:underline;color:#6795B4;' onclick="
					+ "\"gotoPage("
					+ nextPage
					+ ",'"
					+ which
					+ "');\">��һҳ</a></span>&nbsp;&nbsp;");
		if (currentPage >= totalPages)
			pageStr.append("<span style='color:#6795B4;'>��һҳ</span>&nbsp;&nbsp;");
		if (currentPage < totalPages)
			pageStr.append("<span><a style='cursor: pointer;text-decoration:underline;color:#6795B4;' onclick="
					+ "\"gotoPage("
					+ totalPages
					+ ",'"
					+ which
					+ "');\">ĩҳ</a></span>&nbsp;&nbsp;");
		if (currentPage == totalPages)
			pageStr.append("<span style='color:#6795B4;'>ĩҳ</span>&nbsp;&nbsp;");
		pageStr.append("<span style='color:#6795B4;'>��ת����:<input type='text' style='border:1px solid #7F9DB9' value='"
				+ currentPage
				+ "' id='jumpPageBox"
				+ subfix
				+ "' size='2' onblur=\"checkCurrentPage("
				+ totalPages
				+ ",'"
				+ which
				+ "');\"/>ҳ<input class='right-button02' type='button' value='��ת' onclick=\"jumpByInputedNum('"
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
		// ����������ʱ��������ҳ��,���Ϊ0����Ϊ1
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
			// Ĭ��ÿҳ��ʾ10��
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
