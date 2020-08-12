package com.chw.test.listener;


import com.chw.test.kafka.MyKafkaConsumer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * 系统初始化的监听器
 */
@WebListener
public class InitListener implements ServletContextListener {

	private MyKafkaConsumer myKafkaConsumer;

	public void contextInitialized(ServletContextEvent sce) {
		MyKafkaConsumer myKafkaConsumer = new MyKafkaConsumer();
		this.myKafkaConsumer=myKafkaConsumer;
		myKafkaConsumer.start();
	}

	public void contextDestroyed(ServletContextEvent sce) {
		myKafkaConsumer.closed();
	}
}
