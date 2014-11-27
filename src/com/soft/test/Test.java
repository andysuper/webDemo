package com.soft.test;

import java.util.List;
import java.util.Map;

import com.soft.service.InfoService;
import com.soft.service.impl.InfoServiceImpl;

public class Test {

	public static void main(String[] args){
		InfoService infoService=new InfoServiceImpl();
		List<Map<String, Object>> list=infoService.findAllInfo();
		Map<String, Object> map=null;
		System.out.println(list);
		for(int i=0; i<list.size();i++){
			map=list.get(i);
			for(int j=0;j<map.size();j++){
				System.out.println(map.get("name"));
			}
		}
		
	}
	
	public static String parseJsonData(){
		
		return "";
	}
}
