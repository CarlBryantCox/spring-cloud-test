package com.chw.test.utils.timingwheel;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 对时间轮的包装
 */
public class SystemTimer {

    private boolean stop;

    /**
     * 底层时间轮
     */
    private TimingWheel timeWheel;

    /**
     * 一个Timer只有一个delayQueue
     */
    private DelayQueue<TimerTaskList> delayQueue = new DelayQueue<>();

    /**
     * 过期任务执行线程
     */
    private ExecutorService workerThreadPool;

    private ExecutorService bossThreadPool;

    /**
     * 构造函数
     */
    public SystemTimer() {
        timeWheel = new TimingWheel(1, 20, delayQueue);
        workerThreadPool = Executors.newFixedThreadPool(10);
        bossThreadPool = Executors.newFixedThreadPool(1);
        bossThreadPool.submit(this::advanceClock);
    }

    /**
     * 添加任务
     */
    public boolean addTask(TimerTask timerTask) {
        return timeWheel.addTask(timerTask);
    }

    public void stop(){
        stop=true;
        bossThreadPool.shutdown();
        workerThreadPool.shutdown();
    }


    /**
     * 取消任务
     */
    public boolean cancelTask(TimerTask timerTask) {
        if (timerTask.timerTaskList == null) {
            return false;
        }
        return timerTask.timerTaskList.removeTask(timerTask);
    }

    /**
     * 推动时间轮转动
     */
    private void advanceClock() {
        while (!stop) {
            try {
                TimerTaskList timerTaskList = delayQueue.poll(30, TimeUnit.SECONDS);
                if(stop){
                    break;
                }
                if(timerTaskList==null){
                    continue;
                }
                TimerTask root = timerTaskList.getRoot();
                TimerTask next = root.next;
                while (!root.equals(next)) {
                    if (timerTaskList.getLevel() != 1 && next.getDelayTime() >= timerTaskList.getIntervalStamp()) {
                        TimerTask timerTask = new TimerTask(next.getTask(), next.getDelayTime() - timerTaskList.getIntervalStamp(), next.toString());
                        workerThreadPool.execute(()-> this.addTask(timerTask));
                    } else {
                        System.out.println(next+"---开始执行--"+System.currentTimeMillis()/1000);
                        workerThreadPool.execute(next.getTask());
                    }
                    timerTaskList.removeTask(next);
                    next = root.next;
                }
                timerTaskList.reSetExpiration();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}