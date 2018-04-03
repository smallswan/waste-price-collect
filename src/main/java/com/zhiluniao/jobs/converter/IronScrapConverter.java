package com.zhiluniao.jobs.converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import com.zhiluniao.jobs.model.entity.WastePrice;


/**
 * 
 *
 * @author huangshunle<br>
 *         2017年5月3日  下午4:24:44
 */
@Slf4j
@Component
public class IronScrapConverter implements Converter {

    @Override
    public List<WastePrice> convert(Document doc) {
        List<WastePrice> prices = new ArrayList<WastePrice>();
        try {
            Element title = doc.select("div.title_list").first();

            String titleText = title.text();
            Pattern pattern = Pattern.compile("\\D+");
            String[] strs = pattern.split(titleText);
            DateTime date = new DateTime();
            if (strs.length == 3) {
                date = new DateTime(Integer.valueOf(strs[0]), Integer.valueOf(strs[1]), Integer.valueOf(strs[2]), 0, 0);
            }
            
            int areaStart = titleText.indexOf("日");
            int areaEnd = titleText.indexOf("废");
            String area = null;
            if (areaStart < areaEnd) {
                area = titleText.substring(areaStart + 1, areaEnd);
                log.info("area : {}", area);

            }

            Element table = doc.getElementsByTag("table").first();
            Elements trs = table.select("tr");
            int row = 0;
            for (Element tr : trs) {
                ++row;

                if (row >= 3) {
                    Elements tds = tr.select("td");
                    Date saveTime = new Date();
                    if (tds.size() == 4) {
                        WastePrice price = new WastePrice();
                        price.setArea(tds.get(0).text());
                        price.setWasteName(tds.get(1).text());
                        price.setLowestPrice(new BigDecimal(tds.get(2).text()));
                        price.setHighestPrice(new BigDecimal(tds.get(3).text()));
                        price.setDate(date.toDate());
                        price.setSaveTime(saveTime);

                        prices.add(price);
                    }else if(tds.size() == 3){
                        WastePrice price = new WastePrice();
                        price.setArea(area);
                        price.setWasteName(tds.get(0).text());
                        price.setLowestPrice(new BigDecimal(tds.get(1).text()));
                        price.setHighestPrice(new BigDecimal(tds.get(2).text()));
                        price.setDate(date.toDate());
                        price.setSaveTime(saveTime);
                        
                        prices.add(price);
                    }
                }
            }

        } catch (Exception e) {
            log.error("解析废铁详情页面出错了", e);
        }

        return prices;
    }

}
