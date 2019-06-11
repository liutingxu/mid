package com.aliware.tianchi.strategy.utils;

import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;

public class LnCounter {

    private static final Logger logger = LoggerFactory.getLogger(LnCounter.class);
    private static LnCounter COUNTER = new LnCounter();
    private static Boolean IS_EFFECTIVE = false;
    private int length;
    private int[] requestCounter;
    private int[] responseCounter;
    private ThreadLocal<Integer> threadLocal;
//    private Timer timer = new Timer();

    private LnCounter() {
        length = 3;
        requestCounter = new int[3];
        responseCounter = new int[3];
        for (int i = 0; i < length; i++) {
            requestCounter[i] = 1;
            responseCounter[i] = 1;
        }
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                COUNTER.resetMin();
//            }
//        }, 2000, 1000);
        threadLocal = new ThreadLocal<>();
    }

    public static LnCounter getInstance() {

        return COUNTER;
    }

    public void decreaseRequest() {

        int index = threadLocal.get();
        if (requestCounter[index] > 10) {
            requestCounter[index]--;
        } else {
            requestCounter[index] = 10;
        }
    }

    public void increaseRequest() {

        int index = threadLocal.get();
        if (requestCounter[index] < Integer.MAX_VALUE) {
            requestCounter[index]++;
        }
    }

    public void increaseResponse() {
        int index = threadLocal.get();
        if (responseCounter[index] < Integer.MAX_VALUE) {
            responseCounter[index]++;
        }
    }


    public int getIndexRadomly() {

        int selectedIndex = -1;

        double[] snapshot = new double[requestCounter.length];
        double sum = 0.0;
        for (int i = 0; i < length; i++) {
            snapshot[i] = Math.log10(responseCounter[i]) / Math.log10(requestCounter[i]);
            sum += snapshot[i];
        }


        double randomValue = ThreadLocalRandom.current().nextDouble(sum);

        for (int i = 0; i < length; i++) {

            randomValue = randomValue - snapshot[i];
            if (randomValue <= 0) {
                selectedIndex = i;
                break;
            }

        }


        if (selectedIndex == -1) {
            selectedIndex = ThreadLocalRandom.current().nextInt(length);
        }

        threadLocal.set(selectedIndex);
        return selectedIndex;
    }
}