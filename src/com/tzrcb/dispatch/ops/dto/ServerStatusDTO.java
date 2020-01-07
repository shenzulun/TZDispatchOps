/**
 * File Name: ServerStatusDTO.java
 * Date: 2019-09-12 15:41:41
 */
package com.tzrcb.dispatch.ops.dto;

import com.jfinal.plugin.activerecord.IBean;

/**
 * Description: 服务状态DTO
 * @author shenzulun
 * @date 2019-09-12
 * @version 1.0
 */
public class ServerStatusDTO implements IBean{
	
	private String host;
	
	private int port;
	
	private int status;
	
	private String output;
	
	private String statusStr;
	
	public ServerStatusDTO() {};
	
	public ServerStatusDTO(String host, int port, int status, String output) {
		super();
		this.host = host;
		this.port = port;
		this.status = status;
		this.output = output;
	}

	public ServerStatusDTO(int port, int status, String output) {
		super();
		this.port = port;
		this.status = status;
		this.output = output;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public String getStatusStr() {
		return statusStr;
	}

	public void setStatusStr(String statusStr) {
		this.statusStr = statusStr;
	}
}
