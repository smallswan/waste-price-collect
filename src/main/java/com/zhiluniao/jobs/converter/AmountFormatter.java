package com.zhiluniao.jobs.converter;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;


/**
 * @Description: 
 *
 * @author          huangshunle
 * @version         V1.0  
 * @Date           2018年4月3日 下午2:25:58 
 */
public class AmountFormatter {

	public static BigDecimal normalFormat(String amount){
		if(StringUtils.isNotBlank(amount)){
			return new BigDecimal(amount.trim().replaceAll(",", ""));
		}else{
			throw new RuntimeException("金额数据为空，无法格式化");
		}
	}
}
