/**
 * File Name: IndexController.java
 * Date: 2019-09-09 17:34:34
 */
package com.tzrcb.dispatch.ops.controller;

import java.util.List;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.tzrcb.dispatch.dto.CommonSearchDTO;
import com.tzrcb.dispatch.model.table.TransRecord;
import com.tzrcb.dispatch.ops.dto.MenuDTO;
import com.tzrcb.dispatch.ops.dto.MsgDTO;
import com.tzrcb.dispatch.ops.dto.ServerStatusDTO;
import com.tzrcb.dispatch.ops.util.OpsUtils;
import com.tzrcb.dispatch.service.TransRecordService;

/**
 * Description: 首页控制器
 * @author shenzulun
 * @date 2019-09-09
 * @version 1.0
 */
public class IndexController extends Controller{
	
	@Inject
	TransRecordService transRecordService;
	/**
	 * 首页
	 */
	public void index() {
		List<MenuDTO> menus = OpsUtils.getMenus();
		setAttr("tabs", menus);
		setAttr("firstTabId", menus.get(0).getId());
		
		setAttr("servers", OpsUtils.checkServerStatus());
		render("index.html");
	}
	
	/**
	 * 查找报文交易记录表
	 */
	public void listTransRecord() {
		CommonSearchDTO searchDTO = getBean(CommonSearchDTO.class,"");
		Page<TransRecord> page = transRecordService.paginate(searchDTO);
		renderJson(page); 
	}
	
	/**
	 * 检查服务状态
	 */
	public void checkPortStatus() {
		List<ServerStatusDTO> list = OpsUtils.checkServerStatus();
		renderJson(list); 
	}
	
	/**
	 * 远程调用-发送报文
	 * @throws Exception 
	 */
	public void sendMsg() throws Exception{
		String msgReq = getPara("msgReq");
		String esbEnv = getPara("esbEnv");
		int threads = getParaToInt("concurrency",1);
		MsgDTO msgDTO = OpsUtils.sendMsg(msgReq,esbEnv,threads);
		renderJson(msgDTO);
	}
	
	public void http(){
		render("esbSend.html");
	}

}
