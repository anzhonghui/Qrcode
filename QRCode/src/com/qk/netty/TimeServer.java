package com.qk.netty;

import org.apache.log4j.Logger;

import com.qk.util.ErrorUtil;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class TimeServer {

	private Logger logger = Logger.getLogger(TimeServer.class);

	public void bind(int port){
		//配置服务端的NIO线程
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 1024)
//				.option(ChannelOption.SO_REUSEADDR, true)
				.handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(new ChildChannelHandler());
			//绑定端口
			ChannelFuture cf = bootstrap.bind(port).sync();
			
			//等待服务端监听的端口关闭
			cf.channel().closeFuture().sync();
			
		} catch (InterruptedException e) {
			logger.error("netty报异常了,异常信息为：");
			logger.error(ErrorUtil.getErrorMsg(e));
		} finally {
			//释放线程池资
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			//分隔符问题添加
//			ByteBuf delimiter = Unpooled.copiedBuffer(new byte[]{0x0d,0x0a});
//			ch.pipeline().addFirst(new DelimiterBasedFrameDecoder(1024, delimiter));
			//设置读取的字符编码
//			ch.pipeline().addLast(new StringDecoder(Charset.forName("gbk")));
//			ch.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
//			System.out.println("当前信息："+ch.id().toString());
//			if(){
//			}
//			ch.pipeline().addLast(new FixedLengthFrameDecoder(5));
//			ch.pipeline().addLast(new FixedLengthFrameDecoder(15));
			//maxFrameLength, lengthFieldOffset, lengthFieldLength
//			ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 11, 1));
			
//			ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
//			ch.pipeline().addLast(new StringDecoder());
			
//			获取端口号
			if(ch.localAddress().getPort() == 8020){
				
				//11+438+2，数据位是两位，不加数据位往后读取438个字节，包含两位校验位，不需要偏移
				ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 11, 2, 0, 0));
			}else{
				
				//11+136+2,数据位是1位，不加数据位读取后面的136个字节，包含以为校验位，然后偏移一位，不足另一个校验位
				ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 11, 1, 1, 0));
			}
			
			ch.pipeline().addLast(new TimeServerHandler());
		}
		
	}
	
//	public static void main(String[] args) {
//		int port = 8010;
//		if (args!=null && args.length>0){
//			try {
//				port = Integer.parseInt(args[0]);
//			} catch (NumberFormatException e) {
//				
//			}
//		}
//		new TimeServer().bind(port);
//		new Thread(){
//			public void run(){
//				new TimeServer().bind(8010);
//			}
//		}.start();
//		new Thread(){
//			public void run(){
//				new TimeServer().bind(8020);
//			}
//		}.start();
//		new Thread(){
//			public void run(){
//				new TimeServer().bind(8030);
//			}
//		}.start();
//		new Thread(){
//			public void run(){
//				new TimeServer().bind(8040);
//			}
//		}.start();
//	}
}
