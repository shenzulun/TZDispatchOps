/**
 * File Name: OpsUtils.java
 * Date: 2019-09-10 15:41:36
 */
package com.tzrcb.dispatch.ops.util;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tzrcb.dispatch.config.ConstantConfig;
import com.tzrcb.dispatch.core.client.SocketClientFactory;
import com.tzrcb.dispatch.ops.dto.MenuDTO;
import com.tzrcb.dispatch.ops.dto.MsgDTO;
import com.tzrcb.dispatch.ops.dto.ServerStatusDTO;
import com.tzrcb.dispatch.protocol.MessageRequest;
import com.tzrcb.dispatch.protocol.MessageResponse;
import com.tzrcb.dispatch.util.XmlUtils;

/**
 * Description: 运维工具类
 * @author shenzulun
 * @date 2019-09-10
 * @version 1.0
 */
public class OpsUtils {
	protected static Logger log = LoggerFactory.getLogger(OpsUtils.class);
	public static final int MAX_CONCURRENCY = 200;
	public static final int MAX_THREADS = 16;
	
	/**
	 * 获取菜单配置
	 * @return
	 */
	public static List<MenuDTO> getMenus() {
		List<MenuDTO> menus = new ArrayList<MenuDTO>();
		menus.add(new MenuDTO(0, "交易记录", "/pages/trans_record.html"));
		menus.add(new MenuDTO(1, "日志查看", "/pages/logs_center.html"));
		menus.add(new MenuDTO(2, "系统状态", "/pages/system_status.html"));
//		menus.add(new MenuDTO(3, "模拟报文", "/pages/send_msg.html"));
		return menus;
	}
	
	/**
	 * 检查端口状态
	 * @return
	 */
	public static List<ServerStatusDTO> checkServerStatus(){
		List<ServerStatusDTO> list = new ArrayList<ServerStatusDTO>();
		int port1 = ConstantConfig.remoteServerConfig1.getPort();		
		list.add(checkServerStatus(ConstantConfig.remoteServerConfig1.getHost(), port1));
		int port2 = ConstantConfig.remoteServerConfig2.getPort();
		list.add(checkServerStatus(ConstantConfig.remoteServerConfig2.getHost(), port2));
		return list;
	}
	
	/**
	 * 检查端口状态
	 * @param port
	 * @return
	 */
	public static ServerStatusDTO checkServerStatus(String host, int port) {
		int status = -1;
		String output = "";
		Socket socket = null;
		try {
			socket = new Socket(host, port);
			status = 1;
		} catch (UnknownHostException e) {
			log.error("", e);
			output = e.getMessage();
		} catch (IOException e) {
			log.error("", e);
			output = e.getMessage();
		} finally {
			if(socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					log.error("", e);
				}
			}
		}
		ServerStatusDTO serverStatusDTO = new ServerStatusDTO(host, port, status, output);
		return serverStatusDTO;
	}
	
	/**
	 * 发送报文
	 * @param msgReq
	 * @return
	 * @throws Exception
	 */
	public static String sendMsg(String msgReq, String env) throws Exception{
		if(msgReq == null || "".equals(msgReq)){
			return "请求报文不能为空";
		}

		MessageRequest messageRequest = new MessageRequest();
		String[] arr = XmlUtils.getValuesOfXml(msgReq, "GrpHdr", "BusCd", "Ref");
        String transNo = arr[0];
        String requestClassName = new StringBuffer(ConstantConfig.API_CLASS_PATH)
        									 .append("._")
											 .append(transNo)
											 .append("._")
											 .append(transNo)
											 .append("_RequestDocument")
											 .toString();
        Object messageObject = XmlUtils.convertXmlToObject(msgReq, Class.forName(requestClassName));
        messageRequest.setLength(msgReq.getBytes().length);
        messageRequest.setMessageObject(messageObject);
        messageRequest.setTransNo(transNo);
        messageRequest.setMsg(msgReq);
        messageRequest.setMsgId(arr[1]);
        MessageResponse messageResponse = null;
        if("socket1".equals(env)) {
        	messageResponse = SocketClientFactory.sendMsgToSS(messageRequest);
        }else if("socket2".equals(env)) {
        	String fileName = XmlUtils.getValueOfXml(msgReq, "BusiText", "FileName");
        	messageRequest.setFileName(ConstantConfig.local_file_save_path + fileName);
        	messageResponse = SocketClientFactory.sendMsgToRCB(messageRequest);
        }
		String respXml = XmlUtils.convertObjectToXml(messageResponse.getMessageObject());
		return respXml;	
	}
	
	/**
	 * 发送报文,支持多线程并发
	 * @param msgReq
	 * @param env
	 * @param threads
	 * @return
	 * @throws Exception
	 */
	public static MsgDTO sendMsg(final String msgReq, final String env, int threads) throws Exception{
		MsgDTO msgDTO = new MsgDTO();
		if(threads <= 1){
			long start = System.currentTimeMillis();
			String respMsg = sendMsg(msgReq, env);
			long end = System.currentTimeMillis();
			msgDTO.setRespMsg(respMsg);
			msgDTO.setCost(end - start);
		}else{
			if(threads > MAX_CONCURRENCY){
				//超过最大并发数
				msgDTO.setErrCode("100001");
				msgDTO.setErrMsg("最大支持并发数" + MAX_CONCURRENCY + ",请调整");
				return msgDTO;
			}
			//取第一个线程的返回报文
			long start = System.currentTimeMillis();
			String respMsg = sendMsg(msgReq, env);
			long end = System.currentTimeMillis();
			msgDTO.setRespMsg(respMsg);
			
			ExecutorService pool = Executors.newFixedThreadPool(threads > MAX_THREADS ? MAX_THREADS : threads);
			CompletionService<Long> completionService = new ExecutorCompletionService<Long>(pool); 
			for(int i=2;i<=threads;i++){
				completionService.submit(new Callable<Long>(){
					public Long call(){
						long cost = 0L;
						try {
							long start = System.currentTimeMillis();
							OpsUtils.sendMsg(msgReq, env);
							long end = System.currentTimeMillis();
							cost = end - start;
						} catch (Exception e) {
							log.error("",e);
						}
						return cost;
						
					}
				});
			}
			long cost = end - start;
			for(int i=2;i<=threads;i++){
				cost += completionService.take().get();
			}
			pool.shutdown();
			log.info("{}个并发,总耗时：{}", threads, cost);
			msgDTO.setCost(cost / threads);
		}
		return msgDTO;
	}
}
