/**
 * File Name: MenuDTO.java
 * Date: 2019-09-10 15:44:02
 */
package com.tzrcb.dispatch.ops.dto;

import com.jfinal.plugin.activerecord.IBean;

/**
 * Description: 菜单
 * @author shenzulun
 * @date 2019-09-10
 * @version 1.0
 */
public class MenuDTO implements IBean{
	
	/**
	 * id
	 */
	private int id;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 文件路径
	 */
	private String filePath;
	
	public MenuDTO() {}
	
	public MenuDTO(int id, String name, String filePath) {
		super();
		this.id = id;
		this.name = name;
		this.filePath = filePath;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}
