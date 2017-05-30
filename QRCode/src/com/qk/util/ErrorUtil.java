package com.qk.util;

import java.io.PrintWriter;
import java.io.StringWriter;
/**
 * 打印具体的异常信息
 * @author AN
 *
 */
public class ErrorUtil {
	
	public static String getErrorMsg(Exception e){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		Throwable cause = e.getCause();
		while (cause != null) {
		cause.printStackTrace(pw);
		cause = cause.getCause();
		}
		pw.close();
		String result = sw.toString();
		return result;
	}
}
