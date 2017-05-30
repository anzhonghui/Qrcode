package com.qk.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
/**
 * 处理消息
 * @author AN
 */
public class HandlerMsg {
	
	private static Logger logger = Logger.getLogger(HandlerMsg.class);
	
	public static String getMessage(int port, String message, int id){
		StringBuffer sBuffer = new StringBuffer(port + "#" + id + "#1#0#0$" + message);
		logger.info("处理后的消息为："+sBuffer.toString());
		return sBuffer.toString();
	}
	
	public static List<String> getMessageList(int port, String message, int id){
		int len = message.length();
		List<String> lists = new ArrayList<String>();
		
		//获取分割的最大个数
		int number = (len/Constant.MAX_MSG_LEGTH == 0 ? len/Constant.MAX_MSG_LEGTH : len/Constant.MAX_MSG_LEGTH+1);
		
		for(int i=1; i<=number; i++){
			if(i == number){
				lists.add(port + "#" + id + "#0#1#" + i + "$" + message.substring((i-1)*Constant.MAX_MSG_LEGTH, len));
			}else{
				lists.add(port + "#" + id + "#0#0#" + i + "$" + message.substring((i-1)*Constant.MAX_MSG_LEGTH, (i)*Constant.MAX_MSG_LEGTH));
			}
		}
		
		logger.info("处理后的消息为："+lists.toString());
		
		return lists;
	}
	
}
