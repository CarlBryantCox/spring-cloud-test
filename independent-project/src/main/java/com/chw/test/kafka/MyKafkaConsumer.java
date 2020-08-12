package com.chw.test.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Collections;
import java.util.Properties;

@Slf4j
public class MyKafkaConsumer extends Thread{

    private volatile boolean run = true;

    @Override
    public void run() {
        String topicName="lyn";
        String groupId="test-consumer-group";
        Properties props = new Properties();
        props.put("bootstrap.servers", "zookeeper_test:9092,kafka_test:9092,nginx_test:9092");
        props.put("group.id", groupId);
        props.put("enable.auto.commit","true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("auto.offset.reset","earliest");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topicName));
        log.info("---------Kafka消费者启动---------");
        try {
            while (run) {
                ConsumerRecords<String,String> records = consumer.poll(1000);
                for (ConsumerRecord<String,String> record : records) {
                    System.out.println("分区："+record.partition()+" offset:"+record.offset()+"key:"+record.key()+"value:"+record.value());
                }
            }
            log.info("---------Kafka消费者即将销毁---------");
        } catch (Exception e) {
            log.error(e.toString());
        } finally {
            consumer.close();
            log.info("---------Kafka消费者已经销毁---------");
        }
    }

    public void closed(){
        this.run = false;
        //this.interrupt();
    }
}
