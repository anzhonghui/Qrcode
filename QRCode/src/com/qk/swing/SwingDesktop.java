package com.qk.swing;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.Timer;

import org.apache.log4j.Logger;

import com.qk.bean.Message;
import com.qk.dao.MessageDao;
import com.qk.netty.TimeServer;
import com.qk.timer.TimerManager;
import com.qk.util.Constant;
import com.qk.util.ErrorUtil;
import com.qk.util.ExcelUtil;
import com.qk.util.HandlerMsg;
import com.qk.util.ImgJPanel;
import com.qk.util.Lock;
import com.qk.util.QRCode;

public class SwingDesktop extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private ImgJPanel imgJPanel;
	private List<File> imgList = new ArrayList<File>();
	//日志信息的打印
	private static Logger logger = Logger.getLogger(SwingDesktop.class);
	//生成二维码的类
	private static QRCode qrCode = new QRCode();
	private MessageDao messageDao = MessageDao.getInstance();
	//获取端口号
//	private static final Map<Double, Double> portMap = ExcelUtil.getPorts();
	
	//构造方法初始化窗口
	public SwingDesktop(){
		imgJPanel = new ImgJPanel("");
		add(imgJPanel);
		//设置标题
		setTitle("显示二维码");
		//设置窗体大小
		setSize(Constant.WIDTH, Constant.HEIGHT);
		setLocationRelativeTo(null);
		
		
		 Toolkit kit = Toolkit.getDefaultToolkit(); // 定义工具包 
		 Dimension screenSize = kit.getScreenSize(); // 获取屏幕的尺寸 
		 int screenWidth = screenSize.width; // 获取屏幕的宽
		 int screenHeight = screenSize.height; // 获取屏幕的高
		 int height = this.getHeight();
		 int width = this.getWidth();
		 //窗体
		 setLocation((screenWidth-width)*4/5, (screenHeight-height)/2);
		
		
        //设置可以显示窗口
  		setVisible(true);
  		//设置关闭模式
  		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				Date endDate = new Date(System.currentTimeMillis());
				SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				logger.info("结束显示二维码，退出程序:"+sdFormatter.format(endDate));
				System.exit(0);
			}
		});
  		
  		//设置图片的显示时间
  		Timer timer = new Timer(Constant.TIME_PERIOD, new ActionListener() {
  			int imgCount = 1;
  			//当数据量过长添加一个标识，是否去 读取下一条数据
  			int next = 1;
  			//记录某些长数据经过消息处理后的条数
  			int number = 0;
  			int msgCount = 1;
  			Message message = new Message();
  			
			@Override
			public void actionPerformed(ActionEvent e) {
//				
				synchronized(Lock.lock){
					
					//通过同步锁设置线程休眠
					if(!Lock.isRun){
						try {
							System.out.println("线程休眠");
							Lock.lock.wait();
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
					
					Image image = null;
					File imgFile = null;
					message = messageDao.getMsg();
					
					if(message.getMeg() != null){
						if(message.getMeg().length() <= Constant.MAX_MSG_LEGTH){
							logger.info("从库中库取消息-msgCount:"+msgCount+message.toString());
							qrCode.createQR(HandlerMsg.getMessage(message.getPort(), message.getMeg(), message.getId()), imgCount);
							try {
								logger.info("显示消息"+msgCount+"的二维码");
								imgFile = new File(Constant.IMG_PATH + imgCount + "." +Constant.FORMAT);
								image = ImageIO.read(imgFile);
							} catch (Exception e1) {
								logger.error("读取图片报异常了，异常信息为：");
								logger.error(ErrorUtil.getErrorMsg(e1));
							}
							imgJPanel.loadPhoto(image);
							if(messageDao.updateMsg(message.getId())>0){
								logger.info("显示完成");
							}
							msgCount++;
						} else {
							
							//当消息过长时，直接生成所有的二维码
							int i=0;
							if(next == 1){
								logger.info("从库中取消息-msgCount:"+msgCount+message.toString());
								//获取处理后的数据
								List<String> lists = HandlerMsg.getMessageList(message.getPort(), message.getMeg(), msgCount);
								number = lists.size();
								for (String string : lists) {
									qrCode.createQR(string, imgCount+i++);
								}
							}
							
							try {
								logger.info("显示消息"+msgCount+"的第"+next+"张二维码");
								imgFile = new File(Constant.IMG_PATH + imgCount + "." + Constant.FORMAT);
								image = ImageIO.read(imgFile);
								next++;
							} catch (Exception e1) {
								logger.error("读取图片报异常了，异常信息为：");
								logger.error(ErrorUtil.getErrorMsg(e1));
							}
							imgJPanel.loadPhoto(image);
							
							//判断是否需要读取下一条数据
							if(next == number+1){
								msgCount++;
								if(messageDao.updateMsg(message.getId())>0){
									logger.info("显示完成");
								}
								next = 1;
							}
						}
						
						//将文件保存，准备删除
						if(imgCount%Constant.MAX_IMG_CON == 0){
							imgList.add(imgFile);
							for (File file : imgList) {
								file.delete();
							}
							logger.info("删除5张图片");
						}else{
							imgList.add(imgFile);
						}
						
						//计数增加
						message = null;
						imgCount++;
				
					} else {
						//如果没有数据，显示背景
						imgJPanel.loadPhoto(null);
						Lock.isRun = false;
					}
				}
			}
		});
		timer.start();
		
	}
	
	public static void main(String[] args) {
		
		Date beginDate = new Date(System.currentTimeMillis());
		SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		logger.info("开始显示二维码:"+ sdFormatter.format(beginDate));
		SwingDesktop swingDesktop = new SwingDesktop(); 
		
		//开启netty的线程，遍历监听端口号
		for (final Map.Entry<Double, Double> entry : ExcelUtil.getInstance().getPortMap().entrySet()) {
			new Thread(){
				public void run(){
					new TimeServer().bind(entry.getKey().intValue());
				}
			}.start();
		}
		
		//开启删除的定时任务
		new TimerManager();
		 
		
//		new Thread(){
//			public void run(){
//				 
//				new TimeServer().bind(Constant.PORT);
//			}
//		}.start();
//		 
//		new Thread(){
//			public void run(){
//				 
//				new TimeServer().bind(8020);
//			}
//		}.start();
//		
//		new Thread(){
//			public void run(){
//				 
//				new TimeServer().bind(8030);
//			}
//		}.start();
	}
}
