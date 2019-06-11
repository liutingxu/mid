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
            LnCounter.getInstance().increaseRequest();
            Result result = invoker.invoke(invocation);
            logger.info("response got");
            if( !(result instanceof AsyncRpcResult)) {
                logger.info("response check");

                checkResult(result,false);
            }
            return result;

        } catch (Exception e) {
            logger.error(e);
            logger.info(e);
            throw e;
        }

    }

    private void checkResult(Result result, boolean isAsync) {
        LnCounter.getInstance().decreaseRequest();
        if (result.hasException() || ((result.getValue() == null || result.getValue().equals("")))) {
//            logger.info("Consumer receive value = " + result.getValue() + (isAsync ? " async" : ""));
            LnCounter.getInstance().decreaseResponse();


        } else {
            LnCounter.getInstance().increaseResponse();
        }
    }

    @Override
    public Result onResponse(Result result, Invoker<?> invoker, Invocation invocation) {
        logger.info("onResponse check begin");
        LnCounter.getInstance().decreaseRequest();
        logger.info("onResponse decrease request finish");

        if (result.hasException() || result.getValue() == null || result.getValue().equals("")) {
            logger.info("onResponse check decrease");
            LnCounter.getInstance().decreaseResponse();

        } else {
            logger.info("onResponse check increase");

            LnCounter.getInstance().increaseResponse();
        }
        logger.info("onResponse check end");
        return result;
    }

}
