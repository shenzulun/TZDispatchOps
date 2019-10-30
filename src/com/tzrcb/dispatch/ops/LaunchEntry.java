/**
 * File Name: LaunchEntry.java
 * Date: 2019-09-09 17:29:00
 */
package com.tzrcb.dispatch.ops;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;
import com.tzrcb.dispatch.ops.controller.IndexController;
import com.tzrcb.dispatch.model.table._MappingKit;
import com.tzrcb.dispatch.util.CommonUtils;


/**
 * Description: 启动入口
 * @author shenzulun
 * @date 2019-09-09
 * @version 1.0
 */
public class LaunchEntry extends JFinalConfig{
	protected static Logger log = LoggerFactory.getLogger(LaunchEntry.class);
	private static Prop prop;

	public void configConstant(Constants me) {
		loadConfig();
		me.setDevMode(prop.getBoolean("devMode", false));
		me.setMaxPostSize(1024 * 1024 * 1024);    //1G
		me.setViewType(ViewType.FREE_MARKER);
		/**
		 * 支持 Controller、Interceptor、Validator 之中使用 @Inject 注入业务层，并且自动实现 AOP
		 * 注入动作支持任意深度并自动处理循环注入
		 */
		me.setInjectDependency(true);
		// 配置对超类中的属性进行注入
		me.setInjectSuperClass(true);
	}
	
	/**
	 * 载入配置文件
	 */
	static void loadConfig() {
		if (prop == null) {
			prop = PropKit.useFirstFound("sys-config.properties");
		}
	}
	
	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
		me.add("/", IndexController.class);
	}

	public void configEngine(Engine me) {
		
	}
	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers me) {
		
	}
	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {
		
	}
	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {
		DruidPlugin druid = createDruidPlugin(); 
		me.add(druid);
		ActiveRecordPlugin arp = new ActiveRecordPlugin(druid);
		arp.setShowSql(prop.getBoolean("showSql", false));
		_MappingKit.mapping(arp);
		me.add(arp);
	}
	
	public static DruidPlugin createDruidPlugin() {	
		loadConfig();
		String url = prop.get("jdbc-url");
		String driverClass = prop.get("jdbc-driverClass");
		String username = prop.get("jdbc-user");
		String password = prop.get("jdbc-password");
		DruidPlugin druid = new DruidPlugin(url, username, password,driverClass); 
		return druid;
	}
	
	/**
	 * 应用启动后执行事件
	 */
	public void afterJFinalStart(){
		CommonUtils.init();
	}
	/**
	 * 应用停止前执行事件
	 */
	public void beforeJFinalStop(){
		
	}
	
	public static void main(String[] args) {
		JFinal.start("WebContent", 9999, "/", 5);
	}
}
