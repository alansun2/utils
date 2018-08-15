package com.tj.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.tj.model.util.AliOssParams;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * @author ：AlanSun
 * @date ：2018/8/12 15:15
 * @description ：阿里oss
 */
@Slf4j
public class AliOssUtils {
    // Endpoint以杭州为例，其它Region请按实际情况填写。
    private static String endpoint = null;
    // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
    private static String accessKeyId = null;
    private static String accessKeySecret = null;

    /**
     * 获取 ossClient
     *
     * @param aliOssParams
     * @return
     */
    private static OSS getOssClient(AliOssParams aliOssParams) {
        if (StringUtils.hasEmpty(endpoint, accessKeyId, accessKeySecret)) {
            aliOssParams.valid();
            endpoint = aliOssParams.getEndPoint();
            accessKeyId = aliOssParams.getAccessKeyId();
            accessKeySecret = aliOssParams.getAccessKeySecret();
        }
        // 创建OSSClient实例。
//            CredentialsProvider credsProvider = new DefaultCredentialProvider(accessKeyId, accessKeySecret);
//            ClientConfiguration config = new ClientBuilderConfiguration();
//            ossClient = new OSSClient(endpoint, credsProvider, config);
        OSSClientBuilder ossClientBuilder = new OSSClientBuilder();
        return ossClientBuilder.build(endpoint, accessKeyId, accessKeySecret);
    }

    /**
     * 简单上传 流
     */
    public static void putObject(AliOssParams aliOssParams) throws Exception {
        OSS ossClient = getOssClient(aliOssParams);
        try {
            ossClient.putObject(aliOssParams.getBucketName(), aliOssParams.getObjectName(), aliOssParams.getInputStream());
        } catch (Exception e) {
            log.error("oss error", e);
            throw new Exception("oss error");
        } finally {
            if (ossClient != null)
                ossClient.shutdown();
        }
    }

    /**
     * 简单上传 文件
     */
    public static void putFile(AliOssParams aliOssParams) throws Exception {
        OSS ossClient = getOssClient(aliOssParams);
        try {
            ossClient.putObject(aliOssParams.getBucketName(), aliOssParams.getObjectName(), new File("C:\\Users\\AlanSun\\Desktop\\aliyun-oss-java-sdk-2.8.3\\aliyun-oss-java-sdk-2.8.3\\src\\samples\\AppendObjectSample.java"));
        } catch (Exception e) {
            log.error("oss error", e);
            throw new Exception("oss error");
        } finally {
            if (ossClient != null)
                ossClient.shutdown();
        }
    }

    /**
     * 检查文件是否存在
     *
     * @param aliOssParams {@link AliOssParams}
     * @return boolean
     * @throws Exception e
     */
    public static boolean checkIsExist(AliOssParams aliOssParams) throws Exception {
        OSS ossClient = getOssClient(aliOssParams);
        boolean isExist;
        try {
            isExist = ossClient.doesObjectExist(aliOssParams.getBucketName(), aliOssParams.getObjectName());
        } catch (Exception e) {
            log.error("oss error", e);
            throw new Exception("oss error");
        } finally {
            if (ossClient != null)
                ossClient.shutdown();
        }
        return isExist;
    }

    /**
     * 删除object
     *
     * @param aliOssParams {@link AliOssParams}
     * @throws Exception e
     */
    public static void deleteObject(AliOssParams aliOssParams) throws Exception {
        OSS ossClient = getOssClient(aliOssParams);
        try {
            ossClient.deleteObject(aliOssParams.getBucketName(), aliOssParams.getObjectName());
        } catch (Exception e) {
            log.error("oss error", e);
            throw new Exception("oss error");
        } finally {
            if (ossClient != null)
                ossClient.shutdown();
        }
    }

    public static void main(String[] args) throws Exception {
        AliOssParams aliOssParams = new AliOssParams();
        aliOssParams.setObjectName("test/image/tt");
        aliOssParams.setBucketName("tj-static");
        aliOssParams.setAccessKeyId("LTAI301q1m2yHxwN");
        aliOssParams.setAccessKeySecret("lBTDMEMLcNRKDncQ8fbrtDosJ183dd");
        aliOssParams.setEndPoint("oss-cn-hangzhou.aliyuncs.com");
        putFile(aliOssParams);
    }
}
