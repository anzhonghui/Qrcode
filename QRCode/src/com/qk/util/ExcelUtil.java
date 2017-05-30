package com.qk.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {
	
	private static Logger logger = Logger.getLogger(ExcelUtil.class);
	private static Map<Double, Double> portMap = new HashMap<Double, Double>();
	private static volatile ExcelUtil excelUtil;
	
	private ExcelUtil(){
		getPort();
	}
	
	//通过单例模式保证只初始化一次
	public static ExcelUtil getInstance(){
		if(excelUtil == null){
			synchronized(ExcelUtil.class){
				if(excelUtil == null){
					excelUtil = new ExcelUtil();
				}
			}
		}
		
		return excelUtil;
	}
	
	//对外提供方法去获取map集合
	public Map<Double, Double> getPortMap() {
		return portMap;
	}

	private static Map<Double, Double> getPort(){
		File file = new File(Constant.EXCEL_NAME);
		String fileName = file.getName();  
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);  
        if(suffix.equals("xls")){
        	portMap = getContentByXls(file);
        	
        } else if(suffix.equals("xlsx")){
        	portMap = getContentByXlsx(file);
        	
        } else {
        	logger.error("文件格式不对");
        	portMap = null;
        }
        
		return portMap;
	}
	
	private static Map<Double, Double> getContentByXls(File file){
		InputStream is = null;
		HSSFWorkbook hssfWorkbook = null;
		try {
			is = new FileInputStream(file);
			hssfWorkbook = new HSSFWorkbook(is);
		} catch (FileNotFoundException e) {
			logger.error("Excel文件找不到,异常信息为：");
			logger.error(ErrorUtil.getErrorMsg(e));
		} catch (IOException e) {
			logger.error("读取Excel错误,异常信息为：");
			logger.error(ErrorUtil.getErrorMsg(e));
		}
        
        // 获取每一个工作薄
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                continue;
            }
            // 获取当前工作薄的每一行
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow != null) {
                    HSSFCell receive = hssfRow.getCell(0);
                    HSSFCell send = hssfRow.getCell(1);
                    double receivePort = receive.getNumericCellValue();
                    double sendPort = send.getNumericCellValue();
                    portMap.put(receivePort, sendPort);
                }
            }
        }
        
        logger.info("读取的端口信息为："+portMap.toString());
        return portMap;	
	}
	
	//读取.xlsx表格的方法
	private static Map<Double, Double> getContentByXlsx(File file){
		InputStream is = null;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
        XSSFWorkbook xssfWorkbook = null;
		try {
			xssfWorkbook = new XSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
        // 获取每一个工作薄
        for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
            if (xssfSheet == null) {
                continue;
            }
            // 获取当前工作薄的每一行
            for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                if (xssfRow != null) {
                    XSSFCell receive = xssfRow.getCell(0);
                    XSSFCell send = xssfRow.getCell(1);
                    double receivePort = receive.getNumericCellValue();
                    double sendPort = send.getNumericCellValue();
                    portMap.put(receivePort, sendPort);
                }
            }
        }
		 logger.info("读取的端口信息为："+portMap.toString());
	     return portMap;	
	}
	
	//转换数据格式
//    private static String getValue(XSSFCell xssfRow) {
//
//        if (xssfRow.getCellType() == xssfRow.CELL_TYPE_BOOLEAN) {
//            return String.valueOf(xssfRow.getBooleanCellValue());
//        } else if (xssfRow.getCellType() == xssfRow.CELL_TYPE_NUMERIC) {
//            return String.valueOf(xssfRow.getNumericCellValue());
//        } else {
//            return String.valueOf(xssfRow.getStringCellValue());
//        }
//    }
	
	
    
//    public static void main(String[] args) {
//		ExcelUtil execlUtil = ExcelUtil.getInstance();
////		execlUtil.getConnection();
//		execlUtil.getPortMap();
//	    Map<Double, Double> map  = execlUtil.portMap;
//	    System.out.println(map.toString());
//	    
////	    for (Map.Entry<Double, Double> entry : map.entrySet()) {
////	    	System.out.println(entry.getKey());
////	    	 System.out.println("key= " + entry.getKey() + " and value= "
////	    	                     + entry.getValue());
////	    	
////	    }
//    }
}
