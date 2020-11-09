package com.chw.test.utils.timingwheel;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class TimingWheelTest {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        SystemTimer systemTimer = new SystemTimer();
        Random random = new Random();
        for (int i = 1; i <= 10; i++) {
            int i1 = random.nextInt(30);
            TimerTask timerTask = new TimerTask(() -> {
                System.out.println(LocalDateTime.now());
                countDownLatch.countDown();
            }, i1,String.valueOf(i));
            systemTimer.addTask(timerTask);
            System.out.println(i + "----加入是时间轮--"+ System.currentTimeMillis()/1000+"--延时"+i1+"s!");
        }
        countDownLatch.await();
        System.out.println("执行完毕！");
        systemTimer.stop();
    }
}