package com.ehu.util;

import com.ehu.model.util.HttpParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@Slf4j
public class HttpClientUtils {
    // 池化管理
    private static PoolingHttpClientConnectionManager poolConnManager = null;

    private static CloseableHttpClient httpClient;
    //请求器的配置
    private static RequestConfig requestConfig;

    /**
     * 绕过验证
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSLv3");

        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sc.init(null, new TrustManager[]{trustManager}, null);
        return sc;
    }

    static {

        try {
            log.info("初始化HttpClientTest~~~开始");
//            SSLContextBuilder builder = new SSLContextBuilder();
//            builder.loadTrustMaterial(SSlUtil.getKeyStore("/opt/appdata/ssl/server.keystore", "tissot"), new TrustSelfSignedStrategy());
            // 配置同时支持 HTTP 和 HTPPS
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register(
                    "http", PlainConnectionSocketFactory.getSocketFactory()).register(
                    "https", new SSLConnectionSocketFactory(createIgnoreVerifySSL())).build();
            // 初始化连接管理器
            poolConnManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

            // 将最大连接数增加到200，实际项目最好从配置文件中读取这个值
            poolConnManager.setMaxTotal(1000);

            // 设置最大路由
            poolConnManager.setDefaultMaxPerRoute(2);

            // 根据默认超时限制初始化requestConfig
            int socketTimeout = 10000;
            int connectTimeout = 10000;
            int connectionRequestTimeout = 10000;
            requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(connectionRequestTimeout)
                    .setSocketTimeout(socketTimeout)
                    .setConnectTimeout(connectTimeout)
                    .build();

            // 初始化httpClient
            httpClient = getConnection();

            log.info("初始化HttpClientTest~~~结束");
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    public static CloseableHttpClient getConnection() {
        CloseableHttpClient httpClient = HttpClients.custom()
                // 设置连接池管理
                .setConnectionManager(poolConnManager)
                // 设置请求配置
                .setDefaultRequestConfig(requestConfig)
                // 设置重试次数
                .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))
                .build();

        if (poolConnManager != null && poolConnManager.getTotalStats() != null) {
            log.info("now client pool "
                    + poolConnManager.getTotalStats().toString());
        }

        return httpClient;
    }

    /**
     * post
     *
     * @param httpParams http参数
     */
    public static CloseableHttpResponse doPost(HttpParams httpParams) {
        HttpPost post = new HttpPost(httpParams.getUrl());
        if (httpParams.getHeaders() == null) {
            post.setHeaders(httpParams.getHeaders());
        }
        if (httpParams.getStrEntity() != null) {
            StringEntity se = new StringEntity(httpParams.getStrEntity(), "UTF-8");
            post.setEntity(se);
        }
        // Send the post request and get the response
        try {
            return httpClient.execute(post);
        } catch (IOException e) {
            log.error("doPost error", e);
        } finally {

        }
        return null;
    }

    /**
     * 执行GET请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String doGet(String url) throws IOException {
        // 创建http GET请求
        HttpGet httpGet = new HttpGet(url);

        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = httpClient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

    public static void main(String[] str) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        HttpParams params = new HttpParams();
        params.setUrl("https://www.ehoo100.com/ehu/swagger-ui.html");
        BasicHeader[] headers = new BasicHeader[1];
        BasicHeader header = new BasicHeader("User-Agent", "Mozilla/5.0");
        headers[0] = header;
        params.setHeaders(headers);
        doPost(params);
//        params.setHeaders(headers);
//        CloseableHttpResponse closeableHttpResponse = doPost(params);
//        String s = EntityUtils.toString(closeableHttpResponse.getEntity());
//        int i = 0;
//        try {
//            doGet("http://www.baidu.com");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}