package com.chw.test.listener;


import com.chw.test.kafka.MyKafkaConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * 系统初始化的监听器
 */
@Slf4j
@WebListener
public class InitListener implements ServletContextListener {

	private MyKafkaConsumer myKafkaConsumer;

//	@Autowired
//	private KafkaProducer<String,String> kafkaProducer;

	public void contextInitialized(ServletContextEvent sce) {
//		MyKafkaConsumer myKafkaConsumer = new MyKafkaConsumer();
//		this.myKafkaConsumer=myKafkaConsumer;
//		myKafkaConsumer.start();
	}

	public void contextDestroyed(ServletContextEvent sce) {
//		myKafkaConsumer.closed();
//		kafkaProducer.close();
//		log.info("---------Kafka生产者已经销毁---------");
	}
}
