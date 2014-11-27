package com.soft.util;

public class Constants {
	/**
	 * session中存放的登录用户的key,默认为"user"
	 */
	public static final String sessionUserKey = "user";
	/**
	 * session中存放的登录用户教务组织id的key,默认为"userEduOrgs"
	 */
	public static final String sessionUserEduOrgs = "userEduOrgs";
	/**
	 * session中存放的登录用户组织机构id的key,默认为"userOrgDepts"
	 */
	public static final String sessionUserOrgDepts = "userOrgDepts";
	/**
	 * session中存放的登录用户组织机构id的key,默认为"userOrgSchools"
	 */
	public static final String sessionUserOrgSchools = "userOrgSchools";
	/**
	 * 临时存放登录用户可访问栏目的列表的key,默认为"programs"
	 */
	public static final String sessionPrograms = "programs";
	/**
	 * session中存放的显示的菜单的key,默认为"shownMenus"
	 */
	public static final String sessionMenus = "shownMenus";
	/**
	 * session中存放登录用户有权访问的url的key,默认为"userAuthUrls"
	 */
	public static final String sessionAuthUrls = "userAuthUrls";
	/**
	 * session中存放的系统权限的更新时间的key,默认为"authsUpdatedTime".<br>
	 * (application中存放系统权限更新时间的key也是这个)
	 */
	public static final String sessionAuthsUpdatedTime = "authsUpdatedTime";
	/**
	 * session中存放关于用户的小图标设置的key，默认为"userToolSet"; pld 20130719
	 */
	public static final String sessionUserToolSet = "userToolSet";
	/**
	 * application中存放全部系统字典的key,默认为"allDics"
	 */
	public static final String applicationDics = "allDics";
	/**
	 * application中按层存放系统字典的key,默认为"dicTrees"
	 */
	public static final String applicationDicTrees = "dicTrees";
	/**
	 * session中存放的登录用户是否是管理员的key,默认为"userIsAdmin"
	 */
	public static final String userIsAdmin = "userIsAdmin";
	/**
	 * application中存放的所有允许登录的ip设置
	 */
	public static final String allSafeIps = "allSafeIps";
	/**
	 * application中存放的公共安全设置
	 */
	public static final String publicSafeSet = "publicSafeSet";
	/**
	 * application中存放的登录时间设置
	 */
	public static final String loginTimes = "loginTimes";
	/**
	 * application中存放的分配预约设置
	 */
	public static final String distriResvSetting = "distriResvSetting";
	/**
	 * application中存放的模考分配预约设置
	 */
	public static final String distriResvSettingMokao = "distriResvSettingMokao";

	// ------------新增-------------------------
	/**
	 * session中存放的登录用户所有机构id的key,默认为"userOrg" List<Map<String, Object>>
	 */
	public static final String sessionUserOrg = "userOrg";

	/**
	 * session中存放的登录用户所有机构id的key,默认为"userOrgId" id,id,id
	 */
	public static final String sessionUserOrgId = "userOrgId";

	/**
	 * session中存放的登录用户所有机构id的key,默认为"userOrgName" name,name,name
	 */
	public static final String sessionUserOrgName = "userOrgName";

	/**
	 * session中存放的登录用户所有校区的key,默认为"userAllSchools" List<Map<String, Object>>
	 */
	public static final String sessionUserAllSchools = "userAllSchools";
	/**
	 * session中存放的登录用户所有校区的key,默认为"userAllSchoolsId" id,id,id
	 */
	public static final String sessionUserAllSchoolsId = "userAllSchoolsId";
	/**
	 * session中存放的登录用户所有校区的key,默认为"userAllSchoolsName" name,name,name
	 */
	public static final String sessionUserAllSchoolsName = "userAllSchoolsName";

	/**
	 * session中存放的登录用户所有业务模式的key,默认为"userAllEduOrgs" List<Map<String, Object>>
	 */
	public static final String sessionUserAllEduOrgs = "userAllEduOrgs";
	/**
	 * session中存放的登录用户所有业务模式的key,默认为"userAllEduOrgsId" id,id,id
	 */
	public static final String sessionUserAllEduOrgsId = "userAllEduOrgsId";
	/**
	 * session中存放的登录用户所有业务模式的key,默认为"userAllEduOrgsName" name,name,name
	 */
	public static final String sessionUserAllEduOrgsName = "userAllEduOrgsName";

	/**
	 * session中存放的登录用户简写名称
	 */
	public static final String sessionSecondName = "secondName";

}
