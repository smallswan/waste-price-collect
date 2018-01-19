
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for collect_task
-- ----------------------------
DROP TABLE IF EXISTS `collect_task`;
CREATE TABLE `collect_task` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date` date DEFAULT NULL COMMENT '报价日期',
  `remote_uri` varchar(255) DEFAULT NULL COMMENT '请求页面URL',
  `local_uri` varchar(255) DEFAULT NULL COMMENT '本地保存地址',
  `is_download` char(1) DEFAULT NULL COMMENT '是否已下载，0 未下载，1 已下载',
  `is_load` char(1) DEFAULT NULL COMMENT '是否已入库，0 未入库，1 已入库',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=552111 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `t_waste_price`;
CREATE TABLE `t_waste_price` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `area` varchar(30) DEFAULT NULL,
  `waste_name` varchar(30) DEFAULT NULL,
  `specifications` varchar(255) DEFAULT NULL COMMENT '规格',
  `measurement` varchar(255) DEFAULT NULL COMMENT '计量单位',
  `lowest_price` decimal(10,0) DEFAULT NULL,
  `highest_price` decimal(10,0) DEFAULT NULL,
  `avg_price` decimal(10,0) DEFAULT NULL COMMENT '均价',
  `price_float` decimal(10,0) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `save_time` datetime DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `remote_uri` varchar(255) DEFAULT NULL COMMENT '请求的页面URL',
  `local_uri` varchar(255) DEFAULT NULL COMMENT '本地保存地址',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2197267 DEFAULT CHARSET=utf8;