package com.soft.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.soft.dao.InfoDao;
import com.soft.service.InfoService;

@Service("infoService")
public class InfoServiceImpl implements InfoService {

	@Resource(name = "infoDao")
	private InfoDao infoDao;

	public List<Map<String, Object>> findAllInfo() {
		return infoDao.findAllInfo();
	}
}
