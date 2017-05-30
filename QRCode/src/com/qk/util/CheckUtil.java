package com.qk.util;

import java.util.Scanner;

public class CheckUtil {

	// 获取校验码
    public static String obtainCheckCode(String str) {
    	String checkCode = null;
    	int count=0;
        int len = (str.length() + 2) / 2;
        String[] transit = new String[len];
        // 每次获取两位（因为是按字节异或）
        for (int i = 0; i < str.length(); i += 2) {
            transit[count] = str.substring(i, i + 2);
            count++;
        }

        for (int i = 0; i < count - 1; i++) {
            if (i == 0) {
                checkCode = getCheckCode(transit[i], transit[i + 1]);
            } else {
                checkCode = getCheckCode(checkCode, transit[i + 1]);
            }
        }

        if (checkCode.length() < 2) {
            len = checkCode.length();
            for (int i = 0; i < 2 - len; i++) {
                checkCode = "0" + checkCode;
            }
        }
        return checkCode;
    }
    
	private static String getCheckCode(String strHex_X,String strHex_Y){   
        //将x、y转成二进制形式   
        String anotherBinary=Integer.toBinaryString(Integer.valueOf(strHex_X,16));   
        String thisBinary=Integer.toBinaryString(Integer.valueOf(strHex_Y,16));   
        String result = "";   
        //判断是否为8位二进制，否则左补零   
        if(anotherBinary.length() != 8){   
        for (int i = anotherBinary.length(); i <8; i++) {   
                anotherBinary = "0"+anotherBinary;   
            }   
        }   
        if(thisBinary.length() != 8){   
        for (int i = thisBinary.length(); i <8; i++) {   
                thisBinary = "0"+thisBinary;   
            }   
        }   
        //异或运算   
        for(int i=0;i<anotherBinary.length();i++){   
        //如果相同位置数相同，则补0，否则补1   
                if(thisBinary.charAt(i)==anotherBinary.charAt(i))   
                    result+="0";   
                else{   
                    result+="1";   
                }   
            }  
        return Integer.toHexString(Integer.parseInt(result, 2));   
    }  
	
	public static void main(String[] args) {
		while(true){
			Scanner scanner = new Scanner(System.in);
			String str = scanner.next();
			System.out.println(obtainCheckCode(str));
		}
	}
}
