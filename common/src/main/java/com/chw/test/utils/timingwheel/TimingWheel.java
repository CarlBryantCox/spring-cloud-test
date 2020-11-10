package com.chw.test.utils.timingwheel;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

/**
 * 时间轮的实现
 */
public class TimingWheel {

    private int level;

    private long startStamp;

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
        this(tickTime,wheelSize,delayQueue,1,timeUnit,timeUnit.convert(System.currentTimeMillis(),TimeUnit.MILLISECONDS));
    }

    private TimingWheel(int tickTime, int wheelSize, DelayQueue<TimerTaskList> delayQueue, int level, TimeUnit timeUnit,long startStamp) {
        this.tickTime = tickTime;
        this.wheelSize = wheelSize;
        this.interval = tickTime * wheelSize;
        this.timerTaskLists = new TimerTaskList[wheelSize];
        this.delayQueue = delayQueue;
        this.level = level;
        this.timeUnit=timeUnit;
        this.startStamp=startStamp;
        for (int i = 0; i < wheelSize; i++) {
            timerTaskLists[i] = new TimerTaskList(level,timeUnit);
        }
    }

    /**
     * 创建或者获取上层时间轮
     */
    private TimingWheel getOverflowWheel() {
        if (overflowWheel == null) {
            synchronized (this) {
                if (overflowWheel == null) {
                    overflowWheel = new TimingWheel(interval, wheelSize, delayQueue,level+1,timeUnit,startStamp);
                }
            }
        }
        return overflowWheel;
    }

    /**
     * 添加任务到时间轮
     */
    boolean addTask(TimerTask timerTask) {
        long now = timeUnit.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        int delayTime = timerTask.getDelayTime();
        if(timerTask.getDelayStamp()==0){
            timerTask.setDelayStamp(now+delayTime);
        }else {
            if(delayTime==0){
                delayTime= (int) (timerTask.getDelayStamp()-now);
            }
        }
        if(delayTime>interval){
            return this.getOverflowWheel().addTask(timerTask);
        }
        int current = (int) (((now - startStamp) % interval) / tickTime);
        //System.out.println("delayTime="+delayTime+"--interval="+interval+"--current="+current);
        int gap = delayTime / tickTime - 1;
        gap = gap<0 ? 0 : gap;
        int index = gap+current;
        index = index >= wheelSize ? index-wheelSize : index;
        TimerTaskList timerTaskList = timerTaskLists[index];
        if(timerTaskList.addTask(timerTask)){
            if(timerTaskList.setExpiration((gap+1)*tickTime)){
                delayQueue.add(timerTaskList);
            }
            return true;
        }
        return false;
    }

}
