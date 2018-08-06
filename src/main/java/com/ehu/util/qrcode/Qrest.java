package com.ehu.util.qrcode;

import com.google.zxing.WriterException;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.util.Base64;

public class Qrest {

    public static void main(String[] args) {

        String content = "大家好，我是李庆文，很高兴认识大家";
        String logUri = "/media/alan/Data/图片/ff.gif";
        String outFileUri = "/media/alan/Data/ttt.jpg";
        String outFileUri1 = "/media/alan/Data/tttr.jpg";
        int[] size = new int[]{430, 430};
        String format = "jpg";

        try {

            FastByteArrayOutputStream byteArrayOutputStream = new FastByteArrayOutputStream();
            Base64OutputStream base64OutputStream = new Base64OutputStream(byteArrayOutputStream);
            QrCodeUtils.writeToStream(QrCodeUtils.creatDefaultBitMatrix("www.ehoo100.com"), format, byteArrayOutputStream, null);
//            String s = byteArrayOutputStream.toString();
//            System.out.println(s);
            byte[] decode = Base64.getEncoder().encode(byteArrayOutputStream.toByteArray());
            System.out.println(new String(decode));
            FileCopyUtils.copy(byteArrayOutputStream.toByteArray(), new File(outFileUri));
//            String s = byteArrayOutputStream.toString();
//            saveToImgByStr(byteArrayOutputStream.toByteArray(), new File(outFileUri));
//            org.apache.commons.io.FileUtils.write(new File(outFileUri1), byteArrayOutputStream.toString(), true);
        } catch (IOException | WriterException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将接收的字符串转换成图片保存
     *
     * @param imgStr 二进制流转换的字符串
     * @param file   图片的保存路径
     * @return 1：保存正常
     * 0：保存失败
     */
    public static int saveToImgByStr(byte[] imgStr, File file) {
        int stateInt = 1;
        try {

            // 将字符串转换成二进制，用于显示图片
            // 将上面生成的图片格式字符串 imgStr，还原成图片显示


            InputStream in = new ByteArrayInputStream(imgStr);

            FileOutputStream fos = new FileOutputStream(file);

            byte[] b = new byte[1024];
            int nRead = 0;
            while ((nRead = in.read(b)) != -1) {
                fos.write(b, 0, nRead);
            }
            fos.flush();
            fos.close();
            in.close();

        } catch (Exception e) {
            stateInt = 0;
            e.printStackTrace();
        } finally {
        }
        return stateInt;
    }


    /**
     * 字符串转二进制
     *
     * @param str 要转换的字符串
     * @return 转换后的二进制数组
     */
    public static byte[] hex2byte(String str) { // 字符串转二进制
        if (str == null)
            return null;
        str = str.trim();
        int len = str.length();
        if (len == 0 || len % 2 == 1)
            return null;
        byte[] b = new byte[len / 2];
        try {
            for (int i = 0; i < str.length(); i += 2) {
                b[i / 2] = (byte) Integer
                        .decode("0X" + str.substring(i, i + 2)).intValue();
            }
            return b;
        } catch (Exception e) {
            return null;
        }


    }
}