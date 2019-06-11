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
            requestCounter[i] = 11;
            responseCounter[i] = 11;
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

    public synchronized void decreaseRequest() {

        int index = threadLocal.get();
        if (requestCounter[index] > 10) {
            requestCounter[index]--;
        } else {
            requestCounter[index] = 10;
        }
    }

    public synchronized void increaseRequest() {

        int index = threadLocal.get();
        if (requestCounter[index] < Integer.MAX_VALUE) {
            requestCounter[index]++;
        }
    }

    public void increaseResponse() {
        int index = threadLocal.get();
        logger.info("increase response count "+index);
        if (responseCounter[index] < Integer.MAX_VALUE) {
            responseCounter[index]++;
        }
    }

    public synchronized void decreaseResponse() {
        int index = threadLocal.get();
        logger.info("decrease response count "+index);

        if (responseCounter[index]>1000) {
            responseCounter[index]=responseCounter[index]>>>2;
        }
        else if(responseCounter[index]>20){
            responseCounter[index]--;
        }
        else{
            responseCounter[index]=10;
        }
    }


    public int getIndexRadomly() {

        int selectedIndex = -1;

        double[] snapshot = new double[requestCounter.length];
        double sum = 0.0;
        for (int i = 0; i < length; i++) {
            snapshot[i] = (responseCounter[i]) / Math.log1p(requestCounter[i]);
            sum += snapshot[i];
        }

        double randomValue = ThreadLocalRandom.current().nextDouble(sum);

        for (int i = 0; i < length; i++) {

            randomValue = randomValue - snapshot[i];
            if (randomValue <= 0.0) {
                selectedIndex = i;
                break;
            }

        }

        logger.info("sum="+sum+", selectedIndex="+selectedIndex+", requestCounter=["+requestCounter[0]+","+requestCounter[1]+","+requestCounter[2]+"], responseCounter=["+responseCounter[0]+","+responseCounter[1]+","+responseCounter[2]+"]");
        if (selectedIndex == -1) {
            selectedIndex = ThreadLocalRandom.current().nextInt(length);
        }

        threadLocal.set(selectedIndex);
        return selectedIndex;
    }
}