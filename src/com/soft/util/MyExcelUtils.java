package com.soft.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormat;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import com.soft.entity.DataDictionary;
public class MyExcelUtils {
	/**
	 * 每个sheet的最大行数-1(建议&lt;=60000),如果=10,则实际生成的sheet中的最大行数=11<br>
	 * 1.WritableSheetImpl->numRowsPerSheet=65536<br>
	 * 2.maxRowsPerSheet=60000->一般情况下就不用计算header和title占用的行了
	 * */
	public static final int maxRowsPerSheet = 60000;
	/**
	 * excel文本内容的字体
	 */
	public static WritableFont contentFont = null;
	/**
	 * excel标题的字体
	 */
	public static WritableFont headerFont = null;
	/**
	 * excel文本内容的格式
	 */
	public static WritableCellFormat contentFormat = null;
	/**
	 * excel标题的格式
	 */
	public static WritableCellFormat headerFormat = null;
	/**
	 * excel日期的格式
	 */
	public static WritableCellFormat dateFormat = null;
	/**
	 * excel数字的格式
	 */
	public static WritableCellFormat numberFormat = null;

	/**
	 * 初始化excel的格式(每次写excel时都需要重新初始化格式,否则格式不起作用)
	 */
	private static void initFormat() {
		contentFont = new WritableFont(WritableFont.ARIAL, 10,
				WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
				Colour.BLACK);
		headerFont = new WritableFont(WritableFont.ARIAL, 10,
				WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,
				Colour.BLACK);
		contentFormat = new WritableCellFormat(contentFont);
		headerFormat = new WritableCellFormat(headerFont);
		dateFormat = new WritableCellFormat(new DateFormat(
				"yyyy-MM-dd HH:mm:ss"));
		numberFormat = new WritableCellFormat(new NumberFormat("###,##0.00"));
		try {
			contentFormat.setAlignment(Alignment.CENTRE);
			contentFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			dateFormat.setAlignment(Alignment.CENTRE);
			dateFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			numberFormat.setAlignment(Alignment.CENTRE);
			numberFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			headerFormat.setAlignment(Alignment.CENTRE);
			headerFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			headerFormat.setBackground(jxl.format.Colour.GRAY_25);// 表格底色
			headerFormat.setBorder(Border.ALL, BorderLineStyle.THIN,
					Colour.BLACK);// 黑色边框
		} catch (WriteException e) {
			MyLogUtils.logError("excel设置错误", e);
		}
	}

	/**
	 * 根据指定(excel)文件获得Workbook(如果文件不存在返回null)
	 * 
	 * @param excel
	 *            excel文件(File/String)
	 * @return
	 */
	public static Workbook getLocalExcel(Object excel) {
		if (excel != null) {
			try {
				File obj2File = null;
				if (excel instanceof File) {
					obj2File = (File) excel;
				} else {
					obj2File = MyFileUtils.getFile(excel);
				}
				if (obj2File != null && obj2File.isFile()) {
					return Workbook.getWorkbook(obj2File);
				}
			} catch (Exception e) {
				// MyLogUtils.logError("根据指定("+excel+")文件获得Workbook: failed!",e);
				return null;
			}
		}
		return null;
	}

	/**
	 * 创建workbook
	 * 
	 * @param excel
	 *            excel文件(File/String)或输出流(OutputStream)
	 * @return
	 */
	public static WritableWorkbook createWorkbook(Object excel) {
		WritableWorkbook workbook = null;
		try {
			if (excel != null) {
				if (excel instanceof OutputStream) {
					workbook = Workbook.createWorkbook((OutputStream) excel);
				} else {
					Workbook old = getLocalExcel(excel);
					File obj2File = MyFileUtils.createFile(excel);
					if (obj2File != null) {
						if (old != null) {
							workbook = Workbook.createWorkbook(obj2File, old);
						} else {
							workbook = Workbook.createWorkbook(obj2File);
						}
					}
				}
			}
			if (workbook != null) {
				initFormat();
			}
		} catch (Exception e) {
			MyLogUtils.logError("创建excel失败", e);
		}
		return workbook;
	}

