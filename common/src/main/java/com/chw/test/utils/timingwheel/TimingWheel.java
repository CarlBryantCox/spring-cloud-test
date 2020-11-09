package com.chw.test.utils.timingwheel;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

/**
 * 时间轮的实现
 */
public class TimingWheel {

    private int level;

    /**
     * 一个时间槽的范围
     */
    private int tickTime;

    /**
     * 时间轮大小
     */
    private int wheelSize;

    /**
     * 时间跨度
     */
    private int interval;

    /**
     * 时间槽
     */
    private TimerTaskList[] timerTaskLists;

    /**
     * 上层时间轮
     */
    private volatile TimingWheel overflowWheel;

    /**
     * 一个Timer只有一个delayQueue
     */
    private DelayQueue<TimerTaskList> delayQueue;

    private TimeUnit timeUnit;

    TimingWheel(int tickTime, int wheelSize, DelayQueue<TimerTaskList> delayQueue,TimeUnit timeUnit) {
        this(tickTime,wheelSize,delayQueue,1,timeUnit);
    }

    private TimingWheel(int tickTime, int wheelSize, DelayQueue<TimerTaskList> delayQueue, int level, TimeUnit timeUnit) {
        this.tickTime = tickTime;
        this.wheelSize = wheelSize;
        this.interval = tickTime * wheelSize;
        this.timerTaskLists = new TimerTaskList[wheelSize];
        this.delayQueue = delayQueue;
        this.level = level;
        this.timeUnit=timeUnit;
        for (int i = 0; i < wheelSize; i++) {
            int intervalStamp = (i+1)* tickTime;
            timerTaskLists[i] = new TimerTaskList(intervalStamp,level,timeUnit);
        }
    }

    /**
     * 创建或者获取上层时间轮
     */
    private TimingWheel getOverflowWheel() {
        if (overflowWheel == null) {
            synchronized (this) {
                if (overflowWheel == null) {
                    overflowWheel = new TimingWheel(interval, wheelSize, delayQueue,level+1,timeUnit);
                }
            }
        }
        return overflowWheel;
    }

    /**
     * 添加任务到时间轮
     */
    boolean addTask(TimerTask timerTask) {
        int delayTime = timerTask.getDelayTime();
        if(delayTime>=interval){
            return this.getOverflowWheel().addTask(timerTask);
        }
        int index = delayTime / tickTime - 1;
        index = index<0 ? 0 : index;
        TimerTaskList timerTaskList = timerTaskLists[index];
        if(timerTaskList.addTask(timerTask)){
            if(timerTaskList.setExpiration(timerTaskList.getIntervalStamp())){
                delayQueue.add(timerTaskList);
            }
            return true;
        }
        return false;
    }

}
