package com.qk.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.qk.bean.Message;
import com.qk.util.Constant;
import com.qk.util.ErrorUtil;

public class MessageDao {
		
	private static volatile Connection connection;
	private PreparedStatement preparedStatement;
	private ResultSet resultSet;
	private static final String tableName = "qrcode";
	
	private static Logger logger = Logger.getLogger(MessageDao.class);
	
	private MessageDao(){
		try {
			Class.forName(Constant.DRIVER);
		} catch (ClassNotFoundException e) {
			logger.error("驱动异常,异常信息为:"+e.getMessage());
			e.printStackTrace();
		}
		
		init();
	}
	
	public void init(){
		try {
			String sql = "create table if not exists qrcode(id INTEGER PRIMARY key autoincrement,port INTEGER,flag INTEGER,message varchar(1000));";
			
			connection = getConnection();
			preparedStatement = connection.prepareStatement(sql);
			
			preparedStatement.executeUpdate();
			
		} catch (SQLException e) {
			logger.error("初始化创建数据库异常，异常信息为：");
			logger.error(ErrorUtil.getErrorMsg(e));
		}finally {
//			closeResource(connection, preparedStatement, resultSet);
			closeResource(preparedStatement, resultSet);
		}
	}
	
	//通过单例模式获取连接对象
	private static volatile MessageDao instance;
	public static MessageDao getInstance(){
		if(instance == null){
			synchronized(MessageDao.class){
				if(instance == null){
					instance = new MessageDao();
				}
			}
		}
		return instance;
	}
	
//	private Connection getConnection(){
//		try {
//			connection = DriverManager.getConnection(Constant.URL);
//			System.out.println(Thread.currentThread().getName()+"  " + connection.toString());
//		} catch (SQLException e) {
//			logger.error("获取连接失败"+e.getMessage());
//			e.printStackTrace();
//		}
//		return connection;
//	}
	private static Connection getConnection(){
		if(connection == null){
			synchronized(MessageDao.class){
				if(connection == null){
					try {
						connection = DriverManager.getConnection(Constant.URL);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return connection;
	}
	

	public void saveMsg(Message message){
		synchronized(MessageDao.class){
			try {
				String sql = "INSERT INTO "+ tableName +" VALUES(?,?,?,?)";
				
				connection = getConnection();
				preparedStatement = connection.prepareStatement(sql);
				
//				preparedStatement.setInt(1, message.getId());
				preparedStatement.setInt(2, message.getPort());
				//插入的flag均为1，视为没有处理的消息
				preparedStatement.setInt(3, 1);
				preparedStatement.setString(4, message.getMeg());
				
				preparedStatement.executeUpdate();
				
			} catch (SQLException e) {
				logger.error("保存信息异常,异常信息为:");
				logger.error(ErrorUtil.getErrorMsg(e));
			}finally {
//				closeResource(connection, preparedStatement, resultSet);
				closeResource(preparedStatement, resultSet);
			}
		}
	}
	
	public Message getMsg(){
		synchronized(MessageDao.class){
			Message message = new Message();
			try {
				String sql = "SELECT * FROM "+ tableName +" WHERE flag=1 ORDER BY id LIMIT 0,1";
				
				connection = getConnection();
				preparedStatement = connection.prepareStatement(sql);
//				preparedStatement.setInt(1, id);

				resultSet = preparedStatement.executeQuery();
				
				message.setId(resultSet.getInt("id"));
				message.setPort(resultSet.getInt("port"));
				message.setFlag(resultSet.getInt("flag"));
				message.setMeg(resultSet.getString("message"));
				
		        return message;
			} catch (SQLException e) {
				logger.error("获取信息异常,异常信息为:");
				logger.error(ErrorUtil.getErrorMsg(e));
			} finally {
//				closeResource(connection, preparedStatement, resultSet);
				closeResource(preparedStatement, resultSet);
			}
			return null;
		}
	}
	
	public int updateMsg(int id){
		synchronized(MessageDao.class){
			int num=0;
			try {
				String sql = "UPDATE "+ tableName + " SET flag=0 WHERE id=?";
				connection = getConnection();
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setInt(1, id);
				num = preparedStatement.executeUpdate();
			} catch (SQLException e) {
				logger.error("修改信息异常,异常信息为:");
				logger.error(ErrorUtil.getErrorMsg(e));
			}finally {
//				closeResource(connection, preparedStatement, resultSet);
				closeResource(preparedStatement, resultSet);
			}
			return num;
		}
	}
	
	public void deleteMsg(){
		synchronized(MessageDao.class){
			try {
				String sql = "DELETE FROM "+ tableName +" WHERE flag=0";
				
				connection = getConnection();
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.executeUpdate();
				
			} catch (SQLException e) {
				logger.error("删除信息异常,异常信息为:");
				logger.error(ErrorUtil.getErrorMsg(e));
			}finally {
//				closeResource(connection, preparedStatement, resultSet);
				closeResource(preparedStatement, resultSet);
			}
		}
		
	}
	
	private void closeResource( PreparedStatement pre, ResultSet res){
		if(res != null){
			try {
				res.close();
			} catch (SQLException e) {
				logger.error("关闭资源失败：");
				logger.error(ErrorUtil.getErrorMsg(e));
			}
		}
		if(pre != null){
			try {
				pre.close();
			} catch (SQLException e) {
				logger.error("关闭资源失败：");
				logger.error(ErrorUtil.getErrorMsg(e));
			}
		}
//		Connection conn,
//		if(conn != null){
//			try {
//				conn.close();
//			} catch (SQLException e) {
//				logger.error("关闭资源失败："+e.getMessage());
//				e.printStackTrace();
//			}
//		}
	}
	
}
