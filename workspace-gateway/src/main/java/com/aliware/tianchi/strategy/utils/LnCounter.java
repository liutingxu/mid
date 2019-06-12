package com.aliware.tianchi.strategy.utils;

import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.rpc.Invoker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class LnCounter {

    private static final Logger logger = LoggerFactory.getLogger(LnCounter.class);
    private static LnCounter COUNTER = new LnCounter();
    private int length;
    private int[] requestCounter;
    private int[] responseCounter;
    private static ThreadLocal<Integer> threadLocal;
//    private Timer timer = new Timer();

    private List<String> invokers=new ArrayList<>();
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

    public void decreaseRequest(Invoker invoker) {
        try {
            int index = invokers.indexOf(invoker.getUrl().toIdentityString());
            if (requestCounter[index] > 10) {
                requestCounter[index]--;
            } else {
                requestCounter[index] = 10;
            }
        } catch (Exception e) {

        }
    }

    public void increaseRequest(Invoker invoker) {
        try {
            int index = invokers.indexOf(invoker.getUrl().toIdentityString());
            if (requestCounter[index] < Integer.MAX_VALUE) {
                requestCounter[index]++;
            }
        } catch (Exception e) {

        }
    }

    public void increaseResponse(Invoker invoker) {
        try {
            int index = invokers.indexOf(invoker.getUrl().toIdentityString());
//            logger.info("increase response count " + index);
            if (responseCounter[index] < Integer.MAX_VALUE) {
                responseCounter[index]++;
            }
        } catch (Exception e) {

        }
    }

    public void decreaseResponse(Invoker invoker) {
        try {
            int index = invokers.indexOf(invoker.getUrl().toIdentityString());
//            logger.info("decrease response count " + index);

            if (responseCounter[index] > 1000) {
                responseCounter[index] = responseCounter[index] >>> 2;
            } else if (responseCounter[index] > 20) {
                responseCounter[index]--;
            } else {
                responseCounter[index] = 10;
            }
        } catch (Exception e) {

        }
    }


    public int getIndexRadomly(List<Invoker> invokers) {
        if (this.invokers.size() == 0) {

            synchronized (this.invokers) {
                if (this.invokers.size() == 0) {
                    for (Invoker invoker : invokers) {
//                    logger.info("invoker id="+invoker.getUrl().toIdentityString());
                        this.invokers.add(invoker.getUrl().toIdentityString());
                    }
                }
            }
        }

        int selectedIndex = -1;

        double maxWeight=0.0;
        for (int i = 0; i < length; i++) {
            double snapshot = (responseCounter[i]) / Math.log1p(requestCounter[i]);
            if(snapshot>maxWeight){
                selectedIndex=i;
                maxWeight=snapshot;
            }
        }


//        logger.info("sum=" + sum + ", selectedIndex=" + selectedIndex + ", requestCounter=[" + requestCounter[0] + "," + requestCounter[1] + "," + requestCounter[2] + "], responseCounter=[" + responseCounter[0] + "," + responseCounter[1] + "," + responseCounter[2] + "]");
        if (selectedIndex == -1) {
            selectedIndex = ThreadLocalRandom.current().nextInt(length);
        }

        threadLocal.set(selectedIndex);
        return selectedIndex;
    }
}