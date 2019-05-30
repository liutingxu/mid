package com.aliware.tianchi.strategy;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.cluster.loadbalance.AbstractLoadBalance;

import java.util.List;

public class TestLoadBalanceStrategy extends AbstractLoadBalance {

    private static final String name="test";

    private static final Logger logger=LoggerFactory.getLogger(TestLoadBalanceStrategy.class);

    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        logger.info(invokers.get(2).getUrl().toFullString());
        return invokers.get(invokers.size()-1);
    }
}
