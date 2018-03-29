package com.zhiluniao.jobs.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.zhiluniao.jobs.model.entity.CollectTaskEntity;

public interface CollectTaskRespository extends JpaRepository<CollectTaskEntity, Integer>, JpaSpecificationExecutor<CollectTaskEntity>{

    public CollectTaskEntity findByRemoteUri(String remoteUri);
    
    public List<CollectTaskEntity> findByIsDownload(String isDownload);
    
    public List<CollectTaskEntity> findByIsLoad(String isLoad);
}