package com.ehu.util;

import com.alibaba.fastjson.JSON;
import com.ehu.response.elasticsearch.ElasticsearchResponse;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.RequestLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Properties;

/**
 * @author alan
 * @createtime 17-12-26 * elasticsearch工具类
 */
@Getter
@Setter
public class ElasticSearchUtils {

    private static RestClientBuilder builder = null;

    public static RestClient getElasticSearchRestClient() {
//        Header[] defaultHeaders = new Header[]{new BasicHeader("header", "value")};
//        builder.setDefaultHeaders(defaultHeaders);
        if (builder == null) {
            Properties properties = new Properties();
            try {
                properties.load(RedisClient.class.getClassLoader().getResourceAsStream("elasticsearch.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            builder = RestClient.builder(new HttpHost(properties.getProperty("elasticsearch.host"), Integer.parseInt(properties.getProperty("elasticsearch.port", "9200")), "http"));
            builder.setMaxRetryTimeoutMillis(10000);
            //设置一个监听程序，每次节点失败时都会收到通知，以防需要采取措施。在启用失败时进行嗅探时内部使用
//            builder.setFailureListener(new RestClient.FailureListener() {
//                @Override
//                public void onFailure(HttpHost host) {
//
//                }
//            });
            //超时时间
            builder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
                @Override
                public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                    return requestConfigBuilder.setConnectTimeout(5000)
                            .setSocketTimeout(60000);
                }
            }).setMaxRetryTimeoutMillis(60000);
            //basic
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(properties.getProperty("elasticsearch.userName", "elastic"), properties.getProperty("elasticsearch.password")));
            builder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                    return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                }
            });
        }

        return builder.build();
    }

    /**
     * 获取el返回数据
     *
     * @param response
     * @return
     * @throws IOException
     */
    public static ElasticsearchResponse getResponse(Response response, Type type) throws IOException {

        RequestLine requestLine = response.getRequestLine();
        HttpHost host = response.getHost();
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 200) {
            Header[] headers1 = response.getHeaders();
            String responseBody = EntityUtils.toString(response.getEntity());
            if (!StringUtils.isEmpty(responseBody)) {
                ElasticsearchResponse elasticsearchResponse = JSON.parseObject(responseBody, type);
                return elasticsearchResponse;
            }
        }
        return null;
    }

}
