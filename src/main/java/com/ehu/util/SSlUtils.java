package com.ehu.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * 20150503
 *
 * @author AlanSun
 * 验证微信证书
 */
@Slf4j
public class SSlUtils {

    private static KeyStore redpaperKeyStore;

    /**
     * 获取SSLConnectionSocketFactory
     * 采用设置信任自签名证书实现https
     *
     * @param keyStorepass
     * @return
     * @throws Exception
     */
    public static SSLConnectionSocketFactory getSSL(String keyStorePath, String keyStorepass, int type) {
        KeyStore keyStore = getKeyStore(keyStorePath, keyStorepass, type);

        // Trust own CA and all self-signed certs
        SSLContext sslContext = null;
        try {
            sslContext = SSLContexts.custom()
                    .loadKeyMaterial(keyStore, keyStorepass.toCharArray())
                    .build();
        } catch (NoSuchAlgorithmException | KeyManagementException | UnrecoverableKeyException | KeyStoreException e) {
            log.info("get SSL errer", e);
            throw new RuntimeException("get SSL error");
        }
        // Allow TLSv1 protocol only
        return new SSLConnectionSocketFactory(sslContext);
    }

    /**
     * 获取keyStore
     *
     * @param keyStorePath keyStore路径
     * @param keyStorepass keyStore密码
     * @return
     */
    public static KeyStore getKeyStore(String keyStorePath, String keyStorepass) {
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            FileInputStream instream = new FileInputStream(new File(keyStorePath));
            try {
                keyStore.load(instream, keyStorepass.toCharArray());
            } finally {
                instream.close();
            }
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            log.info("get keyStore errer", e);
        }
        return keyStore;
    }

    /**
     * @param type
     * @return
     */
    public static KeyStore getKeyStore(String keyStorePath, String keyStorepass, Integer type) {
        if (type == null)
            return getKeyStore(keyStorePath, keyStorepass);
        switch (type) {
            case 1:
                if (redpaperKeyStore == null) {
                    return getKeyStore(keyStorePath, keyStorepass);
                }
                return redpaperKeyStore;
            default:
                break;
        }
        return null;
    }
}
