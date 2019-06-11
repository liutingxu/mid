package com.aliware.tianchi.strategy;

import com.aliware.tianchi.strategy.utils.Counter;
import com.aliware.tianchi.strategy.utils.LnCounter;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.cluster.loadbalance.AbstractLoadBalance;

import java.util.List;

public class DynamicCountLoadBalance extends AbstractLoadBalance {

    private static final String name="DynamicCount";

    private static final Logger logger= LoggerFactory.getLogger(DynamicCountLoadBalance.class);

    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {

        int i= LnCounter.getInstance().getIndexRadomly((List)invokers);
//        logger.info("Dynamic Counter select "+i);
        return invokers.get(i);
    }
}

