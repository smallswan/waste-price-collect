package com.zhiluniao.jobs.converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

import com.zhiluniao.jobs.model.entity.WastePrice;


@Slf4j
@Component
public class PlasticConverter implements Converter {
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
				date = new DateTime(Integer.valueOf(strs[0]),
						Integer.valueOf(strs[1]), Integer.valueOf(strs[2]), 0,
						0);
			}

			Element table = doc.getElementsByTag("table").first();
			Elements trs = table.select("tr");

			// colNum,areaName
			Map<Integer, String> areaMap = new HashMap<Integer, String>();
			// 第一行为表头
			Elements ths = trs.get(0).select("td");
			int size = ths.size();
			if (size > 3) {
				for (int i = 3; i < size; i++) {
					Element area = ths.get(i);
					areaMap.put(i, area.text());
				}
			}

			int row = 0;
			for (Element tr : trs) {
				++row;
				if (row <= 1) {
					continue;
				}

				Elements tds = tr.select("td");
				int tdsNum = tds.size();
				String td1 = tds.get(0).attr("rowspan");
				Date saveTime = new Date();
				int rowspan = StringUtils.isEmpty(td1) ? 0 : Integer.valueOf(td1) ;
				if (rowspan > 0 || (tdsNum == size && size >= 3)) {
					String wasteName = tds.get(1).text();
					String specifications = tds.get(2).text();

					for (int i = 3; i < size; i++) {
						WastePrice price = new WastePrice();
						price.setWasteName(wasteName);
						price.setArea(areaMap.get(i));
						String priceRange = tds.get(i).text();
						String[] lowAndHigh = priceRange.split("-");
						if (lowAndHigh.length == 2) {
							price.setLowestPrice(new BigDecimal(lowAndHigh[0]));
							price.setHighestPrice(new BigDecimal(lowAndHigh[1]));
						}
						price.setDate(date.toDate());
						price.setSaveTime(saveTime);
						price.setSpecifications(specifications);
						prices.add(price);
					}
				} else {
					if(tds.size() < 2){
						log.error("数据异常",tds.text());
						continue;
					}
					String wasteName = tds.get(0).text();
					
					String specifications = tds.get(1).text();
					for (int i = 2; i < size - 1; i++) {
						WastePrice price = new WastePrice();
						price.setWasteName(wasteName);
						price.setArea(areaMap.get(i + 1));
						String priceRange = tds.get(i).text();
						String[] lowAndHigh = priceRange.split("-");
						if (lowAndHigh.length == 2) {
							price.setLowestPrice(new BigDecimal(lowAndHigh[0]));
							price.setHighestPrice(new BigDecimal(lowAndHigh[1]));
						}
						price.setDate(date.toDate());
						price.setSaveTime(saveTime);
						price.setSpecifications(specifications);
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
