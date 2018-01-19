package com.zhiluniao.jobs.converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;

import com.zhiluniao.jobs.model.entity.WastePrice;

import org.joda.time.DateTime;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

/**
 * 
 *
 * @author huangshunle<br>
 *         2017年4月29日 上午8:40:56
 */
@Slf4j
@Component
public class SteelScrapPriceConverter implements Converter {

    // Map<标准名称,List<同义词>>
    Map<String, List<String>> symMap = new HashMap<String, List<String>>();

    /**
     * 
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
//            log.info("area : {}", area);

        }

        DateTime now = new DateTime();
        List<WastePrice> prices = new ArrayList<WastePrice>();
        try {
            Element table = doc.select("table").first();
            Element tbody = table.select("tbody").first();
            Elements trs = tbody.select("tr");

            // col,colName
            Map<String, Integer> colsMap = new HashMap<String, Integer>();
            String format = "";
            int maxColNum = 0;
            int totalRow = trs.size();
            int row = 0; // 定位数据所在的行

            for (int rowNum = 0; rowNum < totalRow; rowNum++) {

                Elements ths = trs.get(rowNum).select("th");
                int thSize = ths.size();
                if (thSize > 0) {
                    row = rowNum + 1;

                    maxColNum = thSize;
                    Object[] arguments = new Object[thSize];
                    for (int i = 0; i < thSize; i++) {

                        if (i < thSize - 1) {
                            format = format.concat("{},");
                        } else {
                            format = format.concat("{}");
                        }

                        arguments[i] = ths.get(i).text();

                        String standName = SynonymUtils.getSynonym(ths.get(i).text());
                        if (!StringUtils.isEmpty(standName)) {
                            colsMap.put(standName, i);

                        }

                    }
//                    log.info(format, arguments);
                    break;
                }
                
                Elements tds = trs.get(rowNum).select("td");
                int tdSize = tds.size();
                if (tdSize > 1) {
                    row = rowNum + 1;
                    maxColNum = tdSize;
                    Object[] arguments = new Object[maxColNum];
                    for (int i = 0; i < maxColNum; i++) {

                        if (i < maxColNum - 1) {
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
                    log.info(format, arguments);
                    break;
                }

            }

            // 采集真实的数据
//            log.info("总共有{}行数据（包括表头），从第{}行开始读取数据", totalRow, row);
            for (int i = row; i < totalRow; i++) {

                Elements tds = trs.get(i).select("td");
                int length = tds.size();

                if (length < maxColNum) {
                    continue;
                }

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
                    price.setLowestPrice(new BigDecimal(lowAndHigh[0].replaceAll(",", "")));
                    price.setHighestPrice(new BigDecimal(lowAndHigh[1].replaceAll(",", "")));
                }

                if (colsMap.containsKey("avgPrice")) {
                    String avgPrice = tds.get(colsMap.get("avgPrice")).text();
                    String[] lowAndHigh = avgPrice.split("-");
                    if (lowAndHigh.length == 2) {
                        price.setLowestPrice(new BigDecimal(lowAndHigh[0].replaceAll(",", "")));
                        price.setHighestPrice(new BigDecimal(lowAndHigh[1].replaceAll(",", "")));
                    } else if (StringUtils.isNotBlank(avgPrice)) {
                        if (StringUtils.isNumeric(avgPrice)) {
                            price.setAvgPrice(new BigDecimal(avgPrice));
                        }

                    }

                }
                if (colsMap.containsKey("priceFloat")) {
                    String priceFloat = tds.get(colsMap.get("priceFloat")).text();
                    if (StringUtils.isNotBlank(priceFloat)) {
                        if (StringUtils.isNumeric(priceFloat)) {
                            price.setPriceFloat(new BigDecimal(priceFloat));
                        }
                    }

                }

                price.setSaveTime(now.toDate());

                prices.add(price);
            }

        } catch (Exception e) {
            log.error("", e);

        }

        return prices;
    }

}