	/**
	 * 创建一个工作簿
	 * 
	 * @param workbook
	 *            excel对象
	 * @param sheetName
	 *            工作簿名称
	 * @param sheetIndex
	 *            工作簿index(index基于0,类似于数组)
	 * @return
	 */
	public static WritableSheet createSheet(WritableWorkbook workbook,
			String sheetName, int sheetIndex) {
		if (workbook != null) {
			int total = workbook.getNumberOfSheets();
			total = total < 0 ? 0 : total;
			if (sheetName == null || sheetName.length() <= 0) {
				sheetName = "sheet_" + total;
			}
			if (sheetIndex < 0) {
				sheetIndex = total;
			} else if (sheetIndex > total) {
				sheetIndex = total;
			}
			if (total > 0) {
				// 防止工作簿名称重复
				String[] sheetNames = workbook.getSheetNames();
				if (sheetNames != null) {
					for (int i = 0; i < sheetNames.length; i++) {
						String name = sheetNames[i];
						if (sheetName.equals(name)) {
							String[] arr_ = MyArrayUtils.splitStrBy(sheetName,
									"_");
							if (arr_ != null && arr_.length > 1) {
								Integer int_ = MyNumberUtils.toInt(
										arr_[arr_.length - 1], 10);
								if (int_ != null && int_ == total - 1) {
									sheetName = sheetName.substring(0,
											sheetName.lastIndexOf('_') + 1)
											+ total;
									break;
								}
							}
							sheetName += "_" + total;
							break;
						}
					}
				}
			}
			WritableSheet sheet = workbook.createSheet(sheetName, sheetIndex);
			return sheet;
		}
		return null;
	}

	/**
	 * 往excel工作簿insert一行
	 * 
	 * @param sheet
	 * @param insertTo
	 *            以0开始的行号
	 * @param args
	 *            不定参数数组: 第1个("1"/"0")=是否开启行数限制功能(类似于矩阵格式的excel不建议开启此功能),<br>
	 *            <strong>如果开启的话务必将此方法返回的不为null的sheet重新赋值给调用此方法时传递的sheet参数,</
	 *            strong><br>
	 *            其他参数待定
	 * @return 如果返回的新sheet不为null,将调用此方法的实参重新赋值: sheet=newSheet,row=0
	 */
	public static WritableSheet insertRow(WritableSheet sheet, int insertTo,
			Object... args) {
		if (sheet != null) {
			if ("1".equals(MyArrayUtils.getEleInArray(args, 0))
					&& insertTo > maxRowsPerSheet) {
				WritableWorkbook ww = (WritableWorkbook) MyReflectUtils
						.invokeNonStaticMethod(sheet, "getWorkbook",
								new Class<?>[] {});
				WritableSheet newSheet = createSheet(ww, sheet.getName(), -1);
				insertRow(newSheet, 0);
				return newSheet;
			}
			sheet.insertRow(insertTo);
		}
		return null;
	}

	/**
	 * 往excel工作簿insert一列
	 * 
	 * @param sheet
	 * @param insertTo
	 *            以0开始的列号
	 */
	public static void insertColumn(WritableSheet sheet, int insertTo) {
		if (sheet != null) {
			sheet.insertColumn(insertTo);
		}
	}

	/**
	 * 合并单元格
	 * 
	 * @param sheet
	 * @param beginCol
	 *            左上角单元格-(列)
	 * @param beginRow
	 *            左上角单元格-(行)
	 * @param endCol
	 *            右下角单元格-(列)
	 * @param endRow
	 *            右下角单元格-(行)
	 */
	public static void mergeCells(WritableSheet sheet, int beginCol,
			int beginRow, int endCol, int endRow) {
		if (sheet != null) {
			if ((endCol < beginCol) || (endRow < beginRow)) {
				return;
			}
			try {
				sheet.mergeCells(beginCol, beginRow, endCol, endRow);
			} catch (Exception e) {
				MyLogUtils
						.logError("合并单元格失败,左上角单元格:row=" + beginRow + ",col="
								+ beginCol + ",右下角单元格:row=" + endRow + ",col="
								+ endCol, e);
			}
		}
	}

