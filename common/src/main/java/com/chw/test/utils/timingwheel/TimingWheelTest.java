package com.chw.test.utils.timingwheel;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TimingWheelTest {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        TimeUnit timeUnit = TimeUnit.SECONDS;
        SystemTimer systemTimer = new SystemTimer(timeUnit);
        Random random = new Random();
        for (int i = 10; i >= 1; i--) {
            //int i1 = random.nextInt(3000);
            Thread.sleep(1000);
            TimerTask timerTask = new TimerTask(() -> {
                System.out.println(LocalDateTime.now());
                countDownLatch.countDown();
            }, i,String.valueOf(i));
            systemTimer.addTask(timerTask);
            System.out.println(i + "----加入是时间轮--"+ timeUnit.convert(System.currentTimeMillis(),TimeUnit.MILLISECONDS)+"--延时"+i);
        }
        countDownLatch.await();
        System.out.println("执行完毕！");
        systemTimer.stop();
    }
}