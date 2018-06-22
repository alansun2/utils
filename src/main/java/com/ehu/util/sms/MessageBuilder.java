package com.ehu.util.sms;

import com.ehu.util.StringUtils;

import java.util.Map;

/**
 * Created by Admin on 2017/7/5.
 */
public class MessageBuilder {


    /**
     * 短信构建模板
     * 1:[签名]启禀${userName}，您的快递已到(${storeAddress})，
     * 请凭取件码『${receiveCode}』到店领取,询(${storePhone})联系驿站。
     * 社区生活，易乎百应:${cmsContent}. 立即下载app:http://t.cn/RXKzQBg
     * @param params
     * @return
     */
    public static String getMsg1(Map<String, Object> params) {

        if(StringUtils.isEmpty((String)params.get("userName"))){
            params.put("userName","小主");
        }
        StringBuilder sb = new StringBuilder("");
//        sb.append("【易乎社区】启禀")
        sb.append("启禀")
                .append(params.get("userName"))
                .append("，您的快递已到")
                .append(params.get("storeAddress"))
                .append("，请凭取件码『")
                .append(params.get("receiveCode"))
                .append("』到店领取，询(")
                .append(params.get("storePhone"))
                .append(")联系驿站。社区生活，易乎百应:")
                .append(params.get("cmsContent"))
                .append(" 立即下载app:http://t.cn/RXKzQBg");
        return sb.toString();
    }
    /**
     * 用户转盘抽奖手机验证:
     * [易乎社区] 1234,正在验证您的手机,请勿向任何人透露,20分钟有效
     * @param code 验证码
     * @return
     */
    public static String getMsg2(String code) {
        StringBuilder sb = new StringBuilder(" ");
        sb.append("验证码[ ")
          .append(code)
          .append(" ],正在验证您的手机,请勿向任何人透露。");
//                .append("社区生活，易乎百应:零食饮料,啤酒辣条,29分钟送达! 立即下载app:http://t.cn/RXKzQBg");
        return sb.toString();
    }
}