	/**
	 * 创建单元格
	 * 
	 * @param row
	 *            行号(>=0)
	 * @param column
	 *            列号(>=0)
	 * @param val
	 *            单元格内容
	 * @param format
	 *            单元格格式(当且仅当单元格内容是字符串时这个格式才有效, 因为单元格内容为数字或日期的格式已经在此方法内设置为默认的了)
	 * @return
	 */
	public static WritableCell createCell(int row, int column, Object val,
			CellFormat format) {
		WritableCell cell = null;
		if (val == null) {
			val = "";
		}
		// String superClassName=val.getClass().getSuperclass().getName();
		// if("java.lang.Number".equalsIgnoreCase(superClassName)){
		if (val instanceof java.lang.Number) {
			boolean isFloat = val instanceof Float;
			boolean isDouble = val instanceof Double;
			boolean isBigDecimal = val instanceof BigDecimal;
			boolean isXiaoshu = isFloat || isDouble || isBigDecimal;
			double doubleValue = ((java.lang.Number) val).doubleValue();
			if (isXiaoshu && val.toString().indexOf('.') != -1) {
				cell = new Number(column, row, doubleValue, numberFormat);
			} else {
				cell = new Number(column, row, doubleValue, contentFormat);
			}
		} else if (val instanceof Date) {
			cell = new DateTime(column, row, (Date) val, dateFormat);
		} else {
			if (format != null) {
				cell = new Label(column, row, val.toString(), format);
			} else {
				cell = new Label(column, row, val.toString(), contentFormat);
			}
		}
		return cell;
	}

	/**
	 * 添加单元格
	 * 
	 * @param sheet
	 * @param row
	 *            行号(>=0)
	 * @param column
	 *            列号(>=0)
	 * @param val
	 *            单元格内容
	 * @param format
	 *            单元格格式(当且仅当单元格内容是字符串时这个格式才有效, 因为单元格内容为数字或日期的格式已经在此方法内设置为默认的了)
	 * @param args
	 *            不定参数数组: 第1个("1"/"0")=是否开启行数限制功能(类似于矩阵格式的excel不建议开启此功能),<br>
	 *            <strong>如果开启的话务必将此方法返回的不为null的sheet重新赋值给调用此方法时传递的sheet参数,</
	 *            strong><br>
	 *            其他参数待定
	 * @return 如果返回的newSheet不为null,将调用此方法的实参重新赋值: sheet=newSheet, row=0
	 */
	public static WritableSheet addCell2Sheet(WritableSheet sheet, int row,
			int column, Object val, CellFormat format, Object... args) {
		if (sheet != null) {
			if ("1".equals(MyArrayUtils.getEleInArray(args, 0))
					&& row > maxRowsPerSheet) {
				WritableWorkbook ww = (WritableWorkbook) MyReflectUtils
						.invokeNonStaticMethod(sheet, "getWorkbook",
								new Class<?>[] {});
				WritableSheet newSheet = createSheet(ww, sheet.getName(), -1);
				addCell2Sheet(newSheet, 0, column, val, format);
				return newSheet;
			}
			WritableCell cell = createCell(row, column, val, format);
			if (cell != null) {
				try {
					sheet.addCell(cell);
				} catch (Exception e) {
					MyLogUtils.logError(
							"添加单元格失败,row=" + row + ",col=" + column, e);
				}
			}
		}
		return null;
	}

