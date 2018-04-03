package com.zhiluniao.jobs.converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;

import org.joda.time.DateTime;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.zhiluniao.jobs.model.entity.WastePrice;

/**
 * 
 *
 * @author huangshunle<br>
 *         2017年4月29日 上午8:40:56
 */
@Slf4j
@Component
public class AluminiumScrapPriceConverter implements Converter {

    // Map<标准名称,List<同义词>>
    Map<String, List<String>> symMap = new HashMap<String, List<String>>();

    /**
     * http://www.zgfp.com/price/View/40/2929278.htm
     * 
     * @param table
     * @return
     */
    @Override
    public List<WastePrice> convert(Document doc) {
        // 解析标题获得日期、地区
        String title = doc.select("div.title_list").first().text();

        int areaStart = title.indexOf("日");
        int areaEnd = title.indexOf("废");
        String area = null;
        if (areaStart < areaEnd) {
            area = title.substring(areaStart + 1, areaEnd);
            log.info("area : {}", area);

        }

        DateTime now = new DateTime();
        List<WastePrice> prices = new ArrayList<WastePrice>();
        try {
            Element table = doc.select("table").first();
            Element tbody = table.select("tbody").first();
            Elements trs = tbody.select("tr");

            // col,colName
            Map<String, Integer> colsMap = new HashMap<String, Integer>();
            int row = 0;
            String format = "";
            for (Element tr : trs) {
                Elements tds = tr.select("td");
                int length = tds.size();

                if (length <= 1) {
                    continue;
                }

                if (row == 0) {
                    Object[] arguments = new Object[length];
                    for (int i = 0; i < length; i++) {
                        if (i < length - 1) {
                            format = format.concat("{},");
                        } else {
                            format = format.concat("{}");
                        }

                        arguments[i] = tds.get(i).text();

                        String standName = SynonymUtils.getSynonym(tds.get(i).text());
                        if (!StringUtils.isEmpty(standName)) {
                            colsMap.put(standName, i);
                        }
                    }

                }

                // 跳过标题
                if (row > 0) {
                    WastePrice price = new WastePrice();
                    price.setArea(area);
                    if (colsMap.containsKey("wasterName")) {
                        String wasterName = tds.get(colsMap.get("wasterName")).text();
                        if (StringUtils.isEmpty(wasterName)) {
                            // 跳过空行
                            continue;
                        }
                        price.setWasteName(wasterName);
                    }
                    if (colsMap.containsKey("specifications")) {
                        price.setSpecifications(tds.get(colsMap.get("specifications")).text());
                    }
                    if (colsMap.containsKey("measurement")) {
                        price.setMeasurement(tds.get(colsMap.get("measurement")).text());
                    }

                    if (colsMap.containsKey("lowestPrice")) {
                        price.setLowestPrice(new BigDecimal(tds.get(colsMap.get("lowestPrice")).text()));
                    }
                    if (colsMap.containsKey("highestPrice")) {
                        price.setHighestPrice(new BigDecimal(tds.get(colsMap.get("highestPrice")).text()));
                    }

                    if (colsMap.containsKey("priceRange")) {
                        String priceRange = tds.get(colsMap.get("priceRange")).text();
                        String[] lowAndHigh = priceRange.split("-");
                        
                        
                        if(lowAndHigh.length >= 2 && StringUtils.isNotBlank(lowAndHigh[0].trim()) && StringUtils.isNotBlank(lowAndHigh[1].trim())){
                        	log.info("lowAndHigh :{}|{}",lowAndHigh[0],lowAndHigh[1]);
                        	price.setLowestPrice(AmountFormatter.normalFormat(lowAndHigh[0]));
                            price.setHighestPrice(AmountFormatter.normalFormat(lowAndHigh[1]));
                        }else{
                        	log.info("priceRange : {}",priceRange);
                        }

                    }

                    if (colsMap.containsKey("avgPrice")) {
                        price.setAvgPrice(new BigDecimal(tds.get(colsMap.get("avgPrice")).text()));
                    }
                    if (colsMap.containsKey("priceFloat") && StringUtils.isNotBlank(tds.get(colsMap.get("priceFloat")).text())) {
                        price.setPriceFloat(AmountFormatter.normalFormat(tds.get(colsMap.get("priceFloat")).text()));
                    }

                    price.setSaveTime(now.toDate());

                    prices.add(price);
                }

                Object[] arguments = new Object[length];
                for (int i = 0; i < length; i++) {

                    arguments[i] = tds.get(i).text();

                }

                // log.info(format, arguments);

                row++;
            }

        } catch (Exception e) {
            log.error("", e);

        }

        return prices;
    }

}
