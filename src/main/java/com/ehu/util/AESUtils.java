package com.ehu.utils;
/**
 * @Title: AESUtil.java
 * @Package com.g.mavenspring.demo.util
 * @Description: TODO(用一句话描述该文件做什么)
 * @author
 * @date 2015年12月29日 下午2:38:00
 * @version V1.0
 */

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AESUtils {

    public static void main(String[] args) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException {
        // 密钥的种子，可以是任何形式，本质是字节数组
        String seed = "aaaa";

//        // 密钥数据
//        byte[] rawKey = getSecretKey(strKey);
//        System.out.println(Arrays.toString(rawKey));

        // 密码的明文
        String clearPwd = "My world is full of wonders! No body can match my spirit";
        System.out.println("加密前的数据：" + clearPwd);
        // 密码加密后的密文
        byte[] encryptedByteArr = encrypt(seed, clearPwd);
        System.out.println("密文:" + encryptedByteArr);
        String encode = Base64Utils.encode(encryptedByteArr);
        System.out.println("base64密文:" + encode.length());
        byte[] decode = Base64Utils.decode(encode);
        // 解密后的字符串
//        String decode = Base64Utils.decode(encryptedByteArr);
        String decryptedPwd = decrypt(decode, seed);
        System.out.println("解密后的数据：" + decryptedPwd);
    }

    /**
     * @param seed    密钥种子
     * @param content 明文字符串
     * @return 密文字节数组 (经过base64编码)
     */
    private static byte[] encrypt(String seed, String content) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(getSecretKey(seed), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        return cipher.doFinal(content.getBytes());
    }

    /**
     * @param encrypted 密文字节数组
     * @param seed      密钥种子
     * @return 解密后的字符串
     */
    private static String decrypt(byte[] encrypted, String seed) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(getSecretKey(seed), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted);
    }

    /**
     * @param seed 种子数据
     * @return 密钥数据
     */
    private static byte[] getSecretKey(String seed) throws NoSuchAlgorithmException {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(seed.getBytes());
        // AES加密数据块分组长度必须为128比特，密钥长度可以是128比特、192比特、256比特中的任意一个
        kgen.init(128, secureRandom);
        SecretKey secretKey = kgen.generateKey();
        return secretKey.getEncoded();
    }
}
