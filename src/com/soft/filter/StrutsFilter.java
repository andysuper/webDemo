package com.soft.filter;

import java.io.IOException;

import javax.servlet.FilterConfig;

import org.apache.struts2.config.StrutsXmlConfigurationProvider;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;


public class StrutsFilter  extends StrutsPrepareAndExecuteFilter{


	/* 一直想把struts2的配置文件放到src/com/soft/resources文件夹下，试了多次未成功。
	protected void postInit(Dispatcher dispatcher, FilterConfig filterConfig) {

		String patternPathConfig = filterConfig
				.getInitParameter("patternPathConfig");

		PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();

		try {
			Resource[] resources = pathMatchingResourcePatternResolver
					.getResources("classpath*:" + patternPathConfig + "/*.xml");
			for (Resource resource : resources) {
				System.out.println("path:>>>" + patternPathConfig + "/"
						+ resource.getFilename());
				dispatcher.getConfigurationManager().addContainerProvider(
						new StrutsXmlConfigurationProvider(patternPathConfig
								+ "/" + resource.getFilename(), false,
								filterConfig.getServletContext()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		dispatcher.getConfigurationManager().reload();

	}
	
	<filter>
	<filter-name>strutsFilter</filter-name>
	<filter-class>com.soft.filter.StrutsFilter</filter-class>
	<init-param>
		<param-name>patternPathConfig</param-name>
		<param-value>com/soft/resources/strutsConfigs</param-value>
	</init-param>
</filter>
<filter-mapping>
	<filter-name>strutsFilter</filter-name>
	<url-pattern></url-pattern>
</filter-mapping>*/

	/*
    <include file="struts-default.xml"></include>
    <include file="struts-plugin.xml"></include> */
}