	/**
	 * 设置excel中某个工作簿中某一行的高度
	 * 
	 * @param sheet
	 *            工作簿
	 * @param row
	 *            行号(>=0)
	 * @param rowHeight
	 *            高度(>0,如果<=0则默认为100)
	 */
	public static void setRowHeight(WritableSheet sheet, int row, int rowHeight) {
		if (sheet != null && row >= 0) {
			if (rowHeight <= 0) {
				rowHeight = 100;
			}
			try {
				sheet.setRowView(row, rowHeight);
			} catch (Exception e) {
				MyLogUtils.logError(
						"设置excel中某个工作簿中某一行的高度失败,sheet=" + sheet.getName()
								+ ",row=" + row, e);
			}
		}
	}

	/**
	 * 设置表格列宽
	 * 
	 * @param sheet
	 * @param column
	 *            列号(>=0)
	 * @param columnWidth
	 *            列宽度
	 */
	public static void setColumnWidth(WritableSheet sheet, int column,
			int columnWidth) {
		if (sheet != null && column >= 0 && columnWidth > 0) {
			sheet.setColumnView(column, columnWidth);
		}
	}

	/**
	 * 往response中输出excel
	 * 
	 * @param request
	 * @param response
	 * @param filename
	 *            下载的文件名(带有后缀名,比如:"部门.xls")
	 * @param sheetName
	 *            工作簿名称
	 * @param title
	 *            工作簿中一行多列的title(可以为null)
	 * @param headerNames
	 *            工作簿中标题名称(可以为空)
	 * @param datas
	 *            工作簿数据集合(List[Map/Collection/Array/Object])
	 * @param getterNames
	 *            如果datas中的元素不是collection&array,而是普通对象或map,则此参数为对象的属性或map的键的集合<br>
	 *            <strong>(<br>
	 *            1.如果 getterName 是数据字典相关列,比如:学员性别列(stuSex)中"男"对应数据字典id为115,<br>
	 *            那么强烈建议您将"stuSex"重构为"dic:stuSex".<br>
	 *            2.如果 getterName 是其他对象,比如用户实体中关联了dept对象(部门名称),那么强烈建议您将"dept"重构为
	 *            "po:dept.deptName")</strong><br>
	 *            示例:[id,name,dic:stuSex,po:dept.deptName,closeOpen:
	 *            classroomState] closeOpen：0禁用 1启用
	 *            示例:[id,name,dic:stuSex,po:dept.deptName,specialDate:startTime]
	 *            specialDate:(HH:mm:ss)将日期截取时间
	 * @throws Exception
	 *             调用此方法后,当且仅当此方法抛出异常时response才可以继续使用
	 *             (所以,如果想在调用此方法后继续操作response,则需要将此方法单独用
	 *             try...catch块捕获异常,在catch块中才可以继续操作response)
	 */
	public static void writeExcel2Response(HttpServletRequest request,
			HttpServletResponse response, String filename, String sheetName,
			String title, List<String> headerNames, List<?> datas,
			List<String> getterNames) throws Exception {
		if (response != null && !response.isCommitted()) {
			if (MyStringUtils.isBlank(filename)) {
				filename = new Date().getTime() + "";
			}
			try {
				filename = MyProjectUtlis.createDownloadFilename(filename,
						request, response);
			} catch (UnsupportedEncodingException e) {
				MyLogUtils
						.logError("系统不支持字符编码：" /*+ EdusAction.serverEncoding*/, e);
			}
			response.setContentType("application/vnd.ms-excel;charset="
					/*+ EdusAction.serverEncoding*/);
			/*
			 * response.setHeader("Content-Disposition","attachment; filename="
			 * +filename+".xls");
			 */
			// response.setHeader("Content-disposition",
			// String.format("attachment; filename=\"%s.xls\"",
			// filename));//文件名外的双引号处理firefox的空格截断问题
			writeExcelOneSheet(null, sheetName, headerNames, datas,
					getterNames, title, response);
		}
	}

