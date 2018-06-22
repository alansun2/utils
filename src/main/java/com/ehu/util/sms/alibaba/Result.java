package com.ehu.util.sms.alibaba;

/**
 * 短信验证返回参数
 * @author chenlong 2016-1-25
 *
 */
public class Result {
	private String err_code;

	private String model;

	private boolean success;

	public void setErr_code(String err_code) {
		this.err_code = err_code;
	}

	public String getErr_code() {
		return this.err_code;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getModel() {
		return this.model;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean getSuccess() {
		return this.success;
	}

}