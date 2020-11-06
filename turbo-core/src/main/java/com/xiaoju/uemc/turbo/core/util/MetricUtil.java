package com.xiaoju.uemc.turbo.core.util;

import com.xiaoju.metric.MetricBuilderFactory;
import com.xiaoju.metric.MetricClient;
import com.xiaoju.metric.MetricClientFactory;
import com.xiaoju.metric.Rpc;

/**
 * Created by Stefanie on 2019/12/13.
 */
public class MetricUtil {

    public static void report(String callerName, String calleeName, String code, Long latencyMillisecond) {
        try {
            MetricBuilderFactory factory = new MetricBuilderFactory();
            Rpc rpc = factory.create(Rpc.Builder.class)
                    .caller(callerName)      // 主调实体名称, 可以是服务名、服务名+函数名等
                    .callee(calleeName)      // 被调实体名称, 可以是服务名、服务名+函数名等
                    .code(code)                 // 调用结果, 可取 ok 或者 error
                    .latency(latencyMillisecond)              // 调用延迟, 单位毫秒
                    .build();
            MetricClient client = MetricClientFactory.create();
            client.send(rpc);
        } catch (Exception e) {
        }
    }
}
