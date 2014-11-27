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
	 * ÿ��sheet���������-1(����&lt;=60000),���=10,��ʵ�����ɵ�sheet�е��������=11<br>
	 * 1.WritableSheetImpl->numRowsPerSheet=65536<br>
	 * 2.maxRowsPerSheet=60000->һ������¾Ͳ��ü���header��titleռ�õ�����
	 * */
	public static final int maxRowsPerSheet = 60000;
	/**
	 * excel�ı����ݵ�����
	 */
	public static WritableFont contentFont = null;
	/**
	 * excel���������
	 */
	public static WritableFont headerFont = null;
	/**
	 * excel�ı����ݵĸ�ʽ
	 */
	public static WritableCellFormat contentFormat = null;
	/**
	 * excel����ĸ�ʽ
	 */
	public static WritableCellFormat headerFormat = null;
	/**
	 * excel���ڵĸ�ʽ
	 */
	public static WritableCellFormat dateFormat = null;
	/**
	 * excel���ֵĸ�ʽ
	 */
	public static WritableCellFormat numberFormat = null;

	/**
	 * ��ʼ��excel�ĸ�ʽ(ÿ��дexcelʱ����Ҫ���³�ʼ����ʽ,�����ʽ��������)
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
			headerFormat.setBackground(jxl.format.Colour.GRAY_25);// ����ɫ
			headerFormat.setBorder(Border.ALL, BorderLineStyle.THIN,
					Colour.BLACK);// ��ɫ�߿�
		} catch (WriteException e) {
			MyLogUtils.logError("excel���ô���", e);
		}
	}

	/**
	 * ����ָ��(excel)�ļ����Workbook(����ļ������ڷ���null)
	 * 
	 * @param excel
	 *            excel�ļ�(File/String)
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
				// MyLogUtils.logError("����ָ��("+excel+")�ļ����Workbook: failed!",e);
				return null;
			}
		}
		return null;
	}

	/**
	 * ����workbook
	 * 
	 * @param excel
	 *            excel�ļ�(File/String)�������(OutputStream)
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
			MyLogUtils.logError("����excelʧ��", e);
		}
		return workbook;
	}

	/**
	 * ����һ��������
	 * 
	 * @param workbook
	 *            excel����
	 * @param sheetName
	 *            ����������
	 * @param sheetIndex
	 *            ������index(index����0,����������)
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
				// ��ֹ�����������ظ�
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
	 * ��excel������insertһ��
	 * 
	 * @param sheet
	 * @param insertTo
	 *            ��0��ʼ���к�
	 * @param args
	 *            ������������: ��1��("1"/"0")=�Ƿ����������ƹ���(�����ھ����ʽ��excel�����鿪���˹���),<br>
	 *            <strong>��������Ļ���ؽ��˷������صĲ�Ϊnull��sheet���¸�ֵ�����ô˷���ʱ���ݵ�sheet����,</
	 *            strong><br>
	 *            ������������
	 * @return ������ص���sheet��Ϊnull,�����ô˷�����ʵ�����¸�ֵ: sheet=newSheet,row=0
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
	 * ��excel������insertһ��
	 * 
	 * @param sheet
	 * @param insertTo
	 *            ��0��ʼ���к�
	 */
	public static void insertColumn(WritableSheet sheet, int insertTo) {
		if (sheet != null) {
			sheet.insertColumn(insertTo);
		}
	}

	/**
	 * �ϲ���Ԫ��
	 * 
	 * @param sheet
	 * @param beginCol
	 *            ���Ͻǵ�Ԫ��-(��)
	 * @param beginRow
	 *            ���Ͻǵ�Ԫ��-(��)
	 * @param endCol
	 *            ���½ǵ�Ԫ��-(��)
	 * @param endRow
	 *            ���½ǵ�Ԫ��-(��)
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
						.logError("�ϲ���Ԫ��ʧ��,���Ͻǵ�Ԫ��:row=" + beginRow + ",col="
								+ beginCol + ",���½ǵ�Ԫ��:row=" + endRow + ",col="
								+ endCol, e);
			}
		}
	}

	/**
	 * ������Ԫ��
	 * 
	 * @param row
	 *            �к�(>=0)
	 * @param column
	 *            �к�(>=0)
	 * @param val
	 *            ��Ԫ������
	 * @param format
	 *            ��Ԫ���ʽ(���ҽ�����Ԫ���������ַ���ʱ�����ʽ����Ч, ��Ϊ��Ԫ������Ϊ���ֻ����ڵĸ�ʽ�Ѿ��ڴ˷���������ΪĬ�ϵ���)
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
	 * ��ӵ�Ԫ��
	 * 
	 * @param sheet
	 * @param row
	 *            �к�(>=0)
	 * @param column
	 *            �к�(>=0)
	 * @param val
	 *            ��Ԫ������
	 * @param format
	 *            ��Ԫ���ʽ(���ҽ�����Ԫ���������ַ���ʱ�����ʽ����Ч, ��Ϊ��Ԫ������Ϊ���ֻ����ڵĸ�ʽ�Ѿ��ڴ˷���������ΪĬ�ϵ���)
	 * @param args
	 *            ������������: ��1��("1"/"0")=�Ƿ����������ƹ���(�����ھ����ʽ��excel�����鿪���˹���),<br>
	 *            <strong>��������Ļ���ؽ��˷������صĲ�Ϊnull��sheet���¸�ֵ�����ô˷���ʱ���ݵ�sheet����,</
	 *            strong><br>
	 *            ������������
	 * @return ������ص�newSheet��Ϊnull,�����ô˷�����ʵ�����¸�ֵ: sheet=newSheet, row=0
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
							"��ӵ�Ԫ��ʧ��,row=" + row + ",col=" + column, e);
				}
			}
		}
		return null;
	}

	/**
	 * ����excel��ĳ����������ĳһ�еĸ߶�
	 * 
	 * @param sheet
	 *            ������
	 * @param row
	 *            �к�(>=0)
	 * @param rowHeight
	 *            �߶�(>0,���<=0��Ĭ��Ϊ100)
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
						"����excel��ĳ����������ĳһ�еĸ߶�ʧ��,sheet=" + sheet.getName()
								+ ",row=" + row, e);
			}
		}
	}

	/**
	 * ���ñ���п�
	 * 
	 * @param sheet
	 * @param column
	 *            �к�(>=0)
	 * @param columnWidth
	 *            �п��
	 */
	public static void setColumnWidth(WritableSheet sheet, int column,
			int columnWidth) {
		if (sheet != null && column >= 0 && columnWidth > 0) {
			sheet.setColumnView(column, columnWidth);
		}
	}

	/**
	 * ��response�����excel
	 * 
	 * @param request
	 * @param response
	 * @param filename
	 *            ���ص��ļ���(���к�׺��,����:"����.xls")
	 * @param sheetName
	 *            ����������
	 * @param title
	 *            ��������һ�ж��е�title(����Ϊnull)
	 * @param headerNames
	 *            �������б�������(����Ϊ��)
	 * @param datas
	 *            ���������ݼ���(List[Map/Collection/Array/Object])
	 * @param getterNames
	 *            ���datas�е�Ԫ�ز���collection&array,������ͨ�����map,��˲���Ϊ��������Ի�map�ļ��ļ���<br>
	 *            <strong>(<br>
	 *            1.��� getterName �������ֵ������,����:ѧԱ�Ա���(stuSex)��"��"��Ӧ�����ֵ�idΪ115,<br>
	 *            ��ôǿ�ҽ�������"stuSex"�ع�Ϊ"dic:stuSex".<br>
	 *            2.��� getterName ����������,�����û�ʵ���й�����dept����(��������),��ôǿ�ҽ�������"dept"�ع�Ϊ
	 *            "po:dept.deptName")</strong><br>
	 *            ʾ��:[id,name,dic:stuSex,po:dept.deptName,closeOpen:
	 *            classroomState] closeOpen��0���� 1����
	 *            ʾ��:[id,name,dic:stuSex,po:dept.deptName,specialDate:startTime]
	 *            specialDate:(HH:mm:ss)�����ڽ�ȡʱ��
	 * @throws Exception
	 *             ���ô˷�����,���ҽ����˷����׳��쳣ʱresponse�ſ��Լ���ʹ��
	 *             (����,������ڵ��ô˷������������response,����Ҫ���˷���������
	 *             try...catch�鲶���쳣,��catch���вſ��Լ�������response)
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
						.logError("ϵͳ��֧���ַ����룺" /*+ EdusAction.serverEncoding*/, e);
			}
			response.setContentType("application/vnd.ms-excel;charset="
					/*+ EdusAction.serverEncoding*/);
			/*
			 * response.setHeader("Content-Disposition","attachment; filename="
			 * +filename+".xls");
			 */
			// response.setHeader("Content-disposition",
			// String.format("attachment; filename=\"%s.xls\"",
			// filename));//�ļ������˫���Ŵ���firefox�Ŀո�ض�����
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
			// ȥ���ж�Ϊ��,����Ϊ��ʱ������ȷдexcel
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
			String personMessage = "mesType:"; // ������Ϣ������ ���������� 1 ���� 0ϵͳ���ѣ�
			String week = "xq:"; // ���� 1-7 תΪ ��һ-����
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
												// �����ֵ��id
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
												coll_map.add("����");
											} else {
												// �����ֵ��id
												dids = dicids.toString().split(
														",");
												for (int i = 0; i < dids.length; i++) {
													dicName += (dids[i] != null && dids[i]
															.equals("1")) ? "����"
															: "����";
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
												coll_map.add("��");
											} else {
												// �����ֵ��id
												dids = dicids.toString().split(
														",");
												for (int i = 0; i < dids.length; i++) {
													dicName += (dids[i] != null && dids[i]
															.equals("1")) ? "��"
															: "��";
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
														.toString()) ? "ϵͳ����"
														: "����");
											} else {
												coll_map.add("");
											}
										} else if (key.startsWith(week)) {
											dicids = map_0.get(key
													.substring(week.length()));
											if (dicids == null) {
												coll_map.add("");
											} else {
												// �����ֵ��id
												dids = dicids.toString().split(
														",");
												for (int i = 0; i < dids.length; i++) {
													if (MyStringUtils
															.notBlank(dids[i])) {
														switch (Integer
																.parseInt(dids[i])) {
														case 1:
															dicName = "����һ";
															break;
														case 2:
															dicName = "���ڶ�";
															break;
														case 3:
															dicName = "������";
															break;
														case 4:
															dicName = "������";
															break;
														case 5:
															dicName = "������";
															break;
														case 6:
															dicName = "������";
															break;
														case 7:
															dicName = "������";
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
											// ����
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
						// ��ͨ����,�÷���õ���Ӧֵ
						if (keyOrFields != null) {
							Collection<Object> coll_obj = new ArrayList<Object>();
							for (String field : keyOrFields) {
								if (field != null) {
									if (field.startsWith(dicPrefix)) {
										// �����ֵ��id
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
										// ʵ���й���������ʵ��
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
	 * дexcel
	 * 
	 * @param excel
	 *            {@code File}����{@code OutputStream}
	 *            ����,�������response�����excel�˲���Ϊnull����
	 * @param sheetNames
	 *            List[String] ���������Ƽ���(����Ϊ��)
	 * @param headerNames
	 *            List[List[String]] �������б�������(һ����ÿ�������������е�����)����(����Ϊ��)
	 * @param datas
	 *            List[List[List[Object]]] ���������ݼ���
	 * @param titles
	 *            List[String] ��������һ�ж��е�title(�����ڱ���е�title)����(����Ϊ��)
	 * @param response
	 *            ���excelĿ�����ļ��������,��˲���Ϊnull����
	 * @throws Exception
	 *             1.���˷��������excelĿ��������ļ��������ʱ,��������쳣�ͻ��׳����쳣
	 *             2.���˷��������excelĿ�������responseʱ
	 *             ,���response�Ѿ���Ӧ��(eg:getOutputStream
	 *             ()),�򲻻��׳��������쳣,��֮�׳��������쳣��response��reset.
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
								// һ�������,�û�ȡ������ʱ���쳣: ClientAbortException
								MyLogUtils.logError("����excel�쳣,sheetNames="
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
				 * excel���������:1.�ļ��������,2.response��response��û����Ӧ��ʱ, �׳��쳣
				 */
				if (isResponse && !response.isCommitted()) {
					response.reset();
				}
				throw e;
			} else {
				/*
				 * excel���������response��response�Ѿ���Ӧ��ʱ, ��־��¼�쳣�������׳��쳣
				 */
				MyLogUtils.logError("дexcel�ļ�����", e);
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
					MyLogUtils.logError("дexcel�ļ�-�ر�excelʱ����", e);
				}
			}
		}
	}

	/**
	 * ��乤����һ�ж��еı���
	 * 
	 * @param sheet
	 *            ������
	 * @param title
	 *            ����
	 * @param column_num
	 *            (Ҫ�ϲ���)����
	 * @param row
	 *            (�������ڵ�)�к�
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
	 * ��乤�����ж�Ӧÿ�����ݵı���
	 * 
	 * @param sheet
	 *            ������
	 * @param headerName
	 *            ���⼯��
	 * @param row
	 *            �к�
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
	 * ��乤��������
	 * 
	 * @param sheet
	 *            ������
	 * @param data
	 *            ����
	 * @param startRow
	 *            (������ݵ�)��ʼ��
	 * @return ������ص�newSheet��Ϊnull,�����ô˷�����ʵ�����¸�ֵ: sheet=newSheet, row=0
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
