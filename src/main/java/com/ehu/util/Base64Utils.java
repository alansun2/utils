package com.ehu.utils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * @author alan
 * @createtime 18-7-17 下午2:24 *
 */
public class Base64Utils {

    public static String encode(String content) throws UnsupportedEncodingException {
        //编码
        return encode(content.getBytes());
    }

    public static String encode(byte[] contentByte) {
        final Base64.Encoder encoder = Base64.getEncoder();
        //编码
        return encoder.encodeToString(contentByte);
    }

    /**
     * 解码
     */
    public static byte[] decode(String content) throws UnsupportedEncodingException {
        final Base64.Decoder decoder = Base64.getDecoder();
        return decoder.decode(content);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String content = "my name is AlanSun";
        System.out.println("原文：" + content);
        String encode = encode(content);
        System.out.println("编码后：" + encode);
        System.out.println("解码后" + decode(encode));
    }
}
