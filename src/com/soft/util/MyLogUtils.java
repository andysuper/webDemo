package com.soft.util;


import org.apache.log4j.Logger;
//import org.slf4j.LoggerFactory;

public class MyLogUtils {

	public static Logger logger=Logger.getLogger(MyLogUtils.class);
	//public static Logger logger_logback=LoggerFactory.getLogger(MyLogUtils.class);
	/**
	 * 记录info级别日志
	 * @param message String
	 * 		error信息
	 * @param t Throwable
	 * 		异常
	 */
	public static void logInfo(Object message){
		logger.info(message==null?"":message);
	}
	/**
	 * 记录info级别日志
	 * @param message String
	 * 		error信息
	 * @param e Throwable
	 * 		异常
	 */
	public static void logInfo(Object message,Throwable e){
		logger.info(message==null?"":message, e);
	}
	
	/**
	 * 记录warn级别日志
	 * @param message String
	 * 		error信息
	 * @param t Throwable
	 * 		异常
	 */
	public static void logWarn(Object message){
		logger.warn(message==null?"":message);
	}
	/**
	 * 记录warn级别日志
	 * @param message String
	 * 		error信息
	 * @param e Throwable
	 * 		异常
	 */
	public static void logWarn(Object message,Throwable e){
		logger.warn(message==null?"":message, e);
	}
	
	/**
	 * 记录error级别日志
	 * @param message String
	 * 		error信息
	 * @param t Throwable
	 * 		异常
	 */
	public static void logError(Object message){
		logger.error(message==null?"":message);
	}
	/**
	 * 记录error级别日志
	 * @param message String
	 * 		error信息
	 * @param e Throwable
	 * 		异常
	 */
	public static void logError(Object message,Throwable e){
		logger.error(message==null?"":message, e);
	}
}
