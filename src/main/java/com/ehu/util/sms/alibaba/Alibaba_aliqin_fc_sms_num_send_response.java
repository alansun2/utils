package com.ehu.util.sms.alibaba;

/**
 * 短信验证返回参数
 * @author chenlong 2016-1-25
 *
 */
public class Alibaba_aliqin_fc_sms_num_send_response {
	private Result result;

	private String request_id;

	public void setResult(Result result) {
		this.result = result;
	}

	public Result getResult() {
		return this.result;
	}

	public void setRequest_id(String request_id) {
		this.request_id = request_id;
	}

	public String getRequest_id() {
		return this.request_id;
	}

}
