package com.zhiluniao.jobs.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.alibaba.druid.util.StringUtils;

/**
 * 同义词工具
 *
 * @author huangshunle<br>
 *         2017年4月30日 下午12:19:57
 */
@Slf4j
@Component
public class SynonymUtils implements InitializingBean {

    static Map<String, List<String>> synonymMap = new ConcurrentHashMap<String, List<String>>();

    @Override
    public void afterPropertiesSet() throws Exception {
        //
        List<String> areas = new ArrayList<String>();
        areas.add("交货地");

        synonymMap.put("area", areas);
        
        List<String> wasterNames = new ArrayList<String>();
        wasterNames.add("名称");
        wasterNames.add("品名");
        wasterNames.add("品种");

        synonymMap.put("wasterName", wasterNames);

        List<String> specifications = new ArrayList<String>();
        specifications.add("材质");
        specifications.add("规格");

        synonymMap.put("specifications", specifications);

        List<String> measurement = new ArrayList<String>();
        measurement.add("单位");

        synonymMap.put("measurement", measurement);

        List<String> lowestPrices = new ArrayList<String>();
        lowestPrices.add("最低价");

        synonymMap.put("lowestPrice", lowestPrices);

        List<String> highestPrices = new ArrayList<String>();
        highestPrices.add("最高价");
        highestPrices.add("最高");

        synonymMap.put("highestPrice", highestPrices);

        List<String> avgPrices = new ArrayList<String>();
        avgPrices.add("平均价");
        avgPrices.add("均价");
        avgPrices.add("价格（元/吨）");
        avgPrices.add("价格(元/吨)");
        avgPrices.add("今天价格");
        avgPrices.add("今日价格");
        
        synonymMap.put("avgPrice", avgPrices);

        List<String> priceFloats = new ArrayList<String>();
        priceFloats.add("涨跌");

        synonymMap.put("priceFloat", priceFloats);

        List<String> priceRanges = new ArrayList<String>();
        priceRanges.add("价格范围");
        priceRanges.add("价格区间");

        synonymMap.put("priceRange", priceRanges);

        List<String> remarks = new ArrayList<String>();
        remarks.add("备注");
        synonymMap.put("remark", remarks);

        log.info("初始化同义词");
    }

    /**
     * 获得同义词
     * 
     * @param colName
     * @return
     */
    public static synchronized String getSynonym(String colName) {

        if(StringUtils.isEmpty(colName) || colName.contains("昨日价格") || colName.contains("昨天价格")){
            return null;
        }
        
        for (Entry<String, List<String>> entry : synonymMap.entrySet()) {
            List<String> syms = entry.getValue();
            for (String sym : syms) {
                if (colName.contains(sym)) {
//                    log.info("{} --> {}", colName, entry.getKey());
                    return entry.getKey();
                }
            }

        }
        log.error("未能匹配" + colName + "的同义词");
        return null;
    }

}
