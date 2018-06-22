package com.ehu.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.UUID;


/**
 * @author AlanSun
 * @Date 2017年1月18日 下午4:40:27
 */
@Slf4j
public class ChatUtils {

    /**
     * 获取聊天用户名
     *
     * @param source
     * @return
     */
    public static String getHxUserName(String source) {
        return source + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6);
    }

    /**
     * 注册
     *
     * @param chatUserName 聊天账号
     * @param nickName     聊天昵称
     * @return
     */
    public static String registerHx(String chatUserName, String nickName, String avatar, boolean sigFlag) {
        try {

            String register = "";/*TencentImUtil.register(chatUserName, nickName, avatar, sigFlag);*/
            if (StringUtils.isEmpty(register)) {
                return null;
            } else {
                log.info("resetChatInfo chatUserName = " + chatUserName + " at " + new Date());
                return register;
            }
        } catch (Exception e) {
            log.error("register chat error", e);
            return null;
        }
    }

    /**
     * 重置聊天签名
     *
     * @param chatUserName
     * @return
     */
//    public static String resetSig(String chatUserName) {
//        return TencentImUtil.getSig(chatUserName);
//    }

    /**
     * 清除聊天
     *
     * @param chatUserName
     */
//    public static void remove(String chatUserName) {
//        boolean logout = TencentImUtil.logout(chatUserName);
//    }



/*
    private static String usersig = "eJxlj9FOgzAUhu95CsKtxrSFbtPEiznUoYwxN2e8Ig0tcGQU7OoEzd59E5fYxHP7ff-5z-m2bNt2VuHygqVp-SF1ortGOPaV7SDn-A82DfCE6cRV-B8UbQNKJCzTQvUQU0oJQqYDXEgNGZyMrmC8AmkIW14mfcvvBu8YH*AhJaYCeQ9nt8*TYOELvhGVR2JepLC8EVE8d7uzR3f9FNXwsqN*Fn2p13L8TvxxkE9C11s-qOIzfmO1vyCrTaBmNS8v21E4l*0dSe9LjKZTJvNro1JDJU4v4QEeoSE2D9oJtYVa9gJBR4W46Gcca28dADCRXhA_";
    private static String identifier = "yhadmin";
    private static String sdkappid = "1400061752";
    private static String register_url;
    private static String logout_url;
    private static String filePath;

    public ImUtils() {
    }

    public static int getRandom() {
        Random rand = new Random();
        int i = rand.nextInt();
        return i > 0 ? i : -i;
    }

    public static String register(String userName, String nick) {
        CloseableHttpClient httpCzlient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";

        String var8;
        try {
            HttpPost httpPost = new HttpPost(register_url);
            Map<String, String> param = new HashMap();
            param.put("Identifier", userName);
            param.put("Nick", nick);
            System.out.println("请求数据" + JSON.toJSONString(param));
            StringEntity entity = new StringEntity(JSON.toJSONString(param), ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            response = httpCzlient.execute(httpPost);
            if (resultString != null) {
                resultString = EntityUtils.toString(response.getEntity(), "utf-8");
                System.out.println("返回数据" + resultString);
                if ("OK".equals(JSON.parseObject(resultString).get("ActionStatus"))) {
                    System.out.println("登录成功");
                    var8 = SigUtil.getSig(filePath, userName, sdkappid);
                    return var8;
                }

                return null;
            }

            var8 = null;
        } catch (Exception var20) {
            var20.printStackTrace();
            return null;
        } finally {
            try {
                response.close();
            } catch (IOException var19) {
                var19.printStackTrace();
            }

        }

        return var8;
    }

    public static boolean logout(String userName) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";

        boolean var5;
        try {
            HttpPost httpPost = new HttpPost(logout_url);
            Map<String, String> param = new HashMap();
            param.put("Identifier", userName);
            System.out.println("请求数据" + JSON.toJSONString(param));
            StringEntity entity = new StringEntity(JSON.toJSONString(param), ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("返回数据" + resultString);
            return true;
        } catch (Exception var15) {
            var15.printStackTrace();
            var5 = false;
        } finally {
            try {
                response.close();
            } catch (IOException var14) {
                var14.printStackTrace();
            }

        }

        return var5;
    }

    static {
        register_url = "https://console.tim.qq.com/v4/im_open_login_svc/account_import?usersig=" + usersig + "&identifier=" + identifier + "&sdkappid=" + sdkappid + "&random=" + getRandom() + "&contenttype=json";
        logout_url = "https://console.tim.qq.com/v4/im_open_login_svc/kick?usersig=" + usersig + "&identifier=" + identifier + "&sdkappid=" + sdkappid + "&random=" + getRandom() + "&contenttype=json";
        filePath = "/opt/appdata/tls/";
    }*/
}
