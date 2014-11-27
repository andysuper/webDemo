package com.soft.util;


import org.apache.log4j.Logger;
//import org.slf4j.LoggerFactory;

public class MyLogUtils {

	public static Logger logger=Logger.getLogger(MyLogUtils.class);
	//public static Logger logger_logback=LoggerFactory.getLogger(MyLogUtils.class);
	/**
	 * ��¼info������־
	 * @param message String
	 * 		error��Ϣ
	 * @param t Throwable
	 * 		�쳣
	 */
	public static void logInfo(Object message){
		logger.info(message==null?"":message);
	}
	/**
	 * ��¼info������־
	 * @param message String
	 * 		error��Ϣ
	 * @param e Throwable
	 * 		�쳣
	 */
	public static void logInfo(Object message,Throwable e){
		logger.info(message==null?"":message, e);
	}
	
	/**
	 * ��¼warn������־
	 * @param message String
	 * 		error��Ϣ
	 * @param t Throwable
	 * 		�쳣
	 */
	public static void logWarn(Object message){
		logger.warn(message==null?"":message);
	}
	/**
	 * ��¼warn������־
	 * @param message String
	 * 		error��Ϣ
	 * @param e Throwable
	 * 		�쳣
	 */
	public static void logWarn(Object message,Throwable e){
		logger.warn(message==null?"":message, e);
	}
	
	/**
	 * ��¼error������־
	 * @param message String
	 * 		error��Ϣ
	 * @param t Throwable
	 * 		�쳣
	 */
	public static void logError(Object message){
		logger.error(message==null?"":message);
	}
	/**
	 * ��¼error������־
	 * @param message String
	 * 		error��Ϣ
	 * @param e Throwable
	 * 		�쳣
	 */
	public static void logError(Object message,Throwable e){
		logger.error(message==null?"":message, e);
	}
}