	private static void writeExcelOneSheet(Object excel, String sheetName,
			List<String> headerNames, List<?> datas, List<String> getterNames,
			String title, HttpServletResponse response) throws Exception {
		if (excel != null || response != null) {
			if (datas == null) {
				datas = new ArrayList<String>();
			}
			// 去掉判断为空,否则当为空时不能正确写excel
			// if (!MyCollectionUtils.isEmpty(datas)) {
			List<List<Collection<Object>>> datas_0 = new ArrayList<List<Collection<Object>>>();
			List<Collection<Object>> datas_01 = new ArrayList<Collection<Object>>();
			datas_0.add(datas_01);
			String[] keyOrFields = null;
			if (getterNames != null) {
				keyOrFields = new String[getterNames.size()];
				getterNames.toArray(keyOrFields);
			}
			String dicPrefix = "dic:";
			String poPrefix = "po:";
			String closeAndOpenPrefix = "closeOpen:";
			String datePrefix = "date:";// date:dateKey:format
			String specialDatePrefix = "specialDate:";// date:dateKey:format
			String yesAndNofix = "yes:";
			String personMessage = "mesType:"; // 个人消息中心中 （提醒类型 1 公告 0系统提醒）
			String week = "xq:"; // 星期 1-7 转为 周一-周日
			int datePre_len = datePrefix.length();
			int ix_0 = -1;
			String dateKey = null;
			String fmt = null;
			String[] dids = null;
			Object dicids = null;
			String dicName = "";
			for (Object data : datas) {
				if (data != null) {
					if (Map.class.isAssignableFrom(data.getClass())) {
						// map
						if (keyOrFields != null) {
							Map<Object, Object> map_0 = (Map<Object, Object>) data;
							if (map_0 != null) {
								Collection<Object> coll_map = new ArrayList<Object>();
								for (String key : keyOrFields) {
									dicName = "";
									if (key != null) {
										if (key.startsWith(dicPrefix)) {
											dicids = map_0.get(key
													.substring(dicPrefix
															.length()));
											if (dicids == null) {
												coll_map.add("");
											} else {
												// 数据字典的id
												dids = dicids.toString().split(
														",");
												for (String did : dids) {
													DataDictionary dic = MyProjectUtlis
															.getDicById(did);
													if (dic != null
															&& MyStringUtils
																	.notBlank(dic
																			.getDicName())
															&& !did.equals("-1")
															&& !did.equals("1")) {
														if (MyStringUtils
																.notBlank(dicName)) {
															dicName += ",";
														}
														dicName += dic
																.getDicName();
													}

												}
												coll_map.add(dicName);
											}
										} else if (key
												.startsWith(closeAndOpenPrefix)) {
											dicids = map_0
													.get(key.substring(closeAndOpenPrefix
															.length()));
											if (dicids == null) {
												coll_map.add("禁用");
											} else {
												// 数据字典的id
												dids = dicids.toString().split(
														",");
												for (int i = 0; i < dids.length; i++) {
													dicName += (dids[i] != null && dids[i]
															.equals("1")) ? "启用"
															: "禁用";
													if (i < dids.length - 1) {
														dicName += ",";
													}
												}
												coll_map.add(dicName);
											}
										} else if (key.startsWith(yesAndNofix)) {
											dicids = map_0.get(key
													.substring(yesAndNofix
															.length()));
											if (dicids == null) {
												coll_map.add("否");
											} else {
												// 数据字典的id
												dids = dicids.toString().split(
														",");
												for (int i = 0; i < dids.length; i++) {
													dicName += (dids[i] != null && dids[i]
															.equals("1")) ? "是"
															: "否";
													if (i < dids.length - 1) {
														dicName += ",";
													}
												}
												coll_map.add(dicName);
											}
										} else if (key
												.startsWith(personMessage)) {
											Object object = map_0.get(key
													.substring(personMessage
															.length()));
											if (object != null) {
												coll_map.add("0".equals(object
														.toString()) ? "系统提醒"
														: "公告");
											} else {
												coll_map.add("");
											}
										} else if (key.startsWith(week)) {
											dicids = map_0.get(key
													.substring(week.length()));
											if (dicids == null) {
												coll_map.add("");
											} else {
												// 数据字典的id
												dids = dicids.toString().split(
														",");
												for (int i = 0; i < dids.length; i++) {
													if (MyStringUtils
															.notBlank(dids[i])) {
														switch (Integer
																.parseInt(dids[i])) {
														case 1:
															dicName = "星期一";
															break;
														case 2:
															dicName = "星期二";
															break;
														case 3:
															dicName = "星期三";
															break;
														case 4:
															dicName = "星期四";
															break;
														case 5:
															dicName = "星期五";
															break;
														case 6:
															dicName = "星期六";
															break;
														case 7:
															dicName = "星期日";
															break;
														default:
															dicName = "";
															break;
														}
														if (i < dids.length - 1) {
															dicName += ",";
														}
													}
												}
												coll_map.add(dicName);
											}
										} else if (key
												.startsWith(specialDatePrefix)) {
											String tmp_key = key
													.substring(specialDatePrefix
															.length());
											coll_map.add(MyDateUtils.date2String(
													MyDateUtils
															.obj2Date(
																	map_0.get(tmp_key),
																	"yyyy-MM-dd HH:mm:ss"),
													"HH:mm"));
										} else if (key.startsWith(datePrefix)) {
											ix_0 = key
													.indexOf(":", datePre_len);
											dateKey = ix_0 == -1 ? key
													.substring(datePre_len)
													: key.substring(
															datePre_len, ix_0);
											fmt = ix_0 == -1 ? null : key
													.substring(ix_0 + 1);
											fmt = fmt == null ? "yyyy-MM-dd HH:mm:ss"
													: fmt;
											// 日期
											if (dateKey != null) {
												if (fmt != null) {
													coll_map.add(MyDateUtils.date2String(
															MyDateUtils.obj2Date(
																	map_0.get(dateKey),
																	fmt), fmt));
												} else {
													coll_map.add(map_0.get(key));
												}
											} else {
												coll_map.add(map_0.get(key));
											}
										} else {
											coll_map.add(map_0.get(key));
										}
									} else {
										coll_map.add(map_0.get(key));
									}
								}
								datas_01.add(coll_map);
							}
						}
					} else if (Collection.class.isAssignableFrom(data
							.getClass())) {
						// collection
						Collection coll_0 = (Collection) data;
						datas_01.add(coll_0);
					} else if (data.getClass().isArray()) {
						// array
						Object[] arr_0 = (Object[]) data;
						if (arr_0 != null) {
							datas_01.add(Arrays.asList(arr_0));
						}
					} else {
						// 普通对象,用反射得到对应值
						if (keyOrFields != null) {
							Collection<Object> coll_obj = new ArrayList<Object>();
							for (String field : keyOrFields) {
								if (field != null) {
									if (field.startsWith(dicPrefix)) {
										// 数据字典的id
										DataDictionary dic = MyProjectUtlis
												.getDicById(MyReflectUtils
														.getNonStaticFieldValue(
																data,
																field.substring(dicPrefix
																		.length())));
										coll_obj.add(dic == null ? "" : dic
												.getDicName());
									} else if (field.startsWith(poPrefix)
											&& field.indexOf('.') > poPrefix
													.length()) {
										// 实体中关联的其他实体
										coll_obj.add(MyReflectUtils.getNonStaticFieldValue(
												MyReflectUtils
														.getNonStaticFieldValue(
																data,
																field.substring(
																		poPrefix.length(),
																		field.indexOf('.'))),
												field.split("\\.")[1]));
									} else {
										coll_obj.add(MyReflectUtils
												.getNonStaticFieldValue(data,
														field));
									}
								} else {
									coll_obj.add(MyReflectUtils
											.getNonStaticFieldValue(data, field));
								}
							}
							datas_01.add(coll_obj);
						}
					}
				}
			}
			//
			List<String> sheetNames = new ArrayList<String>();
			sheetNames.add(sheetName);
			//
			List<List<String>> headerNames_0 = new ArrayList<List<String>>();
			headerNames_0.add(headerNames);
			//
			List<String> titles = new ArrayList<String>();
			titles.add(title);
			writeExcel(excel, sheetNames, headerNames_0, datas_0, titles,
					response);
			// }
		}
	}

