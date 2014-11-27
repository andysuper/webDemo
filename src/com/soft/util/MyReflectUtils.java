package com.soft.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.springframework.util.ReflectionUtils;

public class MyReflectUtils {
	/**
	 * 根据字段名称获得获得类的对应字段
	 * 
	 * @param clazz
	 * @param fieldName
	 * @return
	 */
	public static Field getFieldByName(Class<?> clazz, String fieldName) {
		if (clazz != null && fieldName != null) {
			return ReflectionUtils.findField(clazz, fieldName);
		}
		return null;
	}

	/**
	 * 获得类非静态字段的值
	 * 
	 * @param obj
	 * @param fieldName
	 * @return
	 * @throws Exception
	 */
	public static Object getNonStaticFieldValue(Object obj, String fieldName)
			throws Exception {
		Field field = getFieldByName(obj == null ? null : obj.getClass(),
				fieldName);
		Object value = null;
		if (field != null) {
			if (field.isAccessible()) {
				value = field.get(obj);
			} else {
				field.setAccessible(true);
				value = field.get(obj);
				field.setAccessible(false);
			}
		}
		return value;
	}

	/**
	 * 设置类非静态字段的值
	 * 
	 * @param obj
	 * @param fieldName
	 * @param value
	 * @throws Exception
	 */
	public static void setNonStaticFieldValue(Object obj, String fieldName,
			Object value) throws Exception {
		Field field = getFieldByName(obj == null ? null : obj.getClass(),
				fieldName);
		if (field != null && !Modifier.isFinal(field.getModifiers())) {
			if (field.isAccessible()) {
				field.set(obj, value);
			} else {
				field.setAccessible(true);
				field.set(obj, value);
				field.setAccessible(false);
			}
		}
	}

	/**
	 * 获得类静态字段的值
	 * 
	 * @param clazz
	 * @param fieldName
	 * @return
	 * @throws Exception
	 */
	public static Object getStaticFieldValue(Class<?> clazz, String fieldName)
			throws Exception {
		Field field = getFieldByName(clazz, fieldName);
		Object value = null;
		if (field != null) {
			if (field.isAccessible()) {
				value = field.get(clazz);
			} else {
				field.setAccessible(true);
				value = field.get(clazz);
				field.setAccessible(false);
			}
		}
		return value;
	}

	/**
	 * 设置类静态字段的值
	 * 
	 * @param clazz
	 * @param fieldName
	 * @param value
	 * @throws Exception
	 */
	public static void setStaticFieldValue(Class<?> clazz, String fieldName,
			Object value) throws Exception {
		Field field = getFieldByName(clazz, fieldName);
		if (field != null && !Modifier.isFinal(field.getModifiers())) {
			if (field.isAccessible()) {
				field.set(clazz, value);
			} else {
				field.setAccessible(true);
				field.set(clazz, value);
				field.setAccessible(false);
			}
		}
	}

	/**
	 * 获得类的方法
	 * 
	 * @param clazz
	 * @param methodName
	 *            方法名称
	 * @param paramTypes
	 *            方法参数类型数组
	 * @return
	 */
	public static Method findMethodByName(Class<?> clazz, String methodName,
			Class<?>... paramTypes) {
		if (clazz != null && methodName != null) {
			return ReflectionUtils.findMethod(clazz, methodName, paramTypes);
		}
		return null;
	}

	/**
	 * invoke类的非静态方法
	 * 
	 * @param obj
	 *            对象实体
	 * @param methodName
	 *            方法名称
	 * @param paramTypes
	 *            方法参数类型数组
	 * @param args
	 *            方法参数值数组
	 */
	public static Object invokeNonStaticMethod(Object obj, String methodName,
			Class<?>[] paramTypes, Object... args) {
		if (obj != null) {
			Method method = findMethodByName(obj.getClass(), methodName,
					paramTypes);
			if (method != null) {
				if (!method.isAccessible()) {
					method.setAccessible(true);
					Object im = invokeMethodInner(method, obj, args);
					method.setAccessible(false);
					return im;
				}
				return invokeMethodInner(method, obj, args);
			}
		}
		return null;
	}

	/**
	 * invoke类的非静态方法
	 * 
	 * @param target
	 *            类的实例对象
	 * @param method
	 * @param args
	 * @return
	 */
	public static Object invokeNonStaticMethod(Object target, Method method,
			Object... args) {
		if (target != null && method != null) {
			if (!method.isAccessible()) {
				method.setAccessible(true);
				Object im = invokeMethodInner(method, target, args);
				method.setAccessible(false);
				return im;
			}
			return invokeMethodInner(method, target, args);
		}
		return null;
	}

	/**
	 * invoke类的静态方法
	 * 
	 * @param clazz
	 * @param methodName
	 *            方法名称
	 * @param paramTypes
	 *            方法参数类型数组
	 * @param args
	 *            方法参数值数组
	 * @return
	 */
	public static Object invokeStaticMethod(Class<?> clazz, String methodName,
			Class<?>[] paramTypes, Object... args) {
		if (clazz != null) {
			Method method = findMethodByName(clazz, methodName, paramTypes);
			if (method != null) {
				if (!method.isAccessible()) {
					method.setAccessible(true);
					Object im = invokeMethodInner(method, clazz, args);
					method.setAccessible(false);
					return im;
				}
				return invokeMethodInner(method, clazz, args);
			}
		}
		return null;
	}

	/**
	 * invoke类的静态方法
	 * 
	 * @param clazz
	 * @param method
	 * @param args
	 * @return
	 */
	public static Object invokeStaticMethod(Class<?> clazz, Method method,
			Object... args) {
		if (clazz != null && method != null) {
			if (!method.isAccessible()) {
				method.setAccessible(true);
				Object im = invokeMethodInner(method, clazz, args);
				method.setAccessible(false);
				return im;
			}
			return invokeMethodInner(method, clazz, args);
		}
		return null;
	}

	private static Object invokeMethodInner(Method method, Object target,
			Object... args) {
		if (method != null && target != null) {
			try {
				return ReflectionUtils.invokeMethod(method, target, args);
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}
}
