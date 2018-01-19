package com.zhiluniao.jobs.dao;

import java.util.Date;
import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.zhiluniao.jobs.model.entity.WastePrice;

/**
 * 
 *
 * @author huangshunle<br>
 *         2017年4月28日  上午8:45:33
 */
public interface WastePriceRespository extends PagingAndSortingRepository<WastePrice, Integer>,JpaRepository<WastePrice, Integer>, JpaSpecificationExecutor<WastePrice>{

    /**
     * 根据URL地址查询价格数据
     * 
     * @param remoteUri
     * @return
     */
    List<WastePrice> findByRemoteUri(String remoteUri);
    
    /**
     * 查询某日，某一废旧物资各个地区最低价、最高价的平均值
     * 
     * @param date
     * @param wasteName
     * @return
     */
    @Query(value ="SELECT AVG(lowest_price),AVG(highest_price) FROM t_waste_price WHERE date = ?1 AND waste_name = ?2" ,nativeQuery=true)
    List<Object[]> avgPrice(Date date,String wasteName);
    
    @Query(value ="SELECT area,lowest_price,highest_price FROM t_waste_price WHERE date = ?1 AND waste_name = ?2 AND area IN (?3)" ,nativeQuery=true)
    List<Object[]> areasPrice(Date date,String wasteName,List<String> areas);
}