	/**
	 * 写excel
	 * 
	 * @param excel
	 *            {@code File}或者{@code OutputStream}
	 *            对象,如果是往response中输出excel此参数为null即可
	 * @param sheetNames
	 *            List[String] 工作簿名称集合(不能为空)
	 * @param headerNames
	 *            List[List[String]] 工作簿中标题名称(一般是每个工作簿中首行的内容)集合(可以为空)
	 * @param datas
	 *            List[List[List[Object]]] 工作簿数据集合
	 * @param titles
	 *            List[String] 工作簿中一行多列的title(类似于表格中的title)集合(可以为空)
	 * @param response
	 *            如果excel目标是文件或输出流,则此参数为null即可
	 * @throws Exception
	 *             1.当此方法输出的excel目标对象是文件或输出流时,如果发生异常就会抛出此异常
	 *             2.当此方法输出的excel目标对象是response时
	 *             ,如果response已经响应过(eg:getOutputStream
	 *             ()),则不会抛出发生的异常,反之抛出发生的异常且response会reset.
	 */
	public static void writeExcel(Object excel, List<String> sheetNames,
			List<List<String>> headerNames,
			List<List<Collection<Object>>> datas, List<String> titles,
			HttpServletResponse response) throws Exception {
		boolean isClosed = false;
		boolean responseCanCall = true;
		boolean isResponse = response != null;
		ByteArrayOutputStream baos = null;
		WritableWorkbook workbook = null;
		try {
			if (sheetNames != null && !sheetNames.isEmpty()) {
				if (isResponse && !response.isCommitted()) {
					baos = new ByteArrayOutputStream();
					workbook = createWorkbook(baos);
				} else if (excel != null) {
					workbook = createWorkbook(excel);
				}
				if (workbook != null) {
					Iterator<String> ite_sheet = sheetNames.iterator();
					if (ite_sheet != null) {
						int headerNameSize = headerNames == null ? 0
								: headerNames.size();
						int dataSize = datas == null ? 0 : datas.size();
						int titleSize = titles == null ? 0 : titles.size();
						int idx = 0;
						while (ite_sheet.hasNext()) {
							String sheetName = ite_sheet.next();
							WritableSheet sheet = createSheet(workbook,
									sheetName, idx);
							if (sheet != null) {
								List<String> headerName = null;
								if (headerNames != null && headerNameSize > idx) {
									headerName = headerNames.get(idx);
								}
								int column_num = headerName == null ? 0
										: headerName.size();
								int startRow = 0;
								if (titles != null && titleSize > idx) {
									String t = titles.get(idx);
									if (t != null) {
										fillSheetTitle(sheet, t,
												column_num - 1, startRow);
										startRow += 1;
									}
								}
								if (headerName != null) {
									fiilSheetHeader(sheet, headerName, startRow);
									startRow += 1;
								}
								int columnSize = -1;
								if (datas != null && dataSize > idx) {
									List<Collection<Object>> data = datas
											.get(idx);
									if (data != null) {
										WritableSheet newSheet = fillSheetData(
												sheet, data, startRow);
										if (newSheet != null) {
											sheet = newSheet;
											startRow = 0;
										}
									}
								}
								// setRowHeight(sheet, 0, 20);
								setColumnWidth(sheet, columnSize, 17);
							}
							idx += 1;
						}
					}
					workbook.write();
					workbook.close();
					isClosed = true;
					if (isResponse && baos != null && !response.isCommitted()) {
						byte[] ba = baos.toByteArray();
						if (ba != null) {
							response.setContentLength(ba.length);
							// response.setHeader("Content-Length", "" +
							// ba.length);
							ServletOutputStream os = response.getOutputStream();
							try {
								os.write(ba);
							} catch (Exception e) {
								// 一般情况下,用户取消下载时报异常: ClientAbortException
								MyLogUtils.logError("下载excel异常,sheetNames="
										+ MyCollectionUtils.joinCollection(
												sheetNames, ",") + " :"
										+ e.getClass().getName());
							} finally {
								try {
									if (os != null) {
										os.close();
									}
								} catch (Exception e) {
								}
							}
							responseCanCall = false;
						}
					}
				}
			}
		} catch (Exception e) {
			if (responseCanCall) {
				/*
				 * excel输出对象是:1.文件或输出流,2.response且response还没有响应过时, 抛出异常
				 */
				if (isResponse && !response.isCommitted()) {
					response.reset();
				}
				throw e;
			} else {
				/*
				 * excel输出对象是response且response已经响应过时, 日志记录异常而不是抛出异常
				 */
				MyLogUtils.logError("写excel文件出错", e);
			}
		} finally {
			try {
				if (workbook != null) {
					if (isClosed) {
						/*
						 * System.out.println("The excel has been closed earlier!"
						 * );
						 */
					} else {
						/* System.out.println("The excel is closed finally!"); */
						workbook.close();
					}
				}
			} catch (Exception e) {
				if (responseCanCall) {
					if (isResponse && !response.isCommitted()) {
						response.reset();
					}
					throw e;
				} else {
					MyLogUtils.logError("写excel文件-关闭excel时出错", e);
				}
			}
		}
	}

