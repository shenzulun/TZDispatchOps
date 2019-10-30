/**
 * File Name: MsgDTO.java
 * Date: 2019-09-12 16:48:24
 */
package com.tzrcb.dispatch.ops.dto;

import com.jfinal.plugin.activerecord.IBean;

/**
 * Description: 
 * @author shenzulun
 * @date 2019-09-12
 * @version 1.0
 */
public class MsgDTO implements IBean{

	/**
	 * 返回报文
	 */
	private String respMsg;
	/**
	 * 耗时
	 */
	private Long cost;
	/**
	 * 错误代码
	 * 000000：成功
	 * 其它失败
	 */
	private String errCode;
	/**
	 * 错误信息
	 */
	private String errMsg;
	
	public MsgDTO() {}
	
	public MsgDTO(String respMsg, Long cost, String errCode, String errMsg) {
		super();
		this.respMsg = respMsg;
		this.cost = cost;
		this.errCode = errCode;
		this.errMsg = errMsg;
	}
	public String getRespMsg() {
		return respMsg;
	}
	public void setRespMsg(String respMsg) {
		this.respMsg = respMsg;
	}
	public Long getCost() {
		return cost;
	}
	public void setCost(Long cost) {
		this.cost = cost;
	}
	public String getErrCode() {
		return errCode;
	}
	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}
	public String getErrMsg() {
		return errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
}
