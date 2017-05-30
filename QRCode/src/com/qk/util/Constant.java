package com.qk.util;
/**
 * 常量的定义
 * @author AN
 */
public interface Constant {

	//窗体的高和宽
	int WIDTH = 425;
	int HEIGHT = 450;
	//指定二维码的长宽
	int QR_WIDTH = 410;
	int QR_HEIGHT = 410;
	//图片格式
	String FORMAT = "jpg";
	//设置二维码显示的时间
	int TIME_PERIOD = 2000;
	//图片的删除量
	int MAX_IMG_CON = 5;
	//设置每条消息的最大处理长度
	int MAX_MSG_LEGTH = 400;
	//图片前半部分路径：/usr/java/image/img  ./image/img
	String IMG_PATH = "/home/pi/Desktop/image/img";
	//netty绑定的端口号
	int PORT = 8010;
	//连接sqlite的URL：jdbc:sqlite:///usr/java/sqlite/tim.db jdbc:sqlite://d:/DevRepository/sqlite/tim.db
	String URL = "jdbc:sqlite:///home/pi/Desktop/sqlite/tim.db";
	String DRIVER = "org.sqlite.JDBC";
	//设置netty接受的最大字节数
	int MAX_NETTY_LEGTH = 1024;
	//Excel文件的名称./11.xls  /home/pi/Desktop/port.xls
	String EXCEL_NAME = "/home/pi/Desktop/port.xls";
}
