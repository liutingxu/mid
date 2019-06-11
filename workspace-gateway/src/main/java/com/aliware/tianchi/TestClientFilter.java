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
@Activate(group = Constants.CONSUMER)
public class TestClientFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(TestClientFilter.class);


    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            LnCounter.getInstance().increaseRequest();
            Result result = invoker.invoke(invocation);

//            if( !(result instanceof AsyncRpcResult)) {
//            checkResult(result, false);
//            }
            return result;

        } catch (Exception e) {
            logger.error(e);
            logger.info(e);
            throw e;
        }

    }

    private void checkResult(Result result, boolean isAsync) {
        if (result.hasException() || ( isAsync && (result.getValue() == null || result.getValue().equals("")))) {
//            logger.info("Consumer receive value = " + result.getValue() + (isAsync ? " async" : ""));
            LnCounter.getInstance().decreaseRequest();

        } else {
            LnCounter.getInstance().increaseResponse();
        }
    }

    @Override
    public Result onResponse(Result result, Invoker<?> invoker, Invocation invocation) {
        if (result.hasException() || ( (result.getValue() == null || result.getValue().equals("")))) {
            LnCounter.getInstance().decreaseRequest();

        } else {
            LnCounter.getInstance().increaseResponse();
        }
        return result;
    }

}
