package com.soft.service;

import java.util.List;
import java.util.Map;

public interface InfoService {
	/**
	 * 查找全部信息
	 * 
	 * @return
	 */
	public List<Map<String, Object>> findAllInfo();
}
