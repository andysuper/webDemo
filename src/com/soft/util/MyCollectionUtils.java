package com.soft.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyCollectionUtils {
	private static final String childKey = "children";

	/**
	 * �õ�map��ָ��key��value��ת��Ϊ�ض�����<br>
	 * (�������ȷ�����ص�����,�� clazz ����Ϊ {@link Object} ����)
	 * 
	 * @param <T>
	 * @param map
	 * @param key
	 * @param clazz
	 *            value��Ӧ������
	 * @return
	 */
	public static <T> T getFromMap(Map map, Object key, Class<T> clazz) {
		if (map != null) {
			Object o = map.get(key);
			if (o == null) {
				return null;
			}
			if (clazz.isAssignableFrom(o.getClass())) {
				return (T) o;
			}
			return null;
		}
		return null;
	}

	public static <T> T getFromMap(Map map, Object key, Object defaultVal,
			Class<T> clazz) {
		T obj = getFromMap(map, key, clazz);
		if (obj == null) {
			if (defaultVal != null) {
				if (clazz.isAssignableFrom(defaultVal.getClass())) {
					return (T) defaultVal;
				}
			}
			return null;
		}
		return obj;
	}

	/**
	 * �жϼ����Ƿ�Ϊ��(=null��size<=0)
	 * 
	 * @param collection
	 * @return
	 */
	public static boolean isEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	/**
	 * �жϼ����Ƿ�Ϊ��(!=null��size<=0)
	 * 
	 * @param collection
	 * @return
	 */
	public static boolean notEmpty(Collection<?> collection) {
		return !isEmpty(collection);
	}

	/**
	 * listȥ��(˳�򲻱�)
	 * 
	 * @param list
	 */
	public static void removeDuplicateWithOrder(List list) {
		if (!isEmpty(list)) {
			Set set = new HashSet();
			List newList = new ArrayList();
			for (Iterator iter = list.iterator(); iter.hasNext();) {
				Object element = iter.next();
				if (set.add(element)) {
					newList.add(element);
				}
			}
			set.clear();
			set = null;
			list.clear();
			list.addAll(newList);
		}
	}

	public static Object getFromCollection(Collection<?> col, int index) {
		if (col != null && col.size() > index && index > -1) {
			return col.toArray()[index];
		}
		return null;
	}

	public static <T> T getFromCollection(Collection<?> col, int index,
			Class<T> clazz) {
		Object obj = getFromCollection(col, index);
		if (obj != null) {
			if (clazz.isAssignableFrom(obj.getClass())) {
				return (T) obj;
			}
		}
		return null;
	}

	/**
	 * ������js�����join����
	 * 
	 * @param col
	 * @param sepa
	 * @return
	 */
	public static String joinCollection(Collection<?> col, String sepa) {
		if (!isEmpty(col)) {
			Iterator<?> ite = col.iterator();
			if (ite != null) {
				StringBuffer result = new StringBuffer();
				int i = 0;
				while (ite.hasNext()) {
					Object next = ite.next();
					result.append((i == 0 ? "" : sepa) + next);
					i = 1;
				}
				return result.toString();
			}
		} else if (col != null && col.size() == 0) {
			return "";
		}
		return null;
	}

	/**
	 * ��listת��Ϊtree�ĸ�ʽ(tree��root��id: null��<=0)
	 * 
	 * @param nodes
	 *            List[Map{id,pId,...}]<strong>(id��pId����,����Ϊ����)</strong>
	 * @param idKey
	 *            id����,Ĭ��Ϊid
	 * @param pIdKey
	 *            parent id����,Ĭ��ΪpId
	 * @return <p>
	 *         List[Map{id,pId,children:[]...}]
	 *         </p>
	 *         like below:
	 *         <p>
	 * 
	 *         <pre>
	 *   [{
	 *       id = 5,
	 *       pId = null,
	 *       name = 5,
	 *       children = [{
	 *            id = 2,
	 *            pId = 5,
	 *            name = 2,
	 *            children = [{
	 *                 id = 23,
	 *                 pId = 2,
	 *                 name = 23
	 *               }
	 *            ]
	 *          },{
	 *            id = 34,
	 *            pId = 5,
	 *            name = 34
	 *          }
	 *       ]
	 *   },...
	 * ]
	 * </pre>
	 * 
	 *         </p>
	 */
	public static List<Map<String, Object>> list2Tree(
			List<Map<String, Object>> nodes, String idKey, String pIdKey) {
		List<Map<String, Object>> trees = new ArrayList<Map<String, Object>>();
		if (nodes != null) {
			if (idKey == null) {
				idKey = "id";
			}
			if (pIdKey == null) {
				pIdKey = "pId";
			}
			// ������е�1���ڵ�(��:���ڵ��¼ֵ�������ڵ�ǰnodes�����еĽڵ�)
			List<Object> ids = new ArrayList<Object>();
			Set<Object> pIds = new HashSet<Object>();
			for (Map<String, Object> n : nodes) {
				if (n != null) {
					ids.add(n.get(idKey));
					pIds.add(n.get(pIdKey));
				}
			}
			pIds.removeAll(ids);
			long time = new Date().getTime();
			String k = "isFirstNode_" + time;
			for (Map<String, Object> n : nodes) {
				if (n != null && pIds.contains(n.get(pIdKey))) {
					n.put(k, "1");
				}
			}
			ids.clear();
			ids = null;
			pIds.clear();
			pIds = null;
			//
			for (int i = 0; i < nodes.size(); i++) {
				if (i >= nodes.size() || i < 0) {
					continue;
				}
				Map<String, Object> node = nodes.get(i);
				if (node != null) {
					Object pId_obj = node.get(pIdKey);
					Integer pId = MyNumberUtils.toInt(pId_obj, 10);
					if (pId == null || pId <= 0 || "1".equals(node.get(k))) {
						node.remove(k);
						trees.add(node);
						nodes.remove(i);
						i -= 1;
						i -= subTrees(nodes, node, idKey, pIdKey);
					}
				}
			}
		}
		return trees;
	}

	private static int subTrees(List<Map<String, Object>> nodes,
			Map<String, Object> parent, String idKey, String pIdKey) {
		int num = 0;
		for (int i = 0; i < nodes.size(); i++) {
			if (i >= nodes.size() || i < 0) {
				continue;
			}
			Map<String, Object> node = nodes.get(i);
			if (node != null) {
				if (parent.get(idKey).equals(node.get(pIdKey))) {
					List<Object> children = (List<Object>) parent.get(childKey);
					if (children == null) {
						parent.put(childKey, children = new ArrayList<Object>());
					}
					children.add(node);
					nodes.remove(i);
					i -= 1;
					num += 1;
					int n_ = subTrees(nodes, node, idKey, pIdKey);
					i -= n_;
					num += n_;
				}
			}
		}
		return num;
	}

	/**
	 * Ŀ�ģ���̨����ztree����Ҫ��json��ʽ���ݡ�
	 * ��ʽ1��ÿ�ζ��ݹ��ҵ�ĳ���ڵ�Ԫ���µ������ӽڵ������㲻������ġ�
	 * ��ʽ2��ÿ�ζ�ֻ�Ҹ��ڵ�Ԫ�ص���һ���ӽڵ㡣
	 * @param nodes
	 * @param idKey
	 * @param pIdKey
	 * @return
	 */
	public static List<Map<String, Object>> makeTrees(
			List<Map<String, Object>> nodes, String idKey, String pIdKey) {
		List<Map<String, Object>> trees = new ArrayList<Map<String,Object>>();
		if (nodes != null) {
			if (idKey == null) {
				idKey = "id";
			}
			if (pIdKey == null) {
				pIdKey = "pId";
			}
			for (int i = 0; i < nodes.size(); i++) {
				Map<String, Object> map = nodes.get(i);
				recursive(nodes, map, idKey, pIdKey);
				trees.add(map);
			}
		}
		System.out.println("Trees:" + trees);
		return nodes;
	}

	private static int recursive(List<Map<String, Object>> nodes,
			Map<String, Object> map, String idKey, String pIdKey) {
		int index = 0;
		for (int j = 0; j < nodes.size(); j++) {
			Map<String, Object> _map = nodes.get(j);
			List<Object> _list = null;
			
			//�����ӽڵ�͵ݹ��ȡ
			if (map.get(idKey).equals(_map.get(pIdKey))) {
				_list = new ArrayList<Object>();
				_list.add(_map);
				map.put(childKey, _list);
				nodes.remove(j);
				index = recursive(nodes, _map, idKey, pIdKey);
			}
		}
		return index;
	}

	/***************************************************************** ��������addby lmf ****************************************************************************/
	/**
	 * ��������Դ �� ��Ҫ���� ��ϵ��ֶν������ݷ�װ
	 * 
	 * @param list
	 *            Դ����
	 * @param objType
	 *            �������� "com.edus.entity.EduOrg"
	 * @param id
	 *            "EDU_ORG_ID"
	 * @param name
	 *            "EDU_ORG_NAME"
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<Object, Object>> getGroupData(
			List<Map<String, Object>> list, String objType, String id,
			String name) {
		List<Map<Object, Object>> resultMap = new ArrayList<Map<Object, Object>>();
		try {
			// ��󷵻ص�list�д�ŵ�map
			Map<Object, Object> mapFinal = new HashMap<Object, Object>();
			// List<Integer> ��� �Ƿ����Ѿ�ʹ�ù��� ҵ��ģʽ����
			Map<Integer, Integer> tmpJudge = new HashMap<Integer, Integer>();
			Class<?> forName = Class.forName(objType);
			Object obj = forName.newInstance();
			int size = list.size();
			for (int i = 0; i < size; i++) {
				Map<String, Object> tmpMap = list.get(i);
				if (tmpMap != null) {
					Integer int_1 = MyNumberUtils.toInt(
							((BigDecimal) tmpMap.get(id)), 10);
					if (tmpJudge.containsKey(int_1)) {
						if (tmpJudge.size() > 0) {
							mapFinal = resultMap.get(tmpJudge.size() - 1);
							List<Map<String, Object>> list_oneDate = (List<Map<String, Object>>) mapFinal
									.get(obj);
							list_oneDate.add(tmpMap);
						}
					} else {
						tmpJudge.put(int_1, 0);
						mapFinal = new HashMap<Object, Object>();
						// ��� ҵ��ģʽ���ƺ��������� map��key��
						obj = forName.newInstance();
						String setId = MyStringUtils.getMethodName(
								id.toLowerCase(), "set");
						String setName = MyStringUtils.getMethodName(
								name.toLowerCase(), "set");
						Class[] tmpClassId = { Class
								.forName("java.lang.Integer") };
						Class[] tmpClassName = { Class
								.forName("java.lang.String") };
						MyReflectUtils.invokeNonStaticMethod(obj, setId,
								tmpClassId,
								MyNumberUtils.toInt(tmpMap.get(id), 10));
						MyReflectUtils.invokeNonStaticMethod(obj, setName,
								tmpClassName, (String) tmpMap.get(name));
						// eduOrg.setEduOrgId(MyNumberUtils.toInt(tmpMap.get("EDU_ORG_ID"),
						// 10));
						// eduOrg.setEduOrgName((String)tmpMap.get("EDU_ORG_NAME"));
						List<Map<String, Object>> list_oneDate = new ArrayList<Map<String, Object>>();
						list_oneDate.add(tmpMap);
						mapFinal.put(obj, list_oneDate);
						resultMap.add(mapFinal);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	/** =================================����================================== **/
	/**
	 * �� map �� ��ȡ BigDecimal ���� ת��Ϊ Integer ����
	 * 
	 * @param map
	 * @param key
	 * @param defaultInt
	 *            ����Ĭ�� ֵ
	 * @return
	 */
	public static Integer getFromMapBigDecimalToInteger(Map map, Object key,
			Integer defaultInt) {
		if (defaultInt != null) {
			return getFromMap(map, key, new BigDecimal(defaultInt),
					BigDecimal.class).intValue();
		} else {
			BigDecimal tmp = getFromMap(map, key, BigDecimal.class);
			if (tmp == null) {
				return null;
			} else {
				return tmp.intValue();
			}
		}
	}

	/**
	 * ƴ��list�� map��ĳ������
	 * 
	 * @param list
	 * @param key
	 * @param join
	 * @return
	 */
	public static String listMapJoinChar(List<Map<String, Object>> list,
			String key, Class clazz, String join) {
		StringBuffer sb = new StringBuffer("");
		for (Map<String, Object> object : list) {
			if (object != null) {
				if (clazz.isAssignableFrom(BigDecimal.class)) {
					sb.append(getFromMapBigDecimalToInteger(object, key, 0)
							+ join);
				} else {
					sb.append(object.get(key) + join);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * ������ѭ�� �Ƿ��ܱ��� ���Ȳ�ͬ ���еĳ��� �����ڵ��� ��һ������ĳ���
	 * 
	 * @param aa
	 * @return
	 */
	public static boolean judgeArrLengthEq(Object[]... aa) {
		int length = 0;
		for (Object[] objects : aa) {
			if (objects != null) {
				if (length > 0 && length > objects.length) {
					return false;
				} else {
					length = objects.length;
				}
			} else {
				return false;
			}
		}
		if (length > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * �ж����� map ȡ�� ���в�һ���� ��Ϣ
	 * 
	 * @param prevMap
	 * @param nowMap
	 * @return
	 */
	public static Integer judgeTwoMap(Map<String, Object> prevMap,
			Map<String, Object> nowMap) {
		Set<String> set = nowMap.keySet();
		for (String key : set) {
			Object prevValue = prevMap.get(key);
			Object nowValue = nowMap.get(key);
			if (prevValue == null && MyNumberUtils.can2Int(nowValue)) {
				return MyNumberUtils.toInt(nowValue, 10, 0);
			}
		}
		return 0;
	}
}
