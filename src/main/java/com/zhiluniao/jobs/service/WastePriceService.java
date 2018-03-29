package com.zhiluniao.jobs.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
            dao.flush();
            log.info("保存价格成功");
        }
        return 0;
    }


    public CollectTaskEntity findCollectTaskByRemoteUri(String remoteUri) {
        return collectTaskRespository.findByRemoteUri(remoteUri);
    }

    public void saveCollectTask(CollectTaskEntity task) {
        collectTaskRespository.saveAndFlush(task);
    }

    public void saveCollectTasks(List<CollectTaskEntity> tasks) {
        collectTaskRespository.save(tasks);
        dao.flush();
    }

    // 查询未入库记录
    public List<CollectTaskEntity> findNotInStorage() {
        return collectTaskRespository.findByIsLoad("0");
    }
    
    public List<CollectTaskEntity> findNotInStorageLimit() {
    	Pageable pageable = new PageRequest(0, 100);
    	Page<CollectTaskEntity> result = collectTaskRespository.findAll(new Specification<CollectTaskEntity>(){
			@Override
			public Predicate toPredicate(Root<CollectTaskEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<>();
				Predicate p = cb.equal(root.<String>get("isLoad"), "0");
				predicates.add(p);
				return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
			}},pageable);
        return result.getContent();
    }

}
