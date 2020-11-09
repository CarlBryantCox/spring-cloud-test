package com.chw.test.utils.timingwheel;

/**
 * 任务节点
 */
public class TimerTask{

    /**
     * 延迟时间
     */
    private int delayTime;

    private long delayStamp;

    /**
     * 任务
     */
    private Runnable task;

    /**
     * 时间槽
     */
    TimerTaskList timerTaskList;

    /**
     * 下一个节点
     */
    TimerTask next;

    /**
     * 上一个节点
     */
    TimerTask prev;

    /**
     * 描述
     */
    private String desc;

    public TimerTask(Runnable task,int delayTime) {
        this.delayStamp=0;
        this.delayTime = delayTime;
        this.task = task;
        this.timerTaskList = null;
        this.next = null;
        this.prev = null;
    }

    public TimerTask(Runnable task, long delayStamp, String desc) {
        this.delayStamp=delayStamp;
        this.delayTime = 0;
        this.task = task;
        this.timerTaskList = null;
        this.next = null;
        this.prev = null;
        this.desc=desc;
    }

    public TimerTask(Runnable task, int delayTime, String desc) {
        this.delayStamp=0;
        this.delayTime = delayTime;
        this.task = task;
        this.timerTaskList = null;
        this.next = null;
        this.prev = null;
        this.desc=desc;
    }

    Runnable getTask() {
        return task;
    }

    public int getDelayTime() {
        return delayTime;
    }

    public long getDelayStamp() {
        return delayStamp;
    }

    public void setDelayStamp(long delayStamp) {
        this.delayStamp = delayStamp;
    }

    @Override
    public String toString() {
        return desc;
    }
}
