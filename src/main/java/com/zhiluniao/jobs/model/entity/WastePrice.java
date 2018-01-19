package com.zhiluniao.jobs.model.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;

/**
 * 
 *
 * @author huangshunle<br>
 *         2017年4月28日 上午8:38:33
 */
@Data
@Entity
@ToString
@Table(name = "t_waste_price")
public class WastePrice implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 8801257554283727958L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "area")
    private String area;

    @Column(name = "waste_name")
    private String wasteName;

    /** 规格 */
    @Column(name = "specifications")
    private String specifications;

    /** 计量单位 */
    @Column(name = "measurement")
    private String measurement;

    @Column(name = "lowest_price")
    private BigDecimal lowestPrice;

    @Column(name = "highest_price")
    private BigDecimal highestPrice;

    @Column(name = "avg_price")
    private BigDecimal avgPrice;
    
    @Column(name = "price_float")
    private BigDecimal priceFloat;

    @Column(name = "date")
    private Date date;

    @Column(name = "save_time")
    private Date saveTime;

    @Column(name = "remark")
    private String remark;
    
    @Column(name = "remote_uri")
    private String remoteUri;
    
    @Column(name = "local_uri")
    private String localUri;

}
