package com.chw.test.utils.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * ZooKeeperSession
 * @author Administrator
 *
 */
@Slf4j
public class ZooKeeperSession {
	
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	
	private ZooKeeper zookeeper;

	private ZooKeeperSession() {
		// 去连接zookeeper server，创建会话的时候，是异步去进行的
		// 所以要给一个监听器，说告诉我们什么时候才是真正完成了跟zk server的连接
		try {
			this.zookeeper = new ZooKeeper(
					"zookeeper-test:2181,kafka-test:2181,storm-test:2181",
					50000,
					new ZooKeeperWatcher());

			// 给一个状态CONNECTING，连接中
			log.info(zookeeper.getState().toString());

			try {
				// CountDownLatch
				// java多线程并发同步的一个工具类
				// 会传递进去一些数字，比如说1,2 ，3 都可以
				// 然后await()，如果数字不是0，那么久卡住，等待
				
				// 其他的线程可以调用coutnDown()，减1
				// 如果数字减到0，那么之前所有在await的线程，都会逃出阻塞的状态
				// 继续向下运行
				
				connectedSemaphore.await();
			} catch(InterruptedException e) {
				e.printStackTrace();
			}

			log.info("ZooKeeper session established......");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取分布式锁
	 */
	public boolean acquireDistributedLock(Long productId) {
		String path = "/product-lock-" + productId;
	
		try {
			zookeeper.create(path, "".getBytes(), 
					Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			log.info("success to acquire lock for product[id=" + productId + "]");
			return true;
		} catch (Exception e) {
			// 如果那个商品对应的锁的node，已经存在了，就是已经被别人加锁了，那么就这里就会报错
			// NodeExistsException
			int count = 0;
			while(true) {
				if(count>9){
					log.info("fail to acquire lock for product[id=" + productId + "] after 10 times try......");
					return false;
				}
				try {
					Thread.sleep(1000);
					zookeeper.create(path, "".getBytes(),
							Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
				} catch (Exception e2) {
					count++;
					log.info("the " + count + " times try to acquire lock for product[id=" + productId + "]......");
					continue;
				}
				log.info("success to acquire lock for product[id=" + productId + "] after " + count + " times try......");
				return true;
			}
		}
	}
	
	/**
	 * 释放掉一个分布式锁
	 */
	public void releaseDistributedLock(Long productId) {
		String path = "/product-lock-" + productId;
		try {
			zookeeper.delete(path, -1); 
			log.info("release the lock for product[id=" + productId + "]......");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 建立zk session的watcher
	 * @author Administrator
	 *
	 */
	private class ZooKeeperWatcher implements Watcher {

		public void process(WatchedEvent event) {
			log.info("Receive watched event: " + event.getState());
			if(KeeperState.SyncConnected == event.getState()) {
				connectedSemaphore.countDown();
			} 
		}
		
	}
	
	/**
	 * 封装单例的静态内部类
	 * @author Administrator
	 *
	 */
	private static class Singleton {
		
		private static ZooKeeperSession instance;
		
		static {
			instance = new ZooKeeperSession();
		}
		
		private static ZooKeeperSession getInstance() {
			return instance;
		}
		
	}
	
	/**
	 * 获取单例
	 */
	public static ZooKeeperSession getInstance() {
		return Singleton.getInstance();
	}

}
