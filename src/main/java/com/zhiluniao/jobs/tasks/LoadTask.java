package com.zhiluniao.jobs.tasks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import lombok.extern.slf4j.Slf4j;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zhiluniao.jobs.converter.AluminiumScrapPriceConverter;
import com.zhiluniao.jobs.converter.Converter;
import com.zhiluniao.jobs.converter.IronScrapConverter;
import com.zhiluniao.jobs.converter.PlasticConverter;
import com.zhiluniao.jobs.converter.SteelScrapPriceConverter;
import com.zhiluniao.jobs.model.entity.CollectTaskEntity;
import com.zhiluniao.jobs.model.entity.WastePrice;
import com.zhiluniao.jobs.service.WastePriceService;

/**
 * 废旧物资价格html页面并将废旧物资价格数据入库任务
 *
 * @author huangshunle<br>
 *         2017年5月16日  下午6:50:16
 */
@Slf4j
@Component
public class LoadTask {
    @Autowired
    private IronScrapConverter ironScrapConverter;

    @Autowired
    private SteelScrapPriceConverter steelScrapPriceConverter;

    @Autowired
    private AluminiumScrapPriceConverter aluminiumScrapPriceconverter;

    @Autowired
    private PlasticConverter plasticConverter;

    @Autowired
    private WastePriceService wastePriceService;
    
    
    public void run(){
        List<CollectTaskEntity> tasks = wastePriceService.findNotInStorage();
        log.info("list : {}", tasks);
        if (tasks != null && !tasks.isEmpty()) {
            List<WastePrice> prices = new ArrayList<WastePrice>();
            for (CollectTaskEntity task : tasks) {
                try {
                    int classId = 0;
                    String localUri = task.getLocalUri();
                    String dirs[] = localUri.split("/");

                    if (dirs.length > 2) {

                        classId = Integer.valueOf(dirs[dirs.length - 2]);
                        log.info("classId :{}", classId);
                    } else {
                        continue;
                    }
                    File input = new File(task.getLocalUri());
                    Document doc = Jsoup.parse(input, "gb2312", "");
                    Converter converter = null;
                    switch (classId) {
                        case 12:
                        case 33:
                        case 39:
                        case 61:
                            converter = steelScrapPriceConverter;
                            break;
                        case 14:
                        case 17:
                        case 19:
                        case 22:
                            converter = ironScrapConverter;
                            break;
                        case 15:
                            converter = plasticConverter;
                            break;
                        case 40:
                            converter = aluminiumScrapPriceconverter;
                            break;
                        default:
                            log.error("目前暂不支持废旧类型[classId=" + classId + "]");

                    }

                    prices = converter.convert(doc);

                    // 3. 保存数据
                    if (prices != null && !prices.isEmpty()) {
                        for(WastePrice price : prices){
                            price.setRemoteUri(task.getRemoteUri());
                            price.setLocalUri(task.getLocalUri());
                            price.setDate(task.getDate());
                        }
                        wastePriceService.saveWastePrice(prices);
                        task.setIsDownload("1");
                        task.setIsLoad("1");
                        task.setCreateTime(new Date());
                        wastePriceService.saveCollectTask(task);
                    }

                } catch (IOException e) {
                    log.error("解析本地html文件出错了", e);
                }
            }

            log.info("完成数据入库");
        }
    }

}
