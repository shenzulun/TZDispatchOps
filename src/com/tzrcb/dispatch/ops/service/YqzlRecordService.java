/**
 * File Name: YqzlRecordService.java
 * Date: 2019-12-30 15:44:45
 */
package com.tzrcb.dispatch.ops.service;

import com.jfinal.plugin.activerecord.Page;
import com.tzrcb.dispatch.dto.CommonSearchDTO;
import com.tzrcb.dispatch.model.table.YqzlRecord;
import com.tzrcb.dispatch.ops.util.OpsUtils;

import me.belucky.easytool.util.StringUtils;

/**
 * Description: 交易记录服务类
 * @author shenzulun
 * @date 2019-12-30
 * @version 1.0
 */
public class YqzlRecordService {
	
	private YqzlRecord dao = new YqzlRecord().dao();

	/**
	 * 分页查询
	 * @param commonSearchDTO
	 * @return
	 */
	public Page<YqzlRecord> paginate(CommonSearchDTO commonSearchDTO) {
		Page<YqzlRecord> result = null;
		int pageNumber = commonSearchDTO.getPageNumber();
		int pageSize = commonSearchDTO.getPageSize();
		String orderBy = commonSearchDTO.getOrderBy();
		String orderDir = commonSearchDTO.getOrderDir();
		if(StringUtils.isBlank(orderBy)) {
			orderBy = "id";
			orderDir = "desc";
		}
		if(StringUtils.isBlank(orderDir)) {
			orderDir = "desc";
		}
		String fromSql = "from t_yqzl_record order by " + orderBy + " " + orderDir;
		String queryValue = commonSearchDTO.getQueryValue();
		if(StringUtils.isBlank(queryValue)) {
			result = dao.paginate(pageNumber, pageSize, "select *", fromSql);
		}else {
			fromSql = new StringBuffer("from t_yqzl_record where ")
							.append(" trans_code like ? or ")
							.append(" cust_no like ? or ")
							.append(" cust_name like ? or ")
							.append(" request_serno like ? or ")
							.append(" response_serno like ? or ")
							.append(" status like ? or ")
							.append(" err_msg like ? or ")
							.append(" request_msg like ? or ")
							.append(" response_msg like ?  ")
							.append(" order by ")
							.append(orderBy)
							.append(" ")
							.append(orderDir).toString();
			String condValue = "%" + queryValue + "%";
			result = dao.paginate(pageNumber, pageSize, "select *", fromSql, condValue, condValue, condValue, condValue, condValue, condValue);
		}
		
		//格式化xml
		if(result != null && result.getList() != null) {
			for(YqzlRecord record : result.getList()) {
				record.put("request_msg_f", OpsUtils.formatXml(record.getRequestMsg(), "UTF-8"));
				record.put("response_msg_f", OpsUtils.formatXml(record.getResponseMsg(), "UTF-8"));
			}
		}
		
		return result;
	}
}
