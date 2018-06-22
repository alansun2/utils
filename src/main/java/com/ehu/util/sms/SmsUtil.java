package com.ehu.util.sms;

import lombok.extern.slf4j.Slf4j;

/**
 * write something to describe this file.
 *
 * @author demon
 * @since 2017-04-14 18:08.
 */
@Slf4j
public class SmsUtil {
//    //common
//    private static final String SMS_NAME = "易乎社区";
//    private static final String SMS_TYPE = "normal";
//    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";// 加密算法
//    private static final String ENCODE = "UTF-8";// 编码
//
//    //ali
//    private static final String ALI_URL = "http://gw.api.taobao.com/router/rest";//短信路径
//    private static final String ALI_APPKEY = "23301207";//appkey
//    private static final String ALI_SECRET = "5c15e98dfc8926f9042e3de5a82fd992";//密钥
//
//    /***
//     * 生成短信请求,如果发送短信连续失败,切换供应商,各发送两次,总共四次
//     *
//     * @param params
//     * @param phone
//     * @param templateCode
//     * @return
//     */
//    public static boolean sendSms(Map<String, Object> params, String phone, String templateCode) {
//        boolean send = false;
//        StringBuilder message = new StringBuilder("");
//        try {
//            // 如果连续失败,切换供应商,各发送两次
//            send = sendByAli(params, phone, templateCode);
//            if (!send) {
//                send = sendByAli(params, phone, templateCode);
//                message.append("ali(1) ");
//            }
//            if (!send) {
//                message.append("ali(2) ");
//            }
//        } catch (ApiException e) {
//            e.printStackTrace();
//            log.error("短信发送失败，用户手机号{}.", phone);
//            send = false;
//        }
//        if (!message.toString().equals("")) {//打印发送失败详情
//            log.info("短信发送详情: " + message + "failed!");
//        }
//        return send;
//    }
//
//    //阿里大鱼
//    public static boolean sendByAli(Map<String, Object> params, String phone, String templateCode) throws ApiException {
//        String content = new JSONObject(params).toJSONString();
//        boolean result = false;
//        TaobaoClient client = new DefaultTaobaoClient(ALI_URL, ALI_APPKEY, ALI_SECRET);
//        AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
//        //req.setExtend("123456");
//        //短信验证类型，固定不变
//        req.setSmsType(SMS_TYPE);
//        //app签名，固定不变
//        req.setSmsFreeSignName(SMS_NAME);
//        //手机号
//        req.setRecNum(String.valueOf(phone));
//
//        req.setSmsParamString(content);
//
//        //设置模板ID
//        req.setSmsTemplateCode(templateCode);
//        AlibabaAliqinFcSmsNumSendResponse rsp;
//        rsp = client.execute(req);
//        SendSMSResult sendSMSResult = JSONObject.parseObject(rsp.getBody(), SendSMSResult.class);
////        Alibaba_aliqin_fc_sms_num_send_response response = sendSMSResult.getAlibaba_aliqin_fc_sms_num_send_response();
//        if (sendSMSResult != null && sendSMSResult.getAlibaba_aliqin_fc_sms_num_send_response() != null) {
//            // 判断短信是否发送成功
//            result = sendSMSResult.getAlibaba_aliqin_fc_sms_num_send_response().getResult().getSuccess();
//        }
//        return result;
//    }

}
