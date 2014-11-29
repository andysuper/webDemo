package com.soft.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;

import com.soft.action.base.BaseAction;
import com.soft.service.InfoService;
import com.soft.util.MyCollectionUtils;
import com.soft.util.MyStringUtils;

@SuppressWarnings("serial")
@Controller
public class InfoAction extends BaseAction {

	@Resource(name = "infoService")
	InfoService infoService;

	public String findAllInfo() {
		List<Map<String, Object>> list=infoService.findAllInfo();
		List<Map<String, Object>> jsonObjs=new ArrayList<Map<String, Object>>();
		try {
            if (list != null) {
                for (Map<String, Object> eo : list) {
                    Map<String, Object> j = new HashMap<String, Object>();
                    j.put("id", eo.get("ID"));
                    j.put("pId", eo.get("PID"));
                    j.put("name", eo.get("NAME"));
                    jsonObjs.add(j);
                }
            }
            list.clear();
            list = null;
            String trees = MyStringUtils.toJSON(MyCollectionUtils.list2Tree(jsonObjs, "id", "pId"));
            renderText(trees);
        } catch (Exception e) {
            e.printStackTrace();
        }
		System.out.println("aaaa");
		return null;
	}
}
