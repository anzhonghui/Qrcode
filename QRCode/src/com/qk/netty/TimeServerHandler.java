package com.qk.netty;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.qk.bean.Message;
import com.qk.dao.MessageDao;
import com.qk.util.CRC16Util;
import com.qk.util.CheckUtil;
import com.qk.util.ExcelUtil;
import com.qk.util.Lock;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TimeServerHandler extends ChannelHandlerAdapter  {
	
	//标识量，定义为全局，为了读取生成图片方便，图片的名称也是一次递增
//	private static int counter=1;
	private MessageDao messageDao = MessageDao.getInstance();
	private Logger logger = Logger.getLogger(TimeServerHandler.class);
//	private static final Map<Double, Double> portMap = ExcelUtil.getPosts();
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
		
		ByteBuf buf = (ByteBuf) msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		
//		String str = CRC16Util.getBufHexStr(req);
//		// 获取要发送的byte数组,这个是直接发送的
//		byte[] sendBuf = CRC16Util.getSendBuf(new String(req,"ISO-8859-1"));
//		// 为了验证方便,将16进制的byte数组转化为字符串输出
//		logger.info("以16进制的形式输出："+CRC16Util.getBufHexStr(sendBuf));
//		System.out.println("以16进制的形式输出："+new String(CRC16Util.HexString2Buf(CRC16Util.getBufHexStr(req))));
//		String body = (String)msg;
//		System.out.println(body);
//		String responseStr = null;
//		//获取当前的端口号
//		Double receivePort = getPort(ctx);
//		Double sendPort = null;
//		logger.info("接受消息"+counter+":"+body);
//		获取客户端的IP和端口号
//		InetSocketAddress insocket = (InetSocketAddress) ctx.channel()
//                .remoteAddress();
//		System.out.println(ctx.channel().toString());
//		int clientPort = insocket.getPort();
//        String clientIP = insocket.getAddress().getHostAddress();
//        logger.info(clientPort+":"+clientIP+"--发送的第" + counter +"消息："+body);
//		if(body.startsWith("&")){
//			logger.info(receivePort+"接受消息"+counter+":"+body);
//			body = body.substring(1, body.length());
//			byte[] bytes = body.getBytes();
//			if(portMap!=null){
//				//获取对应接受端口的发送端口的信息
//				for (Map.Entry<Double, Double> entry : portMap.entrySet()) {
//					//转成double比价值
//					if((double)entry.getKey() ==  (double)receivePort){
//						sendPort = entry.getValue();
//					}
//				}
//				logger.info("存入消息"+sendPort.intValue()+":"+body);
//				messageDao.saveMsg(new Message(sendPort.intValue(), new String(bytes)));
////				if(!Lock.isRun){
////					Lock.isRun = true;
////					synchronized(Lock.lock){
////						System.out.println("线程唤醒");
////						Lock.lock.notify();
////					}
////				}
//				//获取回执消息
//				responseStr = getReceive(bytes) + counter++;
//			}
//			logger.info("回执消息为："+responseStr+":");
//			ByteBuf resp = Unpooled.copiedBuffer(responseStr.getBytes());
//			ctx.writeAndFlush(resp);
//		}
		
		Double receivePort = getPort(ctx);
		Double sendPort = null;
		
		if(ExcelUtil.getInstance().getPortMap() != null){
			//获取对应接受端口的发送端口的信息
			for (Map.Entry<Double, Double> entry : ExcelUtil.getInstance().getPortMap().entrySet()) {
				//转成double比价值
				if((double)entry.getKey() ==  (double)receivePort){
					sendPort = entry.getValue();
				}
			}
//			String message = CRC16Util.getBufHexStr(req);
//			logger.info("存入消息"+sendPort.intValue()+":"+message.substring(0, message.length()));
			logger.info("存入消息"+sendPort.intValue()+":"+CRC16Util.getBufHexStr(req));
			messageDao.saveMsg(new Message(sendPort.intValue(), CRC16Util.getBufHexStr(req)));
			
			if(!Lock.isRun){
				Lock.isRun = true;
				synchronized(Lock.lock){
					System.out.println("线程唤醒");
					Lock.lock.notify();
				}
			}
			
		}else{
			logger.error("没有端口信息");
		}	
		//获取回执消息
		byte[] responseStr = getReceive(req);
		logger.info("回执消息："+CRC16Util.getBufHexStr(responseStr));
		ByteBuf resp = Unpooled.copiedBuffer(responseStr);
		ctx.writeAndFlush(resp);
	}
	
	//获取回执消息
	private byte[] getReceive(byte[] bytes){
		byte[] byteSend = new byte[11];
		//报文标识
		byteSend[0] = 0x24;
		//检测来源
		byteSend[1] = bytes[1];
		//站点编号,报文时间,帧号帧
		byteSend[2] = bytes[3];
		byteSend[3] = bytes[4];
		
		//拼接时间
		Date beginDate = new Date(System.currentTimeMillis());
		SimpleDateFormat sdFormatter = new SimpleDateFormat("YYMMddHHmm");
		byte[] timeBytes = CRC16Util.getStrToHex(sdFormatter.format(beginDate));
		
		byteSend[4] = timeBytes[0];
		byteSend[5] = timeBytes[1];
		byteSend[6] = timeBytes[2];
		byteSend[7] = timeBytes[3];
		byteSend[8] = timeBytes[4];
		
		byteSend[9] = 0x11;
		byte[] check = CRC16Util.HexString2Buf(CheckUtil.obtainCheckCode(CRC16Util.getBufHexStr(new byte[]{byteSend[1], byteSend[2], byteSend[3], byteSend[4] ,byteSend[5], byteSend[6], byteSend[7], byteSend[8], byteSend[9]})));
		byteSend[10] = check[0];
		
		return byteSend;
	}
	
	//获取当前服务端监听的端口
	private Double getPort(ChannelHandlerContext ctx){
		String ctxStr = ctx.channel().toString().substring(1,ctx.channel().toString().length()-1);
		String strs[] = ctxStr.split("[:,/-]");
		Double nowPort = Double.parseDouble(strs[6].trim());
		return nowPort;
	}
	
//	@Override
//	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception{
//		//将消息队列中的消息写入到SocketChannel中发送给对方
//		ctx.flush();
//	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}
	
}
