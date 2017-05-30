package com.qk.util;
/**
 * 轮训数据库的线程锁
 * @author AN
 */
public class Lock {
	//线程锁
	public static Lock lock = new Lock();
	//是否唤醒的标志
	public static boolean isRun = true;
	
}
