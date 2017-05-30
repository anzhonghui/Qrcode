package com.qk.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.junit.Test;

import com.qk.util.CRC16Util;

public class Test01 {

	/**
	 * 使用正则表达式，字符串每两个中间加一个空格
	 */
	@Test
	public void fun(){
		String str = "2612011388e1050f0e281188f00000021a4e01010101010000000001010101000000000000000977000000000000030375001f000003cb004c0052000100040a64001d044c01132af800030007016900000000000000013e000000000bb80bb80000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002a14";
        String regex = "(.{2})";
        str = str.replaceAll (regex, "$1 ");
        System.out.println (str);
	}
	
	@Test
	public void fun2(){
		Date beginDate = new Date(System.currentTimeMillis());
		SimpleDateFormat sdFormatter = new SimpleDateFormat("YYMMddHHmm");
		String str = sdFormatter.format(beginDate);
		
		byte[] bytes = new byte[str.length()/2];
		for(int i=0, j=0; i<str.length();j++, i+=2){
			bytes[j] = Byte.decode("0x" + new String(Integer.toHexString(Integer.parseInt(str.substring(i,i+2)))));
		}
		
		System.out.println(CRC16Util.getBufHexStr(bytes));
		
	}
}
