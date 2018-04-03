package com.zhiluniao.jobs.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.druid.util.StringUtils;
import com.zhiluniao.jobs.converter.AluminiumScrapPriceConverter;
import com.zhiluniao.jobs.converter.Converter;
import com.zhiluniao.jobs.converter.IronScrapConverter;
import com.zhiluniao.jobs.converter.PlasticConverter;
import com.zhiluniao.jobs.converter.SteelScrapPriceConverter;
import com.zhiluniao.jobs.model.entity.CollectTaskEntity;
import com.zhiluniao.jobs.model.entity.WastePrice;
import com.zhiluniao.jobs.service.WastePriceService;

/**
 * 下载本地不存在的html文件，并解析入库
 * @Description: 
 *
 * @author          huangshunle
 * @version         V1.0  
 * @Date           2018年3月28日 下午7:17:20 
 */
@Slf4j
@Component
public class LoadNotDownloadTask {
    private static final String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31";

    
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
        List<CollectTaskEntity> tasks = wastePriceService.findNotInStorageLimit();
        
        log.info("list : {}", tasks);
        if (tasks != null && !tasks.isEmpty()) {
            List<WastePrice> prices = new ArrayList<WastePrice>();
            for (CollectTaskEntity task : tasks) {
                try {
                    int classId = 0;
                    //http://www.zgfp.com/price/View/  length : 31 
                    
                    String remoteUri = task.getRemoteUri();
                    if(!StringUtils.isEmpty(remoteUri) && remoteUri.length() > 33 ){
                    	classId = Integer.valueOf(String.valueOf(remoteUri.subSequence(31, 33)));
                    	log.info("classId :{}", classId);
                    } else {
                        continue;
                    }

                    
                    Document detail = Jsoup.connect(remoteUri).timeout(6000).userAgent(userAgent).get();

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

                    prices = converter.convert(detail);

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
