package com.xiaoju.uemc.turbo.engine.util;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import com.google.common.base.Stopwatch;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Stefanie on 2019/12/13.
 */
// TODO: 2019/12/16 upgrade
public class HttpUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);

    public static String postJson(String urlName, String url, String body, int timeout) {
        Long costTime = 0L;

        CloseableHttpClient httpclient = null;
        CloseableHttpResponse httpResponse = null;
        int code = -1;
        String reStr = "";
        try {
            Stopwatch stopwatch = Stopwatch.createStarted();

            httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);

            body = StringUtils.trimToEmpty(body);
            HttpEntity jsonEntity = new ByteArrayEntity(body.getBytes("UTF-8"), ContentType.APPLICATION_JSON);
            httpPost.setEntity(jsonEntity);

            RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeout)
                    .setConnectTimeout(timeout).setSocketTimeout(timeout).build();
            httpPost.setConfig(requestConfig);

            httpResponse = httpclient.execute(httpPost);
            costTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);

            code = httpResponse.getStatusLine().getStatusCode();  // 是int,没有拆箱
            HttpEntity entity = httpResponse.getEntity();
            reStr = EntityUtils.toString(entity); // reStr只有body,没有status
            return reStr;
        } catch (IOException e) {
            LOGGER.error("postJson IOException.||urlName={}||url={}||body={}, ", urlName, url, body, e);
            return StringUtils.EMPTY;
        } finally {
            try {
                if (httpclient != null) {
                    httpclient.close();
                }
                if (httpResponse != null) {
                    httpResponse.close();
                }
            } catch (IOException e) {
                LOGGER.error("postJson exception.||url={}, ", url, e);
            }
            LOGGER.info("postJson.||urlName={}||url={}||body={}||code={}||reStr={}||costTime={}",
                    urlName, url, body, code, reStr, costTime);
            MetricUtil.report("ProcessEngine", url, String.valueOf(code), costTime);
        }
    }
}