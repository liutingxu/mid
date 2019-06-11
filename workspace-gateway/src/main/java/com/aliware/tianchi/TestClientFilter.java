package com.aliware.tianchi;

import com.aliware.tianchi.strategy.DynamicCountLoadBalance;
import com.aliware.tianchi.strategy.utils.Counter;
import com.aliware.tianchi.strategy.utils.LnCounter;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.rpc.*;

/**
 * @author daofeng.xjf
 * <p>
 * 客户端过滤器
 * 可选接口
 * 用户可以在客户端拦截请求和响应,捕获 rpc 调用时产生、服务端返回的已知异常。
 */
@Activate(group = Constants.CONSUMER,order = -40000)
public class TestClientFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(TestClientFilter.class);


    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            LnCounter.getInstance().increaseRequest(invoker);
            Result result = invoker.invoke(invocation);
            logger.info("response got");
            if( !(result instanceof AsyncRpcResult)) {
                logger.info("response check");

                checkResult(result,false,invoker);
            }
            return result;

        } catch (Exception e) {
            logger.error(e);
            logger.info(e);
            throw e;
        }

    }

    private void checkResult(Result result, boolean isAsync,Invoker invoker) {
        LnCounter.getInstance().decreaseRequest(invoker);
        if (result.hasException() || ((result.getValue() == null || result.getValue().equals("")))) {
//            logger.info("Consumer receive value = " + result.getValue() + (isAsync ? " async" : ""));
            LnCounter.getInstance().decreaseResponse(invoker);


        } else {
            LnCounter.getInstance().increaseResponse(invoker);
        }
    }

    @Override
    public Result onResponse(Result result, Invoker<?> invoker, Invocation invocation) {
//        System.out.println("onResponse check begin");
        LnCounter.getInstance().decreaseRequest(invoker);
//        System.out.println("onResponse decrease request finish");

        if (result.hasException() || result.getValue() == null || result.getValue().equals("")) {
//            System.out.println("onResponse check decrease");
            LnCounter.getInstance().decreaseResponse(invoker);

        } else {
//            System.out.println("onResponse check increase");

            LnCounter.getInstance().increaseResponse(invoker);
        }
//        System.out.println("onResponse check end");
        return result;
    }

}
