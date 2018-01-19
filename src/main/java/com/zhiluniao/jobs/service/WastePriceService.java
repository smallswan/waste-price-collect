package com.zhiluniao.jobs.service;

import java.util.List;

import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.zhiluniao.jobs.dao.CollectTaskRespository;
import com.zhiluniao.jobs.dao.WastePriceRespository;
import com.zhiluniao.jobs.model.entity.CollectTaskEntity;
import com.zhiluniao.jobs.model.entity.WastePrice;

/**
 * 
 *
 * @author huangshunle<br>
 *         2017年4月28日 上午8:47:24
 */
@Slf4j
@Service
public class WastePriceService {
    @Resource
    private WastePriceRespository dao;

    @Resource
    private CollectTaskRespository collectTaskRespository;



    public int saveWastePrice(List<WastePrice> list) {
        if (!list.isEmpty()) {
            dao.save(list);
            log.info("保存价格成功");
        }
        return 0;
    }


    public CollectTaskEntity findCollectTaskByRemoteUri(String remoteUri) {
        return collectTaskRespository.findByRemoteUri(remoteUri);
    }

    public void saveCollectTask(CollectTaskEntity task) {
        collectTaskRespository.save(task);
    }

    public void saveCollectTasks(List<CollectTaskEntity> tasks) {
        collectTaskRespository.save(tasks);
    }

    // 查询未入库记录
    public List<CollectTaskEntity> findNotInStorage() {
        return collectTaskRespository.findByIsLoad("0");
    }

}