	/**
	 * 填充工作簿一行多列的标题
	 * 
	 * @param sheet
	 *            工作簿
	 * @param title
	 *            标题
	 * @param column_num
	 *            (要合并的)列数
	 * @param row
	 *            (标题所在的)行号
	 */
	private static void fillSheetTitle(WritableSheet sheet, String title,
			int column_num, int row) {
		if (sheet != null && column_num > 0 && title != null) {
			if (row < 0) {
				row = 0;
			}
			addCell2Sheet(sheet, row, 0, title, headerFormat);
			setRowHeight(sheet, row, 400);
			mergeCells(sheet, 0, row, column_num, row);
		}
	}

	/**
	 * 填充工作簿中对应每行数据的标题
	 * 
	 * @param sheet
	 *            工作簿
	 * @param headerName
	 *            标题集合
	 * @param row
	 *            行号
	 */
	private static void fiilSheetHeader(WritableSheet sheet,
			List<String> headerName, int row) {
		if (sheet != null && headerName != null && !headerName.isEmpty()) {
			Iterator<String> ite = headerName.iterator();
			if (ite != null) {
				if (row < 0) {
					row = 0;
				}
				int column = 0;
				while (ite.hasNext()) {
					String next = ite.next();
					addCell2Sheet(sheet, row, column, next, headerFormat);
					column += 1;
				}
			}
		}
	}

