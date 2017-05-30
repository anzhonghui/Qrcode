package com.qk.util;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.qk.util.Constant;
/**
 * 生成二维码
 *  
 * @author AN
 */
public class QRCode {
	
	private static Logger logger = Logger.getLogger(QRCode.class);
	
	//生成二维码(二维码的内容)
	public void createQR(String contents, int imgCount){
		
		//定义二维码的参数
		HashMap hints = new HashMap();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");//定义字符集，即使用编码
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);//纠错等级
		hints.put(EncodeHintType.MARGIN, 1);//定义边距
		
		//生成二维码:参数 -内容，类型（二维码），宽度，高度，参数
		try {
			BitMatrix bitMatrix = new MultiFormatWriter().encode(contents, BarcodeFormat.QR_CODE, Constant.QR_WIDTH, Constant.QR_HEIGHT, hints);
			
			Path file = new File(Constant.IMG_PATH + imgCount+ "." + Constant.FORMAT).toPath();
			//生成:二维码，格式，文件路径
			MatrixToImageWriter.writeToPath(bitMatrix, Constant.FORMAT, file);
			
		} catch (Exception e) {
			logger.error("生成二维码出错，异常信息为：");
			logger.error(ErrorUtil.getErrorMsg(e));
		} finally {
			hints = null;
		}
	}
}
