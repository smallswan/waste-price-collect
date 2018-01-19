package com.zhiluniao.jobs.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


import lombok.extern.slf4j.Slf4j;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zhiluniao.jobs.model.entity.CollectTaskEntity;
import com.zhiluniao.jobs.service.WastePriceService;

/**
 * 采集废旧物资价格html页面任务
 *
 * @author huangshunle<br>
 *         2017年5月15日 下午5:06:45
 */
@Slf4j
@Component
public class CollectTask {
    private Integer startPage;
    private Integer endPage;
    /** 本地保存html页面的根目录 */
    private String savePath;

    public Integer getStartPage() {
        return startPage;
    }

    public void setStartPage(Integer startPage) {
        this.startPage = startPage;
    }

    public Integer getEndPage() {
        return endPage;
    }

    public void setEndPage(Integer endPage) {
        this.endPage = endPage;
    }
    
    

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }



    @Autowired
    private WastePriceService wastePriceService;

    private static final List<Integer> SUPPORT_CLASS_IDS = new ArrayList<Integer>(Arrays.asList(12, 14, 15, 17, 19, 22,
            33, 39, 40, 61));

    public void run() {

        DateTime now = new DateTime();
        log.info("测试任务线程开始执行,{}", now.toString("yyyy-MM-dd HH:mm:ss"));

        ExecutorService es = null;
        List<Future<List<CollectTaskEntity>>> tasks = new ArrayList<Future<List<CollectTaskEntity>>>();
        try {
            es = Executors.newCachedThreadPool();
            for (Integer classId : SUPPORT_CLASS_IDS) {
                // es.submit(new Collector(startPage, endPage, classId));
            	for(int i = startPage; i <= endPage; i++){
                    Future<List<CollectTaskEntity>> result = es.submit(new Collector(i,classId, savePath));
                    tasks.add(result);
            	}


            }
            
            for(Future<List<CollectTaskEntity>> task : tasks){
                List<CollectTaskEntity> t = task.get();
                log.info("采集废旧处理结果：{}", t);
                if (t != null) {
                    wastePriceService.saveCollectTasks(t);
                }
            }

            log.info("采集价格数据成功");
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (es != null) {
                es.shutdown();
            }
        }

    }
}
