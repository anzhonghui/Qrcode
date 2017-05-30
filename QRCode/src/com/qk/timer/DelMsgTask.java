package com.qk.timer;

import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.qk.dao.MessageDao;

public class DelMsgTask extends TimerTask {
	
	private Logger logger = Logger.getLogger(DelMsgTask.class);
	private MessageDao messageDao = MessageDao.getInstance();

	@Override
	public void run() {
		messageDao.deleteMsg();
	}

}