	/**
	 * 填充工作簿数据
	 * 
	 * @param sheet
	 *            工作簿
	 * @param data
	 *            数据
	 * @param startRow
	 *            (填充数据的)起始行
	 * @return 如果返回的newSheet不为null,将调用此方法的实参重新赋值: sheet=newSheet, row=0
	 */
	private static WritableSheet fillSheetData(WritableSheet sheet,
			List<Collection<Object>> data, int startRow) {
		WritableSheet newSheet = null;
		if (startRow < 0) {
			startRow = 0;
		}
		if (sheet != null && data != null) {
			Iterator<Collection<Object>> ite_data = data.iterator();
			if (ite_data != null) {
				int row = startRow;
				int columns = -1;
				while (ite_data.hasNext()) {
					Collection<Object> ele = ite_data.next();
					if (ele != null) {
						columns = Math.max(ele.size(), columns);
						Iterator<Object> ite_data_1 = ele.iterator();
						if (ite_data_1 != null) {
							int column = 0;
							while (ite_data_1.hasNext()) {
								Object val = ite_data_1.next();
								newSheet = addCell2Sheet(sheet, row, column,
										val, contentFormat, "1");
								if (newSheet != null) {
									sheet = newSheet;
									row = 0;
								}
								column += 1;
							}
						}
					}
					row += 1;
				}
			}
		}
		return newSheet;
	}
}
