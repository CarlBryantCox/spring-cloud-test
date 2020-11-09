package com.chw.test.utils.timingwheel;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 任务列表
 */
public class TimerTaskList implements Delayed {

    private static final long defaultExpiration=-1L;

    private int level;

    private AtomicInteger size;

    /**
     * 过期时间-时间戳
     */
    private AtomicLong expiration;

    /**
     * 根节点
     */
    private TimerTask root;

    private TimeUnit timeUnit;

    TimerTaskList(int level,TimeUnit timeUnit) {
        this.level = level;
        expiration = new AtomicLong(defaultExpiration);
        size = new AtomicInteger(0);
        root = new TimerTask( null,-1);
        root.prev = root;
        root.next = root;
        this.timeUnit = timeUnit;
    }

    /**
     * 设置过期时间
     */
    boolean setExpiration(int delayMs) {
        long expireStamp = timeUnit.convert(System.currentTimeMillis(),TimeUnit.MILLISECONDS) + delayMs;
        return expiration.compareAndSet(defaultExpiration,expireStamp);
    }

    /**
     * 设置过期时间
     */
    void reSetExpiration() {
        expiration.set(defaultExpiration);
    }

    /**
     * 新增任务
     */
    boolean addTask(TimerTask timerTask) {
        synchronized (this) {
            if (timerTask.timerTaskList == null) {
                timerTask.timerTaskList = this;
                TimerTask tail = root.prev;
                timerTask.next = root;
                timerTask.prev = tail;
                tail.next = timerTask;
                root.prev = timerTask;
                size.getAndIncrement();
                return true;
            }
            return false;
        }
    }

    /**
     * 移除任务
     */
    boolean removeTask(TimerTask timerTask) {
        synchronized (this) {
            if (timerTask.timerTaskList.equals(this)) {
                timerTask.next.prev = timerTask.prev;
                timerTask.prev.next = timerTask.next;
                timerTask.timerTaskList = null;
                timerTask.next = null;
                timerTask.prev = null;
                size.getAndDecrement();
                return true;
            }
            return false;
        }
    }

    int size(){
        return size.get();
    }

    TimerTask getRoot() {
        return root;
    }

    int getLevel() {
        return level;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return Math.max(0, unit.convert(expiration.get() - timeUnit.convert(System.currentTimeMillis(),TimeUnit.MILLISECONDS), timeUnit));
    }

    @Override
    public int compareTo(Delayed o) {
        if (o instanceof TimerTaskList) {
            return Long.compare(expiration.get(), ((TimerTaskList) o).expiration.get());
        }
        return 0;
    }
}
