package com.zhiluniao.jobs.converter;

import java.util.List;


import org.jsoup.nodes.Document;

import com.zhiluniao.jobs.model.entity.WastePrice;

/**
 * 
 *
 * @author huangshunle<br>
 *         2017年4月29日  上午8:33:58
 */
public interface Converter {

    public List<WastePrice> convert(Document doc);
}
