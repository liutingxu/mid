package com.aliware.tianchi.strategy.utils;

import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class Counter {

    private static final Logger logger = LoggerFactory.getLogger(Counter.class);
    private static Counter COUNTER = null;
    private static Boolean IS_EFFECTIVE=false;
    private int length;
    private int[] counter;
    private ThreadLocal<Integer> threadLocal;
    private Timer timer = new Timer();

    private Counter() {
        length = 3;
        counter = new int[3];
        for (int i = 0; i < length; i++) {
            counter[i] = 10000;
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                COUNTER.resetMin();
            }
        }, 2000, 1000);
        threadLocal = new ThreadLocal<>();
    }

    public static Counter getInstance() {

        return COUNTER;
    }

    public int sum() {
        return Arrays.stream(counter).filter(value -> {
            return value > 0;
        }).sum();
    }

    public int max() {
        return Arrays.stream(counter).filter(v -> {
            return v > 0;
        }).max().orElse(0);

    }

    public synchronized void decrease() {
//        if(!UserLoadBalance.isDynamicCount()){
//            return;
//        }
        if(!IS_EFFECTIVE){
            return;
        }
        int index = threadLocal.get();
        if (counter[index] > 500) {
            counter[index] = counter[index] >>> 2;
        } else if (counter[index] > 100) {
            counter[index] = counter[index] - 20;
        } else {
            counter[index] = 200;
        }

//        for(int i=0;i<length;i++){
//            logger.info("counter["+i+"]="+counter[i]);
//        }
    }

    public synchronized void increase() {
//        if(!UserLoadBalance.isDynamicCount()){
//            return;
//        }
        if(!IS_EFFECTIVE){
            return;
        }
        int index = threadLocal.get();
//        logger.info("increase index=" + index);
        if (counter[index] < Integer.MAX_VALUE) {
            counter[index]++;
        }
    }

    public synchronized void resetMin() {
        int min = Arrays.stream(counter).min().orElse(0);
        int secondMin = Arrays.stream(counter).filter(value -> {
            return value > min;
        }).min().orElse(10000);

        for (int i = 0; i < length; i++) {
            if (counter[i] == min) {
                counter[i] = secondMin;
            }
        }


        int max = Arrays.stream(counter).max().orElse(0);
        if (max >= (Integer.MAX_VALUE >>> 2)) {
            for (int i = 0; i < length; i++) {
                counter[i] = counter[i] >>> 2;
            }
        }

    }

    public int getIndexRadomly() {
//        logger.info("Counter randomly select start");
        if (!IS_EFFECTIVE) {

            synchronized (IS_EFFECTIVE) {
                if (!IS_EFFECTIVE) {
                    IS_EFFECTIVE = true;
                }
            }
        }
        int sum = sum();
        int selectedIndex = -1;
        int maxWeight = max();
        int maxIndex = -1;
        int originalRandom = -1;

        if (sum > 0) {

            int randomValue = ThreadLocalRandom.current().nextInt(sum);
            originalRandom = randomValue;

            for (int i = 0; i < length; i++) {
                if (counter[i] <= 0) {
                    continue;
                }
                randomValue = randomValue - counter[i];
                if (randomValue <= 0) {
                    selectedIndex = i;
                    break;
                }
                if (counter[i] == maxWeight) {
                    maxIndex = i;
                }
            }

            if (selectedIndex == -1) {
                selectedIndex = maxIndex;
            }
        }
        if (selectedIndex == -1) {
            selectedIndex = ThreadLocalRandom.current().nextInt(length);
        }

        threadLocal.set(selectedIndex);
//        logger.info("Counter randomly select finish" + selectedIndex + " Counter randomly value=" + originalRandom + "counter=[" + counter[0] + "," + counter[1] + "," + counter[2] + "]");
        return selectedIndex;
    }
}